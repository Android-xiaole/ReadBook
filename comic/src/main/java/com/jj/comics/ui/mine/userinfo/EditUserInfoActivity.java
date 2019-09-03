package com.jj.comics.ui.mine.userinfo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.gyf.barlibrary.ImmersionBar;
import com.jj.base.imageloader.ILFactory;
import com.jj.base.ui.BaseActivity;
import com.jj.base.utils.FileUtil;
import com.jj.base.utils.MyEdittextFilter;
import com.jj.base.utils.PackageUtil;
import com.jj.base.utils.RouterMap;
import com.jj.base.utils.Utils;
import com.jj.comics.R;
import com.jj.comics.R2;
import com.jj.comics.common.constants.RequestCode;
import com.jj.comics.data.model.UserInfo;
import com.jj.comics.util.EditTextFilterUtil;
import com.jj.comics.util.LoginHelper;
import com.jj.comics.util.IntentUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.OnClick;

@Route(path = RouterMap.COMIC_EDIT_USER_INFO_ACTIVITY)
public class EditUserInfoActivity extends BaseActivity<EditUserInfoPresenter> implements EditUserInfoContract.IEditUserInfoView{
    @BindView(R2.id.btn_select_sex)
    LinearLayout mBtnSecectSex;
    @BindView(R2.id.btn_select_birthday)
    LinearLayout mBtnSecectBirthday;
    @BindView(R2.id.btn_select_area)
    LinearLayout mBtnSecectArea;
    @BindView(R2.id.tv_edit_userinfo_save)
    TextView mTvSave;
    @BindView(R2.id.tv_edit_userinfo_cancel)
    TextView mTvCancel;
    @BindView(R2.id.tv_select_sex)
    TextView mTvSelectSex;
    @BindView(R2.id.tv_select_birthday)
    TextView mTvSelectBirthday;
    @BindView(R2.id.tv_select_area)
    TextView mTvSelectArea;
    @BindView(R2.id.et_edit_user_sign)
    EditText mEtSign;
    @BindView(R2.id.et_edit_user_nickname)
    EditText mEtNickName;
    @BindView(R2.id.iv_edit_userinfo_camera)
    ImageView mUserInfoCamera;
    @BindView(R2.id.iv_head_image)
    ImageView mUserHeadImage;
    @BindView(R2.id.rl_container)
    RelativeLayout mRlContainer;
    //保存路径
    String filePath;
    private String fileName;
    //user info
    private String mNickName;
    private int mGender = -1;
    private String mSign;
    private String mBirth;
    private String mCountry;
    private String mProvince;
    private String mCity;
    private String mAvatarUrl;


