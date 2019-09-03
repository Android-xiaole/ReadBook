package com.jj.comics.ui.mine.pay;

import com.jj.base.mvp.BasePresenter;
import com.jj.base.mvp.BaseRepository;
import com.jj.base.net.ApiSubscriber2;
import com.jj.base.net.NetError;
import com.jj.comics.data.biz.user.UserRepository;
import com.jj.comics.data.model.RecharegeRecordsResponse;
import com.jj.comics.data.model.RechargeRecordModel;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;

public class RechargeRecordPresenter extends BasePresenter<BaseRepository,RechargeRecordContract.IRechargeRecordView> implements RechargeRecordContract.IRechargeRecordPresenter{

    @Override
    public void loadData(int currentPage) {

        UserRepository.getInstance().getRechargeRecord(currentPage)
                .observeOn(AndroidSchedulers.mainThread())
                .as(this.<RecharegeRecordsResponse>bindLifecycle())
                .subscribe(new ApiSubscriber2<RecharegeRecordsResponse>() {
                    @Override
                    public void onNext(RecharegeRecordsResponse model) {
                        List<RechargeRecordModel> rechargeRecords = model.getData();
                        if (model.error() != null) {
                            onFail(model.error());
                        } else if (rechargeRecords == null || rechargeRecords.isEmpty()) {
                            onFail(NetError.noDataError());
                        } else
                            getV().fillData(rechargeRecords);
                    }

                    @Override
                    protected void onFail(NetError error) {
                        getV().getDataFail(error);
                    }
                });
    }

}
