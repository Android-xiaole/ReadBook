package com.jj.comics.ui.mine.login;


import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.request.RequestOptions;
import com.jj.base.imageloader.ILFactory;
import com.jj.base.ui.BaseActivity;
import com.jj.base.utils.RouterMap;
import com.jj.comics.R;
import com.jj.comics.R2;
import com.jj.comics.common.constants.Constants;
import com.jj.comics.data.model.UserInfo;
import com.jj.comics.util.RegularUtil;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 第三方登录绑定手机号的页面
 */
@Route(path = RouterMap.COMIC_LOGIN_BINDPHONE__ACTIVITY)
public class BindPhoneActivity extends BaseActivity<BindPhonePresenter> implements BindPhoneContract.View {

    @BindView(R2.id.tv_skipBind)
    TextView tv_skipBind;//跳过绑定
    @BindView(R2.id.iv_avatar)
    ImageView iv_avatar;//用户头像
    @BindView(R2.id.tv_nickName)
    TextView tv_nickName;//用户昵称
    @BindView(R2.id.et_phone)
    EditText et_phone;//手机号输入框

    private UserInfo userInfo;
    private String phoneNum;

    /**
     * 第三方登录跳转到绑定手机号码的页面
     */
    public static void toBindPhoneActivity(UserInfo userInfo,Activity context){
        ARouter.getInstance().build(RouterMap.COMIC_LOGIN_BINDPHONE__ACTIVITY)
                .withSerializable(Constants.IntentKey.USER_INFO,userInfo)
                .navigation(context);
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        userInfo = (UserInfo) getIntent().getSerializableExtra(Constants.IntentKey.USER_INFO);
        ILFactory.getLoader().loadNet(iv_avatar,userInfo.getAvatar(),new RequestOptions().circleCrop());
        if (!TextUtils.isEmpty(userInfo.getNickname())){
            tv_nickName.setText(userInfo.getNickname());
        }else{
            tv_nickName.setText("null");
        }
    }

    @OnClick({R2.id.btn_get_code,R2.id.tv_skipBind})
    public void onClick_BindPhone(View view){
        if (view.getId() == R.id.tv_skipBind){//跳过绑定
            //直接跳转到选择性别页面
            GenderSelectActivity.toActivity(this);
            finish();
        }else if (view.getId() == R.id.btn_get_code){//获取验证码
            String phoneNum = et_phone.getText().toString().trim();
            if (TextUtils.isEmpty(phoneNum)){
                showToastShort("请输入手机号");
                return;
            }
            if (!RegularUtil.isMobile(phoneNum)) {
                showToastShort("请输入正确的手机号");
                return;
            }
            this.phoneNum = phoneNum;
            getP().getCode(phoneNum);
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
    public void onGetCode() {
        GetCodeActivity.toActivity(true,true,phoneNum,this);
        finish();
    }

    @Override
    public void onBackPressed() {
    }
}
