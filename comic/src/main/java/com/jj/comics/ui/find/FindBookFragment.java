package com.jj.comics.ui.find;


import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.android.material.appbar.AppBarLayout;
import com.jj.base.net.NetError;
import com.jj.base.ui.BaseCommonFragment;
import com.jj.base.utils.RouterMap;
import com.jj.comics.R;
import com.jj.comics.R2;
import com.jj.comics.adapter.findbook.FindBookTypeAdapter;
import com.jj.comics.adapter.findbook.SortBookAdapter;
import com.jj.comics.adapter.findbook.SortKeyAdapter;
import com.jj.comics.common.callback.GlideOnScrollListener;
import com.jj.comics.common.constants.Constants;
import com.jj.comics.data.model.BookModel;
import com.jj.comics.data.model.CategoryResponse;
import com.jj.comics.data.model.TypeSortKeyBean;
import com.jj.comics.ui.detail.DetailActivityHelper;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import butterknife.OnClick;
import me.jessyan.autosize.utils.ScreenUtils;

/**
 * 分类找书页面
 */
@Route(path = RouterMap.COMIC_FIND_FRAGMENT)
public class FindBookFragment extends BaseCommonFragment<FindBookPresenter> implements FindBookContract.IFindView, SwipeRefreshLayout.OnRefreshListener {

    @BindView(R2.id.findBook_recycler_type)
    RecyclerView mTypeRecycler;
    @BindView(R2.id.findBook_recycler_sort)
    RecyclerView mSortRecycler;
    @BindView(R2.id.findBook_refresh)
    SwipeRefreshLayout mRefresh;
    @BindView(R2.id.findBook_recycler)
    RecyclerView mRecycler;
    @BindView(R2.id.app_bar)
    AppBarLayout mAppBarLayout;
    @BindView(R2.id.type_load_fail)
    LinearLayout mRlTypeLoadFail;
    private FindBookTypeAdapter mTypeAdapter;
    private SortBookAdapter mAdapter;
//    @BindView(R2.id.findBook_float_btn)
//    ImageView mToTop;

    @BindView(R2.id.type_hot)
    TextView typeHot;
    @BindView(R2.id.type_update)
    TextView typeUpdate;
    @BindView(R2.id.type_new)
    TextView typeNew;

    private int currentPage = 1;
    private long type1Code = 0;
    private boolean evict = false;
    private String sort = "allvisit";
    private SortKeyAdapter mSortKeyAdapter;


    @Override
    public void initData(Bundle savedInstanceState) {
        //设置toolbar距离上端的高度
        int statusBarHeight = ScreenUtils.getStatusBarHeight();
        CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) mAppBarLayout.getLayoutParams();
        lp.topMargin = statusBarHeight;
        mAppBarLayout.setLayoutParams(lp);

        mRefresh.setColorSchemeColors(getResources().getColor(R.color.comic_yellow_ffd850));
        mRefresh.setOnRefreshListener(this);

        mTypeAdapter = new FindBookTypeAdapter(getActivity(), R.layout.comic_find_book_type_item);
        mTypeRecycler.setLayoutManager(new GridLayoutManager(getContext(), 4));
        mTypeAdapter.bindToRecyclerView(mTypeRecycler);
        mTypeAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                mTypeAdapter.setSelectIndex(position);
                currentPage = 1;
                type1Code = mTypeAdapter.getData().get(position).getId();
//                showProgress();
                mRefresh.setRefreshing(true);
                evict = false;
                getP().loadList(currentPage, type1Code, sort);
            }
        });

        mSortKeyAdapter = new SortKeyAdapter(getActivity(),R.layout.comic_find_book_type_item);
        mSortRecycler.setLayoutManager(new GridLayoutManager(getContext(), 4));
        mSortKeyAdapter.bindToRecyclerView(mSortRecycler);
        mSortKeyAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                mSortKeyAdapter.setSelectIndex(position);
                TypeSortKeyBean typeSortKeyBean = mSortKeyAdapter.getData().get(position);
                sort = typeSortKeyBean.getKey();
                currentPage = 1;
                getP().loadList(currentPage, type1Code, typeSortKeyBean.getKey());
            }
        });

        ArrayList<TypeSortKeyBean> data = new ArrayList<>();
        TypeSortKeyBean key1 = new TypeSortKeyBean("热门","allvisit");
        TypeSortKeyBean key2 = new TypeSortKeyBean("更新","chapter_uptime");
        TypeSortKeyBean key3 = new TypeSortKeyBean("新上架","create_time");
        data.add(key1);
        data.add(key2);
        data.add(key3);
        mSortKeyAdapter.setNewData(data);

        mAdapter = new SortBookAdapter(R.layout.comic_item_sort);
        mRecycler.addOnScrollListener(new GlideOnScrollListener());
//        mRecycler.addOnScrollListener(new OnScrollListenerWithButton(mToTop));
//        mRecycler.setLayoutManager(new GridLayoutManager(getContext(), 3));
        mRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter.bindToRecyclerView(mRecycler, true);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
