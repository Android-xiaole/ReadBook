package com.jj.comics.ui.mine.rebate.cashout;

import com.jj.base.mvp.BasePresenter;
import com.jj.base.mvp.BaseRepository;
import com.jj.base.net.ApiSubscriber2;
import com.jj.base.net.NetError;
import com.jj.comics.data.biz.user.UserRepository;
import com.jj.comics.data.model.AddCashOutWayResponse;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class AddCashOutWayUnionPresenter extends BasePresenter<BaseRepository, AddCashOutWayUnionActivity> implements AddCashOutWayUnionContract.IAddCashOutWayUnionPresenter{
    @Override
    public void addUnion(String account_number, String opener, String opening_bank) {
        UserRepository.getInstance()
                .addCashOutWayUnion(account_number,opener,opening_bank)
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
