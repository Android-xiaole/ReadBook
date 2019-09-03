package com.jj.maozhua.wxapi;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.jj.base.mvp.BasePresenter;
import com.jj.base.ui.BaseActivity;
import com.jj.comics.common.constants.Constants;
import com.jj.comics.util.LoginHelper;
import com.jj.comics.util.TencentHelper;
import com.jj.comics.util.eventbus.events.WxPayEvent;
import com.jj.comics.util.eventbus.EventBusManager;
import com.jj.comics.util.reporter.ActionReporter;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.modelpay.PayResp;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.umeng.analytics.MobclickAgent;

public class WXPayEntryActivity extends BaseActivity implements IWXAPIEventHandler {
    @Override
    public void initData(Bundle savedInstanceState) {
        try {
            boolean handleIntent = TencentHelper.getWxApi(Constants.WX_APP_ID_PAY()).handleIntent(getIntent(), this);
            if (!handleIntent) finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        try {
            boolean handleIntent = TencentHelper.getWxApi(Constants.WX_APP_ID_PAY()).handleIntent(getIntent(), this);
            if (!handleIntent) finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getLayoutId() {
        return 0;
    }

    @Override
    public BasePresenter setPresenter() {
        return null;
    }


    @Override
    public void onReq(BaseReq baseReq) {

    }

    private WxPayEvent wxPayEvent;

    @Override
    public void onResp(BaseResp baseResp) {
        if (baseResp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            if (wxPayEvent == null) wxPayEvent = new WxPayEvent();
            wxPayEvent.errCode = baseResp.errCode;
            if (baseResp.errCode == -2) {
                wxPayEvent.msg = "取消支付";
            } else {
                wxPayEvent.msg = "支付出错了";
            }

            EventBusManager.sendWxPay(wxPayEvent);
            if (baseResp instanceof PayResp) {
                String pay_info = LoginHelper.getOnLineUser().getUid() + "&" + ((PayResp) baseResp).prepayId + "&" + baseResp.errCode;
                ActionReporter.reportAction(ActionReporter.Event.PAY_RESULT, pay_info, null, null);
            }
            MobclickAgent.onEvent(this, Constants.UMEventId.PAY_RESULT, "" + baseResp.errCode + ": " + baseResp.errStr);
        }
        finish();
    }
}