//                ComicDetailModel mainContent = mAdapter.getData().get(position).getMainContent();
                long bookId = mAdapter.getData().get(position).getId();
                //上传分类漫画点击事件到友盟
                MobclickAgent.onEvent(getContext(), Constants.UMEventId.CLASSIFY, mAdapter.getData().get(position).getId() + " : " + mAdapter.getData().get(position).getTitle());
                DetailActivityHelper.toDetail(getActivity(), bookId, "分类");
            }
        });
        mAdapter.disableLoadMoreIfNotFullPage();
        mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                currentPage++;
                getP().loadList(currentPage, type1Code, sort);
            }
        }, mRecycler);

        mAdapter.setEmptyClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                evict = true;
                showProgress();
                getP().loadType();
            }
        });
        mRefresh.setRefreshing(true);
//        showProgress();
        getP().loadType();
        typeHot.setTextColor(getResources().getColor(R.color.comic_ffffff));
        typeHot.setSelected(true);
        typeHot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgress();
                typeHot.setTextColor(getResources().getColor(R.color.comic_ffffff));
                typeHot.setSelected(true);
                typeUpdate.setTextColor(getResources().getColor(R.color.comic_666666));
                typeUpdate.setSelected(false);
                typeNew.setTextColor(getResources().getColor(R.color.comic_666666));
                typeNew.setSelected(false);
                sort = "allvisit";
                currentPage = 1;
                evict = false;
                getP().loadList(currentPage, type1Code, sort);
            }
        });
        typeUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgress();
                typeUpdate.setTextColor(getResources().getColor(R.color.comic_ffffff));
                typeUpdate.setSelected(true);
                typeHot.setTextColor(getResources().getColor(R.color.comic_666666));
                typeHot.setSelected(false);
                typeNew.setTextColor(getResources().getColor(R.color.comic_666666));
                typeNew.setSelected(false);
                sort = "chapter_uptime";
                currentPage = 1;
                evict = false;
                getP().loadList(currentPage, type1Code, sort);
            }
        });
        typeNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgress();
                typeNew.setTextColor(getResources().getColor(R.color.comic_ffffff));
                typeNew.setSelected(true);
                typeHot.setTextColor(getResources().getColor(R.color.comic_666666));
                typeHot.setSelected(false);
                typeUpdate.setTextColor(getResources().getColor(R.color.comic_666666));
                typeUpdate.setSelected(false);
                sort = "create_time";
                currentPage = 1;
                evict = false;
                getP().loadList(currentPage, type1Code, sort);
            }
        });
    }

    @Override
    public int getLayoutId() {
        return R.layout.comic_fragment_find_book_new;
    }

    @Override
    public FindBookPresenter setPresenter() {
        return new FindBookPresenter();
    }

    @Override
    public void onRefresh() {
        currentPage = 1;
        evict = true;
//        getP().loadType(evict);
        getP().loadList(currentPage, type1Code, sort);
    }

    public void fillType(List<CategoryResponse.DataBean> typeList) {
        mRefresh.setEnabled(true);
        mRlTypeLoadFail.setVisibility(View.GONE);
        mTypeAdapter.setNewData(typeList);
        mTypeAdapter.setOnItemClick(null, 0);
    }

    public void fillData(long total_num,List<BookModel> contentList) {
        if (currentPage == 1) {
            mRecycler.smoothScrollToPosition(0);
            mAdapter.setNewData(contentList);
        } else {
            mAdapter.addData(contentList);
        }
        mAdapter.loadMoreComplete();
        if (mAdapter.getData().size() == total_num) {
            mAdapter.loadMoreEnd(false);
        }
        if (mRefresh.isRefreshing())
            mRefresh.setRefreshing(false);
        hideProgress();
    }

    @OnClick({/*R2.id.findBook_float_btn,*/ R2.id.findBook_search, R2.id.type_load_fail})
    void onClick(View view) {
        int id = view.getId();
        if (id == R.id.findBook_float_btn)
            mRecycler.smoothScrollToPosition(0);
        else if (id == R.id.findBook_search) {
            MobclickAgent.onEvent(getContext(), Constants.UMEventId.CLICK_SEARCH);
            ARouter.getInstance().build(RouterMap.COMIC_SEARCH_ACTIVITY).navigation(getActivity());
        } else if (id == R.id.type_load_fail) {
            mRefresh.setRefreshing(true);
            showToastShort("加载中...");
            getP().loadType();
        }
    }

    @Override
    public void getDataFail(NetError error) {

        if (error.getType() == NetError.NoDataError) {
            mAdapter.loadMoreEnd(false);
        } else {
            mAdapter.loadMoreFail();
        }
        if (mRefresh.isRefreshing())
            mRefresh.setRefreshing(false);
        mAdapter.setEmptyText(error.getMessage());
        if (currentPage == 1) {
            mAdapter.setNewData(null);
        } else {
            mAdapter.addData(new ArrayList<BookModel>());
        }
//        hideProgress();
        currentPage--;
    }

    @Override
    public void getTypeFail(NetError netError) {
        mRlTypeLoadFail.setVisibility(View.VISIBLE);
        mRefresh.setRefreshing(false);
        mRefresh.setEnabled(false);
    }

}
