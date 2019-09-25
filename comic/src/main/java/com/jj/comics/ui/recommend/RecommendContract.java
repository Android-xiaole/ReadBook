package com.jj.comics.ui.recommend;

import com.jj.base.mvp.IView;
import com.jj.base.net.NetError;
import com.jj.comics.data.model.BannerResponse;
import com.jj.comics.data.model.BookListRecommondResponse;
import com.jj.comics.data.model.BookModel;
import com.jj.comics.data.model.PayActionResponse;
import com.jj.comics.data.model.Push;

import java.util.List;

public interface RecommendContract {

    interface IRecommendView extends IView {

        //填充数据
        void fillData(boolean changeChannel, List<BookListRecommondResponse.DataBean> data);

        //加载数据失败
        void getDataFail(NetError error);

        void onLoadRecentlyComicSuccess(boolean changeChannel, List<BookModel> bookModelList);

        void onLoadRecentlyComicFail(NetError netError);


        void sendMessage(int what, Object info);

        void refreshBanner(BannerResponse bannerResponse);

        void getBannerFail();


        void onLoadPopShareSucc(boolean changeChannel, List<BookModel> bookModelList);

        void onLoadPopShareFail(NetError netError);
    }

    interface IRecommendPresenter {
        //记载推荐数据
        void loadData(int channelFlag, int pageNum, boolean evict, boolean changeChannel);

        //获取最近更新数据
        void loadRecentlyComic(int pageNum, int channelFlag, boolean changeChannel);

        void loadPopShare(int channelFlag, boolean changeChannel);


        //获取banner
        void getBanner();

    }
}
