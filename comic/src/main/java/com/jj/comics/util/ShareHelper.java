package com.jj.comics.util;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.bumptech.glide.request.RequestOptions;
import com.jj.base.BaseApplication;
import com.jj.base.imageloader.ILFactory;
import com.jj.base.log.LogUtil;
import com.jj.base.ui.BaseActivity;
import com.jj.base.utils.PackageUtil;
import com.jj.base.utils.toast.ToastUtil;
import com.jj.comics.R;
import com.jj.comics.common.constants.Constants;
import com.jj.comics.data.model.ShareMessageModel;
import com.jj.comics.util.eventbus.events.WxShareEvent;
import com.jj.comics.util.reporter.TaskReporter;
import com.sina.weibo.sdk.WbSdk;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.share.WbShareCallback;
import com.sina.weibo.sdk.share.WbShareHandler;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXTextObject;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import androidx.lifecycle.Lifecycle;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 第三方分享工具类
 */
public class ShareHelper {
    public static ShareHelper instance;
    private String qqUmengEventId;
    private String wxUmengEventId;

    /**
     * 禁止外部实例化
     */
    private ShareHelper() {
        EventBus.getDefault().register(this);
        //初始化微博
        WbSdk.install(BaseApplication.getApplication(), new AuthInfo(BaseApplication.getApplication(), Constants.WEIBO_APP_ID(), Constants.REDIRECT_URL, Constants.SCOPE));
    }

    public static ShareHelper getInstance() {
        if (instance == null) {
            instance = new ShareHelper();
        }
        return instance;
    }

