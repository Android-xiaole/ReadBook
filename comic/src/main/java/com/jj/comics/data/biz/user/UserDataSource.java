package com.jj.comics.data.biz.user;

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
import com.jj.comics.data.model.GetCodeResponse;
import com.jj.comics.data.model.HeadImg;
import com.jj.comics.data.model.LoginResponse;
import com.jj.comics.data.model.PayInfoResponse;
import com.jj.comics.data.model.PaySettingResponse;
import com.jj.comics.data.model.RebateListResponse;
import com.jj.comics.data.model.RecharegeRecordsResponse;
import com.jj.comics.data.model.RechargeCoinResponse;
import com.jj.comics.data.model.ResponseModel;
import com.jj.comics.data.model.RestResponse;
import com.jj.comics.data.model.ShareRecommendResponse;
import com.jj.comics.data.model.TLPayResponse;
import com.jj.comics.data.model.TLPayStatusResponse;
import com.jj.comics.data.model.UserInfo;
import com.jj.comics.data.model.UserInfoResponse;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.MultipartBody;

public interface UserDataSource {
    //第三方登录绑定手机号
    Observable<LoginResponse> bindPhone(String phoneNum, String code);


    //第三方登录获取验证码
    Observable<ResponseModel> getThirdLoginCode(String phoneNum, String type);

    //获取验证码
    Observable<GetCodeResponse> getSecurityCode(String activityName, String mobile);

    //获取验证码
    Observable<ResponseModel> getPhoneCode(String activityName, String mobile);

    //验证码登录
    Observable<LoginResponse> loginBySecurityCode(String phone, String psw, String inviteCode);

    /**
     * 微信登录
     *
     * @param spreadid
     * @param code
     * @return
     */
    Observable<LoginResponse> wxLogin(String spreadid, String code);

    /**
     * QQ 登录
     *
     * @param openid
     * @param access_token
     * @return
     */
    Observable<LoginResponse> qqLogin(String openid, String access_token);


    /**
     * wb login
     *
     * @return
     */
    Observable<LoginResponse> wbLogin(String accecc_tokon, String uid);


    Observable<UserInfoResponse> getUserInfo();

    Observable<PayInfoResponse> getUserPayInfo();

    //保存用户信息并上报
    Observable<UserInfo> saveUser(UserInfo user);

    //添加收藏
    Observable<CommonStatusResponse> collect(long id, String activityName);

    //取消收藏
    Observable<CommonStatusResponse> unCollect(List<BookModel> models, String activityName);

    Observable<CommonStatusResponse> syncHistory(List<BookModel> models);

    //删除阅读历史记录
    Observable<CommonStatusResponse> deleteReadRecord(String articleids);

    //获取用户充值记录
    Observable<RecharegeRecordsResponse> getRechargeRecord(int page);

    //获取用户消费记录
    Observable<ExpenseSumRecordsResponse> getConsumptionRecord(String activityName, int currentPage);

    //订阅内容,使用虚拟货币
    Observable<CommonStatusResponse> subscribe(long bookId, long chapterId);

    //获取充值金币价格列表
    Observable<RechargeCoinResponse> getRechargeCoinList();


    //打赏
    Observable<CommonStatusResponse> doReward(long contentId, String type, int giftNums);

    //获取当前用户的评论记录
    Observable<CommentListResponse> getCommentsRecordByUser(String activityName, long userId, int pageNum, int pageSize);

    //根据评论id列表删除多条评论
    Observable<ResponseModel> deleteCommentByIds(String activityName, long userId, int commentId);

    //上传照片
    Observable<HeadImg> uploadImage(String activityName, MultipartBody.Part part, long userId);

    //点赞内容
    Observable<CommonStatusResponse> favorContent(long id);

    //绑定手机号
    Observable<ResponseModel> bindMobile(String activityName, String mobile, String verify, String newMobile, String securityMobile);

    //修改手机号
    Observable<LoginResponse> alterMobile(String activityName, String phone_number, String code);

    //获取用户阅读历史记录列表
    Observable<CollectionResponse> getHistoryList();


    //获取漫画收藏点赞状态
    Observable<CommonStatusResponse> getCollectStatus(long id, String retryTag);

    //获取漫画内容点赞状态
    Observable<CommonStatusResponse> getFavorStatus(long id, String retryTag);

    //评论点赞
    Observable<CommonStatusResponse> favorComment(long id, String type);

    Observable<PaySettingResponse> getPayCenterInfo(String type);

    Observable<ConsumeDetailListResponse> getConsumeDetail(long bookId);

    /**
     * 更新用户信息
     *
     * @return
     */
    Observable<UserInfo> updateUserInfo(String avatar, String nickname, int sex);

    /**
     * 我的返利列表
     *
     * @param page
     * @return
     */
    Observable<RebateListResponse> getRebateList(int page);

    /**
     * 我的提现列表
     *
     * @param page
     * @return
     */
    Observable<CashOutListResponse> getCashOutList(int page);

    /**
     * 绑定体现方式状态
     *
     * @return
     */
    Observable<CashOutWayResponse> getCashOutWayStatus();

    /**
     * 添加支付宝
     *
     * @param account_number
     * @param opener
     * @return
     */
    Observable<AddCashOutWayResponse> addCashOutWayAli(String account_number, String opener);

    /**
     * 添加银行卡
     *
     * @param account_number
     * @param opener
     * @param opening_bank
     * @return
     */
    Observable<AddCashOutWayResponse> addCashOutWayUnion(String account_number, String opener,
                                                         String opening_bank);

    /**
     * 提现
     *
     * @param type
     * @param money
     * @return
     */
    Observable<CashOutResponse> cashOut(int type, float money);

    /**
     * 徒弟
     *
     * @param type
     * @param page
     * @return
     */
    Observable<ApprenticeListResponse> getApprenticeList(int page, int type);

    /**
     * 获取分享推荐列表
     *
     * @return
     */
    Observable<ShareRecommendResponse> getShareRecommend();

    /**
     * 通联支付宝
     *
     * @return
     */
    Observable<TLPayResponse> getTLPay(long goods_id, long book_id);

    /**
     * 获取通联支付宝订单状态
     *
     * @return
     */
    Observable<TLPayStatusResponse> getTLPayStatus(String tradeNo);

    /**
     * 获取自动购买和消息挂件配置
     *
     * @return
     */
    Observable<RestResponse> getRest();

    /**
     * 设置自动购买和消息挂件配置
     *
     * @return
     */
    Observable<ResponseModel> setAutoReceive(int auto_buy, int is_receive);
}
