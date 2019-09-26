package com.jj.comics.ui.mine.login;


import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.jj.base.mvp.BasePresenter;
import com.jj.base.ui.BaseActivity;
import com.jj.base.utils.RouterMap;
import com.jj.comics.R;
import com.jj.comics.R2;
import com.jj.comics.common.constants.Constants;
import com.jj.comics.data.model.LoginResponse;
import com.jj.comics.data.model.UserInfo;
import com.jj.comics.util.RegularUtil;
import com.jj.comics.util.SharedPreManger;
import com.jj.comics.util.eventbus.EventBusManager;
import com.jj.comics.util.eventbus.events.LoginEvent;
import com.jj.comics.util.eventbus.events.UpdateUserInfoEvent;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 第三方登录绑定手机号的页面
 */
@Route(path = RouterMap.COMIC_LOGIN_BINDPHONE__ACTIVITY)
public class BindPhoneActivity extends BaseActivity<BindPhonePresenter> implements BindPhoneContract.View {

    @BindView(R2.id.et_phone)
    EditText et_phone;//手机号输入框
    @BindView(R2.id.et_code)
    EditText et_code;//验证码输入框
    @BindView(R2.id.tv_code)
    TextView tv_code;//验证码倒计时文字显示

    private String type,openid;

    /**
     * 第三方登录跳转到绑定手机号码的页面
     */
    public static void toBindPhoneActivity(String type, String openid, Activity context){
        ARouter.getInstance().build(RouterMap.COMIC_LOGIN_BINDPHONE__ACTIVITY)
                .withString(Constants.IntentKey.LOGIN_TYPE,type)
                .withString(Constants.IntentKey.LOGIN_OPENID,openid)
                .navigation(context);
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        type = getIntent().getStringExtra(Constants.IntentKey.LOGIN_TYPE);
        openid = getIntent().getStringExtra(Constants.IntentKey.LOGIN_OPENID);
    }

    private CountDownTimer countDownTimer;
    @OnClick({R2.id.tv_code,R2.id.btn_commit})
    public void onClick_BindPhone(View view){
        if (view.getId() == R.id.tv_code){
            String phoneNum = et_phone.getText().toString().trim();
            if (TextUtils.isEmpty(phoneNum)){
                showToastShort("请输入手机号");
                return;
            }
            if (!RegularUtil.isMobile(phoneNum)) {
                showToastShort("请输入正确的手机号");
                return;
            }
            //设置view不可点击
            tv_code.setClickable(false);
            tv_code.setSelected(true);
            if (countDownTimer == null){
                countDownTimer = new CountDownTimer(60*1000,1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        tv_code.setText("重发验证码 "+millisUntilFinished/1000+"秒");
                    }

                    @Override
                    public void onFinish() {
                        tv_code.setClickable(true);
                        tv_code.setText("获取验证码");
                        tv_code.setSelected(false);
                    }
                };
            }
            countDownTimer.start();
            getP().getCode(phoneNum,type);
        }else if (view.getId() == R.id.btn_commit){//点击提交按钮
            String phoneNum = et_phone.getText().toString().trim();
            if (TextUtils.isEmpty(phoneNum)){
                showToastShort("请输入手机号");
                return;
            }
            if (!RegularUtil.isMobile(phoneNum)) {
                showToastShort("请输入正确的手机号");
                return;
            }
            String code = et_code.getText().toString();
            if (TextUtils.isEmpty(code)){
                showToastShort("请输入验证码");
                return;
            }
            getP().bindPhone(phoneNum,code, SharedPreManger.getInstance().getInvitecode(),openid);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.comic_activity_login_bindphone;
    }

    @Override
    public BindPhonePresenter setPresenter() {
        return new BindPhonePresenter();
    }

    @Override
    public void onBindPhone(UserInfo userInfo) {
        EventBusManager.sendLoginEvent(new LoginEvent());
        ARouter.getInstance().build(RouterMap.COMIC_MAIN_ACTIVITY).navigation(this);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer!=null){
            countDownTimer.cancel();
        }
    }
}
