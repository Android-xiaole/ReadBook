package com.jj.comics.ui.mine.userinfo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.jj.base.imageloader.ILFactory;
import com.jj.base.net.NetError;
import com.jj.base.ui.BaseActivity;
import com.jj.base.utils.FileUtil;
import com.jj.base.utils.PackageUtil;
import com.jj.base.utils.RouterMap;
import com.jj.base.utils.Utils;
import com.jj.base.utils.toast.ToastUtil;
import com.jj.comics.R;
import com.jj.comics.R2;
import com.jj.comics.common.constants.Constants;
import com.jj.comics.common.constants.RequestCode;
import com.jj.comics.data.model.OSSResponse;
import com.jj.comics.data.model.UserInfo;
import com.jj.comics.util.DateHelper;
import com.jj.comics.util.IntentUtils;
import com.jj.comics.util.LoginHelper;
import com.jj.comics.util.SignUtil;
import com.jj.comics.util.eventbus.EventBusManager;
import com.jj.comics.util.eventbus.events.LogoutEvent;
import com.jj.comics.util.eventbus.events.UpdateUserInfoEvent;
import com.jj.comics.widget.UserItemView;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

@Route(path = RouterMap.COMIC_USERINFO_ACTIVITY)
public class UserInfoActivity extends BaseActivity<UserInfoPresenter> implements UserInfoContract.IUserInfoView {

    //保存路径
    String filePath;
    private String fileName;

    @BindView(R2.id.user_info_head_img)
    ImageView headImg;
    @BindView(R2.id.user_nickname)
    UserItemView nickName;
    @BindView(R2.id.user_sex)
    UserItemView mSex;
    @BindView(R2.id.user_phone)
    UserItemView phone;
    @BindView(R2.id.user_setting)
    UserItemView setting;
    private OSSResponse.DataBean mOssConfig;

    @Override
    protected void onStart() {
        super.onStart();
        getP().getOSSConfig();
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        filePath = Environment.getExternalStorageDirectory().getAbsoluteFile().getAbsolutePath()
                + File.separator + PackageUtil.getAppName(this) + File.separator;
        updateUserinfo();
    }

