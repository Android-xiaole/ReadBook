package com.jj.comics.ui.mine;

import com.jj.base.mvp.BasePresenter;
import com.jj.base.mvp.BaseRepository;
import com.jj.base.net.ApiSubscriber2;
import com.jj.base.net.NetError;
import com.jj.comics.data.biz.pruduct.ProductRepository;
import com.jj.comics.data.model.NotificationListResponse;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;

class NotificationPresenter extends BasePresenter<BaseRepository,
        NotificationContract.INotionficatonView> implements NotificationContract.INotificatonPresenter{
    @Override
    public void getNotificationList(int pageNum) {
        ProductRepository.getInstance()
                .getNotificationList(pageNum)
                .observeOn(AndroidSchedulers.mainThread())
                .as(this.bindLifecycle())
                .subscribe(new ApiSubscriber2<NotificationListResponse>() {
                    @Override
                    protected void onFail(NetError error) {
                        getV().onGetNotificationFail();
                    }

                    @Override
                    public void onNext(NotificationListResponse notificationListResponse) {
                        NotificationListResponse.DataBeanX data = notificationListResponse.getData();
                        if (data != null) {
                            List<NotificationListResponse.DataBeanX.SimpleNotificationDataBean> simpleNotificationDataBeanList = data.getData();
                            if (simpleNotificationDataBeanList != null) {
                                getV().onGetNotificationSucc(simpleNotificationDataBeanList);
                            }else {
                                getV().onGetNotificationFail();
                            }
                        }else {
                            getV().onGetNotificationFail();
                        }
                    }
                });
    }
}
