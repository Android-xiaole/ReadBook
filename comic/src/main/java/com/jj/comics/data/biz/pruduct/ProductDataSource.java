package com.jj.comics.data.biz.pruduct;

import com.jj.comics.data.model.NotificationListResponse;
import com.jj.comics.data.model.PayActionResponse;
import com.jj.comics.data.model.ProtocalModel;
import com.jj.comics.data.model.Push;
import com.jj.comics.data.model.ResponseModel;
import com.jj.comics.data.model.ShareParamModel;
import com.jj.comics.data.model.UpdateModelProxy;

import io.reactivex.Observable;

public interface ProductDataSource {

    //根据协议code获取用户协议
    Observable<ProtocalModel> getProductProtocalByProtocolCode(String activityName, String agreementKey);

    //提交反馈
    Observable<ResponseModel> uploadFeedback(String msg);

    //产品渠道升级
    Observable<UpdateModelProxy> getChannelUpdateInfo(String activityName);

    //获取分享参数
    Observable<ShareParamModel> getShareParam(String activityName);

    //获取广告
    Observable<Push> getAdsPush(String activityName,String typeId);

    Observable getAppConfig();

    Observable getAppConfigByIP();

    Observable<PayActionResponse> getPayAction();

    //获取消息公告
    Observable<NotificationListResponse> getNotificationList(int pageNum);
}
