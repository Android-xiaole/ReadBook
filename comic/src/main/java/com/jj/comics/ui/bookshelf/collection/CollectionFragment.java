package com.jj.comics.ui.bookshelf.collection;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.jj.base.BaseApplication;
import com.jj.base.imageloader.ILFactory;
import com.jj.base.net.NetError;
import com.jj.base.ui.BaseVPFragment;
import com.jj.base.utils.CommonUtil;
import com.jj.base.utils.RouterMap;
import com.jj.base.utils.toast.ToastUtil;
import com.jj.comics.R;
import com.jj.comics.R2;
import com.jj.comics.adapter.bookshelf.BookShelfAdapter;
import com.jj.comics.adapter.bookshelf.BookShelfFooterAdapter;
import com.jj.comics.common.callback.GlideOnScrollListener;
import com.jj.comics.data.model.BookModel;
import com.jj.comics.data.model.CollectionResponse;
import com.jj.comics.ui.bookshelf.BookShelfFragment;
import com.jj.comics.ui.detail.DetailActivityHelper;
import com.jj.comics.ui.dialog.DialogUtilForComic;
import com.jj.comics.ui.dialog.NormalNotifyDialog;
import com.jj.comics.util.LoginHelper;
import com.jj.comics.util.eventbus.EventBusManager;
import com.jj.comics.util.eventbus.events.ChangeTabBarEvent;
import com.jj.comics.util.eventbus.events.LoginEvent;
import com.jj.comics.util.eventbus.events.LogoutEvent;
import com.jj.comics.util.eventbus.events.RefreshComicCollectionStatusEvent;
import com.jj.comics.widget.MyDecoration;
import com.jj.comics.widget.comic.ComicLinearLayoutManager;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;

public class CollectionFragment extends BaseVPFragment<CollectionPresenter> implements CollectionContract.ICollectionView, View.OnClickListener {
    @BindView(R2.id.sr_with_delete)
    SwipeRefreshLayout mRefresh;
    @BindView(R2.id.rv_with_delete)
    RecyclerView mRecycler;
    @BindView(R2.id.ll_delete_container)
    LinearLayout mDeleteBtns;
    @BindView(R2.id.comic_bookshelf_select_check)
    CheckBox mSelectCheckBox;
    @BindView(R2.id.comic_bookshelf_delete_check)
    CheckBox mDeleteCheckBox;
    @BindView(R2.id.comic_bookshelf_select)
    RelativeLayout mSelectCheckBoxContainer;
    @BindView(R2.id.comic_bookshelf_delete)
    RelativeLayout mDeleteCheckBoxContainer;
    private BookShelfAdapter mAdapter;
    private TextView loadMore;
    RecyclerView mFootRecycler;
    private BookShelfFooterAdapter mFootAdapter;
    private int page = 1;
    private int currentPage = 1;
    private int pageSize = 99;
    private View mLoginHeaderView;
    private View mDeleteHeaderView;

