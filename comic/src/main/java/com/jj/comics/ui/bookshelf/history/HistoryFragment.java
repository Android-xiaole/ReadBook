package com.jj.comics.ui.bookshelf.history;

import android.os.Bundle;
import android.util.Log;
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
import com.jj.comics.R;
import com.jj.comics.R2;
import com.jj.comics.adapter.bookshelf.BookShelfAdapter;
import com.jj.comics.adapter.bookshelf.BookShelfFooterAdapter;
import com.jj.comics.common.callback.GlideOnScrollListener;
import com.jj.comics.common.constants.RequestCode;
import com.jj.comics.data.model.BookModel;
import com.jj.comics.ui.bookshelf.BookShelfFragment;
import com.jj.comics.ui.detail.DetailActivityHelper;
import com.jj.comics.ui.dialog.DialogUtilForComic;
import com.jj.comics.ui.dialog.NormalNotifyDialog;
import com.jj.comics.util.LoginHelper;
import com.jj.comics.util.eventbus.EventBusManager;
import com.jj.comics.util.eventbus.events.ChangeTabBarEvent;
import com.jj.comics.util.eventbus.events.LoginEvent;
import com.jj.comics.util.eventbus.events.LogoutEvent;
import com.jj.comics.util.eventbus.events.UpdateReadHistoryEvent;
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

public class HistoryFragment extends BaseVPFragment<HistoryPresenter> implements HistoryContract.IHistoryView, View.OnClickListener {
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
    private int pageSize = 10086;
    private View mLoginHeaderView;
    private View mDeleteHeaderView;


