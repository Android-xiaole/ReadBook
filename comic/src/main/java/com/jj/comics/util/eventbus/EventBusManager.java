package com.jj.comics.util.eventbus;

import android.content.Intent;

import com.jj.base.utils.NetWorkUtil;
import com.jj.comics.data.model.SearchModel;
import com.jj.comics.util.eventbus.events.BatchBuyEvent;
import com.jj.comics.util.eventbus.events.BatteryReceiverEvent;
import com.jj.comics.util.eventbus.events.FinishLoginActivityEvent;
import com.jj.comics.util.eventbus.events.ChangeTabBarEvent;
import com.jj.comics.util.eventbus.events.LoginEvent;
import com.jj.comics.util.eventbus.events.LogoutEvent;
import com.jj.comics.util.eventbus.events.PaySuccessEvent;
import com.jj.comics.util.eventbus.events.RefreshCatalogListBySubscribeEvent;
import com.jj.comics.util.eventbus.events.RefreshComicCollectionStatusEvent;
import com.jj.comics.util.eventbus.events.RefreshComicFavorStatusEvent;
import com.jj.comics.util.eventbus.events.RefreshDetailActivityDataEvent;
import com.jj.comics.util.eventbus.events.RefreshRewardRecordListEvent;
import com.jj.comics.util.eventbus.events.UpdateAutoBuyStatusEvent;
import com.jj.comics.util.eventbus.events.UpdateReadHistoryEvent;
import com.jj.comics.util.eventbus.events.UpdateSignStatusEvent;
import com.jj.comics.util.eventbus.events.UpdateUserInfoEvent;
import com.jj.comics.util.eventbus.events.WxLoginEvent;
import com.jj.comics.util.eventbus.events.WxPayEvent;
import com.jj.comics.util.eventbus.events.WxShareEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.Date;

/**
 * 这是一个项目中发送eventbus通知的管理类
 */
public class EventBusManager {

    /**
     * 改变tabbar的选中状态的通知
     */
    public static void sendChangeTabBarEvent(ChangeTabBarEvent changeTabBarEvent){
        EventBus.getDefault().post(changeTabBarEvent);
    }

    /**
     * 发送设置了系统时区的通知
     */
    public static void sendTimeChangeReceiverEvent(Date date){
        EventBus.getDefault().post(date);
    }

    /**
     * 发送网络状态改变的通知
     */
    public static void sendNetWorkReceiverEvent(NetWorkUtil.NetworkType networkType){
        EventBus.getDefault().post(networkType);
    }

    /**
     * 发送电量变化的通知
     */
    public static void sendBatteryReceiverEvent(BatteryReceiverEvent batteryReceiverEvent){
        EventBus.getDefault().post(batteryReceiverEvent);
    }

    /**
     * 发送微信登录回调的通知
     */
    public static void sendWxLogin(WxLoginEvent wxLoginEvent){
        EventBus.getDefault().post(wxLoginEvent);
    }

    /**
     * 发送微信分享回调的通知
     */
    public static void sendWxShare(WxShareEvent wxShareEvent){
        EventBus.getDefault().post(wxShareEvent);
    }

    /**
     * 发送微信支付回调的通知
     */
    public static void sendWxPay(WxPayEvent wxPayEvent){
        EventBus.getDefault().post(wxPayEvent);
    }

    /**
     * 发送微博分享回调的通知
     * @warn (由于微博分享的回调通知是需要写在baseactivity里面的，故这里无法调用)
     */
    public static void sendWbShare(Intent data){
        EventBus.getDefault().post(data);
    }

    /**
     * 发送改变漫画内容收藏状态的通知
     */
    public static void sendComicCollectionStatus(RefreshComicCollectionStatusEvent refreshComicCollectionStatusEvent){
        EventBus.getDefault().post(refreshComicCollectionStatusEvent);
    }

    /**
     * 发送改变漫画内容点赞状态的通知
     */
    public static void sendComicFavorStatus(RefreshComicFavorStatusEvent refreshComicFavorStatusEvent){
        EventBus.getDefault().post(refreshComicFavorStatusEvent);
    }

    /**
     * 发送更新记录当前阅读章节的通知
     */
    public static void sendUpdateReadRecord(UpdateReadHistoryEvent event){
        EventBus.getDefault().post(event);
    }

    /**
     * 订阅成功的通知，刷新阅读页面章节目录列表
     */
    public static void sendRefreshCatalogListBySubscribeEvent(RefreshCatalogListBySubscribeEvent refreshCatalogListBySubscribeEvent){
        EventBus.getDefault().post(refreshCatalogListBySubscribeEvent);
    }

    /**
     * 需要去更新用户数据的通知（调用接口刷新）
     */
    public static void sendUpdateUserInfoEvent(UpdateUserInfoEvent updateUserInfoEvent){
        EventBus.getDefault().post(updateUserInfoEvent);
    }

    /**
     * 发送打赏成功后刷新漫画内容打赏列表的通知
     */
    public static void sendRefreshRewardRecordListEvent(RefreshRewardRecordListEvent refreshRewardRecordListEvent){
        EventBus.getDefault().post(refreshRewardRecordListEvent);
    }

    /**
     * 发送更新签到状态的通知
     */
    public static void sendUpdateSignStatusEvent(UpdateSignStatusEvent updateSignStatusEvent){
        EventBus.getDefault().post(updateSignStatusEvent);
    }

    /**
     * 发送插入或者更新搜索键的通知
     */
    public static void sendInsertOrUpdateSearchKeyEvent(SearchModel searchModel){
        EventBus.getDefault().post(searchModel);
    }

    /**
     *  发送更新自动购买状态的通知
     */
    public static void sendUpdateAutoBuyStatusEvent(UpdateAutoBuyStatusEvent updateAutoBuyStatusEvent){
        EventBus.getDefault().post(updateAutoBuyStatusEvent);
    }

    /**
     * 发送登录成功的通知
     */
    public static void sendLoginEvent(LoginEvent loginEvent){
        EventBus.getDefault().post(loginEvent);
    }

    /**
     * 发送注销登录的通知
     */
    public static void sendLogoutEvent(LogoutEvent logoutEvent){
        EventBus.getDefault().post(logoutEvent);
    }

    /**
     * 发送绑定手机号登录成功的通知
     */
    public static void sendFinishLoginActivityEvent(FinishLoginActivityEvent event){
        EventBus.getDefault().post(event);
    }
    /**
     * 发送支付成功的通知
     * @param event
     */
    public static void sendPaySuccessEvent(PaySuccessEvent event){
        EventBus.getDefault().post(event);
    }

    /**
     * 发送全本购买成功的通知,需要刷新bookmodel，控制全本购买icon的隐藏和显示
     */
    public static void sendBatchBuyEvent(BatchBuyEvent event){
        EventBus.getDefault().post(event);
    }

    /**
     * 发送loading界面登录成功后刷新详情页面相关数据的通知
     */
    public static void sendRefreshDetailActivityData(RefreshDetailActivityDataEvent event){
        EventBus.getDefault().post(event);
    }
}