    @Override
    public void initData(Bundle savedInstanceState) {
        UserInfo loginUser = LoginHelper.getOnLineUser();
        if (loginUser != null) {
            if (TextUtils.isEmpty(loginUser.getAvatar())) {
                ILFactory.getLoader().loadResource(mUserHeadImage, R.drawable.icon_user_avatar_default,
                        new RequestOptions().transforms(new CenterCrop(),
                                new CircleCrop()));
            } else {
                mAvatarUrl = loginUser.getAvatar();
                ILFactory.getLoader().loadNet(mUserHeadImage, mAvatarUrl,
                        new RequestOptions().transforms(new CenterCrop(),
                                new CircleCrop()).error(R.drawable.img_loading)
                                .placeholder(R.drawable.img_loading));
            }

            mNickName = loginUser.getNickname();
            if (!TextUtils.isEmpty(mNickName)) {
                if (mNickName.length() >= 15) {
                    mNickName = mNickName.substring(0, 15);
                }
                mEtNickName.setText(mNickName);
                mEtNickName.setSelection(mNickName.length());
            }

            int gender = loginUser.getSex();
            mGender = gender;
            String genderStr = "";
            if (gender == -1) {
                genderStr = getString(R.string.comic_no_sex);
            } else if (gender == 0) {
                genderStr = getString(R.string.comic_sex_women);
            } else if (gender == 1) {
                genderStr = getString(R.string.comic_sex_man);
            }
            mTvSelectSex.setText(genderStr);

//            mSign = loginUser.getCustomSignature();
//            if (!TextUtils.isEmpty(mSign)) {
//                if (mSign.length() >= 30) {
//                    mSign = mSign.substring(0, 30);
//                }
//                mEtSign.setText(mSign);
//                mEtSign.setSelection(mSign.length());
//            }
//
//            if (StringUtils.isNotBlank(loginUser.getBirth())) {
//                mBirth = loginUser.getBirth();
//            } else {
//                mBirth = getString(R.string.comic_select_text);
//            }
//            mTvSelectBirthday.setText(mBirth);
//
//            mCountry = getString(R.string.comic_china);
//            mProvince = loginUser.getProvince();
//            mCity = loginUser.getCity();
//            if (StringUtils.isNotBlank(mProvince) || StringUtils.isNotBlank(mCity)) {
//                mTvSelectArea.setText(mProvince + " " + mCity);
//            } else {
//                mTvSelectArea.setText(getString(R.string.comic_select_text));
//            }

        }


        filePath = Environment.getExternalStorageDirectory().getAbsoluteFile().getAbsolutePath()
                + File.separator + PackageUtil.getAppName(this) + File.separator;

        getP().initAreaJsonData(EditUserInfoActivity.this);

        mEtNickName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    mTvSave.setTextColor(getBaseContext().getResources().getColor(R.color.base_color_666666));
                    showToastShort(getString(R.string.comic_nickname_not_null));
                } else if (s.length() >= 14) {
                    showToastShort(getString(R.string.comic_nickname_limit_remind));
                    mTvSave.setTextColor(getBaseContext().getResources().getColor(R.color.comic_ff9933));
                    mTvSave.setClickable(true);
                } else {
                    mTvSave.setTextColor(getBaseContext().getResources().getColor(R.color.comic_ff9933));
                    mTvSave.setClickable(true);
                }
                mNickName = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

//        mEtNickName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                if (actionId == EditorInfo.IME_ACTION_SEND ||
//                        (event != null && event.getAction() == KeyEvent.KEYCODE_ENTER)) {
//                    return true;
//                }
//                return false;
//            }
//        });

