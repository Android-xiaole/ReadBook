package com.jj.comics.ui.mine.pay;

import com.jj.base.mvp.BasePresenter;
import com.jj.base.mvp.BaseRepository;
import com.jj.base.net.ApiSubscriber2;
import com.jj.base.net.NetError;
import com.jj.comics.data.biz.user.UserRepository;
import com.jj.comics.data.model.RecharegeRecordsResponse;
import com.jj.comics.data.model.RechargeRecordModel;
import com.jj.comics.util.eventbus.EventBusManager;
import com.jj.comics.util.eventbus.events.LogoutEvent;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;

public class NewRechargeRecordPresenter extends BasePresenter<BaseRepository, RechargeRecordFragment> implements NewRechargeContract.INewRechargePresenter {

    /**
     * 查询充值记录数据
     * 请求页面数据
     *
     * @param currentPage 当前页
     */
    @Override
    public void loadData(int currentPage) {

        UserRepository.getInstance().getRechargeRecord(currentPage)
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.<RecharegeRecordsResponse>autoDisposable(AndroidLifecycleScopeProvider.from(getV().getLifecycle())))
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
                        if (error.getType() == NetError.AuthError) {
//                            getV().getBaseActivity().finish();
//                            EventBus.getDefault().post(getV().getActivity());
                            EventBusManager.sendLogoutEvent(new LogoutEvent());
                            getV().hideProgress();
                        } else
                            getV().getDataFail(error);
                    }
                });
    }

    @Override
    public boolean useEventBus() {
        return false;
    }
}
