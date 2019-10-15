package com.jj.comics.ui.sort;

import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.jj.base.BaseApplication;
import com.jj.base.net.NetError;
import com.jj.base.ui.BaseActivity;
import com.jj.base.utils.RouterMap;
import com.jj.base.utils.toast.ToastUtil;
import com.jj.comics.R;
import com.jj.comics.R2;
import com.jj.comics.adapter.sort.SortListAdapter;
import com.jj.comics.common.constants.UmEventID;
import com.jj.comics.data.model.BookModel;
import com.jj.comics.ui.detail.ComicDetailActivity;
import com.jj.comics.widget.comic.toolbar.ComicToolBar;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

@Route(path = RouterMap.COMIC_SORTLIST_ACTIVITY)
public class SortListActivity extends BaseActivity<SortListPresent> implements SortListContract.ISortView {
    private String title;
    private String channel;
    private long id;

    private int page = 1;
    private int length = 20;

    @BindView(R2.id.novel_list_recycler)
    RecyclerView novel_list_recycler;
    private SortListAdapter adapter_sort_list;

    @Override
    protected void initData(Bundle savedInstanceState) {
        ComicToolBar toolBar = findViewById(R.id.bind_phone_bar);
        title = getIntent().getStringExtra("title");
        channel = getIntent().getStringExtra("channel");
        id = getIntent().getLongExtra("id", 0);

        Map<String, Object> page_classify_list = new HashMap<String, Object>();
        page_classify_list.put("from", title);
        page_classify_list.put("channel", "" + channel);
        MobclickAgent.onEventObject(BaseApplication.getApplication(), UmEventID.PAGE_CLASSIFY_LIST, page_classify_list);

        if (title != null) {
            toolBar.setTitleText(title);
        }
        showProgress();
        getP().loadNoverList(page, length, id);

        novel_list_recycler.setLayoutManager(new LinearLayoutManager(this));
        novel_list_recycler.setHasFixedSize(true);
        adapter_sort_list = new SortListAdapter(R.layout.comic_item_sort_list);
        adapter_sort_list.bindToRecyclerView(novel_list_recycler, true);
        adapter_sort_list.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                page++;
                getP().loadNoverList(page, length, id);
            }
        }, novel_list_recycler);
        adapter_sort_list.setEmptyClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getP().loadNoverList(page, length, id);
            }
        });
        adapter_sort_list.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                BookModel model = adapter_sort_list.getData().get(position);
                ComicDetailActivity.toDetail(SortListActivity.this, model.getId(), title);
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.comic_activity_sortlist;
    }

    @Override
    public SortListPresent setPresenter() {
        return new SortListPresent();
    }

    @Override
    public void fillNoverList(List<BookModel> bookModels, long totalSize) {
        hideProgress();
        if (page == 1) {
            adapter_sort_list.setNewData(bookModels);
        } else {
            adapter_sort_list.addData(bookModels);
        }
        adapter_sort_list.loadMoreComplete();
        if (adapter_sort_list.getData().size() >= totalSize) {
            adapter_sort_list.loadMoreEnd();
        }
    }

    @Override
    public void getListFail(NetError netError) {
        hideProgress();
        page = 1;
        ToastUtil.showToastShort(netError.getMessage());
    }
}
