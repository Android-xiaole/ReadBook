package com.jj.comics.ui.bookshelf.collection;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.jj.base.BaseApplication;
import com.jj.base.net.NetError;
import com.jj.base.ui.BaseVPFragment;
import com.jj.base.utils.RouterMap;
import com.jj.base.utils.toast.ToastUtil;
import com.jj.comics.R;
import com.jj.comics.R2;
import com.jj.comics.adapter.bookshelf.CommonBookListAdapter;
import com.jj.comics.common.callback.GlideOnScrollListener;
import com.jj.comics.data.model.BookModel;
import com.jj.comics.ui.dialog.DialogUtilForComic;
import com.jj.comics.ui.dialog.NormalNotifyDialog;
import com.jj.comics.util.LoginHelper;
import com.jj.comics.util.eventbus.events.LoginEvent;
import com.jj.comics.util.eventbus.events.LogoutEvent;
import com.jj.comics.util.eventbus.events.RefreshComicCollectionStatusEvent;
import com.jj.comics.widget.comic.ComicLinearLayoutManager;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import butterknife.OnClick;
import me.jessyan.autosize.utils.ScreenUtils;

/**
 * 收藏、书架页面
 */
@Route(path = RouterMap.COMIC_BOOKCOLLECTION_FRAGMENT)
public class CollectionFragment extends BaseVPFragment<CollectionPresenter> implements CollectionContract.ICollectionView, View.OnClickListener {
    @BindView(R2.id.rootView)
    LinearLayout rootView;//根布局
    @BindView(R2.id.sr_with_delete)
    SwipeRefreshLayout mRefresh;
    @BindView(R2.id.rv_with_delete)
    RecyclerView mRecycler;

    private CommonBookListAdapter mAdapter;
    private int currentPage = 1;
    private NormalNotifyDialog deleteDialog;//删除收藏的弹窗

    @Override
    public void initData(Bundle savedInstanceState) {
        //设置toolbar距离上端的高度
        int statusBarHeight = ScreenUtils.getStatusBarHeight();
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) rootView.getLayoutParams();
        lp.topMargin = statusBarHeight;
        rootView.setLayoutParams(lp);

        mAdapter = new CommonBookListAdapter(R.layout.comic_bookshelf_item,null);

        mRefresh.setColorSchemeColors(getResources().getColor(R.color.base_yellow_ffd850));
        mRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (LoginHelper.getOnLineUser()==null){
                    mRefresh.setRefreshing(false);
                    return;
                }
                currentPage = 1;
                getP().getCollectionList(currentPage);
            }
        });

        mRecycler.setLayoutManager(new ComicLinearLayoutManager(getContext()));
        mRecycler.addOnScrollListener(new GlideOnScrollListener());
        mRecycler.setFocusableInTouchMode(false);
        mRecycler.setFocusable(false);
        mAdapter.closeLoadAnimation();
        mAdapter.bindToRecyclerView(mRecycler);
        mAdapter.setEmptyView(getEmptyView());
        mAdapter.isUseEmpty(true);
        mAdapter.setHeaderFooterEmpty(true, true);

        mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                currentPage++;
                getP().getCollectionList(currentPage);
            }
        },mRecycler);

        mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if (view.getId() == R.id.right){//删除收藏
                    List<BookModel> list = new ArrayList<>();
                    list.add(mAdapter.getData().get(position));
                    showCollectionDialog(list,position);
                }else if (view.getId() == R.id.content){//去阅读
                    getP().toRead(mAdapter.getData().get(position), mAdapter.getData().get(position).getChapterid());
                }
            }
        });

        if (LoginHelper.getOnLineUser()!=null){
            mRefresh.setRefreshing(true);
            getP().getCollectionList(currentPage);
        }
    }

    @Override
    public void onLoadCollectionList(List<BookModel> bookModels) {
        if (currentPage == 1){
            mAdapter.setNewData(bookModels);
        }else {
            mAdapter.addData(bookModels);
        }
        if (bookModels.size()!=0){
            mAdapter.loadMoreComplete();
        }else{
            mAdapter.loadMoreEnd(true);
        }
    }

    @Override
    public void onLoadCollectionListFail(NetError error) {
        if (currentPage ==1){//加载第一页就失败就显示空布局的加载失败
            mAdapter.setNewData(null);
        }else{
            currentPage --;
        }
        ToastUtil.showToastShort(error.getMessage());
    }

    @Override
    public void onLoadCollectionListEnd() {
        hideProgress();
        if (mRefresh.isRefreshing()) mRefresh.setRefreshing(false);
    }

    @OnClick({R2.id.search_edit})
    public void onClick(View view) {
        if (view.getId() == R.id.search_edit) {
            ARouter.getInstance().build(RouterMap.COMIC_SEARCH_ACTIVITY).navigation(getActivity());
        }
    }

    /**
     * 展示删除提示弹窗
     * @param delete
     */
    public void showCollectionDialog(List<BookModel> delete,int position) {
        if (deleteDialog == null) deleteDialog = new NormalNotifyDialog();
        deleteDialog.show(getChildFragmentManager(), BaseApplication.getApplication().getString(R.string.base_delete_title), BaseApplication.getApplication().getString(R.string.comic_delete_comic), new DialogUtilForComic.OnDialogClick() {
            @Override
            public void onConfirm() {
                getP().removeShelf(delete,position);
            }

            @Override
            public void onRefused() {

            }

        });
    }

    @Override
    public void onDeleteComplete(int position) {
        mAdapter.remove(position);
    }


    private View getEmptyView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.comic_empty_collection, mRecycler, false);
        return view;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void login(LoginEvent event) {
        currentPage = 1;
        getP().getCollectionList(currentPage);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void logout(LogoutEvent logoutEvent) {
        currentPage = 1;
        if (mAdapter != null) {
            mAdapter.setNewData(null);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void addOrRemove(RefreshComicCollectionStatusEvent refreshComicCollectionStatusEvent) {
        currentPage = 1;
        getP().getCollectionList(currentPage);
    }

    @Override
    public int getLayoutId() {
        return R.layout.comic_fragment_collection;
    }


    @Override
    public CollectionPresenter setPresenter() {
        return new CollectionPresenter();
    }

    @Override
    public boolean useEventBus() {
        return true;
    }
}
