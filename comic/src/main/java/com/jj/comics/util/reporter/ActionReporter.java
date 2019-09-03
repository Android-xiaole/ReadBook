package com.jj.comics.util.reporter;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.jj.base.log.LogUtil;
import com.jj.base.utils.PackageUtil;
import com.jj.comics.common.constants.Constants;
import com.jj.comics.data.biz.behavior.BehaviorRepository;
import com.jj.comics.data.model.ResponseModel;
import com.jj.comics.data.model.UserInfo;
import com.jj.comics.common.net.RequestBodyBuilder;
import com.jj.comics.util.LoginHelper;

import java.util.Map;
import java.util.UUID;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.RequestBody;

public class ActionReporter {
    public static final String LOGIN = "loginByVerifyCode";//登录
    public static final String REGISTER = "register";//注册
    public static final String READ_COMIC = "read_comic";//阅读漫画
    public static final String SUBSCRIBE_COMIC = "subscribe_comic";//订阅动画
    public static final String PRE_PAY = "pre_pay";//请求支付
    public static final String PAY = "pay";//进入支付页
    public static final String CHANGE_AMOUNT = "change_amount";//切换支付金额

    public static final String TEMP_USER_ID;

    static {
        TEMP_USER_ID = UUID.randomUUID().toString();
    }

    public static void reportAction(final Event event, final String actionObject, final String actionObject2
            , final Map<String, String> params) {
        UserInfo loginUser = LoginHelper.getOnLineUser();
//        if (params == null) {
//            if (loginUser == null) {
//                MobclickAgent.onEvent(activity, event.umKey);
//            } else {
//                MobclickAgent.onEvent(activity, event.umKey, loginUser.getUserId() + "");
//            }
//        } else {
//            if (loginUser != null && !params.containsKey("user_id"))
//                params.put("user_id", loginUser.getUserId() + "");
//            MobclickAgent.onEvent(activity, event.umKey, params);
//        }
        if (Constants.OPEN_STATISTICS)
//            ComicApi.getApi().reportAction(createActionBody(loginUser, event.actionId, actionObject, actionObject2))
            BehaviorRepository.getInstance().reportAction(loginUser,TEMP_USER_ID, event.actionId, actionObject, actionObject2)
//                    .compose(activity.<ResponseModel>bindUntilEvent(ActivityEvent.DESTROY))
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .subscribe(new Observer<ResponseModel>() {

                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(ResponseModel responseModel) {
                            LogUtil.e("上传用户行为成功： " + new Gson().toJson(responseModel));
                        }

                        @Override
                        public void onError(Throwable t) {
                            LogUtil.e("上传用户行为失败： " + t.toString());
                        }

                        @Override
                        public void onComplete() {
                            LogUtil.e("上传用户行为完成： " + event.actionId + "   " + actionObject + "   " + actionObject2);
                        }
                    });

    }

    private static RequestBody createActionBody(UserInfo loginUser, int actionId, String actionObject, String actionObject2) {
        JsonObject userElement = new JsonObject();
        if (loginUser == null) userElement.addProperty("tempUserId", TEMP_USER_ID);
        else userElement.addProperty("userId", loginUser.getUid() + "");
        userElement.addProperty("imsi", PackageUtil.getImsi1());
        userElement.addProperty("imei", PackageUtil.getImei1());

        JsonObject actionElement = new JsonObject();
        actionElement.addProperty("currentActionId", actionId);
        if (actionObject != null) actionElement.addProperty("currentActionObject", actionObject);
        if (actionObject2 != null) actionElement.addProperty("currentActionObject2", actionObject2);
        actionElement.addProperty("currentActionIdentifying", UUID.randomUUID().toString());
        actionElement.addProperty("lastActionId", -1000);
//        actionElement.addProperty("lastActionObject", null);
//        actionElement.addProperty("lastActionObject2", null);
        actionElement.addProperty("lastActionIdentifying", UUID.randomUUID().toString());


        RequestBodyBuilder action = new RequestBodyBuilder()
//                .addProperty("flag", null)
                .addProperty("user", userElement)
                .addProperty("action", actionElement);

        return action.build();
    }

    public enum Event {
        LOGIN("loginByVerifyCode", 210),
        REGISTER("register", 220),
        READ_COMIC("read_comic", 230),
        SUBSCRIBE_COMIC("subscribe_comic", 240),
        PRE_PAY("pre_pay", 260),
        PAY("pay", 270),
        CHANGE_AMOUNT("change_amount", 280),
        LOGIN_START_APP("login_start_app", 290),
        APP_SHARE("app_share", 300),
        CONTENT_SHARE("content_share", 310),
        VIP("vip", 320),
        START_APP("start_app", 330),
        BIG_IMG("big_img", 340),
        PAY_RESULT("uid_pay_result", 360);

        private String umKey;
        private int actionId;

        private Event(String umKey, int actionId) {
            this.umKey = umKey;
            this.actionId = actionId;
        }

    }
}
