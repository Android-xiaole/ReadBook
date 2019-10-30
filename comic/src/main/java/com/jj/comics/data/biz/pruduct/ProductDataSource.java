package com.jj.comics.data.biz.pruduct;

import com.jj.comics.data.model.NotificationListResponse;
import com.jj.comics.data.model.NotificationResponse;
import com.jj.comics.data.model.PayActionResponse;
import com.jj.comics.data.model.ProtocalModel;
import com.jj.comics.data.model.Push;
import com.jj.comics.data.model.ResponseModel;
import com.jj.comics.data.model.ShareParamModel;
import com.jj.comics.data.model.StatusResponse;
import com.jj.comics.data.model.UpdateModelProxy;

import io.reactivex.Observable;
import retrofit2.http.Query;

public interface ProductDataSource {

    //根据协议code获取用户协议
    Observable<ProtocalModel> getProductProtocalByProtocolCode(String activityName, String agreementKey);


    //产品渠道升级
    Observable<UpdateModelProxy> getChannelUpdateInfo(String activityName);


    Observable getAppConfig();

    Observable getAppConfigByIP();


    //获取消息公告
    Observable<NotificationListResponse> getNotificationList(int pageNum);

    //获取公告详情
    Observable<NotificationResponse> getNotificationDetail(long id);

    // App审核开关状态
    Observable<StatusResponse> getStatus();
}