        mEtSign.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() >= 29) {
                    showToastShort(getString(R.string.comic_sign_limit_remind));
                }
                mSign = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mEtNickName.setFilters(new InputFilter[]{EditTextFilterUtil.if_emoji
                , EditTextFilterUtil.if_cnennum, new MyEdittextFilter(15)});
        mEtSign.setFilters(new InputFilter[]{EditTextFilterUtil.if_emoji
                , EditTextFilterUtil.if_cnennum, new MyEdittextFilter(30)});
    }

    @Override
    public int getLayoutId() {
        return R.layout.comic_activity_edit_userinfo;
    }

    @Override
    public EditUserInfoPresenter setPresenter() {
        return new EditUserInfoPresenter();
    }

    @OnClick({R2.id.btn_select_sex, R2.id.btn_select_birthday, R2.id.btn_select_area,
            R2.id.iv_edit_userinfo_camera, R2.id.tv_edit_userinfo_save, R2.id.tv_edit_userinfo_cancel})
    void dealClick(View view) {
        if (view.getId() == R.id.btn_select_sex) {
            getP().showSexPickerView(new EditUserInfoPresenter.OnSexSelectLisenter() {
                @Override
                public void onSelect(int index, String sex) {
//                    LogUtil.e("index"+index+"---"+"sex"+sex);
                    if (index == 0) {//就不告诉你
                        mGender = -1;
                    } else if (index == 1) {//男
                        mGender = 1;
                    } else if (index == 2) {//女
                        mGender = 0;
                    }
                    mTvSelectSex.setText(sex);

                }
            });
        } else if (view.getId() == R.id.btn_select_birthday) {
            getP().showDataPickerView(new OnTimeSelectListener() {
                @Override
                public void onTimeSelect(Date date, View v) {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    String format = simpleDateFormat.format(date);
                    mTvSelectBirthday.setText(format + "");
                    mBirth = format;
                }
            });
        } else if (view.getId() == R.id.btn_select_area) {
            getP().showAreaPickerView(new EditUserInfoPresenter.OnAreaSelectLisenter() {
                @Override
                public void onSelect(String province, String city, String area) {
                    mTvSelectArea.setText(province + " " + city + " " + area + "");
                    mCountry = getString(R.string.comic_china);
                    mProvince = province;
                    mCity = city;
                }
            });
        } else if (view.getId() == R.id.iv_edit_userinfo_camera) {
            IntentUtils.openPic(this);
        } else if (view.getId() == R.id.tv_edit_userinfo_save) {
            if (TextUtils.isEmpty(mNickName)) {
                showToastShort(getString(R.string.comic_nickname_not_null));
                mEtNickName.requestFocus();
                return;
            }
            UserInfo userInfo = LoginHelper.getOnLineUser();
            userInfo.setNickname(mNickName);
            userInfo.setSex(mGender);
//            userInfo.setCustomSignature(mSign);
//            userInfo.setBirth(mBirth);
//            userInfo.setCountry(mCountry);
//            userInfo.setProvince(mProvince);
//            userInfo.setCity(mCity);
            userInfo.setAvatar(mAvatarUrl);
            getP().updateUserInfo(userInfo);
        } else if (view.getId() == R.id.tv_edit_userinfo_cancel) {
            finish();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case RequestCode.OPEN_PIC_REQUEST_CODE:
                    String realFilePath = Utils.uriToFile(this, data.getData()).getAbsolutePath();
                    fileName = FileUtil.getFileName(realFilePath);
                    IntentUtils.cropImageUri(this, data.getData(), filePath + fileName);
                    break;
                case RequestCode.CROP_IMG_REQUEST_CODE:
                    File file = new File(filePath + fileName);
                    if (FileUtil.readFile(file) > 0) {
                        ILFactory.getLoader().loadFile(mUserHeadImage, file, new RequestOptions().transforms(new CenterCrop(), new CircleCrop()));
                        UserInfo loginUser = LoginHelper.getOnLineUser();
                        if (loginUser != null) getP().uploadImage(loginUser, file,filePath);
                    }
                    break;
            }
        }
    }

    public void onUpdateUserInfoComplete(UserInfo user, String msg) {
        if (user != null) {
            //suc
//            EventBus.getDefault().post(user);
//            EventBusManager.sendUpdateUserInfoEvent(user);
            finish();
        }
        showToastShort(msg);
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    public void onImgUploadComplete(String imgUrl) {
        mAvatarUrl = imgUrl;
    }

    @Override
    protected void initImmersionBar() {
        ImmersionBar.with(this)
                .reset()
                .fitsSystemWindows(true)
                .keyboardEnable(true)
                .statusBarDarkFont(false, 0.2f)
                .statusBarColor(R.color.base_color_ffd850)
//                .navigationBarColor(R.color.base_color_ffffff)
                .init();
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        //Finger touch screen event
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            // get current focus,Generally it is EditText
            View view = getCurrentFocus();
            if (isShouldHideSoftKeyBoard(view, ev)) {
                hideSoftKeyBoard(view.getWindowToken());
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    private boolean isShouldHideSoftKeyBoard(View view, MotionEvent event) {
        if (view != null && (view instanceof EditText)) {
            int[] l = {0, 0};
            view.getLocationInWindow(l);
            int left = l[0], top = l[1], bottom = top + view.getHeight(), right = left
                    + view.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // If click the EditText event ,ignore it
                return false;
            } else {
                return true;
            }
        }
        // if the focus is EditText,ignore it;
        return false;
    }

    private void hideSoftKeyBoard(IBinder token) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(token,
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }


}
