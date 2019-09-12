package com.jj.comics.ui.sort;

import android.os.Bundle;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.jj.base.net.NetError;
import com.jj.base.ui.BaseActivity;
import com.jj.base.utils.RouterMap;
import com.jj.base.utils.toast.ToastUtil;
import com.jj.comics.R;
import com.jj.comics.R2;
import com.jj.comics.adapter.recommend.RecentlyAdapter;
import com.jj.comics.data.model.BookModel;
import com.jj.comics.ui.detail.DetailActivityHelper;
import com.jj.comics.ui.find.NovelListFragment;
import com.jj.comics.widget.comic.toolbar.ComicToolBar;

import java.io.Serializable;
import java.util.List;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;

@Route(path = RouterMap.COMIC_SORTLIST_ACTIVITY)
public class SortListActivity extends BaseActivity<SortListPresent> implements SortListContract.ISortView {
    private String title;
    private long id;

    private int page = 1;//记录最近更新分页请求页数
    private int length = 20;

    @BindView(R2.id.novel_list_recycler)
    RecyclerView novel_list_recycler;
    private RecentlyAdapter adapter_recently;//最近更新adapter

    @Override
    protected void initData(Bundle savedInstanceState) {
        ComicToolBar toolBar = findViewById(R.id.bind_phone_bar);
        title = getIntent().getStringExtra("title");
        id = getIntent().getLongExtra("id", 0);
        if (title != null) {
            toolBar.setTitleText(title);
        }
        getP().loadNoverList(page, length, id);

        novel_list_recycler.setLayoutManager(new LinearLayoutManager(this));
        novel_list_recycler.setHasFixedSize(true);
        adapter_recently = new RecentlyAdapter(R.layout.comic_item_recommend_recentlyupdate);
        adapter_recently.bindToRecyclerView(novel_list_recycler, true);
        adapter_recently.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                page++;
                getP().loadNoverList(page, length, id);
            }
        }, novel_list_recycler);
        adapter_recently.setEmptyClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getP().loadNoverList(page, length, id);
            }
        });
        adapter_recently.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                BookModel model = adapter_recently.getData().get(position);
                DetailActivityHelper.toDetail(SortListActivity.this, model.getId(), title);
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
        if (page == 1) {
            adapter_recently.setNewData(bookModels);
        } else {
            adapter_recently.addData(bookModels);
        }
        adapter_recently.loadMoreComplete();
        if (adapter_recently.getData().size() >= totalSize) {
            adapter_recently.loadMoreEnd();
        }
    }

    @Override
    public void getListFail(NetError netError) {
        page = 1;
        ToastUtil.showToastShort(netError.getMessage());
    }
}
