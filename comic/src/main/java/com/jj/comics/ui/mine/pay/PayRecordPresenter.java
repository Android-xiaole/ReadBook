package com.jj.comics.ui.mine.pay;

import com.jj.base.mvp.BasePresenter;
import com.jj.base.mvp.BaseRepository;
import com.jj.base.net.ApiSubscriber2;
import com.jj.base.net.NetError;
import com.jj.comics.data.biz.user.UserRepository;
import com.jj.comics.data.model.ExpenseSumRecordModel;
import com.jj.comics.data.model.ExpenseSumRecordsResponse;
import com.jj.comics.util.eventbus.EventBusManager;
import com.jj.comics.util.eventbus.events.LogoutEvent;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * P 层数据获取实现类
 */
public class PayRecordPresenter extends BasePresenter<BaseRepository, PayRecordContract.IPayRecordView> implements PayRecordContract.IPayRecordPresenter {


    /**
     * 获取数据
     * 请求加载页数据
     *
     * @param currentPage 当前页
     */
    @Override
    public void loadData(int currentPage) {
        UserRepository.getInstance().getConsumptionRecord(getV().getClass().getName(), currentPage)
                .observeOn(AndroidSchedulers.mainThread())
                .as(this.<ExpenseSumRecordsResponse>bindLifecycle())
                .subscribe(new ApiSubscriber2<ExpenseSumRecordsResponse>() {
                    @Override
                    public void onNext(ExpenseSumRecordsResponse model) {
                        ExpenseSumRecordsResponse.DataBeanX dataBeanX = model.getData();
                        List<ExpenseSumRecordModel> expenseRecords = dataBeanX.getData();
                        if (model.error() != null) {
                            onFail(model.error());
                        } else if (expenseRecords == null || expenseRecords.isEmpty()) {
                            onFail(NetError.noDataError());
                        } else
                            getV().fillData(expenseRecords);
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
