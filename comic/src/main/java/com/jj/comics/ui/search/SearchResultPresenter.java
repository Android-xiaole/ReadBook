package com.jj.comics.ui.search;

import com.jj.base.mvp.BasePresenter;
import com.jj.base.mvp.BaseRepository;
import com.jj.base.net.ApiSubscriber2;
import com.jj.base.net.NetError;
import com.jj.comics.data.biz.content.ContentRepository;
import com.jj.comics.data.model.BookListResponse;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class SearchResultPresenter extends BasePresenter<BaseRepository, SearchResultContract.ISearchResultView> implements SearchResultContract.ISearchResultPresenter {

    @Override
    public void getSearchComicListByKeywords(String keyWord) {
        ContentRepository.getInstance().getSearchComicListByKeywords(keyWord,getV().getClass().getSimpleName())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .as(this.<BookListResponse>bindLifecycle())
                .subscribe(new ApiSubscriber2<BookListResponse>() {
                    @Override
                    public void onNext(BookListResponse bookListResponse) {
                        getV().fillSearchComicListByKeywords(bookListResponse.getData());
                        getV().onComplete();
                    }

                    @Override
                    protected void onFail(NetError error) {
                        getV().showToastShort(error.getMessage());
                        getV().fillSearchComicListByKeywords(null);
                        getV().onComplete();
                    }
                });
    }

    @Override
    public void getWatchingComicData() {
        ContentRepository.getInstance().getWatchingComicData(getV().getClass().getSimpleName())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .as(this.<BookListResponse>bindLifecycle())
                .subscribe(new ApiSubscriber2<BookListResponse>() {
                    @Override
                    public void onNext(BookListResponse response) {
                        getV().fillWatchingComicData(response.getData());
                        getV().onComplete();
                    }

                    @Override
                    protected void onFail(NetError error) {
                        getV().showToastShort(error.getMessage());
                        getV().fillWatchingComicData(null);
                        getV().onComplete();
                    }
                });

    }
}
