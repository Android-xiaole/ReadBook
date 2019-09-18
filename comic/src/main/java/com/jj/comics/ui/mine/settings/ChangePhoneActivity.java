package com.jj.comics.ui.mine.settings;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.jj.base.ui.BaseActivity;
import com.jj.base.utils.RouterMap;
import com.jj.base.utils.toast.ToastUtil;
import com.jj.comics.R;
import com.jj.comics.R2;
import com.jj.comics.data.model.UserInfo;
import com.jj.comics.util.RegularUtil;
import com.jj.comics.util.eventbus.EventBusManager;
import com.jj.comics.util.eventbus.events.UpdateUserInfoEvent;
import com.jj.comics.widget.comic.toolbar.ComicToolBar;

import butterknife.BindView;
import butterknife.OnClick;


@Route(path = RouterMap.COMIC_CHANGE_PHONE_ACTIVITY)
public class ChangePhoneActivity extends BaseActivity<ChangePhonePresenter> implements ChangePhoneContract.IBindPhoneView, TextWatcher {
    @BindView(R2.id.comic_login_number)
    EditText mPhoneNumber;
    @BindView(R2.id.comic_login_pwd)
    EditText mPassWord;
    @BindView(R2.id.comic_login_code)
    TextView mCode;

    @Override
    public void initData(Bundle savedInstanceState) {
        mPhoneNumber.addTextChangedListener(this);
        ComicToolBar toolBar = findViewById(R.id.bind_phone_bar);
        toolBar.addChildClickListener(new ComicToolBar.OnComicToolBarListener() {
            @Override
            public void onComicToolBarLeftIconClick(View childView) {
                finish();
            }

            @Override
            public void onComicToolBarRightIconClick(View childView) {

            }

            @Override
            public void onComicToolBarRightTextClick(View childView) {
                String pn = mPhoneNumber.getText().toString().trim();
                String psw = mPassWord.getText().toString().trim();
                if (TextUtils.isEmpty(pn) || TextUtils.isEmpty(psw)) {
                    showToastShort(getString(R.string.comic_phone_code_error_remind));
                } else {
                    getP().alterPhone(pn, psw);
                }
            }
        });
    }

    @Override
    public int getLayoutId() {
        return R.layout.comic_activity_change_phone;
    }

    @Override
    public ChangePhonePresenter setPresenter() {
        return new ChangePhonePresenter();
    }

    @OnClick({ R2.id.comic_login_code})
    public void dealOnClick(View view) {
        if (view.getId() == R.id.comic_login_code) {
            view.setEnabled(false);
            showProgress();
            getP().getCode(mPhoneNumber.getText().toString().trim());
        }
    }

    @Override
    public void alterSuccess(UserInfo userInfo) {
        ToastUtil.showToastShort("修改成功");
        EventBusManager.sendUpdateUserInfoEvent(new UpdateUserInfoEvent(userInfo));
        setResult(RESULT_OK);
        finish();
    }

    public void setCuntDownText(String text, boolean enable) {
        mCode.setText(text);
        if (enable) {
            mCode.setTextAppearance(this, R.style.comic_code);
            onTextChanged();
        } else {
            mCode.setTextAppearance(this, R.style.comic_code_getting);
            mCode.setEnabled(enable);
        }
    }

    public void onTextChanged() {
        if (!getP().isDown) {
            mCode.setEnabled(RegularUtil.isMobile(mPhoneNumber.getText().toString().trim()));
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        onTextChanged();
    }

    @Override
    protected void onDestroy() {
        if (mPhoneNumber != null) {
            mPhoneNumber.removeTextChangedListener(this);
        }
        super.onDestroy();
    }
}
