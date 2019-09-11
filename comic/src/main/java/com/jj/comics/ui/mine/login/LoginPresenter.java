package com.jj.comics.ui.mine.login;

import android.content.Intent;
import android.os.SystemClock;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;

import com.jj.base.BaseApplication;
import com.jj.base.log.LogUtil;
import com.jj.base.mvp.BasePresenter;
import com.jj.base.mvp.BaseRepository;
import com.jj.base.net.ApiSubscriber2;
import com.jj.base.net.NetError;
import com.jj.base.ui.BaseActivity;
import com.jj.base.utils.SharedPref;
import com.jj.base.utils.toast.ToastUtil;
import com.jj.comics.R;
import com.jj.comics.common.constants.Constants;
import com.jj.comics.data.biz.user.UserRepository;
import com.jj.comics.data.model.LoginByCodeResponse;
import com.jj.comics.data.model.ResponseModel;
import com.jj.comics.data.model.UidLoginResponse;
import com.jj.comics.data.model.UserInfo;
import com.jj.comics.util.RegularUtil;
import com.jj.comics.util.TencentHelper;
import com.jj.comics.util.eventbus.events.WxLoginEvent;
import com.sina.weibo.sdk.WbSdk;
import com.sina.weibo.sdk.auth.AccessTokenKeeper;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WbAuthListener;
import com.sina.weibo.sdk.auth.WbConnectErrorMessage;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.stat.StatMultiAccount;
import com.tencent.stat.StatService;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import androidx.annotation.Nullable;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

public class LoginPresenter extends BasePresenter<BaseRepository, LoginContract.ILogoinView> implements LoginContract.ILoginPresenter, TextWatcher {
    private static final int SECOND = 60;
    boolean isDown = false;
    private IUiListener mQQListener;

    private AuthInfo mAuthInfo;
    private SsoHandler mSsoHandler;
    private MyWbAuthLis mWbAuthListener;


    private class LoginApiSubscriber extends ApiSubscriber2<UserInfo> {

        @Override
        protected void onFail(NetError error) {
            if (getV()!=null){
                getV().showToastShort(error.getMessage());
                getV().hideProgress();
            }
        }

        @Override
        public void onNext(UserInfo UserInfo) {
            if (getV()!=null){
                getV().hideProgress();
                getV().setResultAndFinish();
            }
        }
    }

