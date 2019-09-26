package com.jj.comics.ui.detail;

import com.jj.base.mvp.IView;
import com.jj.comics.data.model.BookCatalogModel;
import com.jj.comics.data.model.BookListDataResponse;
import com.jj.comics.data.model.BookModel;

import java.util.List;

public interface ComicDetailContract {
    interface IDetailView extends IView {
        //加载漫画详情的回调
        void onLoadComicDetail(BookModel model);

        void onCollectionSuccess(boolean collectByCurrUser);

        void fillCollectStatus(boolean collectByCurrUser);

        void onLoadRecommendList(BookListDataResponse response);

        //获取目录列表的回调
        void onGetCatalogList(List<BookCatalogModel> catalogModels, int totalNum);
    }

    interface IDetailPresenter {

        //收藏或者取消收藏的操作
        void addOrRemoveCollect(BookModel model, boolean collectByCurrUser);


        //获取漫画详情
        void getComicDetail(long id);


        //跳转到阅读页面
        void toRead(BookModel bookModel, long chapterid);

        //获取收藏状态
        void getCollectStatus(long id);

        //获取相关推荐列表
        void loadCommendList(long id, int pageNum, int sectionId);

    }
}
