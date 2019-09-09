package com.jj.comics.ui.mine;

import com.jj.base.mvp.IView;
import com.jj.comics.data.model.NotificationListResponse;

import java.util.List;

public interface NotificationContract {
    interface INotionficatonView extends IView {
        void onGetNotificationSucc(List<NotificationListResponse.DataBeanX.SimpleNotificationDataBean> list);
        void onGetNotificationFail();
    }

    interface INotificatonPresenter {
        void getNotificationList(int pageNum);
    }
}
