package com.jj.comics.ui.mine.login;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.jj.base.ui.BaseActivity;
import com.jj.base.utils.RouterMap;
import com.jj.base.utils.toast.ToastUtil;
import com.jj.comics.R;
import com.jj.comics.R2;
import com.jj.comics.common.constants.Constants;
import com.jj.comics.util.SharedPreManger;
import com.jj.comics.util.eventbus.EventBusManager;
import com.jj.comics.util.eventbus.events.FinishLoginActivityEvent;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Author ：le
 * Date ：2019-11-07 11:38
 * Description ：统一获取和填写验证码的页面
 */
@Route(path = RouterMap.COMIC_GETCODE_ACTIVITY)
public class GetCodeActivity extends BaseActivity<GetCodePresenter> implements GetCodeContract.View {

    @BindView(R2.id.tv_phone)
    TextView tv_phone;
    @BindView(R2.id.et_code)
    EditText et_code;
    @BindView(R2.id.tv_getCode)
    TextView tv_code;
    @BindView(R2.id.btn_login)
    ImageButton btn_login;

    private boolean is_first_login;//是否是第一次登录
    private boolean is_third_login;//是否是三方登录
    private String phoneNum;
    private CountDownTimer countDownTimer;

    public static void toActivity(boolean is_first_login, boolean is_third_login, String phoneNum, Context context) {
        ARouter.getInstance().build(RouterMap.COMIC_GETCODE_ACTIVITY)
                .withBoolean(Constants.IntentKey.LOGIN_FIRST, is_first_login)
                .withBoolean(Constants.IntentKey.LOGIN_THIRD, is_third_login)
                .withString(Constants.IntentKey.LOGIN_PHONE, phoneNum)
                .navigation(context);
    }


    @Override
    protected void initData(Bundle savedInstanceState) {
        is_first_login = getIntent().getBooleanExtra(Constants.IntentKey.LOGIN_FIRST, false);
        is_third_login = getIntent().getBooleanExtra(Constants.IntentKey.LOGIN_THIRD, false);
        phoneNum = getIntent().getStringExtra(Constants.IntentKey.LOGIN_PHONE);
        tv_phone.setText("+86 " + phoneNum.substring(0, 3) +" "+ phoneNum.substring(3, 7) +" "+ phoneNum.substring(7, 11));
        if (is_first_login) {
            btn_login.setImageResource(R.drawable.btn_regsiter);
        } else {
            btn_login.setImageResource(R.drawable.btn_login);
        }
        tv_code.setClickable(false);

        if (countDownTimer == null) {
            countDownTimer = new CountDownTimer(60 * 1000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    tv_code.setText(millisUntilFinished / 1000 + "s后重新发送");
                }

                @Override
                public void onFinish() {
                    tv_code.setClickable(true);
                    tv_code.setText("重新获取验证码");
                    tv_code.setSelected(false);
                }
            };
        }
        countDownTimer.start();
        tv_code.setClickable(false);
        tv_code.setSelected(true);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.comic_activity_getcode;
    }

    @Override
    public GetCodePresenter setPresenter() {
        return new GetCodePresenter();
    }

    @OnClick({R2.id.tv_getCode, R2.id.btn_login})
    public void onClick_getCode(View view) {
        int id = view.getId();
        if (id == R.id.tv_getCode) {//重新获取验证码
            //设置view不可点击
            tv_code.setClickable(false);
            tv_code.setSelected(true);
            if (countDownTimer == null) {
                countDownTimer = new CountDownTimer(60 * 1000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        tv_code.setText(millisUntilFinished / 1000 + "s后重新发送");
                    }

                    @Override
                    public void onFinish() {
                        tv_code.setClickable(true);
                        tv_code.setText("重新获取验证码");
                        tv_code.setSelected(false);
                    }
                };
            }
            countDownTimer.start();
            getP().getCode(phoneNum);
        } else if (id == R.id.btn_login) {//注册登录
            String code = et_code.getText().toString().trim();
            if (TextUtils.isEmpty(code)) {
                ToastUtil.showToastShort("请输入验证码");
            }
            if (is_third_login) {
                getP().bindPhone(phoneNum, code);
            }else{
                getP().phoneLogin(phoneNum,code, SharedPreManger.getInstance().getInvitecode());
            }

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    @Override
    public void setResultAndFinish() {
        //发送通知,让LoginActivity执行setResultAndFinish方法
        EventBusManager.sendFinishLoginActivityEvent(new FinishLoginActivityEvent());
        //如果是第一次登录，还需要跳转到选择性别页面
        if (is_first_login){
            GenderSelectActivity.toActivity(this);
        }
        finish();
    }
}
