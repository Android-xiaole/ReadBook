package com.jj.comics.ui.mine.userinfo;

import android.os.Bundle;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.jj.base.ui.BaseVPFragment;
import com.jj.comics.R;
import com.jj.comics.R2;
import com.jj.comics.adapter.mine.AppRaiseInfosAdapter;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;

public class AppRaiseFragment extends BaseVPFragment<AppRaisePresenter> implements AppRaiseContract.IAppRaiseView{
    @BindView(R2.id.common_refresh)
    SwipeRefreshLayout mRefreshLayout;
    @BindView(R2.id.common_recycler)
    RecyclerView mRecyclerView;
    private AppRaiseInfosAdapter mAdapter;

    @Override
    public void initData(Bundle savedInstanceState) {
        mRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.base_yellow_ffd850));
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getP().getMyStarList(1, 99);
            }
        });
        mRefreshLayout.setRefreshing(true);

        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        mAdapter = new AppRaiseInfosAdapter(R.layout.comic_item_app_raise);
        mAdapter.setEmptyView(R.layout.comic_layout_empty_mystar, mRecyclerView);
        mAdapter.isUseEmpty(true);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
            }
        });


        getP().getMyStarList(1, 99);

    }

    @Override
    public int getLayoutId() {
        return R.layout.comic_fragment_simple;
    }

    @Override
    public AppRaisePresenter setPresenter() {
        return new AppRaisePresenter();
    }

}
