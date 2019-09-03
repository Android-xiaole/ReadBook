package com.jj.comics.ui.recommend;

import com.jj.base.mvp.BasePresenter;
import com.jj.base.mvp.BaseRepository;
import com.jj.base.net.ApiSubscriber2;
import com.jj.base.net.NetError;
import com.jj.comics.data.biz.content.ContentRepository;
import com.jj.comics.data.model.BookListDataResponse;
import com.jj.comics.data.model.BookModel;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class FreeListPresenter extends BasePresenter<BaseRepository,FreeListContract.IFreeListView> implements FreeListContract.IFreeListPresenter{
    public static final int PAGE_SIZE = 20;

    @Override
    public void loadData(int currentPage, boolean evict) {
        ContentRepository.getInstance().getFree(currentPage,PAGE_SIZE,getV().getClass().getName())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .as(this.<BookListDataResponse>bindLifecycle())
                .subscribe(new ApiSubscriber2<BookListDataResponse>() {
                    @Override
                    public void onNext(BookListDataResponse comicModel) {
                        BookListDataResponse.DataBean data = comicModel.getData();
                        if (data != null) {
                            List<BookModel> bookModels = data.getData();
                            if (bookModels != null) {
                                getV().fillData(bookModels);
                            }else {
                                getV().getDataFail(new NetError("DATA LIST EMPTY",
                                        NetError.NoDataError));
                            }
                        }else {
                            getV().getDataFail(new NetError("DATA LIST EMPTY",
                                    NetError.NoDataError));
                        }

                    }

                    @Override
                    protected void onFail(NetError error) {
                        getV().getDataFail(error);
                    }
                });
    }

    @Override
    public boolean useEventBus() {
        return false;
    }
}
