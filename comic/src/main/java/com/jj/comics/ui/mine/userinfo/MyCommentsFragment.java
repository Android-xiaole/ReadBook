package com.jj.comics.ui.mine.userinfo;

import android.os.Bundle;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.jj.base.ui.BaseVPFragment;
import com.jj.base.utils.SharedPref;
import com.jj.comics.R;
import com.jj.comics.R2;
import com.jj.comics.adapter.mine.MyCommentListAdapter;
import com.jj.comics.common.constants.Constants;
import com.jj.comics.data.model.CommentListResponse;
import com.jj.comics.data.model.ResponseModel;
import com.jj.comics.ui.dialog.DialogUtilForComic;
import com.jj.comics.ui.dialog.NotifyWithCheckBoxDialog;
import com.jj.comics.util.LoginHelper;

import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;

public class MyCommentsFragment extends BaseVPFragment<MyCommentsPresenter> implements MyCommentsContract.IMyCommentsView {

    @BindView(R2.id.common_refresh)
    SwipeRefreshLayout mRefreshLayout;
    @BindView(R2.id.common_recycler)
    RecyclerView mRecyclerView;
    private MyCommentListAdapter mMyCommentListAdapter;
    private int deletePosition;
    private int currentPosition;

    private NotifyWithCheckBoxDialog notifyWithCheckBoxDialog;

    @Override
    public void initData(Bundle savedInstanceState) {
        mRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.base_yellow_ffd850));
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getP().getCommentsList(LoginHelper.getOnLineUser().getUid(), 1, 9999);
            }
        });
        mRefreshLayout.setRefreshing(true);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mMyCommentListAdapter = new MyCommentListAdapter(R.layout.comic_item_userinfo_mycomment);
        mMyCommentListAdapter.setEmptyView(R.layout.comic_layout_empty_mycomments, mRecyclerView);
        mMyCommentListAdapter.isUseEmpty(true);
        mRecyclerView.setAdapter(mMyCommentListAdapter);

        final boolean preNoMoreStatus =
                SharedPref.getInstance(getContext()).getBoolean(Constants.SharedPrefKey.SP_NO_DIALOG_FOR_DELETE_COMMENT
                        , false);
        mMyCommentListAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                currentPosition = position;

                if (!SharedPref.getInstance(getContext()).getBoolean(Constants.SharedPrefKey.SP_NO_DIALOG_FOR_DELETE_COMMENT, false)) {
                    if (notifyWithCheckBoxDialog == null)
                        notifyWithCheckBoxDialog = new NotifyWithCheckBoxDialog();
                    notifyWithCheckBoxDialog.show(getChildFragmentManager(), getString(R.string.comic_delete_remind_title), getString(R.string.comic_delete_remind_msg), new DialogUtilForComic.OnDialogClickWithSelect() {
                        @Override
                        public void onConfirm() {
                            deleteComment();
                        }

                        @Override
                        public void onRefused() {
                            SharedPref.getInstance(getContext()).putBoolean(Constants.SharedPrefKey.SP_NO_DIALOG_FOR_DELETE_COMMENT, preNoMoreStatus);
                        }

                        @Override
                        public void onSelected(boolean noMore) {
                            SharedPref.getInstance(getContext()).putBoolean(Constants.SharedPrefKey.SP_NO_DIALOG_FOR_DELETE_COMMENT, noMore);
                        }
                    });
//                    DialogUtil.showDeleteDialogWithNoMore(getActivity(), getString(R.string.comic_delete_remind_msg)
//                            , new DialogUtil.OnDialogClickWithNoMore() {
//                                @Override
//                                public void onConfirm() {
//                                    deleteComment();
//                                }
//
//                                @Override
//                                public void onRefused() {
//                                    SharedPref.getInstance(getContext()).putBoolean(Constants.SharedPrefKey.SP_NO_DIALOG_FOR_DELETE_COMMENT, preNoMoreStatus);
//                                }
//
//                                @Override
//                                public void onNoMore(boolean noMore) {
//                                    SharedPref.getInstance(getContext()).putBoolean(Constants.SharedPrefKey.SP_NO_DIALOG_FOR_DELETE_COMMENT, noMore);
//                                }
//                            });
                } else {
                    deleteComment();
                }

            }
        });

        getP().getCommentsList(LoginHelper.getOnLineUser().getUid(), 1, 9999);
    }

    private void deleteComment() {
//        deletePosition = currentPosition;
//        Object item = mMyCommentListAdapter.getItem(currentPosition);
//        if (item instanceof CommentListResponse.CommentInfosBean) {
//            CommentListResponse.CommentInfosBean bean = (CommentListResponse.CommentInfosBean) item;
//            getP().deleteCommentById(LoginHelper.getOnLineUser().getUserId(), bean.getId());
//        }
    }

    @Override
    public void onNewData(List<CommentListResponse.DataBeanX.DataBean> commentInfos) {
        mRefreshLayout.setRefreshing(false);
        mMyCommentListAdapter.setNewData(commentInfos);
    }

    @Override
    public void onCommentDelete(ResponseModel resp) {
        showToastShort(getString(R.string.comic_delete_success_remind));
        if (resp.getCode() == 1) {
            mMyCommentListAdapter.remove(deletePosition);
//            mMyCommentListAdapter.notifyItemRemoved(deletePosition);
        }
    }

    @Override
    public void onDataErr(String err) {
        mRefreshLayout.setRefreshing(false);
    }

    @Override
    public int getLayoutId() {
        return R.layout.comic_fragment_simple;
    }

    @Override
    public MyCommentsPresenter setPresenter() {
        return new MyCommentsPresenter();
    }

}
