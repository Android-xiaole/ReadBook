package com.jj.comics.ui.detail;

import android.app.Activity;

import com.jj.base.mvp.IView;
import com.jj.base.net.ApiSubscriber2;
import com.jj.comics.data.model.BookListDataResponse;
import com.jj.comics.data.model.BookModel;
import com.jj.comics.data.model.CommonStatusResponse;
import com.jj.comics.util.eventbus.events.RefreshComicCollectionStatusEvent;

public interface ComicDetailContract {
    interface IDetailView extends IView {
        //加载漫画详情的回调
        void onLoadComicDetail(BookModel model);

        void onCollectionSuccess(boolean collectByCurrUser);

        void fillCollectStatus(boolean collectByCurrUser);

        void onLoadRecommendList(BookListDataResponse response);
    }

    interface IDetailPresenter {
        //获取改本漫画阅读历史记录
        void getCatalogHistory(long contentId,int chapterid, ApiSubscriber2 apiSubscriber);

        //收藏或者取消收藏的操作
        void addOrRemoveCollect(BookModel model,boolean collectByCurrUser);


        //获取漫画详情
        void getComicDetail(long id);


        //跳转到阅读页面
        void toRead(BookModel bookModel,long chapterid);

        //获取收藏状态
        void getCollectStatus(long id);

        //获取相关推荐列表
        void loadCommendList(long id, int pageNum, int sectionId);

    }
}