    /**
     * 分享到微信
     * @link https://open.weixin.qq.com/cgi-bin/showdocument?action=dir_list&t=resource/res_list&verify=1&lang=zh_CN
     */
    public void shareToWechat(final BaseActivity activity, final ShareMessageModel shareMessageModel) {
        if (!isWxInstall()) {
            ToastUtil.showToastLong(activity.getString(R.string.comic_not_install_wx));
            return;
        }
        activity.showProgress();
        getDownObservable(shareMessageModel.getShareImgUrl())
                .as(AutoDispose.<File>autoDisposable(AndroidLifecycleScopeProvider.from(activity, Lifecycle.Event.ON_DESTROY)))
                .subscribe(new Observer<File>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(File file) {
//                        WXWebpageObject webpage = new WXWebpageObject();
//                        webpage.webpageUrl = shareMessageModel.getShareUrl();
//                        //用 WXWebpageObject 对象初始化一个 WXMediaMessage 对象
//                        WXMediaMessage msg = new WXMediaMessage(webpage);
//                        msg.title = shareMessageModel.getShareTitle();
//                        msg.description = shareMessageModel.getShareContent();
//                        msg.thumbData = bitmap2Bytes(url2Bitmap(file, shareMessageModel));
//                        //构造一个Req
//                        SendMessageToWX.Req req = new SendMessageToWX.Req();
//                        req.transaction = buildTransaction("webpage");
//                        req.message = msg;
//                        req.scene = SendMessageToWX.Req.WXSceneSession;//场景值，分享到回话，也就是分享到微信
//                        req.userOpenId = Constants.WX_APP_ID;
//                        wxUmengEventId = "WeChatFriendShare";
//                        //调用api接口，发送数据到微信
//                        TencentHelper.getWxApi().sendReq(req);

                        //初始化一个 WXTextObject 对象，填写分享的文本内容
                        String text = "【"+shareMessageModel.getShareTitle()+"】"+shareMessageModel.getShareUrl()+"复制这段链接，到系统浏览器打开";
                        WXTextObject textObj = new WXTextObject();
                        textObj.text = text;

                        //用 WXTextObject 对象初始化一个 WXMediaMessage 对象
                        WXMediaMessage msg = new WXMediaMessage();
                        msg.mediaObject = textObj;
                        msg.description = "分享好友";

                        SendMessageToWX.Req req = new SendMessageToWX.Req();
                        req.transaction = buildTransaction("text");
                        req.message = msg;
                        req.scene = SendMessageToWX.Req.WXSceneSession;
                        wxUmengEventId = "WeChatFriendShare";
                        //调用api接口，发送数据到微信
                        TencentHelper.getWxApi(Constants.WX_APP_ID_LOGIN()).sendReq(req);
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtil.showToastShort("请求失败，请重试");
                        activity.hideProgress();
                    }

                    @Override
                    public void onComplete() {
                        activity.hideProgress();
                    }
                });
    }

    /**
     * 分享到朋友圈
     */
    public void shareToWechatMoment(final BaseActivity activity, final ShareMessageModel shareMessageModel) {
        if (!isWxInstall()) {
            ToastUtil.showToastLong("请安装微信客户端后分享");
            return;
        }
        activity.showProgress();
        getDownObservable(shareMessageModel.getShareImgUrl())
                .as(AutoDispose.<File>autoDisposable(AndroidLifecycleScopeProvider.from(activity, Lifecycle.Event.ON_DESTROY)))
                .subscribe(new Observer<File>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(File file) {
//                        WXWebpageObject webpage = new WXWebpageObject();
//                        webpage.webpageUrl = shareMessageModel.getShareUrl();
//                        //用 WXWebpageObject 对象初始化一个 WXMediaMessage 对象
//                        WXMediaMessage msg = new WXMediaMessage(webpage);
//                        msg.title = shareMessageModel.getShareTitle();
//                        msg.description = shareMessageModel.getShareContent();
//                        msg.thumbData = bitmap2Bytes(url2Bitmap(file, shareMessageModel));
//                        //构造一个Req
//                        SendMessageToWX.Req req = new SendMessageToWX.Req();
//                        req.transaction = buildTransaction("webpage");
//                        req.message = msg;
//                        req.scene = SendMessageToWX.Req.WXSceneTimeline;//场景值，分享到回话，也就是分享到微信
//                        req.userOpenId = Constants.WX_APP_ID;
//                        wxUmengEventId = "WeChatMomentsShare";
//                        //调用api接口，发送数据到微信
//                        TencentHelper.getWxApi().sendReq(req);

                        //初始化一个 WXTextObject 对象，填写分享的文本内容
                        String text = "【"+shareMessageModel.getShareTitle()+"】"+shareMessageModel.getShareUrl()+"复制这段链接，到系统浏览器打开";
                        WXTextObject textObj = new WXTextObject();
                        textObj.text = text;

                        //用 WXTextObject 对象初始化一个 WXMediaMessage 对象
                        WXMediaMessage msg = new WXMediaMessage();
                        msg.mediaObject = textObj;
                        msg.description = "分享朋友圈";

                        SendMessageToWX.Req req = new SendMessageToWX.Req();
                        req.transaction = buildTransaction("text");
                        req.message = msg;
                        req.scene = SendMessageToWX.Req.WXSceneTimeline;
                        wxUmengEventId = "WeChatMomentsShare";
                        //调用api接口，发送数据到微信
                        TencentHelper.getWxApi(Constants.WX_APP_ID_LOGIN()).sendReq(req);
                    }

                    @Override
                    public void onError(Throwable t) {
                        ToastUtil.showToastShort("请求失败，请重试");
                        activity.hideProgress();
                    }

                    @Override
                    public void onComplete() {
                        activity.hideProgress();
                    }
                });
    }

    /**
     * 分享到QQ
     * @link http://wiki.connect.qq.com/
     * @warn: 如果出现图片不显示的问题一般都是图片太大了
     */
    public void shareToQQ(final BaseActivity activity, final ShareMessageModel shareMessageModel) {
        if (!isQQInstall()) {
            ToastUtil.showToastLong("请安装QQ客户端后分享");
            return;
        }
        activity.showProgress();
        getDownObservable(shareMessageModel.getShareImgUrl())
                .as(AutoDispose.<File>autoDisposable(AndroidLifecycleScopeProvider.from(activity, Lifecycle.Event.ON_DESTROY)))
                .subscribe(new Observer<File>() {

                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(File file) {
                        Bundle params = new Bundle();
                        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
                        //分享的标题。注：PARAM_TITLE、PARAM_IMAGE_URL、PARAM_    SUMMARY不能全为空，最少必须有一个是有值的。
                        params.putString(QQShare.SHARE_TO_QQ_TITLE, shareMessageModel.getShareTitle());
                        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, shareMessageModel.getShareContent());
                        //这条分享消息被好友点击后的跳转URL。
                        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, shareMessageModel.getShareUrl());
                        //分享的图片URL
                        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, url2path(file, shareMessageModel));
                        //手Q客户端顶部，替换“返回”按钮文字，如果为空，用返回代替
                        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, PackageUtil.getAppName(activity));
                        //        params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, "其他附加功能");
                        qqUmengEventId = "QQShare";
                        TencentHelper.getTencent().shareToQQ(activity, params, new BaseUiListener());
                    }

                    @Override
                    public void onError(Throwable t) {
                        ToastUtil.showToastShort("请求失败，请重试");
                        activity.hideProgress();
                    }

                    @Override
                    public void onComplete() {
                        activity.hideProgress();
                    }
                });
    }

    /**
     * 分享到QQ空间
     */
    public void shareToQQzone(final BaseActivity activity, final ShareMessageModel shareMessageModel) {
        if (!isQQInstall()) {
            ToastUtil.showToastLong("请安装QQ客户端后分享");
            return;
        }
        activity.showProgress();
        getDownObservable(shareMessageModel.getShareImgUrl())
                .as(AutoDispose.<File>autoDisposable(AndroidLifecycleScopeProvider.from(activity, Lifecycle.Event.ON_DESTROY)))
                .subscribe(new Observer<File>() {

                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(File file) {
                        Bundle params = new Bundle();
                        params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
                        params.putString(QzoneShare.SHARE_TO_QQ_TITLE, shareMessageModel.getShareTitle());
                        params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, shareMessageModel.getShareContent());
                        params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, shareMessageModel.getShareUrl());
                        ArrayList<String> imageUrls = new ArrayList<>();
                        imageUrls.add(url2path(file, shareMessageModel));
                        params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imageUrls);
                        params.putInt(QzoneShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN);
                        qqUmengEventId = "QZoneShare";
                        TencentHelper.getTencent().shareToQzone(activity, params, new BaseUiListener());
                    }

                    @Override
                    public void onError(Throwable t) {
                        ToastUtil.showToastShort("请求失败，请重试");
                        activity.hideProgress();
                    }

                    @Override
                    public void onComplete() {
                        activity.hideProgress();
                    }
                });
    }

    private WbShareHandler wbShareHandler;
    private Bitmap thumbBmp;

    /**
     * 分享到新浪微博
     */
    public void shareToSina(final BaseActivity activity, final ShareMessageModel shareMessageModel) {
        if (!isWbInstall()) {
            ToastUtil.showToastLong("请安装微博客户端后分享");
            return;
        }
        activity.showProgress();
        getDownObservable(shareMessageModel.getShareImgUrl())
                .as(AutoDispose.<File>autoDisposable(AndroidLifecycleScopeProvider.from(activity, Lifecycle.Event.ON_DESTROY)))
                .subscribe(new Observer<File>() {

                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(File file) {
                        TextObject textObject = new TextObject();
                        textObject.title = shareMessageModel.getShareTitle();
                        textObject.text = shareMessageModel.getShareContent() + shareMessageModel.getShareUrl();
                        ImageObject imageObject = new ImageObject();
                        thumbBmp = url2Bitmap(file, shareMessageModel); //设置缩略图。 注意：最终压缩过的缩略图大小 不得超过 32kb。
                        imageObject.setImageObject(thumbBmp);

                        WeiboMultiMessage message = new WeiboMultiMessage();
                        message.textObject = textObject;
                        message.imageObject = imageObject;
                        if (wbShareHandler == null) {
                            wbShareHandler = new WbShareHandler(activity);
                        }
                        wbShareHandler.registerApp();
                        wbShareHandler.shareMessage(message, true);
                    }

                    @Override
                    public void onError(Throwable t) {
                        ToastUtil.showToastShort("请求失败，请重试");
                        activity.hideProgress();
                    }

                    @Override
                    public void onComplete() {
                        activity.hideProgress();
                    }
                });
    }

    /**
     * 获取下载图片的Observable对象
     *
     * @param imageUrl
     * @return
     */
    private Observable<File> getDownObservable(final String imageUrl) {
        return Observable.create(new ObservableOnSubscribe<File>() {
            @Override
            public void subscribe(ObservableEmitter<File> emitter) throws Exception {
                File file = ILFactory.getLoader().down(BaseApplication.getApplication(), imageUrl, new RequestOptions().skipMemoryCache(true));
                emitter.onNext(file);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 处理微信分享回调
     * 目前分享success和cancel事件已被官网统一成success,详见以下链接
     *
     * @link https://mp.weixin.qq.com/cgi-bin/announce?action=getannouncement&announce_id=11526372695t90Dn&version=&lang=zh_CN&token=
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void wxShareResult(WxShareEvent wxShareEvent) {
        String result;
        switch (wxShareEvent.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                result = "分享成功";
                //上传分享事件到友盟
                MobclickAgent.onEvent(BaseApplication.getApplication(), wxUmengEventId);
                TaskReporter.reportShare();
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                result = "分享取消";
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                result = "分享被拒绝";
                break;
            default:
                result = "分享返回";
                break;
        }
        //wx分享操作结束后及时回收bitmap
        recycleBitmap(thumbBmp);
        ToastUtil.showToastShort(result);
    }

    /**
     * 处理QQ分享回调
     * 单单在这里写回调是无法成功的，还需要在调用的activity的onActivityResult方法里面调用Tencent.onActivityResultData方法,我已经写在了baseactivity里面了
     *
     * @link https://www.oschina.net/code/snippet_266004_50547
     */
    private class BaseUiListener implements IUiListener {
        @Override
        public void onComplete(Object response) {
            //V2.0版本，参数类型由JSONObject 改成了Object,具体类型参考api文档
            doComplete((JSONObject) response);
        }

        protected void doComplete(JSONObject values) {
            //分享成功
            if (shareTemFile != null && shareTemFile.exists()) shareTemFile.delete();
            ToastUtil.showToastShort("分享成功");
            TaskReporter.reportShare();
            //上传友盟事件到友盟
            MobclickAgent.onEvent(BaseApplication.getApplication(), qqUmengEventId);
        }

        @Override
        public void onError(UiError e) {
            //在这里处理错误信息
            if (shareTemFile != null && shareTemFile.exists()) shareTemFile.delete();
            if (e.errorDetail == null) {
                LogUtil.e("QQShareError", "unknown error");
            } else {
                LogUtil.e("QQShareError", e.errorDetail);
            }
            ToastUtil.showToastShort("分享失败");
        }

        @Override
        public void onCancel() {
            //分享被取消
            if (shareTemFile != null && shareTemFile.exists()) shareTemFile.delete();
            ToastUtil.showToastShort("分享取消");
        }
    }

    /**
     * 处理微博分享回调
     *
     * @param data
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void shareSinaResult(Intent data) {
        if (ShareHelper.getInstance().wbShareHandler != null) {
            ShareHelper.getInstance().wbShareHandler.doResultIntent(data, new WbShareCallback() {
                @Override
                public void onWbShareSuccess() {
                    ToastUtil.showToastShort("分享成功");
                    TaskReporter.reportShare();
                    //上传分享时间到友盟
                    MobclickAgent.onEvent(BaseApplication.getApplication(), "WeiboShare");
                    recycleBitmap(thumbBmp);
                }

                @Override
                public void onWbShareCancel() {
                    ToastUtil.showToastShort("分享取消");
                    recycleBitmap(thumbBmp);
                }

                @Override
                public void onWbShareFail() {
                    ToastUtil.showToastShort("分享失败");
                    recycleBitmap(thumbBmp);
                }
            });
        }
    }

    private byte[] bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = null;
        byte[] bytes = null;
        try {
            baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
            int options = 100;
            //微信限制缩略图32K以内
            while (baos.toByteArray().length > 32 * 1024 && options != 10) {
                baos.reset(); //清空output
                bm.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到output中
                options -= 10;
            }
            bytes = baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            recycleBitmap(bm);
            if (baos != null) {
                try {
                    baos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bytes;
    }

    private String buildTransaction(String transaction) {
        return transaction + UUID.randomUUID();
    }

    private File shareTemFile;

    private String bitmapToFile(Bitmap bitmap) {
//        File file = new File(BaseApplication.getApplication().getFilesDir(), "share.png");//设置保存路径
        /**
         * 这里需要注意QQ分享平台的图片保存本地如果是私有目录，测试是无法显示的，放sdcard里面就好了
         * 而且分享的文件名不能重复，分享弹出的预览界面显示的图片会根据图片名称做缓存
         * @ps: 这里面坑是真的多
         */
        shareTemFile = new File(BaseApplication.getApplication().getCacheDir().getPath(),"share_" + System.currentTimeMillis() + ".png");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(shareTemFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            return shareTemFile.getAbsolutePath();
        } catch (IOException e) {
            ToastUtil.showToastShort("操作失败，请重试");
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    private Bitmap url2Bitmap(File file, ShareMessageModel shareMessageModel) {
        Bitmap bitmap;
        if (file != null && file.exists()) {//存在就去转换并压缩
            bitmap = BitmapFactory.decodeFile(file.getPath());
            byte[] bytes = bitmap2Bytes(bitmap);
            bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        } else {//不存在就使用默认的drawable目录下的图片shareMessageModel.getShareImgRes的值
            bitmap = BitmapFactory.decodeResource(BaseApplication.getApplication().getResources(), shareMessageModel.getShareImgRes()); //设置缩略图。 注意：最终压缩过的缩略图大小 不得超过 32kb。
            byte[] bytes = bitmap2Bytes(bitmap);
            bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        }
        return bitmap;
    }

    private String url2path(File file, ShareMessageModel shareMessageModel) {
        Bitmap bitmap;
        String path;
        if (file != null && file.exists()) {//存在就去转换并压缩
            bitmap = BitmapFactory.decodeFile(file.getPath());
            byte[] bytes = bitmap2Bytes(bitmap);
            bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            path = bitmapToFile(bitmap);
        } else {//不存在就使用默认的drawable目录下的图片shareMessageModel.getShareImgRes的值
            bitmap = BitmapFactory.decodeResource(BaseApplication.getApplication().getResources(), shareMessageModel.getShareImgRes()); //设置缩略图。 注意：最终压缩过的缩略图大小 不得超过 32kb。
            byte[] bytes = bitmap2Bytes(bitmap);
            bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            path = bitmapToFile(bitmap);
        }
        recycleBitmap(bitmap);
        return path;
    }

    private void recycleBitmap(Bitmap bitmap) {
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
            bitmap = null;
        }
    }

    private boolean isWxInstall() {
        return checkInstall("com.tencent.mm");
    }

    private boolean isWbInstall() {
        return checkInstall("com.sina.weibo");
    }

    private boolean isQQInstall() {
        return checkInstall("com.tencent.mobileqq") || checkInstall("com.tencent.qqlite");
    }

    private boolean checkInstall(String s) {
        final PackageManager packageManager = BaseApplication.getApplication().getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equalsIgnoreCase(s)) {
                    return true;
                }
            }
        }

        return false;
    }

}
