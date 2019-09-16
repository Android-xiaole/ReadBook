package com.jj.comics.ui.mine.rebate.cashout;

import com.jj.base.mvp.BasePresenter;
import com.jj.base.mvp.BaseRepository;
import com.jj.base.net.ApiSubscriber2;
import com.jj.base.net.NetError;
import com.jj.comics.data.biz.user.UserRepository;
import com.jj.comics.data.model.AddCashOutWayResponse;
import com.jj.comics.data.model.CashOutWayResponse;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class AddCashOutWayAliPresenter extends BasePresenter<BaseRepository,
        AddCashOutWayAliActivity> implements AddCashOutWayAliContract.IAddCashOutWayAliPresenter{
    @Override
    public void addAli(String account, String name) {
        UserRepository.getInstance()
                .addCashOutWayAli(account,name)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .as(this.bindLifecycle())
                .subscribe(new ApiSubscriber2<AddCashOutWayResponse>() {
                    @Override
                    protected void onFail(NetError error) {
                        getV().onAddComplete(false,"添加失败");
                    }

                    @Override
                    public void onNext(AddCashOutWayResponse cashOutWayResponse) {
                        getV().onAddComplete(true,"添加成功");
                    }
                });
    }
}
