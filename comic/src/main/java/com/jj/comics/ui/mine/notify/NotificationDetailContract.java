package com.jj.comics.ui.mine.notify;

import com.jj.base.mvp.IView;

public interface NotificationDetailContract {

    interface INotificationDetailView extends IView {
        void showText(String title,String text);

        void showHtml(String url);

        void showErr(String err);

        void showHtmlLocal(String title,String html);
    }

    interface INotificationDetailPresenter {
        void getNotificationDetail(long id);
    }
}
