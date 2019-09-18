package com.jj.comics.ui.mine.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.gyf.immersionbar.ImmersionBar;
import com.jj.base.ui.BaseActivity;
import com.jj.base.utils.RouterMap;
import com.jj.comics.R;
import com.jj.comics.R2;
import com.jj.comics.util.RegularUtil;
import com.jj.comics.util.SharedPreManger;
import com.jj.comics.util.eventbus.EventBusManager;
import com.jj.comics.util.eventbus.events.LoginEvent;

import butterknife.BindView;
import butterknife.OnClick;

@Route(path = RouterMap.COMIC_LOGIN_ACTIVITY)
public class LoginActivity extends BaseActivity<LoginPresenter> implements LoginContract.ILogoinView {

    @BindView(R2.id.comic_login_number)
    EditText mPhoneNumber;
    @BindView(R2.id.comic_login_pwd)
    EditText mPassWord;
    @BindView(R2.id.comic_login_code)
    TextView mCode;
    @BindView(R2.id.comic_login_agree)
    CheckBox mCheckBox;

    @Override
    public void initData(Bundle savedInstanceState) {
        ((TextView) findViewById(R.id.comic_login_agreement)).setText(getP().getAgreementText());
        //限制手机号码最多显示11位
        mPhoneNumber.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)});
        //限制验证码最大显示长度6位
        mPassWord.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});
        mPhoneNumber.addTextChangedListener(getP());
    }

    @OnClick({R2.id.iv_wxLogin, R2.id.iv_qqLogin, R2.id.iv_wbLogin,
            R2.id.comic_login_btn, R2.id.comic_login_code,R2.id.comic_login_agreement})
    void onClick(View view) {
        int i = view.getId();
        if (i == R.id.comic_login_btn) {//手机号登录
            getP().loginByVerifyCode(mCheckBox.isChecked(), mPhoneNumber.getText().toString().trim(), mPassWord.getText().toString().trim(), SharedPreManger.getInstance().getInvitecode());
        } else if (i == R.id.comic_login_code) {//验证码
            String phoneNum = mPhoneNumber.getText().toString().trim();
            if (TextUtils.isEmpty(phoneNum)){
                showToastShort("请输入手机号");
                return;
            }
            if (!RegularUtil.isMobile(phoneNum)) {
                showToastShort("请输入正确的手机号");
                return;
            }
            view.setEnabled(false);
            showProgress();
            getP().getVerifyCode(phoneNum);
        } else if (i == R.id.iv_qqLogin) {//QQ登录
            getP().qqLogin(mCheckBox.isChecked(), LoginActivity.this);
        } else if (i == R.id.iv_wxLogin) {//微信登录
            getP().wxLogin(mCheckBox.isChecked());
        } else if (i == R.id.iv_wbLogin) {//微博登录
            getP().wbLogin(mCheckBox.isChecked(), LoginActivity.this);
        } else if (i == R.id.comic_login_agreement) {//用户协议
            //服务协议
            ARouter.getInstance().build(RouterMap.COMIC_AGREEMENT_ACTIVITY).navigation();
        }
    }


    @Override
    public void onTextChanged() {
        if (!getP().isDown) {
            mCode.setEnabled(RegularUtil.isMobile(mPhoneNumber.getText().toString().trim()));
        }
    }

    @Override
    public void setCuntDownText(String text, boolean enable) {
        mCode.setText(text);
        mCode.setSelected(!enable);
        if (enable) {
            onTextChanged();
        } else {
            mCode.setEnabled(enable);
        }
    }

    @Override
    public void setResultAndFinish() {
        EventBusManager.sendLoginEvent(new LoginEvent());
        Intent intent = new Intent();
        if (getIntent()!=null){
            intent.putExtras(getIntent().getExtras());
        }
        setResult(RESULT_OK, intent);
        finish();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        getP().onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public int getLayoutId() {
        return R.layout.comic_activity_login;
    }

    @Override
    public LoginPresenter setPresenter() {
        return new LoginPresenter();
    }


    @Override
    protected void initImmersionBar() {
        ImmersionBar.with(this)
                .fitsSystemWindows(false)
                .transparentStatusBar()
                .keyboardEnable(false)
//                .navigationBarColor(R.color.comic_ffffff)
                .statusBarDarkFont(false, 0.2f)
                .init();
    }
}
