package com.jj.comics.ui.list.rank;

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

public class CommonRankPresenter extends BasePresenter<BaseRepository, CommonRankContract.ICommonRankView> implements CommonRankContract.ICommonRankPresenter {

    @Override
    public void getRankListByAction(int currentPage, String action, boolean evict) {
        ContentRepository.getInstance()
                .getRankListByAction(action,currentPage,10,getV().getClass().getName())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .as(this.<BookListDataResponse>bindLifecycle())
                .subscribe(new ApiSubscriber2<BookListDataResponse>() {
                    @Override
                    public void onNext(BookListDataResponse response) {
                        BookListDataResponse.DataBean data = response.getData();
                        if (data != null) {
                            List<BookModel> bookModelList = data.getData();
                            getV().fillData(bookModelList);
                        }else {
                            getV().getDataFail(new NetError("DATA EMPTY",NetError.NoDataError));
                        }
                    }

                    @Override
                    protected void onFail(NetError error) {
                        getV().getDataFail(error);
                    }
                });
    }
}
