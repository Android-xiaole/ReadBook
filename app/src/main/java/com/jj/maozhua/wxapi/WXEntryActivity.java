package com.jj.maozhua.wxapi;

import android.content.Intent;
import android.os.Bundle;

import com.jj.base.mvp.BasePresenter;
import com.jj.base.ui.BaseActivity;
import com.jj.comics.common.constants.Constants;
import com.jj.comics.util.TencentHelper;
import com.jj.comics.util.eventbus.events.WxLoginEvent;
import com.jj.comics.util.eventbus.events.WxShareEvent;
import com.jj.comics.util.eventbus.EventBusManager;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;

import androidx.annotation.Nullable;

public class WXEntryActivity extends BaseActivity implements IWXAPIEventHandler {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        //注意：
        //第三方开发者如果使用透明界面来实现WXEntryActivity，需要判断handleIntent的返回值，
        // 如果返回值为false，则说明入参不合法未被SDK处理，应finish当前透明界面，避免外部通过传递非法参数的Intent导致停留在透明界面，引起用户的疑惑
        try {
            boolean handleIntent = TencentHelper.getWxApi(Constants.WX_APP_ID_LOGIN()).handleIntent(getIntent(),
                    this);
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
            boolean handleIntent = TencentHelper.getWxApi(Constants.WX_APP_ID_LOGIN()).handleIntent(getIntent(), this);
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

    private WxLoginEvent wxLoginEvent;
    private WxShareEvent wxShareEvent;
    @Override
    public void onResp(BaseResp baseResp) {
        switch (baseResp.getType()){
            case ConstantsAPI.COMMAND_SENDAUTH://微信登录
                if (wxLoginEvent == null)wxLoginEvent = new WxLoginEvent();
                wxLoginEvent.errCode = baseResp.errCode;
                if(baseResp instanceof SendAuth.Resp){
                    wxLoginEvent.code = ((SendAuth.Resp)baseResp).code;
                }
                EventBusManager.sendWxLogin(wxLoginEvent);
                break;
            case ConstantsAPI.COMMAND_SENDMESSAGE_TO_WX://微信分享
                if (wxShareEvent == null)wxShareEvent = new WxShareEvent();
                wxShareEvent.errCode = baseResp.errCode;
                EventBusManager.sendWxShare(wxShareEvent);
                break;
        }
        finish();
    }
}
