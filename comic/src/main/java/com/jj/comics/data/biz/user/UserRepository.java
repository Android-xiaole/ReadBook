package com.jj.comics.data.biz.user;


import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.jj.base.net.ComicApiImpl;
import com.jj.base.net.NetError;
import com.jj.base.net.RetryFunction2;
import com.jj.comics.common.constants.Constants;
import com.jj.comics.common.net.ComicApi;
import com.jj.comics.common.net.RequestBodyBuilder;
import com.jj.comics.data.model.AddCashOutWayResponse;
import com.jj.comics.data.model.ApprenticeListResponse;
import com.jj.comics.data.model.BookModel;
import com.jj.comics.data.model.CashOutListResponse;
import com.jj.comics.data.model.CashOutResponse;
import com.jj.comics.data.model.CashOutWayResponse;
import com.jj.comics.data.model.CollectionResponse;
import com.jj.comics.data.model.CommentListResponse;
import com.jj.comics.data.model.CommonStatusResponse;
import com.jj.comics.data.model.ConsumeDetailListResponse;
import com.jj.comics.data.model.ExpenseSumRecordsResponse;
import com.jj.comics.data.model.HeadImg;
import com.jj.comics.data.model.LoginResponse;
import com.jj.comics.data.model.PayInfoResponse;
import com.jj.comics.data.model.PaySettingResponse;
import com.jj.comics.data.model.RebateListResponse;
import com.jj.comics.data.model.RecharegeRecordsResponse;
import com.jj.comics.data.model.RechargeCoinResponse;
import com.jj.comics.data.model.ResponseModel;
import com.jj.comics.data.model.ShareRecommendResponse;
import com.jj.comics.data.model.TLPayResponse;
import com.jj.comics.data.model.TLPayStatusResponse;
import com.jj.comics.data.model.UserInfo;
import com.jj.comics.data.model.UserInfoResponse;
import com.jj.comics.util.LoginHelper;
import com.jj.comics.util.SharedPreManger;
import com.jj.comics.util.reporter.ActionReporter;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
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
    public Observable<LoginResponse> bindPhone(String phoneNum, String code, String inviteCode, String openId) {
        RequestBody requestBody = new RequestBodyBuilder()
                .addProperty(Constants.RequestBodyKey.PHONE_NUMBER, phoneNum)
                .addProperty(Constants.RequestBodyKey.LOGIN_CODE, code)
                .addProperty(Constants.RequestBodyKey.LOGIN_INVITE_CODE, inviteCode)
                .addProperty(Constants.RequestBodyKey.LOGIN_OPENID, openId)
                .build();
        return ComicApi.getApi().bindPhone(requestBody)
                .retryWhen(new RetryFunction2())
                .compose(ComicApiImpl.<LoginResponse>getApiTransformer2())
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<ResponseModel> getThirdLoginCode(String phoneNum, String type) {
        RequestBody requestBody = new RequestBodyBuilder()
                .addProperty(Constants.RequestBodyKey.PHONE_NUMBER, phoneNum)
                .addProperty(Constants.RequestBodyKey.LOGIN_TYPE, type)
                .build();
        return ComicApi.getApi().getThirdLoginCode(requestBody)
                .retryWhen(new RetryFunction2())
                .compose(ComicApiImpl.<ResponseModel>getApiTransformer2())
                .subscribeOn(Schedulers.io());
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

    /**
     * 用户修改手机号获取验证码
     *
     * @param activityName
     * @param mobile
     * @return
     */
    @Override
    public Observable<ResponseModel> getPhoneCode(String activityName, String mobile) {
        Observable<ResponseModel> compose = ComicApi.getApi().getPhoneCode(new RequestBodyBuilder()
                .addProperty(Constants.RequestBodyKey.LOGIN_PHONE_NUMBER, mobile)
                .build())
                .retryWhen(new RetryFunction2(activityName))
                .compose(ComicApiImpl.<ResponseModel>getApiTransformer2())
                .subscribeOn(Schedulers.io());
        return compose;
    }

    @Override
    public Observable<LoginResponse> loginBySecurityCode(String phone, String psw, String inviteCode) {
        RequestBody requestBody = new RequestBodyBuilder()
                .addProperty(Constants.RequestBodyKey.LOGIN_PHONE_NUMBER, phone)
                .addProperty(Constants.RequestBodyKey.LOGIN_CODE, psw)
                .addProperty(Constants.RequestBodyKey.LOGIN_INVITE_CODE, inviteCode)
                .build();
        Observable<LoginResponse> compose = ComicApi.getApi().loginBySecurityCode(requestBody)
                .retryWhen(new RetryFunction2())
                .compose(ComicApiImpl.<LoginResponse>getApiTransformer2())
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
    public Observable<LoginResponse> wxLogin(String spreadid, String code) {
        RequestBody body = new RequestBodyBuilder()
                .addProperty("spreadid", spreadid)
                .addProperty("code", code)
                .build();

        Observable<LoginResponse> observable = ComicApi.getApi().wxLogin(body)
                .subscribeOn(Schedulers.io())
                .compose(ComicApiImpl.<LoginResponse>getApiTransformer2())
                .retryWhen(new RetryFunction2());
        return observable;
    }


    /**
     * @param openid
     * @return
     */
    @Override
    public Observable<LoginResponse> qqLogin(String openid, String access_token) {
        RequestBody body = new RequestBodyBuilder()
                .addProperty("openid", openid)
                .addProperty("access_token", access_token)
                .build();

        Observable<LoginResponse> observable = ComicApi.getApi().qqLogin(body)
                .subscribeOn(Schedulers.io())
                .compose(ComicApiImpl.<LoginResponse>getApiTransformer2())
                .retryWhen(new RetryFunction2());
        return observable;
    }

    /**
     * @return
     */
    @Override
    public Observable<LoginResponse> wbLogin(String access_token, String uid) {
        RequestBody body = new RequestBodyBuilder()
                .addProperty("access_token", access_token)
                .addProperty("uid", uid)
                .build();

        Observable<LoginResponse> observable = ComicApi.getApi().wbLogin(body)
                .subscribeOn(Schedulers.io())
                .compose(ComicApiImpl.<LoginResponse>getApiTransformer2())
                .retryWhen(new RetryFunction2());
        return observable;
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

    /**
     * 修改手机号
     *
     * @param activityName
     * @param phone_number
     * @param code
     * @return
     */
    @Override
    public Observable<LoginResponse> alterMobile(String activityName, String phone_number, String code) {
        RequestBody requestBody = new RequestBodyBuilder()
                .addProperty(Constants.RequestBodyKey.CODE, code)
                .addProperty(Constants.RequestBodyKey.PHONE_NUMBER, phone_number)
                .build();

        Observable<LoginResponse> compose = ComicApi.getApi().alterMobile(requestBody)
                .compose(ComicApiImpl.<LoginResponse>getApiTransformer2())
                .retryWhen(new RetryFunction2(activityName))
                .subscribeOn(Schedulers.io());
        return compose;
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
    public Observable<PaySettingResponse> getPayCenterInfo(String type) {
        return ComicApi.getApi().getPayCenterInfo("android_paysetting", type)
                .compose(ComicApiImpl.<PaySettingResponse>getApiTransformer2())
                .retryWhen(new RetryFunction2());
    }

    @Override
    public Observable<ConsumeDetailListResponse> getConsumeDetail(long bookId) {
        RequestBody body = new RequestBodyBuilder()
                .addProperty("articleid", bookId)
                .build();
        return ComicApi.getApi().getConsumeDetail(body)
                .compose(ComicApiImpl.<ConsumeDetailListResponse>getApiTransformer2())
                .retryWhen(new RetryFunction2())
                .subscribeOn(Schedulers.io());
    }


    @Override
    public Observable<UserInfo> updateUserInfo(String avatar, String nickname, int sex) {
        RequestBodyBuilder requestBodyBuilder = new RequestBodyBuilder()
                .addProperty(Constants.RequestBodyKey.AVATAR, avatar)
                .addProperty(Constants.RequestBodyKey.NICKNAME, nickname)
                .addProperty(Constants.RequestBodyKey.SEX, sex);
        if (avatar == null) {
            requestBodyBuilder.removeProperty(Constants.RequestBodyKey.AVATAR);
        }

        if (nickname == null) {
            requestBodyBuilder.removeProperty(Constants.RequestBodyKey.NICKNAME);
        }

        if (sex == -1) {
            requestBodyBuilder.removeProperty(Constants.RequestBodyKey.SEX);
        }

        RequestBody requestBody = requestBodyBuilder.build();

        return ComicApi.getApi().updateUserInfo(requestBody)
                .subscribeOn(Schedulers.io())
                .compose(ComicApiImpl.<LoginResponse>getApiTransformer2())
                .retryWhen(new RetryFunction2())
                .flatMap(new Function<LoginResponse, ObservableSource<UserInfo>>() {
                    @Override
                    public ObservableSource<UserInfo> apply(LoginResponse loginResponse) throws Exception {
                        //修改用户信息成功之后后台会刷新token
                        if (loginResponse.getData() != null && loginResponse.getData().getUser_info() != null) {
                            //刷新token
                            SharedPreManger.getInstance().saveToken(loginResponse.getData().getBearer_token());
                            //保存用户信息
                            return UserRepository.getInstance().saveUser(loginResponse.getData().getUser_info());
                        } else {
                            return Observable.error(NetError.noDataError());
                        }
                    }
                });
    }

    @Override
    public Observable<RebateListResponse> getRebateList(int page) {
        RequestBody body = new RequestBodyBuilder()
                .addProperty("page", page)
                .build();
        return ComicApi.getApi().getRebateList(body)
                .compose(ComicApiImpl.<RebateListResponse>getApiTransformer2())
                .retryWhen(new RetryFunction2());
    }

    @Override
    public Observable<CashOutListResponse> getCashOutList(int page) {
        return ComicApi.getApi().getCashOutList(page)
                .compose(ComicApiImpl.<CashOutListResponse>getApiTransformer2())
                .retryWhen(new RetryFunction2());
    }


    @Override
    public Observable<CashOutWayResponse> getCashOutWayStatus() {
        RequestBody body = new RequestBodyBuilder()
                .build();
        return ComicApi.getApi().getCashOutWayStatus(body)
                .compose(ComicApiImpl.<CashOutWayResponse>getApiTransformer2())
                .retryWhen(new RetryFunction2());
    }

    @Override
    public Observable<AddCashOutWayResponse> addCashOutWayAli(String account_number, String opener) {
        RequestBody body = new RequestBodyBuilder()
                .addProperty("account_number", account_number)
                .addProperty("opener", opener)
                .build();
        return ComicApi.getApi().addCashOutAli(body)
                .compose(ComicApiImpl.<AddCashOutWayResponse>getApiTransformer2())
                .retryWhen(new RetryFunction2());
    }

    @Override
    public Observable<AddCashOutWayResponse> addCashOutWayUnion(String account_number, String opener, String opening_bank) {
        RequestBody body = new RequestBodyBuilder()
                .addProperty("account_number", account_number)
                .addProperty("opener", opener)
                .addProperty("opening_bank", opening_bank)
                .build();
        return ComicApi.getApi().addCashOutUnion(body)
                .compose(ComicApiImpl.<AddCashOutWayResponse>getApiTransformer2())
                .retryWhen(new RetryFunction2());
    }

    @Override
    public Observable<CashOutResponse> cashOut(int type, float money) {
        RequestBody body = new RequestBodyBuilder()
                .addProperty("type", type)
                .addProperty("money", money)
                .build();
        return ComicApi.getApi().cashOut(body)
                .compose(ComicApiImpl.<CashOutResponse>getApiTransformer2())
                .retryWhen(new RetryFunction2());
    }

    @Override
    public Observable<ApprenticeListResponse> getApprenticeList(int page, int type) {
        return ComicApi.getApi().getApprenticeList(page, type)
                .compose(ComicApiImpl.<ApprenticeListResponse>getApiTransformer2())
                .retryWhen(new RetryFunction2());
    }

    @Override
    public Observable<ShareRecommendResponse> getShareRecommend() {
        return ComicApi.getApi().getShareRecommend()
                .compose(ComicApiImpl.<ShareRecommendResponse>getApiTransformer2())
                .retryWhen(new RetryFunction2());
    }

    @Override
    public Observable<TLPayResponse> getTLPay(long goods_id, long book_id) {
        RequestBody body = new RequestBodyBuilder()
                .addProperty("goods_id", goods_id)
                .addProperty("book_id", book_id)
                .build();
        return ComicApi.getApi().getTLPay(body)
                .compose(ComicApiImpl.<TLPayResponse>getApiTransformer2())
                .retryWhen(new RetryFunction2());
    }

    @Override
    public Observable<TLPayStatusResponse> getTLPayStatus(String tradeNo) {
        RequestBody body = new RequestBodyBuilder()
                .addProperty("out_trade_no", tradeNo)
                .build();
        return ComicApi.getApi().getTLPayStatus(body)
                .compose(ComicApiImpl.<TLPayStatusResponse>getApiTransformer2())
                .retryWhen(new RetryFunction2());
    }
}
