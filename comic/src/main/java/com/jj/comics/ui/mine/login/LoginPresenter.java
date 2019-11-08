package com.jj.comics.ui.mine.login;

import android.content.Intent;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;

import com.jj.base.BaseApplication;
import com.jj.base.log.LogUtil;
import com.jj.base.mvp.BasePresenter;
import com.jj.base.mvp.BaseRepository;
import com.jj.base.net.ApiSubscriber2;
import com.jj.base.net.NetError;
import com.jj.comics.R;
import com.jj.comics.common.constants.Constants;
import com.jj.comics.common.constants.LoginTypeEnum;
import com.jj.comics.data.biz.user.UserRepository;
import com.jj.comics.data.db.DaoHelper;
import com.jj.comics.data.model.GetCodeResponse;
import com.jj.comics.data.model.LoginResponse;
import com.jj.comics.data.model.ResponseModel;
import com.jj.comics.data.model.UserInfo;
import com.jj.comics.util.DateHelper;
import com.jj.comics.util.LoginHelper;
import com.jj.comics.util.SharedPreManger;
import com.jj.comics.util.TencentHelper;
import com.jj.comics.util.eventbus.events.WxLoginEvent;
import com.jj.comics.util.reporter.ActionReporter;
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
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.Nullable;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;

public class LoginPresenter extends BasePresenter<BaseRepository, LoginContract.ILogoinView> implements LoginContract.ILoginPresenter {

    private IUiListener mQQListener;
    private AuthInfo mAuthInfo;
    private SsoHandler mSsoHandler;
    private MyWbAuthLis mWbAuthListener;

    private DaoHelper daoHelper;


    private class LoginApiSubscriber extends ApiSubscriber2<UserInfo> {

        @Override
        protected void onFail(NetError error) {
            if (getV() != null) {
                getV().showToastShort(error.getMessage());
                getV().hideProgress();
            }
        }

        @Override
        public void onNext(UserInfo userInfo) {
            if (userInfo != null) {
                ActionReporter.reportAction(ActionReporter.Event.LOGIN, null, null, null);

                MobclickAgent.onProfileSignIn(userInfo.getLogin_type(), userInfo.getUid() + "");
            }

            if (getV() != null) {
                getV().hideProgress();
                getV().setResultAndFinish();
            }
        }
    }

    @Override
    public void getVerifyCode(String mobile) {
        UserRepository.getInstance().getSecurityCode(getV().getClass().getName(), mobile)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiSubscriber2<GetCodeResponse>() {
                    @Override
                    protected void onFail(NetError error) {
                        getV().showToastShort(error.getMessage());
                    }

                    @Override
                    public void onNext(GetCodeResponse responseModel) {
                        if (responseModel.getData()!=null){
                            getV().onGetCode(responseModel.getData().isIs_first());
                        }else{
                            getV().onGetCode(false);
                        }
                    }
                });
    }

    @Override
    public void qqLogin() {
        Tencent mTencent = TencentHelper.getTencent();
        if (!mTencent.isQQInstalled(BaseApplication.getApplication())) {
            getV().showToastShort("您还未安装QQ客户端");
            return;
        }
        if (!mTencent.isSessionValid()) {
            getV().showProgress();
            mTencent.login((LoginActivity) getV(), "all", getQQListener());
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
                    if (getV() != null) getV().showProgress();
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
                    JSONObject jsonObject = (JSONObject) o;
                    try {
                        UserRepository.getInstance().qqLogin(jsonObject.getString(
                                "openid"), jsonObject.getString(
                                "access_token"))
                                .observeOn(AndroidSchedulers.mainThread())
                                .flatMap(new Function<LoginResponse, ObservableSource<UserInfo>>() {
                                    @Override
                                    public ObservableSource<UserInfo> apply(LoginResponse responseModel) throws Exception {
                                        return dealLoginResponse(responseModel, LoginTypeEnum.QQ);
                                    }
                                }).subscribe(new LoginApiSubscriber());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(UiError uiError) {
                    if (getV() != null) getV().hideProgress();
                    LogUtil.e("QQ登录错误" + uiError.errorCode + "  " + uiError.errorMessage + "  " + uiError.errorDetail);
                }

                @Override
                public void onCancel() {
                    if (getV() != null) getV().hideProgress();
                    LogUtil.e("QQ登录取消");
                }
            };
        return mQQListener;
    }

    @Override
    public void wbLogin() {
        getV().showProgress();
        createAuthInfo();
        if (mSsoHandler == null) mSsoHandler = new SsoHandler((LoginActivity) getV());
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
                UserRepository.getInstance()
                        .wbLogin(oauth2AccessToken.getToken(), oauth2AccessToken.getUid())
                        .observeOn(AndroidSchedulers.mainThread())
                        .flatMap(new Function<LoginResponse, ObservableSource<UserInfo>>() {
                            @Override
                            public ObservableSource<UserInfo> apply(LoginResponse responseModel) {
                                return dealLoginResponse(responseModel, LoginTypeEnum.WB);
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


    private WbAuthListener getWbAuthListener() {
        if (mWbAuthListener == null)
            mWbAuthListener = new MyWbAuthLis();

        return mWbAuthListener;
    }

    @Override
    public void wxLogin() {
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
                .flatMap(new Function<LoginResponse, ObservableSource<UserInfo>>() {
                    @Override
                    public ObservableSource<UserInfo> apply(LoginResponse responseModel) throws Exception {
                        return dealLoginResponse(responseModel, LoginTypeEnum.WX);
//                        MobclickAgent.onEvent(BaseApplication.getApplication(),
//                                Constants.UMEventId.WX_LOGIN);
                    }
                })
                .as(this.<UserInfo>bindLifecycle())
                .subscribe(new LoginApiSubscriber());
    }

    /**
     * 处理三方登录返回的数据
     *
     * @return
     */
    private ObservableSource<UserInfo> dealLoginResponse(LoginResponse loginResponse, LoginTypeEnum loginTypeEnum) {
        LoginResponse.DataBean data = loginResponse.getData();
        if (data != null && data.getUser_info() != null) {
            UserInfo user_info = loginResponse.getData().getUser_info();
            //友盟账号统计
            MobclickAgent.onProfileSignIn(loginTypeEnum.name(),user_info.getUid() + "");
            user_info.setLogin_type(loginTypeEnum.name());
            //先记录当前本地当前用户登出时间
            if (daoHelper == null) daoHelper = new DaoHelper();
            String currentDate = DateHelper.getCurrentDate(Constants.DateFormat.YMDHMS);
            daoHelper.insertORupdateOnlineTimeData(0, null, currentDate);
            //保存token
            SharedPreManger.getInstance().saveToken(data.getBearer_token());
            //保存用户信息
            LoginHelper.updateUser(user_info);
            if (!data.isIs_first()) {//不是第一次登录，不需要跳转到绑定手机号的页面
                //数据向下传递
                return Observable.just(user_info);
            } else {//第一次登录，需要跳转到绑定手机号的页面
                BindPhoneActivity.toBindPhoneActivity(user_info,(LoginActivity) getV());
                return Observable.empty();
            }
        }
        return Observable.error(NetError.noDataError());
    }

    @Override
    public CharSequence getAgreementText() {
        String text = "登录注册即表示同意《金桔小说服务协议》";
        SpannableString agreement = new SpannableString(text);
        agreement.setSpan(new ForegroundColorSpan(BaseApplication.getApplication().getResources().getColor(R.color.comic_ff6d50)), 9, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return agreement;
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
