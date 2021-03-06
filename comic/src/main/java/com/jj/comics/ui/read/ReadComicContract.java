package com.jj.comics.ui.read;

import com.jj.base.mvp.IView;
import com.jj.base.net.NetError;
import com.jj.comics.data.model.BookCatalogModel;
import com.jj.comics.data.model.BookModel;
import com.jj.comics.data.model.CommonStatusResponse;

import java.util.List;

public interface ReadComicContract {

    interface IReadComicView extends IView {

        //获取章节内容没有付费的回调
        void onLoadChapterContentNoPayError(BookCatalogModel model);

        //加载失败的回调
        void onLoadCatalogContentError(NetError error);

        //加载章节内容结束的回调
        void onLoadCatalogContentEnd();

        //文件下载成功，加载资源的回调
        void onLoadChapterContent(long chapterId);

        //获取目录列表的回调
        void onGetCatalogList(List<BookCatalogModel> catalogModels,int totalNum,int pageNum,boolean isNextPage);

        //获取目录列表失败的回调
        void onGetCatalogListFail(NetError error,int pageNum,boolean isNextPage);

        //收藏成功或者取消收藏成功的回调
        void onAddOrRemoveShelfSuccess(boolean collectByCurrUser, boolean needFinish);

        void fillCollectStatus(CommonStatusResponse response);
    }

    interface IReadComicPresenter {

        //添加或取消收藏
        void addOrRemoveShelf(BookModel model, boolean collectByCurrUser, boolean needFinish);

        //获取章节目录列表
        void getCatalogList(BookModel bookModel,int pageNum,boolean isScroll);

        //获取收藏状态
        void getCollectStatus(long id);

        //上传阅读历史记录
        void uploadReadRecord(BookModel bookModel, long chapterid,int chapterorder,String chaptername);

    }
}
