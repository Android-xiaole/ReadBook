package com.jj.comics.ui.read;

import com.jj.base.mvp.IView;
import com.jj.comics.data.model.BookCatalogModel;
import com.jj.comics.data.model.BookModel;
import com.jj.comics.data.model.CommonStatusResponse;
import com.jj.comics.data.model.ResponseModel;

import java.util.List;

public interface ReadComicContract {

    interface IReadComicView extends IView {
        //加载内容资源的回调
        void fillData(BookCatalogModel model);

        //文件下载成功，加载资源的回调
        void onLoadChapterContent();

        void onLoadDataEnd();

        //获取目录列表的回调
        void onGetCatalogList(List<BookCatalogModel> catalogModels);

        //获取目录列表失败
        void onGetCatalogListFail();

        void finish();

        //收藏成功或者取消收藏成功的回调
        void onAddOrRemoveShelfSuccess(boolean collectByCurrUser, boolean needFinish);

        void fillCollectStatus(CommonStatusResponse response);

        void fillFavorStatus(CommonStatusResponse response);

        //内容点赞成功的回调
        void onFavorContentSuccess();

        //发表评论成功的回调
        void onCommentSuccess(ResponseModel result);
    }

    interface IReadComicPresenter {
        //加载章节图片内容
//        void loadData(BookModel bookModel, long chapterid);

        //添加或取消收藏
        void addOrRemoveShelf(BookModel model, boolean collectByCurrUser, boolean needFinish);

        //获取章节目录列表
        void getCatalogList(long bookId, int pageNum);

        //获取收藏状态
        void getCollectStatus(long id);

        //获取点赞状态
        void getFavorStatus(long id);

        //上传阅读历史记录
        void uploadReadRecord(BookModel bookModel, long chapterid,int chapterorder);

        //点赞或取消点赞
        void favorContent(long bookId);

        //发表评论
        void sendComment(long objectId, String commentDetail);

    }
}
