package com.jj.comics.ui.mine.rebate;

import com.jj.base.mvp.BasePresenter;
import com.jj.base.mvp.BaseRepository;
import com.jj.base.net.ApiSubscriber2;
import com.jj.base.net.NetError;
import com.jj.base.utils.toast.ToastUtil;
import com.jj.comics.data.biz.user.UserRepository;
import com.jj.comics.data.model.CashOutWayResponse;
import com.jj.comics.data.model.PayInfo;
import com.jj.comics.data.model.PayInfoResponse;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MyRebatePrenenter extends BasePresenter<BaseRepository,MyRebateActivity> implements MyRebateContract.IMyRebatePresenter{
    @Override
    public void getMyRebateInfo() {
        UserRepository.getInstance().getUserPayInfo()
                .observeOn(AndroidSchedulers.mainThread())
                .as(this.<PayInfoResponse>bindLifecycle())
                .subscribe(new ApiSubscriber2<PayInfoResponse>() {

                    @Override
                    public void onNext(PayInfoResponse payInfoResponse) {
                        PayInfo data = payInfoResponse.getData();
                        if (data != null) {
                            getV().onGetMyRebateInfo(data);
                        }else {
                            getV().onGetMyRebateInfoFail(NetError.noDataError());
                        }

                    }

                    @Override
                    protected void onFail(NetError error) {
                        getV().onGetMyRebateInfoFail(error);
                    }
                });
    }

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
