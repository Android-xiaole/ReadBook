package com.jj.comics.ui.mine.bought;

import com.jj.base.mvp.BasePresenter;
import com.jj.base.mvp.BaseRepository;
import com.jj.base.net.ApiSubscriber2;
import com.jj.base.net.NetError;
import com.jj.comics.data.biz.content.ContentRepository;
import com.jj.comics.data.model.BoughtResponse;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class BoughtPresenter extends BasePresenter<BaseRepository, BoughtContract.IBoughtView> implements BoughtContract.IBoughtPresenter {
    @Override
    public void getBoughtList(int page) {
        ContentRepository.getInstance().myBought(page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .as(this.<BoughtResponse>bindLifecycle())
                .subscribe(new ApiSubscriber2<BoughtResponse>() {
                    @Override
                    public void onNext(BoughtResponse boughtResponse) {
                        BoughtResponse.DataBeanX data = boughtResponse.getData();
                        if (data != null) {
                            List<BoughtResponse.DataBeanX.BoughtModel> modelList = data.getData();
                            if (modelList != null) {
                                getV().onLoadBoughtList(modelList);
                            }else {
                                getV().onLoadBoughtListFail(NetError.noDataError());
                            }

                        }else {
                            getV().onLoadBoughtListFail(NetError.noDataError());
                        }
                    }

                    @Override
                    protected void onFail(NetError error) {
                        getV().onLoadBoughtListFail(NetError.noDataError());
                    }

                });

    }


}
