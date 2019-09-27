package com.jj.comics.ui.detail.subdetail;

import com.jj.base.mvp.IView;
import com.jj.base.net.NetError;
import com.jj.comics.data.model.BookListDataResponse;
import com.jj.comics.data.model.BookModel;
import com.jj.comics.data.model.CommentListResponse;
import com.jj.comics.data.model.CommonStatusResponse;

public interface SubComicDetailContract {
    interface ISubComicDetailView extends IView {
        //加载猜你喜欢列表的回调
        void onLoadLikeComicList(BookListDataResponse response);

        //加载猜你喜欢列表失败的回调
        void onLoadLikeComicListFail(NetError netError);

        //获取评论列表的回调
        void onGetCommentData(CommentListResponse commentListResponse);


        //评论点赞的回调
        void onFavorCommentSuccess(String type);

        //发表评论成功的回调
        void onCommentSuccess(CommonStatusResponse response);

        //获取漫画详情返回值
        void onLoadComicDetail(BookModel model);
    }

    interface ISubComicDetailPresenter {
        //获取猜你喜欢列表
        void loadLikeComicList(long id, int pageNum, int sectionId);

        //获取评论列表
        void getCommentData(long id);

        //获取打赏列表
        void getRewardRecordList(long id);

        //点赞评论
        void favorComment(long userId, String type);

        //发表评论
        void sendComment(long objectId, String commentDetail);

        //获取漫画详情
        void getComicDetail(long id);
    }
}
