package com.jj.comics.ui.detail.comment;

import com.jj.base.mvp.IView;
import com.jj.comics.data.model.CommentListResponse;
import com.jj.comics.data.model.CommonStatusResponse;

import java.util.List;

public interface CommentListContract {

    interface ICommentListView extends IView {

        //获取评论列表的回调
        void onGetCommentList(List<CommentListResponse.DataBeanX.DataBean> commentInfos);

        //点赞成功的回调
        void onFavorCommentSuccess(String type);

        //评论成功的回调
        void onCommentSuccess(CommonStatusResponse result);
    }

    interface ICommentListPresenter {
        //获取评论列表
        void getCommentData(int pageNum, int pageSize, long objectId);

        //点赞评论
        void favorComment(long id, String type);

        //发表评论
        void sendComment(long bookId, String commentDetail);
    }
}
