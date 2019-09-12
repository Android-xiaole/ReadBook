package com.jj.comics.ui.search;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.jj.base.ui.BaseActivity;
import com.jj.base.utils.RouterMap;
import com.jj.base.utils.Utils;
import com.jj.comics.R;
import com.jj.comics.R2;
import com.jj.comics.adapter.mine.RecentAdapter;
import com.jj.comics.adapter.mine.SearchRecentAdapter;
import com.jj.comics.common.constants.Constants;
import com.jj.comics.data.model.SearchHotKeywordsResponse;
import com.jj.comics.data.model.SearchModel;
import com.library.flowlayout.FlowLayoutManager;
import com.library.flowlayout.SpaceItemDecoration;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * 发现搜索页面
 */
@Route(path = RouterMap.COMIC_SEARCH_ACTIVITY)
public class SearchActivity extends BaseActivity<SearchPresenter> implements SearchContract.ISearchView {

    @BindView(R2.id.search_edit)
    EditText et_search;//搜索输入框
    @BindView(R2.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R2.id.search_recycler)
    RecyclerView mRecycler;//主recyclerview，加载大家都在看的列表
    private RecyclerView mRecentRecycler;
    private RecentAdapter mRecentAdapter;
    private SearchRecentAdapter mHotAdapter;//热门搜索adapter

    @Override
    public void initData(Bundle savedInstanceState) {

        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.comic_yellow_ffd850));

        //设置搜索框监听
        et_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                int i = v.getId();
                if (i == R.id.search_edit && actionId == EditorInfo.IME_ACTION_SEARCH) {
                    //输入法点击搜索
                    String key = et_search.getText().toString().trim();
                    if (TextUtils.isEmpty(key)) {
                        showToastShort(getString(R.string.comic_search_remind));
                    } else
                        ARouter.getInstance().build(RouterMap.COMIC_SEARCH_RESULT_ACTIVITY).withString(Constants.IntentKey.KEY, key).navigation(SearchActivity.this);
                }
                return false;
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getP().getHotSearchKeywords();
            }
        });

        mRecycler.setHasFixedSize(true);
        mHotAdapter = new SearchRecentAdapter(R.layout.comic_item_search_recent);
        FlowLayoutManager flowLayoutManager = new FlowLayoutManager();
        mRecycler.addItemDecoration(new SpaceItemDecoration(Utils.dip2px(SearchActivity.this, 5)));
        mRecycler.setLayoutManager(flowLayoutManager);
        mHotAdapter.addHeaderView(getRecentRecycler());
        mHotAdapter.bindToRecyclerView(mRecycler);
        mHotAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                ARouter.getInstance().build(RouterMap.COMIC_SEARCH_RESULT_ACTIVITY).withString(Constants.IntentKey.KEY, mHotAdapter.getData().get(position).getKeyword()).navigation(SearchActivity.this);
            }
        });

        showProgress();
        getP().getHotSearchKeywords();
        getP().loadRecentData();
    }

    @Override
    public int getLayoutId() {
        return R.layout.comic_activity_search;
    }

    @Override
    public SearchPresenter setPresenter() {
        return new SearchPresenter();
    }

    private View getRecentRecycler() {
        View view = getLayoutInflater().inflate(R.layout.comic_search_recent_head, (ViewGroup) mRecycler.getParent(), false);
        mRecentRecycler = view.findViewById(R.id.search_recent_recycler);
        mRecentRecycler.setHasFixedSize(true);
        mRecentAdapter = new RecentAdapter(R.layout.comic_item_search_recent);
        FlowLayoutManager flowLayoutManager = new FlowLayoutManager();
        mRecentRecycler.addItemDecoration(new SpaceItemDecoration(Utils.dip2px(this, 5)));
        mRecentRecycler.setLayoutManager(flowLayoutManager);
        mRecentAdapter.bindToRecyclerView(mRecentRecycler);
        mRecentAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                String key = mRecentAdapter.getData().get(position).getKey();
                if (TextUtils.isEmpty(key)) {
                    showToastShort(getString(R.string.comic_search_remind));
                    return;
                }
                ARouter.getInstance().build(RouterMap.COMIC_SEARCH_RESULT_ACTIVITY).withString(Constants.IntentKey.KEY, key).navigation();
            }
        });
        return view;
    }

    @Override
    public void fillKeyData(List<SearchModel> searchModels) {
        mRecentAdapter.setNewData(searchModels);
//        if (mHotRecycler == null) mHotAdapter.addHeaderView(getRecentRecycler(), 1);
    }


    @Override
    public void fillHotSearchKeywords(SearchHotKeywordsResponse response) {
        mHotAdapter.setNewData(response.getData());
    }

    @Override
    public void onComplete() {
        hideProgress();
        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void dealKey(SearchModel model) {
        getP().dealKey(model);
        if (mRecentAdapter == null) return;
        List<SearchModel> data = mRecentAdapter.getData();
        if (data.contains(model)) {
            mRecentAdapter.remove(data.indexOf(model));
        }
        mRecentAdapter.addData(0, model);
        mRecentAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean useEventBus() {
        return true;
    }

    @OnClick({R2.id.search_back})
    void onClick(View view) {
        if (view.getId() == R.id.search_back) {
            finish();
        }
    }
}
