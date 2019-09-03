package com.jj.comics.ui.recommend;

import com.jj.base.mvp.BasePresenter;
import com.jj.base.mvp.BaseRepository;
import com.jj.base.net.ApiSubscriber2;
import com.jj.base.net.NetError;
import com.jj.comics.data.biz.content.ContentRepository;
import com.jj.comics.data.model.BookListDataResponse;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class RecommendLoadMorePresenter extends BasePresenter<BaseRepository,RecommendLoadMoreContract.View> implements RecommendLoadMoreContract.Presenter{

    @Override
    public void getSectionDataListBySectionId(int currentPage, int sectionId, boolean evict) {
        ContentRepository.getInstance().getSectionListBySectionId(currentPage, sectionId, evict, getV().getClass().getName())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .as(this.<BookListDataResponse>bindLifecycle())
                .subscribe(new ApiSubscriber2<BookListDataResponse>() {
                    @Override
                    public void onNext(BookListDataResponse sectionWithContentListBean) {
                        BookListDataResponse.DataBean data = sectionWithContentListBean.getData();
                        if (data != null)
                            getV().fillData(data.getData());
                    }

                    @Override
                    protected void onFail(NetError error) {
                        getV().getDataFail(error);
                    }
                });
    }
}
