package com.jj.comics.ui.mine.userinfo;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.jj.base.net.NetError;
import com.jj.base.ui.BaseActivity;
import com.jj.base.utils.RouterMap;
import com.jj.base.utils.toast.ToastUtil;
import com.jj.comics.R;
import com.jj.comics.R2;
import com.jj.comics.common.callback.TextWatcherWithClear;
import com.jj.comics.data.model.UserInfo;
import com.jj.comics.util.LoginHelper;
import com.jj.comics.util.eventbus.EventBusManager;
import com.jj.comics.util.eventbus.events.UpdateUserInfoEvent;
import com.jj.comics.widget.comic.toolbar.ComicToolBar;

import butterknife.BindView;
import butterknife.OnClick;

@Route(path = RouterMap.COMIC_EDITNICKNAME_ACTIVITY)
public class EditNickNameActivity extends BaseActivity<EditInfoPresenter> implements EditInfoContract.IEditInfoView {
    @BindView(R2.id.edit_nick_name)
    EditText editText;

    @BindView(R2.id.alter_nick_name)
    LinearLayout linearLayout;

    @Override
    protected void initData(Bundle savedInstanceState) {
        ComicToolBar toolBar = findViewById(R.id.nickname_toolbar);
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
                String nickName = editText.getText().toString();
                if (!nickName.isEmpty()) {
                    getP().updateUserInfo(null, nickName, -1);
                }
            }
        });
        UserInfo userInfo = LoginHelper.getOnLineUser();
        if (userInfo != null) {
            editText.setText(userInfo.getNickname());
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.comic_activity_nickname;
    }

    @Override
    public EditInfoPresenter setPresenter() {
        return new EditInfoPresenter();
    }

    @OnClick({R2.id.edit_nick_name_tv})
    void onClick(View view) {
        if (view.getId() == R.id.edit_nick_name_tv) {
            linearLayout.setFocusable(true);
            linearLayout.setFocusableInTouchMode(true);
            editText.requestFocus();
            editText.setText("");
            InputMethodManager inputManager =
                    (InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.showSoftInput(editText, 0);
        }
    }

    @Override
    public void onSuccess(UserInfo userInfo) {
        ToastUtil.showToastShort("修改成功");
        EventBusManager.sendUpdateUserInfoEvent(new UpdateUserInfoEvent(userInfo));
        setResult(RESULT_OK);
        finish();
    }
}