    public void uidLogin(String uid){
        UserRepository.getInstance().uidLogin(uid)
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Function<UidLoginResponse, ObservableSource<UserInfo>>() {
                    @Override
                    public ObservableSource<UserInfo> apply(UidLoginResponse uidLoginResponse) throws Exception {
                        if (uidLoginResponse.getData()!=null&&uidLoginResponse.getData().getUser_info()!=null){
                            UserInfo user_info = uidLoginResponse.getData().getUser_info();
                            //腾讯统计登录用户统计
                            tecentLoginStat(StatMultiAccount.AccountType.CUSTOM,
                                    user_info.getUid() + "");
                            MobclickAgent.onEvent(BaseApplication.getApplication(),
                                    Constants.UMEventId.UID_LOGIN);
                            SharedPref.getInstance().putString(Constants.SharedPrefKey.TOKEN, uidLoginResponse.getData().getBearer_token());
                            return UserRepository.getInstance().saveUser(user_info);
                        }else{
                            return Observable.error(new NetError("code:"+uidLoginResponse.getCode()+"\n用户信息不存在",uidLoginResponse.getCode()));
                        }
                    }
                })
                .as(bindLifecycle())
                .subscribe(new LoginApiSubscriber());
    }

    @Override
    public void getVerifyCode(String mobile) {
        UserRepository.getInstance().getSecurityCode(getV().getClass().getName(), mobile)
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Function<ResponseModel, ObservableSource<Long>>() {
                    @Override
                    public ObservableSource<Long> apply(ResponseModel responseModel) throws Exception {
                        getV().showToastShort("验证码获取成功");
                        getV().hideProgress();
                        if (isDown) return Observable.empty();
                        isDown = true;
                        return Observable.intervalRange(0, SECOND + 1, 0, 1, TimeUnit.SECONDS)
                                .observeOn(AndroidSchedulers.mainThread())
//                                .compose(getV().<Long>bindUntilEvent(ActivityEvent.DESTROY))
                                ;
                    }
                }, new Function<Throwable, ObservableSource<Long>>() {
                    @Override
                    public ObservableSource<Long> apply(Throwable throwable) throws Exception {
                        getV().showToastShort("验证码获取失败");
                        getV().hideProgress();
                        if (isDown) return Observable.empty();
                        isDown = true;
                        return Observable.intervalRange(0, SECOND + 1, 0, 1, TimeUnit.SECONDS)
                                .observeOn(AndroidSchedulers.mainThread())
//                                .compose(getV().<Long>bindUntilEvent(ActivityEvent.DESTROY))
                                ;
                    }
                }, new Callable<ObservableSource<Long>>() {
                    @Override
                    public ObservableSource<Long> call() throws Exception {
                        return Observable.empty();
                    }
                })
                .doOnNext(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        getV().setCuntDownText(String.format("重发验证码 %ss", String.valueOf(SECOND - aLong)), false);
                    }
                })
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {
                        isDown = false;
                        getV().setCuntDownText("获取验证码", true);
                    }
                })
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        isDown = false;
                        LogUtil.e(throwable.getMessage());
                    }
                })
                .as(this.<Long>bindLifecycle())
                .subscribe();
    }

    @Override
    public void loginByVerifyCode(boolean isCheck, String phone, String psw) {
        if (!isCheck) {
            getV().showToastShort("请同意漫画服务协议");
            return;
        }
        if (TextUtils.isEmpty(phone)){
            getV().showToastShort("请输入手机号");
            return;
        }
        if (!RegularUtil.isMobile(phone)) {
            getV().showToastShort("请输入正确的手机号");
            return;
        }
//        if (psw == null || psw.length() != 6) {
//            getV().showToastShort("请输入6位验证码");
//            return;
//        }
        getV().showProgress();
        UserRepository.getInstance().loginBySecurityCode(isCheck, phone, psw)
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Function<LoginByCodeResponse, ObservableSource<UserInfo>>() {
                    @Override
                    public ObservableSource<UserInfo> apply(LoginByCodeResponse responseModel) throws Exception {
                        if (responseModel.getData() != null && responseModel.getData().getUser_info() != null) {
                            UserInfo user_info = responseModel.getData().getUser_info();
                            //腾讯统计登录用户统计
                            tecentLoginStat(StatMultiAccount.AccountType.PHONE_NO,
                                    user_info.getMobile() + "");
                            MobclickAgent.onEvent(BaseApplication.getApplication(), Constants.UMEventId.PHONE_LOGIN);
                            SharedPref.getInstance().putString(Constants.SharedPrefKey.TOKEN, responseModel.getData().getBearer_token());
                            return UserRepository.getInstance().saveUser(responseModel.getData().getUser_info());
                        }
                        return Observable.error(NetError.noDataError());
                    }
                })
                .as(this.<UserInfo>bindLifecycle())
                .subscribe(new LoginApiSubscriber());
    }

    @Override
    public void qqLogin(boolean isCheck, BaseActivity loginActivity) {
        if (!isCheck) {
            getV().showToastShort("请同意漫画服务协议");
            return;
        }
        Tencent mTencent = TencentHelper.getTencent();
        if (!mTencent.isQQInstalled(BaseApplication.getApplication())) {
            getV().showToastShort("您还未安装QQ客户端");
            return;
        }
        if (!mTencent.isSessionValid()) {
            getV().showProgress();
            mTencent.login(loginActivity, "all", getQQListener());
        }
    }

    private IUiListener getQQListener() {
        if (mQQListener == null)
            mQQListener = new IUiListener() {
                @Override
                public void onComplete(Object o) {
                    /*
                    这个监听里面使用V需要判空的原因是，当activity长期置于后台被回收时候（虽然数据被保存下来），但onDestory里面会销毁V
                    所以当再次回调回来的时候发现原来的V为null
                     */
                    if (getV()!=null)getV().showProgress();
                    /**
                     * {
                     "ret":0,
                     "pay_token":"xxxxxxxxxxxxxxxx",
                     "pf":"openmobile_android",
                     "expires_in":"7776000",
                     "openid":"xxxxxxxxxxxxxxxxxxx",
                     "pfkey":"xxxxxxxxxxxxxxxxxxx",
                     "msg":"sucess",
                     "access_token":"xxxxxxxxxxxxxxxxxxxxx"
                     }
                     */
                    Log.d("GGGGGGG", "GGGGGGG" + SystemClock.currentThreadTimeMillis() + "---" + Thread.currentThread().getId());
                    JSONObject jsonObject = (JSONObject) o;
                    try {
                        MobclickAgent.onEvent(BaseApplication.getApplication(), Constants.UMEventId.QQ_LOGIN);
                        UserRepository.getInstance().qqLogin(jsonObject.getString(
                                "openid"), jsonObject.getString(
                                "access_token"))
                                .observeOn(AndroidSchedulers.mainThread())
                                .flatMap(new Function<LoginByCodeResponse, ObservableSource<UserInfo>>() {
                                    @Override
                                    public ObservableSource<UserInfo> apply(LoginByCodeResponse responseModel) throws Exception {
                                        UserInfo user_info = responseModel.getData().getUser_info();
                                        //腾讯统计登录用户统计
                                        tecentLoginStat(StatMultiAccount.AccountType.OPEN_QQ,
                                                user_info.getUid() + "");
                                        MobclickAgent.onEvent(BaseApplication.getApplication(),
                                                Constants.UMEventId.QQ_LOGIN);
                                        SharedPref.getInstance().putString(Constants.SharedPrefKey.TOKEN, responseModel.getData().getBearer_token());

                                        return UserRepository.getInstance().saveUser(user_info);
                                    }
                                }).subscribe(new LoginApiSubscriber());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(UiError uiError) {
                    if (getV()!=null)getV().hideProgress();
                    LogUtil.e("QQ登录错误" + uiError.errorCode + "  " + uiError.errorMessage + "  " + uiError.errorDetail);
                }

                @Override
                public void onCancel() {
                    if (getV()!=null)getV().hideProgress();
                    LogUtil.e("QQ登录取消");
                }
            };
        return mQQListener;
    }

    @Override
    public void wbLogin(boolean isCheck, BaseActivity activity) {
        if (!isCheck) {
            getV().showToastShort("请同意漫画服务协议");
            return;
        }
        getV().showProgress();
        createAuthInfo();
        if (mSsoHandler == null) mSsoHandler = new SsoHandler(activity);
        mSsoHandler.authorize(getWbAuthListener());
    }

    private AuthInfo createAuthInfo() {
        if (mAuthInfo == null) {
            mAuthInfo = new AuthInfo(BaseApplication.getApplication(), Constants.WEIBO_APP_ID(),
                    Constants.REDIRECT_URL,
                    Constants.SCOPE);
            WbSdk.install(BaseApplication.getApplication(), mAuthInfo);
        }
        return mAuthInfo;
    }

    private class MyWbAuthLis implements WbAuthListener {

        @Override
        public void onSuccess(Oauth2AccessToken oauth2AccessToken) {
            if (oauth2AccessToken.isSessionValid()) {
                AccessTokenKeeper.writeAccessToken(BaseApplication.getApplication(), oauth2AccessToken);
                //上传微博登录事件
                MobclickAgent.onEvent(BaseApplication.getApplication(), Constants.UMEventId.WB_LOGIN);
                UserRepository.getInstance()
                        .wbLogin(oauth2AccessToken.getToken(), oauth2AccessToken.getUid())
                        .observeOn(AndroidSchedulers.mainThread())
                        .flatMap(new Function<LoginByCodeResponse, ObservableSource<UserInfo>>() {
                            @Override
                            public ObservableSource<UserInfo> apply(LoginByCodeResponse responseModel) {
                                UserInfo user_info = responseModel.getData().getUser_info();
                                //腾讯统计登录用户统计
                                tecentLoginStat(StatMultiAccount.AccountType.OPEN_WEIBO,
                                        user_info.getUid() + "");

                                MobclickAgent.onEvent(BaseApplication.getApplication(), Constants.UMEventId.PHONE_LOGIN);
                                SharedPref.getInstance().putString(Constants.SharedPrefKey.TOKEN, responseModel.getData().getBearer_token());

                                return UserRepository.getInstance().saveUser(user_info);
                            }
                        }).subscribe(new LoginApiSubscriber());
            } else {
                // 以下几种情况，您会收到 Code：
                // 1. 当您未在平台上注册的应用程序的包名与签名时；
                // 2. 当您注册的应用程序包名与签名不正确时；
                // 3. 当您在平台上注册的包名和签名与您当前测试的应用的包名和签名不匹配时。
                getV().hideProgress();
                String code = oauth2AccessToken.getBundle().getString("code");
                String message = "授权失败";
                if (!TextUtils.isEmpty(code)) {
                    message = message + "\nObtained the code: " + code;
                }
                getV().showToastShort(message);
            }
        }

        @Override
        public void cancel() {
            getV().hideProgress();
            LogUtil.e("微博登录取消");
        }

        @Override
        public void onFailure(WbConnectErrorMessage wbConnectErrorMessage) {
            getV().hideProgress();
            LogUtil.e("微博登录错误" + wbConnectErrorMessage.getErrorCode()
                    + "  " + wbConnectErrorMessage.getErrorMessage() + "  ");
        }
    }

    private void tecentLoginStat(StatMultiAccount.AccountType accountType, String value) {
        // 登陆时调用
        StatMultiAccount account = new StatMultiAccount(accountType, value);
        long time = System.currentTimeMillis() / 1000;
        // 登陆时间，单秒为秒
        account.setLastTimeSec(time);
        // 过期时间
        account.setExpireTimeSec(time + 24 * 60);
        StatService.reportMultiAccount((BaseActivity) getV(), account);
    }

    private WbAuthListener getWbAuthListener() {
        if (mWbAuthListener == null)
            mWbAuthListener = new MyWbAuthLis();

        return mWbAuthListener;
    }

    @Override
    public void wxLogin(boolean isCheck) {
        if (!isCheck) {
            getV().showToastShort("请同意漫画服务协议");
            return;
        }
        IWXAPI wxApi = TencentHelper.getWxApi(Constants.WX_APP_ID_LOGIN());
        if (!wxApi.isWXAppInstalled()) {
            getV().showToastShort("您还未安装微信客户端");
            return;
        }
        getV().showProgress();
        SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "comic_wx_login";
        wxApi.sendReq(req);
    }

    public void getWxUserInfo(String code) {
        //微信获取openId后获取用户信息
        UserRepository.getInstance().wxLogin("0", code)
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Function<LoginByCodeResponse, ObservableSource<UserInfo>>() {
                    @Override
                    public ObservableSource<UserInfo> apply(LoginByCodeResponse responseModel) throws Exception {
                        UserInfo user_info = responseModel.getData().getUser_info();
                        //腾讯统计登录用户统计
                        tecentLoginStat(StatMultiAccount.AccountType.OPEN_WEIXIN,
                                user_info.getUid() + "");
                        MobclickAgent.onEvent(BaseApplication.getApplication(),
                                Constants.UMEventId.WX_LOGIN);
                        SharedPref.getInstance().putString(Constants.SharedPrefKey.TOKEN, responseModel.getData().getBearer_token());
                        return UserRepository.getInstance().saveUser(responseModel.getData().getUser_info());
                    }
                })
                .as(this.<UserInfo>bindLifecycle())
                .subscribe(new LoginApiSubscriber());
    }

    @Override
    public CharSequence getAgreementText() {
        String text = "同意《金桔小说服务协议》";
        SpannableString agreement = new SpannableString(text);
        agreement.setSpan(new ForegroundColorSpan(BaseApplication.getApplication().getResources().getColor(R.color.comic_ff6d50)), 2, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return agreement;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void dealWxLogin(WxLoginEvent wxLoginEvent) {
        switch (wxLoginEvent.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                getWxUserInfo(wxLoginEvent.code);
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                //用户拒绝授权
                getV().hideProgress();
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                //用户取消
                getV().hideProgress();
                break;
            default:
                getV().hideProgress();
                break;
        }

    }

    @Override
    public void afterTextChanged(Editable s) {
        getV().onTextChanged();
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        Tencent.onActivityResultData(requestCode, resultCode, data, getQQListener());
//        if (requestCode == com.tencent.connect.common.Constants.REQUEST_API) {
//            if (resultCode == getV().RESULT_OK) {
//                Tencent.handleResultData(data, getQQListener());
//            }
//        }
        if (mSsoHandler != null) mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
    }

    @Override
    public boolean useEventBus() {
        return true;
    }
}
