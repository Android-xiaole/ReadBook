package com.jj.comics.ui.detail.subdetail;

import com.jj.base.mvp.BasePresenter;
import com.jj.base.mvp.BaseRepository;
import com.jj.base.net.NetError;
import com.jj.comics.common.net.ComicSubscriber;
import com.jj.comics.data.biz.content.ContentRepository;
import com.jj.comics.data.model.BookCatalogListResponse;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class CatalogPresenter extends BasePresenter<BaseRepository,CatalogContract.ICatalogView> implements CatalogContract.ICatalogPresenter{

    @Override
    public void getCatalogList(long id, int pageNum,String sort) {
        getV().showProgress();
        ContentRepository.getInstance().getCatalogList(id,pageNum,sort)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .as(this.<BookCatalogListResponse>bindLifecycle())
                .subscribe(new ComicSubscriber<BookCatalogListResponse>() {
                    @Override
                    public void onNext(BookCatalogListResponse response) {
                        getV().onLoadCatalogList(response.getData().getData());
                    }

                    @Override
                    protected void onFail(NetError error) {
                        getV().onLoadCatalogListFail(error);
                    }

                    @Override
                    protected void onEnd() {
                        getV().hideProgress();
                    }
                });

    }

}
