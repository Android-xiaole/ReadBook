package com.jj.comics.ui.mine.rebate.cashout;

import com.jj.base.mvp.BasePresenter;
import com.jj.base.mvp.BaseRepository;
import com.jj.base.net.ApiSubscriber2;
import com.jj.base.net.NetError;
import com.jj.comics.common.net.ComicService;
import com.jj.comics.data.biz.user.UserRepository;
import com.jj.comics.data.model.CashOutResponse;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class DoCashOutPresenter extends BasePresenter<BaseRepository,DoCashOutActivity> implements DoCashOutContract.IDoCashOutPresenter {
    @Override
    public void cashOut(int type, float money) {
        UserRepository.getInstance()
                .cashOut(type,money)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .as(bindLifecycle())
                .subscribe(new ApiSubscriber2<CashOutResponse>() {
                    @Override
                    protected void onFail(NetError error) {
                        getV().cashOutFail(error.getMessage());
                    }

                    @Override
                    public void onNext(CashOutResponse cashOutResponse) {
                        CashOutResponse.DataBean data = cashOutResponse.getData();
                        if (data != null && data.isStatus()) {
                            data.setType(type);
                           getV().cashOutComplete(data);
                        }else {
                            getV().cashOutFail(NetError.noDataError().getMessage());
                        }
                    }
                });
    }
}
