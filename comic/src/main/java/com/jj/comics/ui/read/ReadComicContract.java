package com.jj.comics.ui.read;

import com.jj.base.mvp.IView;
import com.jj.comics.data.model.BookCatalogModel;
import com.jj.comics.data.model.BookModel;
import com.jj.comics.data.model.CommonStatusResponse;

import java.util.List;

public interface ReadComicContract {

    interface IReadComicView extends IView {

        //加载章节内容结束的回调
        void onLoadCatalogContentEnd();

        //文件下载成功，加载资源的回调
        void onLoadChapterContent();

        //获取目录列表的回调
        void onGetCatalogList(List<BookCatalogModel> catalogModels,int totalNum);

        //收藏成功或者取消收藏成功的回调
        void onAddOrRemoveShelfSuccess(boolean collectByCurrUser, boolean needFinish);

        void fillCollectStatus(CommonStatusResponse response);

        void shareImage(String content);
    }

    interface IReadComicPresenter {

        //添加或取消收藏
        void addOrRemoveShelf(BookModel model, boolean collectByCurrUser, boolean needFinish);

        //获取章节目录列表
        void getCatalogList(BookModel bookModel);

        //获取收藏状态
        void getCollectStatus(long id);

        //上传阅读历史记录
        void uploadReadRecord(BookModel bookModel, long chapterid,int chapterorder);

    }
}