    @Override
    public void initData(Bundle savedInstanceState) {
        mAdapter = new BookShelfAdapter(R.layout.comic_bookshelf_item,null);

        mRefresh.setColorSchemeColors(getResources().getColor(R.color.base_yellow_ffd850));
        mRefresh.setRefreshing(true);
        mRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                currentPage = 1;
                getP().getCollectionList(currentPage, pageSize);
            }
        });

        mSelectCheckBoxContainer.setOnClickListener(this);
        mDeleteCheckBoxContainer.setOnClickListener(this);

        mRecycler.setLayoutManager(new ComicLinearLayoutManager(getContext()));
        mRecycler.addOnScrollListener(new GlideOnScrollListener());
        mRecycler.setFocusableInTouchMode(false);
        mRecycler.setFocusable(false);
        mAdapter.closeLoadAnimation();
        mAdapter.bindToRecyclerView(mRecycler);
        mAdapter.setEmptyView(getEmptyView());
        mAdapter.isUseEmpty(LoginHelper.getOnLineUser() != null);
        mAdapter.setHeaderFooterEmpty(true, true);
        mAdapter.addFooterView(getFootView());


        mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                getP().toRead(mAdapter.getData().get(position), mAdapter.getData().get(position).getChapterid());
            }
        });

        getP().getCollectionList(currentPage, pageSize);
        getP().loadRecommendData();
    }

    @Override
    public void onLoadCollectionList(List<BookModel> bookModels) {
//        if (bookModels.size() == 0) {
//            loadMore.setVisibility(View.GONE);
//        } else {
//            loadMore.setVisibility(View.VISIBLE);
//        }
//        if (currentPage == 1) {
            mAdapter.setNewData(bookModels);
//        } else {
//            mAdapter.addData(bookModels);
//        }
        if (mRefresh.isRefreshing()) mRefresh.setRefreshing(false);
    }

    @Override
    public void onLoadCollectionListFail(NetError error) {
        mAdapter.setNewData(null);
        if (error.getType() == NetError.AuthError){
            mAdapter.setHeaderView(getLoginHeaderView());
        }else{
            ToastUtil.showToastShort(error.getMessage());
        }
    }

    @Override
    public void onLoadCollectionListEnd() {
        hideProgress();
        if (mRefresh.isRefreshing()) mRefresh.setRefreshing(false);
    }

    private View getFootView() {
        View view = getLayoutInflater().inflate(R.layout.comic_bookshelf_foot, (ViewGroup) mRecycler.getParent(), false);
        view.findViewById(R.id.comic_bookshelf_foot_refresh).setOnClickListener(this);
        loadMore = view.findViewById(R.id.load_more);
        loadMore.setVisibility(View.GONE);
        loadMore.setOnClickListener(this);
        mFootRecycler = view.findViewById(R.id.comic_bookshelf_foot_recycler);
        mFootRecycler.setFocusableInTouchMode(false);
        mFootRecycler.setFocusable(false);
        mFootRecycler.setLayoutManager(new GridLayoutManager(getContext(), 3));
//        if (mFootRecycler.getItemDecorationCount() <= 0) {
//            mFootRecycler.addItemDecoration(new RecycleViewDivider(getContext(),
//                    LinearLayoutManager.HORIZONTAL, Utils.dip2px(getContext(), 10), Color.WHITE));
//            mFootRecycler.addItemDecoration(new RecycleViewDivider(getContext(), LinearLayoutManager.VERTICAL, Utils.dip2px(getContext(), 15), Color.WHITE));
//        }
        mFootRecycler.addItemDecoration(new MyDecoration(CommonUtil.dip2px(getContext(), 15),
                CommonUtil.dip2px(getContext(), 10), 0, 0));
        mFootAdapter = new BookShelfFooterAdapter(R.layout.comic_bookshelf_footer_item);
        mFootAdapter.bindToRecyclerView(mFootRecycler);
        mFootAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                DetailActivityHelper.toDetail(getActivity(),
                        mFootAdapter.getData().get(position).getId(), "收藏列表_推荐");
            }
        });

        return view;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.comic_bookshelf_foot_refresh) {
            page++;
            getP().loadRecommendData();
        } else if (id == R.id.comic_header_login) {
            ARouter.getInstance().build(RouterMap.COMIC_LOGIN_ACTIVITY)
                    .navigation(getActivity());
        } else if (id == R.id.tv_button) {
            //跳转到分类界面
//            EventBus.getDefault().post(new Refresh(1));
            EventBusManager.sendChangeTabBarEvent(new ChangeTabBarEvent(1));
        } else if (id == R.id.comic_bookshelf_select) {
        } else if (id == R.id.comic_bookshelf_delete) {
        } else if (id == R.id.load_more) {
            currentPage++;
            getP().getCollectionList(currentPage, pageSize);
        }
    }

    private NormalNotifyDialog normalNotifyDialog;

    public void deleteCollection(final List<BookModel> delete) {
        if (normalNotifyDialog == null) normalNotifyDialog = new NormalNotifyDialog();
        normalNotifyDialog.show(getChildFragmentManager(), BaseApplication.getApplication().getString(R.string.base_delete_title), BaseApplication.getApplication().getString(R.string.comic_delete_comic), new DialogUtilForComic.OnDialogClick() {
            @Override
            public void onConfirm() {
                getP().removeShelf(delete);
            }

            @Override
            public void onRefused() {

            }

        });
    }

    @Override
    public void onDeleteComplete() {
        currentPage = 1;
        List<BookModel> data = mAdapter.getData();
        int size = data.size();
        data.clear();
        mAdapter.notifyItemRangeRemoved(mAdapter.getHeaderLayoutCount(), size);
        Fragment fragment = getParentFragment();
        if (fragment instanceof BookShelfFragment)
            ((BookShelfFragment) fragment).setEditMode(false);
        getP().getCollectionList(currentPage, pageSize);
    }

    @Override
    public void onLoadRecommendList(List<BookModel> bookModelList) {
        if (mFootAdapter != null)
            mFootAdapter.setNewData(bookModelList);
        if (LoginHelper.getOnLineUser() == null) {
            if (mAdapter.getHeaderLayoutCount() == 0) mAdapter.addHeaderView(getLoginHeaderView());
        }
        hideProgress();
    }

    private View getLoginHeaderView() {
        if (mLoginHeaderView == null) {
            mLoginHeaderView = getLayoutInflater().inflate(R.layout.comic_login_header, (ViewGroup) mRecycler.getParent(), false);
            mLoginHeaderView.findViewById(R.id.comic_header_login).setOnClickListener(this);
        }
        return mLoginHeaderView;
    }

    private View getDeleteHeaderView() {
        if (mDeleteHeaderView == null) {
            mDeleteHeaderView = getLayoutInflater().inflate(R.layout.comic_delete_header, (ViewGroup) mRecycler.getParent(), false);
            mDeleteHeaderView.findViewById(R.id.comic_bookshelf_select).setOnClickListener(this);
            mDeleteHeaderView.findViewById(R.id.comic_bookshelf_delete).setOnClickListener(this);
            mSelectCheckBox = mDeleteHeaderView.findViewById(R.id.comic_bookshelf_select_check);
            mDeleteCheckBox = mDeleteHeaderView.findViewById(R.id.comic_bookshelf_delete_check);
            mSelectCheckBox.setClickable(false);
            mDeleteCheckBox.setClickable(false);
        }
        return mDeleteHeaderView;
    }

    public void setEditMode(boolean editMode) {
        if (mAdapter == null) return;
//        if (mAdapter.getHeaderLayoutCount() > 0) mAdapter.removeAllHeaderView();
        if (editMode) {
//            mAdapter.addHeaderView(getDeleteHeaderView());
            mDeleteBtns.setVisibility(View.VISIBLE);
        } else {
            mDeleteBtns.setVisibility(View.GONE);
        }
        mRecycler.getLayoutManager().scrollToPosition(0);
    }

    private View getEmptyView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.base_empty_view_c, mRecycler, false);
        ILFactory.getLoader().loadResource((ImageView) view.findViewById(R.id.iv_empty), R.drawable.img_no_collection, null);
        TextView textView = (TextView) view.findViewById(R.id.tv_button);
        textView.setText(getString(com.jj.base.R.string.base_go_collect));
        textView.setOnClickListener(this);
        return view;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void login(LoginEvent event) {
        currentPage = 1;
        if (mLoginHeaderView != null && mAdapter != null)
            mAdapter.removeHeaderView(mLoginHeaderView);
        mAdapter.isUseEmpty(true);
        getP().getCollectionList(currentPage, pageSize);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void logout(LogoutEvent logoutEvent) {
        currentPage = 1;
        if (mAdapter != null) {
            mAdapter.isUseEmpty(false);
            if (mDeleteHeaderView != null) mAdapter.removeHeaderView(mDeleteHeaderView);
            if (mAdapter.getHeaderLayoutCount() == 0) mAdapter.addHeaderView(getLoginHeaderView());
            mRecycler.getLayoutManager().scrollToPosition(0);
            getP().getCollectionList(currentPage, pageSize);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void addOrRemove(RefreshComicCollectionStatusEvent refreshComicCollectionStatusEvent) {
        currentPage = 1;
        getP().getCollectionList(currentPage, pageSize);
    }

    @Override
    public int getLayoutId() {
        return R.layout.comic_fragment_list_with_bottom_delete_btns;
    }


    @Override
    public CollectionPresenter setPresenter() {
        return new CollectionPresenter();
    }

    @Override
    public void onLoadRecommendListFail(NetError error) {
        switch (error.getType()) {
            case NetError.NoConnectError:
                page--;
                break;
            default:
                page = 0;
                break;
        }
    }

    @Override
    public boolean useEventBus() {
        return true;
    }
}
