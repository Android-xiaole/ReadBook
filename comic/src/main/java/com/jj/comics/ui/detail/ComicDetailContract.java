package com.jj.comics.ui.detail;

import android.app.Activity;

import com.jj.base.mvp.IView;
import com.jj.base.net.ApiSubscriber2;
import com.jj.comics.data.model.BookModel;
import com.jj.comics.data.model.CommonStatusResponse;
import com.jj.comics.util.eventbus.events.RefreshComicCollectionStatusEvent;

public interface ComicDetailContract {
    interface IDetailView extends IView {
        //加载漫画详情的回调
        void onLoadComicDetail(BookModel model, boolean umengUpload);

        //处理Tag标签显示的逻辑
        void fillTagView(int i, String tag);

        void dealCollection(RefreshComicCollectionStatusEvent refreshComicCollectionStatusEvent);

        //收藏操作的回调
        void dealCollection(boolean collectByCurrUser);

        void onFavorContentSuccess();

        void fillCollectStatus(CommonStatusResponse response);

        void fillFavorStatus(CommonStatusResponse response);
    }

    interface IDetailPresenter {
        //获取改本漫画阅读历史记录
        void getCatalogHistory(long contentId,int chapterid, ApiSubscriber2 apiSubscriber);

        //收藏或者取消收藏的操作
        void addOrRemoveCollect(BookModel model,boolean collectByCurrUser);

        //点赞内容
        void favorContent(long id);

        //获取漫画详情
        void getComicDetail(long id, final boolean umengUpload);

        //保存章节
        void saveCatalog(BookModel model);

        //跳转到阅读页面
        void toRead(BookModel bookModel,long chapterid);

        //友盟埋点
        void umengOnEvent(Activity activity,BookModel model);

        //获取收藏状态
        void getCollectStatus(long id);

        //获取点赞状态
        void getFavorStatus(long id);
    }
}
