package com.jj.comics.ui.mine.userinfo;

import com.jj.base.mvp.IView;
import com.jj.comics.data.model.CommentListResponse;
import com.jj.comics.data.model.ResponseModel;

import java.util.List;

public interface MyCommentsContract {

    interface IMyCommentsView extends IView {
        //获取我发表过的评论列表的回调
        void onNewData(List<CommentListResponse.DataBeanX.DataBean> commentInfosBeans);

        //获取我发表过的评论列表失败的回调
        void onDataErr(String errorMsg);

        //删除评论的回调
        void onCommentDelete(ResponseModel resp);
    }

    interface IMyCommentsPresenter {
        //获取我发表过的评论列表
        void getCommentsList(long userId, int pageNum, int pageSize);

        //删除评论
        void deleteCommentById(long userId, int commentId);
    }
}
