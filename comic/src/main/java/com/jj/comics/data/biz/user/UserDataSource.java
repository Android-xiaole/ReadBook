package com.jj.comics.data.biz.user;

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

import java.util.List;

import io.reactivex.Observable;
import okhttp3.MultipartBody;

public interface UserDataSource {
    //获取验证码
    Observable<ResponseModel> getSecurityCode(String activityName, String mobile);

    //验证码登录
    Observable<LoginByCodeResponse> loginBySecurityCode(boolean isCheck, String phone, String psw);

    /**
     * 微信登录
     *
     * @param spreadid
     * @param code
     * @return
     */
    Observable<LoginByCodeResponse> wxLogin(String spreadid, String code);

    /**
     * QQ 登录
     *
     * @param openid
     * @param access_token
     * @return
     */
    Observable<LoginByCodeResponse> qqLogin(String openid, String access_token);


    /**
     * wb login
     *
     * @return
     */
    Observable<LoginByCodeResponse> wbLogin(String accecc_tokon, String uid);

    Observable<UidLoginResponse> uidLogin(String uid);

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

    //获取当前用户打赏记录
    Observable<RewardHistoryResponse> getRewardsRecord(String activityName);

    //获取全站打赏统计的用户排行
    Observable<RichResponse> getRewardRankingListOfUser(String activityName);

    //获取实时打赏用户的列表
    Observable<RichDataResponse> rewardRecordByAllUser(String activityName, int pageNum);

    //打赏
    Observable<CommonStatusResponse> doReward(long contentId, String type, int giftNums);

    //获取当前用户的评论记录
    Observable<CommentListResponse> getCommentsRecordByUser(String activityName, long userId, int pageNum, int pageSize);

    //根据评论id列表删除多条评论
    Observable<ResponseModel> deleteCommentByIds(String activityName, long userId, int commentId);

    //上传照片
    Observable<HeadImg> uploadImage(String activityName, MultipartBody.Part part, long userId);

    //更新用户信息
    Observable<UserInfoResponse> updateUserInfo(String activityName, UserInfo userInfo);

    //点赞内容
    Observable<CommonStatusResponse> favorContent(long id);

    //绑定手机号
    Observable<ResponseModel> bindMobile(String activityName, String mobile, String verify, String newMobile, String securityMobile);

    //获取用户阅读历史记录列表
    Observable<CollectionResponse> getHistoryList();

    //获取用户反馈列表
    Observable<FeedbackListResponse> getFeedbackList(int pageNum, String tag);

    //获取用户反馈信息状态，有没有新的回复信息
    Observable<FeedbackStatusModel> getFeedbackStatus();

    //获取漫画收藏点赞状态
    Observable<CommonStatusResponse> getCollectStatus(long id, String retryTag);

    //获取漫画内容点赞状态
    Observable<CommonStatusResponse> getFavorStatus(long id, String retryTag);

    //评论点赞
    Observable<CommonStatusResponse> favorComment(long id, String type);

    Observable<PayCenterInfoResponse> getPayCenterInfo();

}
