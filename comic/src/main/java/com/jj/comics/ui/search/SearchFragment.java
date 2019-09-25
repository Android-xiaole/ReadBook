package com.jj.comics.ui.search;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.jj.base.ui.BaseFragment;
import com.jj.base.utils.RouterMap;
import com.jj.base.utils.Utils;
import com.jj.comics.R;
import com.jj.comics.R2;
import com.jj.comics.adapter.detail.CommonRecommendAdapter;
import com.jj.comics.adapter.mine.SearchRecentAdapter;
import com.jj.comics.common.constants.Constants;
import com.jj.comics.data.model.SearchHotKeywordsResponse;
import com.jj.comics.data.model.SearchModel;
import com.jj.comics.ui.detail.ComicDetailActivity;
import com.library.flowlayout.FlowLayoutManager;
import com.library.flowlayout.SpaceItemDecoration;

import java.util.List;

import butterknife.BindView;
import me.jessyan.autosize.utils.ScreenUtils;

/**
 * 发现搜索页面
 */
@Route(path = RouterMap.COMIC_SEARCH_FRAGMENT)
public class SearchFragment extends BaseFragment<SearchPresenter> implements SearchContract.ISearchView {

    @BindView(R2.id.lin_root)
    LinearLayout lin_root;//根布局，调整距离上端的距离
    @BindView(R2.id.search_edit)
    EditText et_search;//搜索输入框
    @BindView(R2.id.iv_search)
    ImageView iv_search;//搜索图标
    @BindView(R2.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R2.id.search_recycler)
    RecyclerView mRecycler;//主recyclerview，加载大家都在看的列表

    RecyclerView mHotRecycler;//热门搜索的recyclerview

    private CommonRecommendAdapter mAapter;//主adapter,加载大家都在看的数据
    private SearchRecentAdapter mHotAdapter;//热门搜索adapter

    @Override
    public void initData(Bundle savedInstanceState) {
        //设置toolbar距离上端的高度
        int statusBarHeight = ScreenUtils.getStatusBarHeight();
        CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) lin_root.getLayoutParams();
        lp.topMargin = statusBarHeight;
        lin_root.setLayoutParams(lp);

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
                        ARouter.getInstance().build(RouterMap.COMIC_SEARCH_RESULT_ACTIVITY).withString(Constants.IntentKey.KEY, key).navigation(getActivity());
                }
                return false;
            }
        });
        //点击搜索图标的点击事件
        iv_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String key = et_search.getText().toString().trim();
                if (TextUtils.isEmpty(key)) {
                    showToastShort(getString(R.string.comic_search_remind));
                } else
                    ARouter.getInstance().build(RouterMap.COMIC_SEARCH_RESULT_ACTIVITY).withString(Constants.IntentKey.KEY, key).navigation(getActivity());
            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getP().getHotSearchKeywords();
            }
        });

        mRecycler.setLayoutManager(new GridLayoutManager(getContext(),3));
        mAapter = new CommonRecommendAdapter(R.layout.comic_item_search_watchingcomicdata);
        mAapter.addHeaderView(getHeadView());
        mAapter.bindToRecyclerView(mRecycler,true);
        mAapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                ComicDetailActivity.toDetail(getActivity(), mAapter.getData().get(position).getId(),
                        "大家都在看");
            }
        });

        showProgress();
        getP().getHotSearchKeywords();
    }

    @Override
    public int getLayoutId() {
        return R.layout.comic_fragment_search;
    }

    @Override
    public SearchPresenter setPresenter() {
        return new SearchPresenter();
    }

//    @Override
//    public boolean useEventBus() {
//        return true;
//    }

    /**
     * 获取头布局，包含热门搜索关键词列表
     * @return
     */
    private View getHeadView(){
        View headView = View.inflate(getActivity(),R.layout.comic_head_searchfragment,null);
        mHotRecycler = headView.findViewById(R.id.rv_searchKeywords);
        mHotRecycler.setHasFixedSize(true);
        mHotAdapter = new SearchRecentAdapter(R.layout.comic_item_search_recent);
        FlowLayoutManager flowLayoutManager = new FlowLayoutManager();
        mHotRecycler.addItemDecoration(new SpaceItemDecoration(Utils.dip2px(getContext(), 5)));
        mHotRecycler.setLayoutManager(flowLayoutManager);
        mHotAdapter.bindToRecyclerView(mHotRecycler);
        mHotAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                ARouter.getInstance().build(RouterMap.COMIC_SEARCH_RESULT_ACTIVITY).withString(Constants.IntentKey.KEY, mHotAdapter.getData().get(position).getKeyword()).navigation(getActivity());
            }
        });
        return headView;
    }

    @Override
    public void fillHotSearchKeywords(SearchHotKeywordsResponse response) {
        mHotAdapter.setNewData(response.getData());
    }
    @Override
    public void onComplete() {
        hideProgress();
        if (swipeRefreshLayout.isRefreshing()){
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void fillKeyData(List<SearchModel> searchModels) {

    }

}
