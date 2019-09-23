package com.jj.comics.ui.mine.rebate.cashout;

import com.jj.base.mvp.BasePresenter;
import com.jj.base.mvp.BaseRepository;
import com.jj.base.net.ApiSubscriber2;
import com.jj.base.net.NetError;
import com.jj.comics.data.biz.user.UserRepository;
import com.jj.comics.data.model.CashOutWayResponse;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class CashOutWayPresenter extends BasePresenter<BaseRepository,CashOutWayActivity> implements CashOutWayContract.ICashOutWayPresenter{
    @Override
    public void getWayStatus() {
        UserRepository.getInstance()
                .getCashOutWayStatus()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .as(bindLifecycle())
                .subscribe(new ApiSubscriber2<CashOutWayResponse>() {
                    @Override
                    protected void onFail(NetError error) {
                        getV().onGetCashOutWayStatus(null);
                    }

                    @Override
                    public void onNext(CashOutWayResponse cashOutWayResponse) {
                        CashOutWayResponse.DataBean data = cashOutWayResponse.getData();
                        getV().onGetCashOutWayStatus(cashOutWayResponse);
                    }
                });
    }
}
