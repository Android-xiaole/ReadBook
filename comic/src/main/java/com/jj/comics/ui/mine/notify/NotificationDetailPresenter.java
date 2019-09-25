package com.jj.comics.ui.mine.notify;

import android.text.Html;

import com.jj.base.mvp.BasePresenter;
import com.jj.base.mvp.BaseRepository;
import com.jj.base.net.ApiSubscriber2;
import com.jj.base.net.NetError;
import com.jj.comics.data.biz.pruduct.ProductRepository;
import com.jj.comics.data.model.NotificationResponse;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class NotificationDetailPresenter extends BasePresenter<BaseRepository,
        NotificationDetailContract.INotificationDetailView> implements NotificationDetailContract.INotificationDetailPresenter {

    @Override
    public void getNotificationDetail(long id) {
        ProductRepository.getInstance()
                .getNotificationDetail(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .as(bindLifecycle())
                .subscribe(new ApiSubscriber2<NotificationResponse>() {
                    @Override
                    protected void onFail(NetError error) {
                        getV().showErr(error.getMessage());
                    }

                    @Override
                    public void onNext(NotificationResponse notificationResponse) {
                        NotificationResponse.DataBean data = notificationResponse.getData();
                        if (data != null) {
                            String content = data.getContent();
                            if (content.startsWith("http")) {
                                getV().showHtml(content);
                            }else if (content.startsWith("<")) {
                                getV().showHtmlLocal(data.getTitle(),content);
                            }else {
                                getV().showText(data.getTitle(), content);
                            }
                        }else {
                            getV().showErr("无数据");
                        }

                    }
                });
    }
}
