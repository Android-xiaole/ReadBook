package com.jj.comics.ui.mine.rebate.detail;

import com.jj.base.mvp.BasePresenter;
import com.jj.base.mvp.BaseRepository;
import com.jj.base.net.ApiSubscriber2;
import com.jj.base.net.NetError;
import com.jj.comics.data.biz.user.UserRepository;
import com.jj.comics.data.model.CashOutListResponse;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class CashOutPresenter extends BasePresenter<BaseRepository, CashOutFragment> implements CashOutContract.ICashOutPresenter{
    @Override
    public void getCashOutList(int page) {
        UserRepository.getInstance()
                .getCashOutList(page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .as(this.bindLifecycle())
                .subscribe(new ApiSubscriber2<CashOutListResponse>() {
                    @Override
                    protected void onFail(NetError error) {
                        getV().onGetDataFail(NetError.noDataError());
                    }

                    @Override
                    public void onNext(CashOutListResponse cashOutListResponse) {
                        CashOutListResponse.DataBeanX data = cashOutListResponse.getData();
                        if (data != null) {
                            List<CashOutListResponse.DataBeanX.CashOutModel> cashOutModelList = data.getData();
                            if (cashOutModelList != null) {
                                getV().onGetDataSucc(cashOutModelList);
                            }else {
                                getV().onGetDataFail(NetError.noDataError());
                            }
                        }else {
                            getV().onGetDataFail(NetError.noDataError());
                        }
                    }
                });
    }
}