    private void updateUserinfo(){
        UserInfo userInfo = LoginHelper.getOnLineUser();
        if (userInfo != null) {
            //设置头像
            if (TextUtils.isEmpty(userInfo.getAvatar())) {
                ILFactory.getLoader().loadResource(headImg, R.drawable.bg_avatar,
                        new RequestOptions().transforms(new CenterCrop(), new CircleCrop()));
            } else {
                ILFactory.getLoader().loadNet(headImg, userInfo.getAvatar(),
                        new RequestOptions().transforms(new CenterCrop(), new CircleCrop()).error(R.drawable.img_loading)
                                .placeholder(R.drawable.img_loading));
            }
            //设置昵称
            if (userInfo.getNickname() != null) {
                nickName.setRight_title(userInfo.getNickname());
            }
            //设置性别
            String sex = userInfo.getSex() == 1 ? "男" : "女";
            mSex.setRight_title(sex);

            //设置手机号
            phone.setRight_title(userInfo.getMobile() + "");
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.comic_activity_user_info;
    }

    @Override
    public UserInfoPresenter setPresenter() {
        return new UserInfoPresenter();
    }

    @Override
    public void onImgUploadComplete(String headImgUrl) {
        if (TextUtils.isEmpty(headImgUrl)) {
            ILFactory.getLoader().loadResource(headImg, R.drawable.bg_avatar,
                    new RequestOptions().transforms(new CenterCrop(), new CircleCrop()));
        } else {
            ILFactory.getLoader().loadNet(headImg, headImgUrl,
                    new RequestOptions().transforms(new CenterCrop(), new CircleCrop()).error(R.drawable.img_loading)
                            .placeholder(R.drawable.img_loading));
        }
    }

    @Override
    public void onLoadFail(NetError error) {
        ToastUtil.showToastShort(error.getMessage());
    }

    @Override
    public void onLoadConfig(OSSResponse.DataBean ossConfig) {
        mOssConfig = ossConfig;
    }


    @OnClick({R2.id.user_head, R2.id.user_nickname, R2.id.user_sex, R2.id.user_phone, R2.id.user_setting})
    void onClick(View view) {
        if (view.getId() == R.id.user_head) {
            AndPermission.with(this)
                    .runtime()
                    .permission(Permission.READ_EXTERNAL_STORAGE)
                    .onGranted(new Action<List<String>>() {
                        @Override
                        public void onAction(List<String> data) {
                            IntentUtils.openPic(UserInfoActivity.this);//编辑头像
                        }
                    })
                    .onDenied(new Action<List<String>>() {
                        @Override
                        public void onAction(List<String> data) {
                            ToastUtil.showToastLong("您拒绝了存储权限，无法使用此功能");
                        }
                    }).start();
        } else if (view.getId() == R.id.user_nickname) {
            ARouter.getInstance().build(RouterMap.COMIC_EDITNICKNAME_ACTIVITY).navigation(UserInfoActivity.this,RequestCode.USERINFO_REQUEST_CODE);
        } else if (view.getId() == R.id.user_sex) {
            ARouter.getInstance().build(RouterMap.COMIC_EDITSEX_ACTIVITY).navigation(UserInfoActivity.this,RequestCode.USERINFO_REQUEST_CODE);
        } else if (view.getId() == R.id.user_phone) {
            ARouter.getInstance().build(RouterMap.COMIC_CHANGE_PHONE_ACTIVITY).navigation(UserInfoActivity.this,RequestCode.USERINFO_REQUEST_CODE);
        } else if (view.getId() == R.id.user_setting) {
            ARouter.getInstance().build(RouterMap.COMIC_SETTING_ACTIVITY).navigation(UserInfoActivity.this);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            updateUserinfo();
        }
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case RequestCode.OPEN_PIC_REQUEST_CODE:
                    String realFilePath = Utils.uriToFile(this, data.getData()).getAbsolutePath();
                    fileName = FileUtil.getFileName(realFilePath);
                    IntentUtils.cropImageUri(this, data.getData(), filePath + fileName);
                    break;
                case RequestCode.CROP_IMG_REQUEST_CODE:
                    String path = filePath + fileName;
                    File file = new File(filePath + fileName);
                    if (FileUtil.readFile(file) > 0) {
                        ossUploadFile(path);
                        ILFactory.getLoader().loadFile(headImg, file, new RequestOptions().transforms(new CenterCrop(), new CircleCrop()));
                    }
                    break;
            }
        }
    }

    /**
     * oss 文件上传
     *
     * @param path
     */
    private void ossUploadFile(String path) {
        OSSCredentialProvider credentialProvider = new OSSStsTokenCredentialProvider(mOssConfig.getAccessKeyId(), mOssConfig.getAccessKeySecret(), mOssConfig.getSecurityToken());
        OSS oss = new OSSClient(this, Constants.OSS_ENDPOINT, credentialProvider);
        String date = DateHelper.getCurrentDate(Constants.DateFormat.YMD);
//                        long uid = LoginHelper.getOnLineUser().getUid();
        long uid = 4864320110116003l;// TODO: 2019/9/10  需要修改id 和 test
        String OssPath = "test/" + date + "/" + SignUtil.md5("" + uid) + "_" + System.currentTimeMillis() + ".jpg";
        PutObjectRequest put = new PutObjectRequest(Constants.BUCKET_NAME, OssPath, path);
        // 异步上传时可以设置进度回调
//        put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
//            @Override
//            public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
//                if (currentSize == totalSize) {
//                }
//                Log.d("PutObject", "当前大小: " + currentSize + " 总大小: " + totalSize);
//            }
//        });
        oss.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                Log.d("PutObject", "UploadSuccess");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String filepath = Constants.BUCKET_URL + request.getObjectKey();
                        getP().updateUserInfo(filepath, null, -1);
                    }
                });
            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                // 请求异常。
                if (clientExcepion != null) {
                    // 本地异常，如网络异常等。
                    clientExcepion.printStackTrace();
                }
                if (serviceException != null) {
                    ToastUtil.showToastShort(serviceException.getRawMessage());
                    // 服务异常。
                    Log.e("ErrorCode", serviceException.getErrorCode());
                    Log.e("RequestId", serviceException.getRequestId());
                    Log.e("HostId", serviceException.getHostId());
                    Log.e("RawMessage", serviceException.getRawMessage());
                }
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void logoutEvent(LogoutEvent event) {
        finish();
    }

    @Override
    public boolean useEventBus() {
        return true;
    }
}