    @Override
    public void initData(Bundle savedInstanceState) {
        mAdapter = new BookShelfAdapter(R.layout.comic_bookshelf_item);
        mRefresh.setColorSchemeColors(getResources().getColor(R.color.base_yellow_ffd850));
        mRefresh.setRefreshing(true);
        mRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                currentPage = 1;
                getP().getHistoryList();
            }
        });

        mSelectCheckBoxContainer.setOnClickListener(this);
        mDeleteCheckBoxContainer.setOnClickListener(this);

        mRecycler.setFocusableInTouchMode(false);
        mRecycler.setFocusable(false);
        mRecycler.setLayoutManager(new ComicLinearLayoutManager(getContext()));
        mRecycler.addOnScrollListener(new GlideOnScrollListener());


        mAdapter.closeLoadAnimation();
        mAdapter.bindToRecyclerView(mRecycler);
        mAdapter.setEmptyView(getEmptyView());
        mAdapter.isUseEmpty(LoginHelper.getOnLineUser() != null);
        mAdapter.setHeaderFooterEmpty(true, true);
        mAdapter.addFooterView(getFootView());
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (mAdapter.isEditMode()) {
                    mAdapter.autoSelect(position);
                    mSelectCheckBox.setChecked(mAdapter.isSelectAll());
                    mDeleteCheckBox.setChecked(!mAdapter.getDelete().isEmpty());
                } else {
                    DetailActivityHelper.toDetail(getActivity(),
                            mAdapter.getData().get(position).getId(),
                            "历史列表");
                }
            }
        });

        mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                getP().toRead(mAdapter.getData().get(position), mAdapter.getData().get(position).getChapterid());
            }
        });
        getP().getHistoryList();
    }

    @Override
    public void onLoadHistoryList(List<BookModel> data) {
        mAdapter.setNewData(data);
        if (mRefresh.isRefreshing()) mRefresh.setRefreshing(false);
        if (mAdapter.getFooterLayoutCount() < 1) mAdapter.addFooterView(getFootView());
    }

    @Override
    public void onLoadHistoryListFail(NetError error) {
        mAdapter.setNewData(null);
        if (mRefresh.isRefreshing()) mRefresh.setRefreshing(false);
        if (mAdapter.getFooterLayoutCount() < 1) mAdapter.addFooterView(getFootView());
        hideProgress();
    }

    /**
     * 登录成功的通知
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void loginEvent(LoginEvent loginEvent) {
        currentPage = 1;
        if (mLoginHeaderView != null && mAdapter != null)
            mAdapter.removeHeaderView(mLoginHeaderView);
        mAdapter.isUseEmpty(true);
        getP().getHistoryList();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void logoutEvent(LogoutEvent logoutEvent) {
        Log.i("History", "eventbus");
        currentPage = 1;
        if (mAdapter != null) {
            mAdapter.setEditMode(false);
            mAdapter.isUseEmpty(false);
            mAdapter.getDelete().clear();
            if (mDeleteHeaderView != null) mAdapter.removeHeaderView(mDeleteHeaderView);
            if (mAdapter.getHeaderLayoutCount() == 0) mAdapter.addHeaderView(getLoginHeaderView());
            mRecycler.getLayoutManager().scrollToPosition(0);
            getP().getHistoryList();
        }
    }

    /**
     * 来自阅读页面产生了历史记录刷新当前列表
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateReadHistory(UpdateReadHistoryEvent event) {
        getP().getHistoryList();
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
        mFootAdapter = new BookShelfFooterAdapter(R.layout.comic_bookshelf_footer_item);
//        if (mFootRecycler.getItemDecorationCount() <= 0) {
//            mFootRecycler.addItemDecoration(new RecycleViewDivider(getContext(),
//                    LinearLayoutManager.HORIZONTAL, Utils.dip2px(getContext(), 10), Color.WHITE));
//            mFootRecycler.addItemDecoration(new RecycleViewDivider(getContext(), LinearLayoutManager.VERTICAL, Utils.dip2px(getContext(), 15), Color.WHITE));
//        }
        mFootRecycler.addItemDecoration(new MyDecoration(CommonUtil.dip2px(getContext(), 15),
                CommonUtil.dip2px(getContext(), 10), 0, 0));
        mFootAdapter.bindToRecyclerView(mFootRecycler);
        mFootAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                DetailActivityHelper.toDetail(getActivity(),
                        mFootAdapter.getData().get(position).getId(), "历史列表_推荐");
            }
        });

        getP().loadRecommendData();
        return view;
    }

    @Override
    public void onLoadRecommendList(List<BookModel> bookModelList) {
        if (mFootAdapter != null) {
            int booklistSize = bookModelList.size();
            int oldSize = mFootAdapter.getData().size();
            if (booklistSize == oldSize) {
                if (bookModelList.get(booklistSize - 1).equals(mFootAdapter.getData().get(oldSize - 1))) {
                    showToastShort("没有更多数据");
                }
            }
            mFootAdapter.setNewData(bookModelList);
        }

        if (LoginHelper.getOnLineUser() == null) {
            if (mAdapter.getHeaderLayoutCount() == 0) mAdapter.addHeaderView(getLoginHeaderView());
        }
        hideProgress();
    }

    @Override
    public int getLayoutId() {
//        return R.layout.comic_recycler;
        return R.layout.comic_fragment_list_with_bottom_delete_btns;
    }

    @Override
    public HistoryPresenter setPresenter() {
        return new HistoryPresenter();
    }

    @Override
    public void onDeleteComplete() {
        currentPage = 1;
        mAdapter.getDelete().clear();
//        List<ComicModel.SectionWithContentListBean.ContentListBean> data = mAdapter.getData();
//        int size = data.size();
//        data.clear();
//        mAdapter.notifyItemRangeRemoved(mAdapter.getHeaderLayoutCount(), size);
        mSelectCheckBox.setChecked(mAdapter.isSelectAll());
        mDeleteCheckBox.setChecked(!mAdapter.getDelete().isEmpty());
        Fragment fragment = getParentFragment();
        if (fragment instanceof BookShelfFragment)
            ((BookShelfFragment) fragment).setEditMode(false);
        getP().getHistoryList();
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
        mAdapter.setEditMode(editMode);
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
        ILFactory.getLoader().loadResource((ImageView) view.findViewById(R.id.iv_empty), R.drawable.img_no_history, null);
        TextView textView = (TextView) view.findViewById(R.id.tv_button);
        textView.setText(R.string.comic_go_read);
        textView.setOnClickListener(this);
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
                    .navigation(getActivity(), RequestCode.LOGIN_REQUEST_CODE);
        } else if (id == R.id.tv_button) {
            //跳转到分类界面
//            EventBus.getDefault().post(new Refresh(1));
            EventBusManager.sendChangeTabBarEvent(new ChangeTabBarEvent(1));
        } else if (id == R.id.comic_bookshelf_select) {
            mSelectCheckBox.setChecked(!mSelectCheckBox.isChecked());
            mAdapter.autoSelectAllOrNot();
            mDeleteCheckBox.setChecked(!mAdapter.getDelete().isEmpty());
        } else if (id == R.id.comic_bookshelf_delete) {
            if (!mAdapter.getDelete().isEmpty())
//                getP().deleteHistory(mAdapter.getDelete());
                deleteHistory(mAdapter.getDelete());
        } else if (id == R.id.load_more) {
            currentPage++;
            getP().getHistoryList();
        }
    }

    private NormalNotifyDialog normalNotifyDialog;

    public void deleteHistory(final List<BookModel> delete) {
        if (normalNotifyDialog == null) normalNotifyDialog = new NormalNotifyDialog();
        normalNotifyDialog.show(getChildFragmentManager(), BaseApplication.getApplication().getString(R.string.base_delete_title), BaseApplication.getApplication().getString(R.string.comic_delete_comic_from_history), new DialogUtilForComic.OnDialogClick() {
            @Override
            public void onConfirm() {
                getP().deleteHistory(delete);
            }

            @Override
            public void onRefused() {

            }

        });
    }


    @Override
    public void getFooterDataFail(NetError error) {
        if (LoginHelper.getOnLineUser() == null) {
            if (mAdapter.getHeaderLayoutCount() == 0) mAdapter.addHeaderView(getLoginHeaderView());
        }
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
