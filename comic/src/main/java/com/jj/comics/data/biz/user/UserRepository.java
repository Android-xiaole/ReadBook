package com.jj.comics.data.biz.user;


import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.jj.base.BaseApplication;
import com.jj.base.net.ComicApiImpl;
import com.jj.base.net.RetryFunction2;
import com.jj.base.utils.SharedPref;
import com.jj.comics.common.constants.Constants;
import com.jj.comics.common.net.ComicApi;
import com.jj.comics.common.net.RequestBodyBuilder;
import com.jj.comics.data.model.BookModel;
import com.jj.comics.data.model.CollectionResponse;
import com.jj.comics.data.model.CommentListResponse;
import com.jj.comics.data.model.CommonStatusResponse;
import com.jj.comics.data.model.ExpenseSumRecordsResponse;
import com.jj.comics.data.model.FeedbackListResponse;
import com.jj.comics.data.model.FeedbackStatusModel;
import com.jj.comics.data.model.HeadImg;
import com.jj.comics.data.model.LoginByCodeResponse;
import com.jj.comics.data.model.PayCenterInfoResponse;
import com.jj.comics.data.model.PayInfoResponse;
import com.jj.comics.data.model.RecharegeRecordsResponse;
import com.jj.comics.data.model.RechargeCoinResponse;
import com.jj.comics.data.model.ResponseModel;
import com.jj.comics.data.model.RewardHistoryResponse;
import com.jj.comics.data.model.RichDataResponse;
import com.jj.comics.data.model.RichResponse;
import com.jj.comics.data.model.UidLoginResponse;
import com.jj.comics.data.model.UserInfo;
import com.jj.comics.data.model.UserInfoResponse;
import com.jj.comics.util.LoginHelper;
import com.jj.comics.util.reporter.ActionReporter;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class UserRepository implements UserDataSource {

    private static UserRepository INSTANCE;

    private UserRepository() {
    }

    public static UserRepository getInstance() {
        if (INSTANCE == null) INSTANCE = new UserRepository();
        return INSTANCE;
    }

    @Override
    public Observable<ResponseModel> getSecurityCode(String activityName, String mobile) {
        Observable<ResponseModel> compose = ComicApi.getApi().getSecurityCode(new RequestBodyBuilder()
                .addProperty(Constants.RequestBodyKey.LOGIN_PHONE_NUMBER, mobile)
                .build())
                .retryWhen(new RetryFunction2(activityName))
                .compose(ComicApiImpl.<ResponseModel>getApiTransformer2())
                .subscribeOn(Schedulers.io());
        return compose;
    }

    @Override
    public Observable<LoginByCodeResponse> loginBySecurityCode(boolean isCheck, String phone, String psw) {
        RequestBody requestBody = new RequestBodyBuilder()
                .addProperty(Constants.RequestBodyKey.LOGIN_PHONE_NUMBER, phone)
                .addProperty(Constants.RequestBodyKey.LOGIN_CODE, psw)
                .build();
        Observable<LoginByCodeResponse> compose = ComicApi.getApi().loginBySecurityCode(requestBody)
                .retryWhen(new RetryFunction2())
                .compose(ComicApiImpl.<LoginByCodeResponse>getApiTransformer2())
                .subscribeOn(Schedulers.io());
        return compose;
    }


    @Override
    public Observable<UserInfo> saveUser(final UserInfo user) {
        return Observable.create(new ObservableOnSubscribe<UserInfo>() {
            @Override
            public void subscribe(ObservableEmitter<UserInfo> emitter) throws Exception {
                LoginHelper.updateUser(user);
                String userId = user.getUid() + "";
                //当用户使用自有账号登录时，统计：
                MobclickAgent.onProfileSignIn(userId);
                ActionReporter.reportAction(ActionReporter.Event.LOGIN, null, null, null);
                emitter.onNext(user);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<UserInfoResponse> getUserInfo() {
        Observable<UserInfoResponse> compose = ComicApi.getApi().getUserInfo()
                .retryWhen(new RetryFunction2())
                .compose(ComicApiImpl.<UserInfoResponse>getApiTransformer2())
                .subscribeOn(Schedulers.io());
        return compose;
    }

    @Override
    public Observable<PayInfoResponse> getUserPayInfo() {
        Observable<PayInfoResponse> compose = ComicApi.getApi().getUserPayInfo()
                .retryWhen(new RetryFunction2())
                .compose(ComicApiImpl.<PayInfoResponse>getApiTransformer2())
                .subscribeOn(Schedulers.io());
        return compose;
    }

    @Override
    public Observable<CommonStatusResponse> collect(long id, String activityName) {

        RequestBodyBuilder requestBodyBuilder = new RequestBodyBuilder()
                .addProperty(Constants.RequestBodyKey.ID, id);
        return ComicApi.getApi().collect(requestBodyBuilder.build())
                .compose(ComicApiImpl.<CommonStatusResponse>getApiTransformer2())
                .retryWhen(new RetryFunction2(activityName))
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<CommonStatusResponse> unCollect(List<BookModel> models, String activityName) {
        String idsStr = "";
        for (BookModel model : models) {
            idsStr = idsStr + model.getId() + ",";
        }
        idsStr = idsStr.substring(0, idsStr.length() - 1);
        RequestBodyBuilder requestBodyBuilder = new RequestBodyBuilder()
                .addProperty(Constants.RequestBodyKey.ARTICLE_ID, idsStr);
        return ComicApi.getApi().delCollect(requestBodyBuilder.build())
                .compose(ComicApiImpl.<CommonStatusResponse>getApiTransformer2())
                .retryWhen(new RetryFunction2(activityName))
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<CommonStatusResponse> deleteReadRecord(String articleids) {
        RequestBodyBuilder bodyBuilder = new RequestBodyBuilder()
                .addProperty(Constants.RequestBodyKey.ARTICLE_ID, articleids);
        Observable<CommonStatusResponse> compose = ComicApi.getApi().deleteReadRecord(bodyBuilder.build())
                .compose(ComicApiImpl.<CommonStatusResponse>getApiTransformer2())
                .retryWhen(new RetryFunction2());
        return compose;
    }

    @Override
    public Observable<RecharegeRecordsResponse> getRechargeRecord(int page) {
        RequestBody body = new RequestBodyBuilder()
                .addProperty("page", page)
                .build();
        Observable<RecharegeRecordsResponse> compose = ComicApi.getApi().getRechargeRecord(body)
                .compose(ComicApiImpl.<RecharegeRecordsResponse>getApiTransformer2())
                .retryWhen(new RetryFunction2())
                .subscribeOn(Schedulers.io());
        return compose;
    }

    @Override
    public Observable<ExpenseSumRecordsResponse> getConsumptionRecord(String activityName, int currentPage) {
        // 请求体
        RequestBody body = new RequestBodyBuilder()
                .addProperty(Constants.RequestBodyKey.PAGE_NUM, currentPage)
                .build();

        Observable<ExpenseSumRecordsResponse> compose = ComicApi.getApi().getConsumptionRecord(body)
                .compose(ComicApiImpl.<ExpenseSumRecordsResponse>getApiTransformer2())
                .retryWhen(new RetryFunction2(activityName))
                .subscribeOn(Schedulers.io());
        return compose;
    }

    @Override
    public Observable<CommonStatusResponse> subscribe(long bookId, long chapterId) {
        Map<String, Object> parames = new HashMap<>();
        parames.put(Constants.RequestBodyKey.BOOK_ID, bookId);
        parames.put(Constants.RequestBodyKey.CHAPTER_ID, chapterId);
        return ComicApi.getApi().subscribe(parames)
                .compose(ComicApiImpl.<CommonStatusResponse>getApiTransformer2())
                .retryWhen(new RetryFunction2())
                .subscribeOn(Schedulers.io());
    }


    @Override
    public Observable<RechargeCoinResponse> getRechargeCoinList() {
        Observable<RechargeCoinResponse> compose = ComicApi.getApi().getRechargeCoinList()
                .compose(ComicApiImpl.<RechargeCoinResponse>getApiTransformer2())
                .retryWhen(new RetryFunction2())
                .subscribeOn(Schedulers.io());
        return compose;
    }

    @Override
    public Observable<RewardHistoryResponse> getRewardsRecord(String activityName) {
        Observable<RewardHistoryResponse> compose = ComicApi.getApi().getRewardsRecordByUser()
                .retryWhen(new RetryFunction2(activityName))
                .compose(ComicApiImpl.<RewardHistoryResponse>getApiTransformer2())
                .subscribeOn(Schedulers.io());
        return compose;
    }

    @Override
    public Observable<RichResponse> getRewardRankingListOfUser(String activityName) {

        Observable<RichResponse> compose = ComicApi.getApi().getRewardRankingListOfUser()
                .retryWhen(new RetryFunction2(activityName))
                .compose(ComicApiImpl.<RichResponse>getApiTransformer2())
                .subscribeOn(Schedulers.io());
        return compose;
    }


    @Override
    public Observable<CommonStatusResponse> doReward(long contentId, String type, int giftNums) {
        RequestBody requestBody = new RequestBodyBuilder()
                .addProperty(Constants.RequestBodyKey.ID, contentId)
                .addProperty(Constants.RequestBodyKey.GIFT_TYPE, type)
                .addProperty(Constants.RequestBodyKey.GIFT_NUM, giftNums)
                .build();

        return ComicApi.getApi().doReward(requestBody)
                .retryWhen(new RetryFunction2())
                .compose(ComicApiImpl.<CommonStatusResponse>getApiTransformer2())
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<CommentListResponse> getCommentsRecordByUser(String activityName, long userId, int pageNum, int pageSize) {
        RequestBody requestBody = new RequestBodyBuilder()
                .addProperty(Constants.RequestBodyKey.USER_ID, userId)
                .addProperty(Constants.RequestBodyKey.PAGE_NUM, pageNum)
                .addProperty(Constants.RequestBodyKey.PAGE_SIZE, pageSize)
                .build();

        Observable<CommentListResponse> compose = ComicApi.getApi().getCommentsRecordByUser(requestBody)
                .compose(ComicApiImpl.<CommentListResponse>getApiTransformer2())
                .subscribeOn(Schedulers.io());
        return compose;
    }

    @Override
    public Observable<ResponseModel> deleteCommentByIds(String activityName, long userId, int commentId) {
        JsonArray jsonArray = new JsonArray();
        jsonArray.add(commentId);
        RequestBody requestBody = new RequestBodyBuilder()
                .addProperty(Constants.RequestBodyKey.USER_ID, userId)
                .addProperty(Constants.RequestBodyKey.COMMENT_IDS, jsonArray)
                .build();

        Observable<ResponseModel> compose = ComicApi.getApi().deleteCommentByIds(requestBody)
                .compose(ComicApiImpl.<ResponseModel>getApiTransformer2())
                .subscribeOn(Schedulers.io());
        return compose;
    }

    @Override
    public Observable<HeadImg> uploadImage(String activityName, MultipartBody.Part part, long userId) {
        Observable<HeadImg> comose = ComicApi.getApi().uploadImage(part, Constants.PRODUCT_CODE, userId)
                .compose(ComicApiImpl.<HeadImg>getApiTransformer2())
                .retryWhen(new RetryFunction2(activityName))
                .subscribeOn(Schedulers.io());
        return comose;
    }

    /**
     * 微信登录
     *
     * @param spreadid 渠道号
     * @param code     微信登录返回code
     * @return
     */
    @Override
    public Observable<LoginByCodeResponse> wxLogin(String spreadid, String code) {
        RequestBody body = new RequestBodyBuilder()
                .addProperty("spreadid", spreadid)
                .addProperty("code", code)
                .build();

        Observable<LoginByCodeResponse> observable = ComicApi.getApi().wxLogin(body)
                .subscribeOn(Schedulers.io())
                .compose(ComicApiImpl.<LoginByCodeResponse>getApiTransformer2())
                .retryWhen(new RetryFunction2());
        return observable;
    }


    /**
     * @param openid
     * @return
     */
    @Override
    public Observable<LoginByCodeResponse> qqLogin(String openid, String access_token) {
        RequestBody body = new RequestBodyBuilder()
                .addProperty("openid", openid)
                .addProperty("access_token", access_token)
                .build();

        Observable<LoginByCodeResponse> observable = ComicApi.getApi().qqLogin(body)
                .subscribeOn(Schedulers.io())
                .compose(ComicApiImpl.<LoginByCodeResponse>getApiTransformer2())
                .retryWhen(new RetryFunction2());
        return observable;
    }

    /**
     * @return
     */
    @Override
    public Observable<LoginByCodeResponse> wbLogin(String access_token, String uid) {
        RequestBody body = new RequestBodyBuilder()
                .addProperty("access_token", access_token)
                .addProperty("uid", uid)
                .build();

        Observable<LoginByCodeResponse> observable = ComicApi.getApi().wbLogin(body)
                .subscribeOn(Schedulers.io())
                .compose(ComicApiImpl.<LoginByCodeResponse>getApiTransformer2())
                .retryWhen(new RetryFunction2());
        return observable;
    }

    @Override
    public Observable<UidLoginResponse> uidLogin(String uid) {
        RequestBody body = new RequestBodyBuilder()
                .addProperty("uid", uid)
                .build();

       return ComicApi.getApi().uidLogin(body)
                .subscribeOn(Schedulers.io())
                .compose(ComicApiImpl.<UidLoginResponse>getApiTransformer2())
                .retryWhen(new RetryFunction2())
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<UserInfoResponse> updateUserInfo(String activityName, UserInfo userInfo) {
        RequestBody requestBody = new RequestBodyBuilder()
                .addProperty(Constants.RequestBodyKey.TOKEN, SharedPref.getInstance(BaseApplication.getApplication()).getString(Constants.SharedPrefKey.TOKEN, ""))
                .addProperty(Constants.RequestBodyKey.USER, new Gson().toJsonTree(userInfo))
                .build();

        Observable<UserInfoResponse> compose = ComicApi.getApi().updateUserInfo(requestBody)
                .compose(ComicApiImpl.<UserInfoResponse>getApiTransformer2())
                .retryWhen(new RetryFunction2(activityName))
                .subscribeOn(Schedulers.io());
        return compose;
    }

    @Override
    public Observable<CommonStatusResponse> favorContent(long id) {
        RequestBody requestBody = new RequestBodyBuilder()
                .addProperty(Constants.RequestBodyKey.ID, id)
                .build();

        return ComicApi.getApi().favorContent(requestBody)
                .compose(ComicApiImpl.<CommonStatusResponse>getApiTransformer2())
                .retryWhen(new RetryFunction2())
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<ResponseModel> bindMobile(String activityName, String mobile, String
            verify, String newMobile, String securityMobile) {
        RequestBody requestBody = new RequestBodyBuilder()
                .addProperty(Constants.RequestBodyKey.TOKEN, SharedPref.getInstance(BaseApplication.getApplication()).getString(Constants.SharedPrefKey.TOKEN, ""))
                .addProperty(Constants.RequestBodyKey.SECURITY_CODE, verify)
                .addProperty(Constants.RequestBodyKey.SECURITY_MOBILE, securityMobile)
                .addProperty(Constants.RequestBodyKey.MOBILE, mobile)
                .addProperty(Constants.RequestBodyKey.NEW_MOBILE, newMobile)
                .build();

        Observable<ResponseModel> compose = ComicApi.getApi().bindMobile(requestBody)
                .compose(ComicApiImpl.<ResponseModel>getApiTransformer2())
                .retryWhen(new RetryFunction2(activityName))
                .subscribeOn(Schedulers.io());
        return compose;
    }

    @Override
    public Observable<RichDataResponse> rewardRecordByAllUser(String activityName, int pageNum) {

        return ComicApi.getApi().rewardRecordByAllUser(pageNum)
                .compose(ComicApiImpl.<RichDataResponse>getApiTransformer2())
                .retryWhen(new RetryFunction2(activityName))
                .subscribeOn(Schedulers.io());
    }

    /**
     * 获取用户阅读历史记录
     *
     * @return
     */
    @Override
    public Observable<CollectionResponse> getHistoryList() {
        Observable<CollectionResponse> compose = ComicApi.getApi().getHistoryList()
                .compose(ComicApiImpl.<CollectionResponse>getApiTransformer2())
                .retryWhen(new RetryFunction2());
        return compose;
    }

    /**
     * 同步用户阅读记录
     *
     * @param models
     * @return
     */
    @Override
    public Observable<CommonStatusResponse> syncHistory(List<BookModel> models) {
        Gson gson = new Gson();
        JsonArray jsonArray = new JsonArray();
        for (BookModel model : models) {
            JsonObject element = (JsonObject) gson.toJsonTree(model);
            element.addProperty("bookid", model.getId());
            jsonArray.add(element);
        }
        RequestBody body = new RequestBodyBuilder()
                .addProperty("log", jsonArray)
                .build();
        Observable<CommonStatusResponse> compose = ComicApi.getApi().syncHistory(body)
                .compose(ComicApiImpl.<CommonStatusResponse>getApiTransformer2())
                .retryWhen(new RetryFunction2());
        return compose;
    }

    @Override
    public Observable<FeedbackListResponse> getFeedbackList(int pageNum, String tag) {
        return ComicApi.getApi().getFeedbackList()
                .compose(ComicApiImpl.<FeedbackListResponse>getApiTransformer2())
                .retryWhen(new RetryFunction2(tag));
    }

    @Override
    public Observable<FeedbackStatusModel> getFeedbackStatus() {
        return ComicApi.getApi().getFeedbackStatus()
                .compose(ComicApiImpl.<FeedbackStatusModel>getApiTransformer2())
                .retryWhen(new RetryFunction2());
    }

    @Override
    public Observable<CommonStatusResponse> getCollectStatus(long id, String retryTag) {
        return ComicApi.getApi().getCollectStatus(id)
                .compose(ComicApiImpl.<CommonStatusResponse>getApiTransformer2())
                .retryWhen(new RetryFunction2(retryTag));
    }

    @Override
    public Observable<CommonStatusResponse> getFavorStatus(long id, String retryTag) {
        return ComicApi.getApi().getFavorStatus(id)
                .compose(ComicApiImpl.<CommonStatusResponse>getApiTransformer2())
                .retryWhen(new RetryFunction2(retryTag));
    }

    @Override
    public Observable<CommonStatusResponse> favorComment(long id, String type) {
        Map<String, Object> parames = new HashMap<>();
        parames.put(Constants.RequestBodyKey.ID, id);
        parames.put(Constants.RequestBodyKey.COMMON_TYPE, type);
        return ComicApi.getApi().favorComment(parames)
                .compose(ComicApiImpl.<CommonStatusResponse>getApiTransformer2())
                .retryWhen(new RetryFunction2());
    }

    @Override
    public Observable<PayCenterInfoResponse> getPayCenterInfo() {
        return ComicApi.getApi().getPayCenterInfo("PAY_SETTING_APP")
                .compose(ComicApiImpl.<PayCenterInfoResponse>getApiTransformer2())
                .retryWhen(new RetryFunction2());
    }


}
