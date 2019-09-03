package com.jj.comics.ui.mine.settings;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.jj.base.dialog.CustomFragmentDialog;
import com.jj.base.ui.BaseActivity;
import com.jj.base.utils.RouterMap;
import com.jj.comics.R;
import com.jj.comics.R2;
import com.jj.comics.common.constants.RequestCode;
import com.jj.comics.util.eventbus.events.UpdateUserInfoEvent;
import com.jj.comics.util.eventbus.EventBusManager;
import com.jj.comics.util.LoginHelper;

import butterknife.BindView;
import butterknife.OnClick;

@Route(path = RouterMap.COMIC_COUPON_CODE_ACTIVITY)
public class CouponCodeActivity extends BaseActivity<CouponCodePresenter> implements CouponCodeContract.ICouponCodeView{
    @BindView(R2.id.et_code)
    EditText mEtCode;

    @Override
    public void initData(Bundle savedInstanceState) {
    }

    @Override
    public int getLayoutId() {
        return R.layout.comic_activity_coupon_code;
    }

    @Override
    public CouponCodePresenter setPresenter() {
        return new CouponCodePresenter();
    }

    @OnClick({R2.id.coupon_redeem})
    void OnClick(View view) {
        if (view.getId() == R.id.coupon_redeem) {
            if (LoginHelper.getOnLineUser() == null) {
                ARouter.getInstance().build(RouterMap.COMIC_LOGIN_ACTIVITY).navigation();
            } else {
                String trim = mEtCode.getText().toString().trim();
                if (TextUtils.isEmpty(trim)) {
                    showToastShort(getString(R.string.comic_coupon_code_error_remind));
                } else {
                    getP().goldExchange(LoginHelper.getOnLineUser().getUid(), trim);
                }
            }
        }

    }

    private CustomFragmentDialog customFragmentDialog;
    @Override
    public void onSuccess() {
        if (customFragmentDialog == null) customFragmentDialog = new CustomFragmentDialog();
        customFragmentDialog.show(this,getSupportFragmentManager(),R.layout.comic_dialog_coupon_bg);
        TextView confirm = customFragmentDialog.getDialog().findViewById(R.id.dialog_coupon_confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBusManager.sendUpdateUserInfoEvent(new UpdateUserInfoEvent());
                customFragmentDialog.dismiss();
                setResult(RequestCode.Coupon_REQUEST_CODE);
                finish();
            }
        });
//        final CustomDialog customDialog = new CustomDialog(CouponCodeActivity.this, R.style.comic_CustomDialog, R.layout.comic_dialog_coupon_bg);
//        customDialog.show();
//        TextView confirm = customDialog.findViewById(R.id.dialog_coupon_confirm);
//        confirm.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                EventBus.getDefault().post(new EventModel());
//                EventBusManager.sendUpdateUserInfoEvent(new UpdateUserInfoEvent());
//                customDialog.dismiss();
//                setResult(RequestCode.Coupon_REQUEST_CODE);
//                finish();
//            }
//        });
    }


}
