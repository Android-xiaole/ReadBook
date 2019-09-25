package com.jj.comics.ui.mine.userinfo;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.jj.base.net.NetError;
import com.jj.base.ui.BaseActivity;
import com.jj.base.utils.RouterMap;
import com.jj.base.utils.toast.ToastUtil;
import com.jj.comics.R;
import com.jj.comics.R2;
import com.jj.comics.data.model.UserInfo;
import com.jj.comics.util.LoginHelper;
import com.jj.comics.util.eventbus.EventBusManager;
import com.jj.comics.util.eventbus.events.UpdateUserInfoEvent;
import com.jj.comics.widget.comic.toolbar.ComicToolBar;

import butterknife.BindView;
import butterknife.OnClick;

@Route(path = RouterMap.COMIC_EDITSEX_ACTIVITY)
public class EditSexActivity extends BaseActivity<EditInfoPresenter> implements EditInfoContract.IEditInfoView {
    @BindView(R2.id.check_male)
    CheckBox maleCheck;
    @BindView(R2.id.check_female)
    CheckBox femaleCheck;
    @BindView(R2.id.check_unknown)
    CheckBox unknown;

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
                if (maleCheck.isChecked()) {
                    getP().updateUserInfo(null, null, 1);
                }

                if (femaleCheck.isChecked()) {
                    getP().updateUserInfo(null, null, 2);
                }

                if (unknown.isChecked()) {
                    getP().updateUserInfo(null, null, 0);
                }
            }
        });
//        初始化性别
        UserInfo userInfo = LoginHelper.getOnLineUser();
        if (userInfo != null) {
            if (userInfo.getSex() == 1) {
                maleCheck.setChecked(true);
                femaleCheck.setChecked(false);
                unknown.setChecked(false);
            } else if (userInfo.getSex() == 2) {
                maleCheck.setChecked(false);
                femaleCheck.setChecked(true);
                unknown.setChecked(false);
            } else {
                unknown.setChecked(true);
                maleCheck.setChecked(false);
                femaleCheck.setChecked(false);
            }
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.comic_activity_sex;
    }

    @Override
    public EditInfoPresenter setPresenter() {
        return new EditInfoPresenter();
    }


    @OnClick({R2.id.check_male, R2.id.check_female, R2.id.check_unknown})
    void onClick(View view) {
        if (view.getId() == R.id.check_male) {
            maleCheck.setChecked(true);
            femaleCheck.setChecked(false);
            unknown.setChecked(false);
        } else if (view.getId() == R.id.check_female) {
            femaleCheck.setChecked(true);
            maleCheck.setChecked(false);
            unknown.setChecked(false);
        } else if (view.getId() == R.id.check_unknown) {
            femaleCheck.setChecked(false);
            maleCheck.setChecked(false);
            unknown.setChecked(true);
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
