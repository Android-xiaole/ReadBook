package com.jj.comics.ui.mine.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.google.android.material.tabs.TabLayout;
import com.gyf.barlibrary.ImmersionBar;
import com.jj.base.log.LogUtil;
import com.jj.base.ui.BaseActivity;
import com.jj.base.utils.RouterMap;
import com.jj.comics.R;
import com.jj.comics.R2;
import com.jj.comics.data.model.UserInfo;
import com.jj.comics.util.RegularUtil;
import com.jj.comics.util.eventbus.EventBusManager;
import com.jj.comics.util.eventbus.events.LoginEvent;

import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.OnClick;

@Route(path = RouterMap.COMIC_LOGIN_ACTIVITY)
public class LoginActivity extends BaseActivity<LoginPresenter> implements LoginContract.ILogoinView{

    @BindView(R2.id.comic_login_number)
    EditText mPhoneNumber;
    @BindView(R2.id.comic_login_pwd)
    EditText mPassWord;
    @BindView(R2.id.comic_login_code)
    TextView mCode;
    @BindView(R2.id.comic_login_agree)
    CheckBox mCheckBox;
    @BindView(R2.id.lin_phoneLogin)
    LinearLayout lin_phoneLogin;//手机号码登录布局块
    @BindView(R2.id.lin_uidLogin)
    LinearLayout lin_uidLogin;//UID登录布局块
    @BindView(R2.id.et_uidLogin)
    EditText et_uidLogin;

    private boolean isUidLogin = false;

    @Override
    public void initData(Bundle savedInstanceState) {
        TabLayout tabLayout = findViewById(R.id.comic_login_tab);
        TabLayout.Tab tab_phoneLogin = tabLayout.newTab().setText("手机登录");
        TabLayout.Tab tab_uidLogin = tabLayout.newTab().setText("UID登录");
        tabLayout.addTab(tab_phoneLogin);
        tabLayout.addTab(tab_uidLogin);
        tabLayout.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab == tab_phoneLogin){
                    isUidLogin = false;
                    lin_phoneLogin.setVisibility(View.VISIBLE);
                    lin_uidLogin.setVisibility(View.GONE);
                }else if (tab == tab_uidLogin){
                    isUidLogin = true;
                    lin_phoneLogin.setVisibility(View.GONE);
                    lin_uidLogin.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        ((TextView) findViewById(R.id.comic_login_agreement)).setText(getP().getAgreementText());
        //限制手机号码最多显示11位
        mPhoneNumber.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)});
        //限制验证码最大显示长度6位
        mPassWord.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});
        mPhoneNumber.addTextChangedListener(getP());
    }

    @OnClick({R2.id.comic_login_agreement, R2.id.comic_login_qq, R2.id.comic_login_wx,
            R2.id.comic_login_btn, R2.id.comic_login_code, R2.id.comic_login_wb})
    void onClick(View view) {
        int i = view.getId();
        if (i == R.id.comic_login_btn) {
            if (isUidLogin){
                String uid = et_uidLogin.getText().toString();
                if (TextUtils.isEmpty(uid)){
                    return;
                }
                getP().uidLogin(uid);
            }else{
                getP().loginByVerifyCode(mCheckBox.isChecked(), mPhoneNumber.getText().toString().trim(), mPassWord.getText().toString().trim());
            }
        } else if (i == R.id.comic_login_code) {
            view.setEnabled(false);
            showProgress();
            getP().getVerifyCode(mPhoneNumber.getText().toString().trim());
        } else if (i == R.id.comic_login_qq) {
            getP().qqLogin(mCheckBox.isChecked(),LoginActivity.this);
        } else if (i == R.id.comic_login_wx) {
            getP().wxLogin(mCheckBox.isChecked());
        } else if (i == R.id.comic_login_wb) {
            getP().wbLogin(mCheckBox.isChecked(),LoginActivity.this);
        } else if (i == R.id.comic_login_agreement) {
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
        if (enable) {
            mCode.setTextAppearance(this, R.style.comic_code);
            mCode.setBackgroundResource(R.drawable.comic_login_code);
            onTextChanged();
        } else {
            mCode.setTextAppearance(this, R.style.comic_code_getting);
            mCode.setBackgroundColor(getResources().getColor(android.R.color.transparent));
            mCode.setEnabled(enable);
        }
    }

    @Override
    public void setResultAndFinish() {
        EventBusManager.sendLoginEvent(new LoginEvent());
        Intent intent = new Intent();
        intent.putExtras(getIntent().getExtras());
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
