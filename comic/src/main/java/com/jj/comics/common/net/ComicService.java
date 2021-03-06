package com.jj.comics.common.net;

import com.jj.comics.common.constants.Constants;
import com.jj.comics.data.model.AddCashOutWayResponse;
import com.jj.comics.data.model.AppConfigResponse;
import com.jj.comics.data.model.ApprenticeListResponse;
import com.jj.comics.data.model.BannerResponse;
import com.jj.comics.data.model.BookCatalogContentResponse;
import com.jj.comics.data.model.BookCatalogListResponse;
import com.jj.comics.data.model.BookListDataResponse;
import com.jj.comics.data.model.BookListPopShareResponse;
import com.jj.comics.data.model.BookListRecommondResponse;
import com.jj.comics.data.model.BookListResponse;
import com.jj.comics.data.model.BookModelResponse;
import com.jj.comics.data.model.BoughtResponse;
import com.jj.comics.data.model.CashOutListResponse;
import com.jj.comics.data.model.CashOutResponse;
import com.jj.comics.data.model.CashOutWayResponse;
import com.jj.comics.data.model.CategoryResponse;
import com.jj.comics.data.model.CollectionResponse;
import com.jj.comics.data.model.CommentListResponse;
import com.jj.comics.data.model.CommonStatusResponse;
import com.jj.comics.data.model.ConsumeDetailListResponse;
import com.jj.comics.data.model.ExpenseSumRecordsResponse;
import com.jj.comics.data.model.GetCodeResponse;
import com.jj.comics.data.model.HeadImg;
import com.jj.comics.data.model.LoginResponse;
import com.jj.comics.data.model.NotificationListResponse;
import com.jj.comics.data.model.NotificationResponse;
import com.jj.comics.data.model.OSSResponse;
import com.jj.comics.data.model.PayActionResponse;
import com.jj.comics.data.model.PayInfoResponse;
import com.jj.comics.data.model.PaySettingResponse;
import com.jj.comics.data.model.PrePayOrderResponseAli;
import com.jj.comics.data.model.PrePayOrderResponseHuifubao;
import com.jj.comics.data.model.PrePayOrderResponseWx;
import com.jj.comics.data.model.ProtocalModel;
import com.jj.comics.data.model.Push;
import com.jj.comics.data.model.RebateListResponse;
import com.jj.comics.data.model.RecharegeRecordsResponse;
import com.jj.comics.data.model.RechargeCoinResponse;
import com.jj.comics.data.model.ResponseModel;
import com.jj.comics.data.model.RestResponse;
import com.jj.comics.data.model.SearchHotKeywordsResponse;
import com.jj.comics.data.model.ShareParamModel;
import com.jj.comics.data.model.ShareRecommendResponse;
import com.jj.comics.data.model.SortListResponse;
import com.jj.comics.data.model.StatusResponse;
import com.jj.comics.data.model.TLPayResponse;
import com.jj.comics.data.model.TLPayStatusResponse;
import com.jj.comics.data.model.UpdateModelProxy;
import com.jj.comics.data.model.UserInfoResponse;
import com.jj.comics.data.model.WxModel;
import com.jj.comics.data.visittime.AccessTokenResponse;
import com.jj.comics.data.visittime.TimeReportData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

public interface ComicService {

    String API_SET_AUTOBUY = "api/set_autobuy";
    String API_LIKEBOOKINFO = "api/likebookinfo";
    String API_REVIEW_ZAN = "api/review_zan";
    String API_GUESS_YOUR_LIKE = "api/guess_your_like";
    String API_CHECK_COLLECT = "api/check_collect";
    String API_CHECK_DIANZAN = "api/check_dianzan";
    String API_RANKING_LIST = "api/ranking_list";
    String API_FREE = "api/free";
    String API_FOR_FREE = "api/for_free";
    String API_RECOMMENDED = "api/recommended";
    String API_RECENT_UPDATES = "api/recent_updates";
    String API_CAROUSEL = "api/carousel";
    String API_HOT = "api/hot";
    String API_SEARCH = "api/search";
    String API_WATCHING = "api/watching";
    String MH_WXMP_WEBAPI_WX_PARAM = "mh.wxmp.webapi/wx/param";
    String API_SECTION_RECOMMENDED = "api/section_recommended";
    String API_CATALOGUE = "api/catalogue";
    String API_NEW_CATALOGUE = "api/new_catalogue";
    String API_READ = "api/read";
    String API_CARTOON_INFO = "api/cartoon_info";
    String API_COLLECTION = "api/collection";
    String API_REVIEW = "api/review";
    String API_BOOK_BONUS = "/api/book_bonus";
    String API_REVIEW_LIST = "api/review_list";
    String API_SEND_CODE = "api/send_code";
    String API_ALTER_PHONE_SEND_CODE = "api/phone_code";
    String API_MLOGIN = "api/mlogin";
    String API_GET_USERINFO = "api/get_userinfo";
    String API_GET_USER_ACCOUNT = "api/get_user_account";
    String API_COLLECT = "api/collect";
    String API_COLLECTDEL = "api/collectdel";
    String API_READINGHISTORYDEL = "api/readinghistorydel";
    String API_PAYLOG = "api/paylog";
    String API_CONSUME = "api/consume";
    String API_BUY_BOOK = "api/buy_book";
    String API_VOUCHERGOLD = "api/vouchergold";
    String API_APP_WXLOGIN = "api/app_wxlogin";
    String API_QQ_LOGIN = "api/qq_login";
    String API_APP_WEIBO_LOGIN = "api/app_weibo_login";
    String API_MYBOOKBONUS = "api/mybookbonus";
    String API_RICHEST_LIST = "api/richest_list";
    String API_BOOK_REWARD = "api/book_reward";
    String USER_WEBAPI_QUERY_USER_COMMENT = "user.webapi/query_user_comment";
    String USER_WEBAPI_DELETE_COMMENT = "user.webapi/delete_comment";
    String USER_WEBAPI_HEADIMG_UPLOAD = "user.webapi/headimg_upload";
    String USER_WEBAPI_ALERT_USERINFO = "user.webapi/alert_userinfo";
    String API_DIANZAN = "api/dianzan";
    String USER_WEBAPI_BIND_SECURITY_MOBILE = "user.webapi/bind_security_mobile";
    String USER_ALTER_MOBILE = "api/replace_phone";
    String API_RICHEST_ROLL = "api/richest_roll";
    String API_READINGHISTORY = "api/readinghistory";
    String API_SYNCHRONIZATION = "api/synchronization";
    String API_REST = "api/rest";
    String API_CHECK = "api/check";
    String API_APP_LOGIN_GIVE = "api/app_login_give";
    String API_FULI = "api/fuli";
    String API_APP_SHARE = "api/app_share";
    String API_GETGOLD = "api/getgold";
    String API_DAYGET = "api/dayget";
    String API_CATEGORY = "api/category";
    String API_BOOK_CATEGORY = "api/book_category";
    String API_SUGGESTION = "api/suggestion";
    String API_RESPONSELIST = "api/responselist";
    String API_REVERT = "api/revert";
    String API_MEMBERCENTER = "api/membercenter";
    String API_GIFT = "api/gift";
    String ACTIVITY_WEBAPI_GOLD_EXCHANGE = "activity.webapi/gold_exchange";
    String API_WECHAT_ORDER = "api/wechat_order";
    String API_ALIPAY_APP = "api/alipay_app";
    String API_HUIFUBAO_APP = "api/huifubao";

    String API_POP_SHARE = "api/pop_share";
    String API_NOTIDICATION_LIST = "api/message_notice";
    String API_NOTIDICATION_DETAIL = "api/notice_details";
    String API_GET_STATUS = "api/getstatus";

    /**
     * 设置是否自动购买(暂时未被使用，客户端只做本地保存)
     *
     * @return
     */
    @POST(API_SET_AUTOBUY)
    Observable<ResponseModel> setAutoBuy(@Body RequestBody requestBody);

    /**
     * 我的打赏猜你喜欢
     *
     * @return
     */
    @GET(API_LIKEBOOKINFO)
    Observable<BookListResponse> getLikeBookList();

    /**
     * 评论点赞
     */
    @POST(API_REVIEW_ZAN)
    Observable<CommonStatusResponse> favorComment(@QueryMap Map<String, Object> parames);

    /**
     * 获取详情页猜你喜欢列表
     */
    @GET(API_GUESS_YOUR_LIKE)
    Observable<BookListDataResponse> getLikeBookList(@QueryMap Map<String, Object> parames);

    /**
     * 查询漫画收藏状态
     */
    @GET(API_CHECK_COLLECT)
    Observable<CommonStatusResponse> getCollectStatus(@Query("id") long id);

    /**
     * 查询用户漫画点赞状态
     */
    @GET(API_CHECK_DIANZAN)
    Observable<CommonStatusResponse> getFavorStatus(@Query("id") long id);


    /**
     * 必看榜单
     *
     * @return
     */
    @GET(API_RANKING_LIST)
    Observable<BookListDataResponse> getRankListByAction(@Query("action") String action,
                                                         @Query("page") int pageNum,
                                                         @Query("length") int pageSize);

    /**
     * 免费专区
     *
     * @return
     */
    @GET(API_FREE)
    Observable<BookListDataResponse> getFree(@Query("page") int pageNum,
                                             @Query("length") int pageSize);

    /**
     * 限时免费(包含本周免费和下周预告)
     *
     * @return
     */
    @GET(API_FOR_FREE)
    Observable<BookListDataResponse> getForFree(@QueryMap Map<String, Object> parames);

    /**
     * 首页推荐
     *
     * @return
     */
    @GET(API_RECOMMENDED)
    Observable<BookListRecommondResponse> getRecommond(@Query("channel") int channelFlag);

    /**
     * 首页最热分享
     *
     * @return
     */
    @GET(API_POP_SHARE)
    Observable<BookListPopShareResponse> getPopShare(@Query("channel") int channelFlag);

    /**
     * 最近更新
     *
     * @return
     */
    @GET(API_RECENT_UPDATES)
    Observable<BookListDataResponse> getRecentUpdate(@Query("channel") int channelFlag,
                                                     @Query("page") int pageNum,
                                                     @Query("length") int pageSize);


    /**
     * banner
     *
     * @return
     */
    @GET(API_CAROUSEL)
    Observable<BannerResponse> getBanner();


    /**
     * 搜索漫画数据
     *
     * @return 获取发现页面热门搜索数据
     */
    @GET(API_HOT)
    Observable<SearchHotKeywordsResponse> getHotSearchKeywords();

    /**
     * 获取根据关键字搜索到的漫画列表
     */
    @GET(API_SEARCH)
    Observable<BookListResponse> getSearchComicListByKeywords(@Query("search") String keyWord);

    /**
     * 获取大家都在看的漫画数据
     *
     * @return
     */
    @GET(API_WATCHING)
    Observable<BookListResponse> getWatchingComicData();


    /*************************************************content 内容模块 *************************************************************************/

    @GET(MH_WXMP_WEBAPI_WX_PARAM)
    Observable<WxModel> getQcordImage();


    /**
     * 根据专区id获取专区数据列表
     *
     * @return
     */
    @GET(API_SECTION_RECOMMENDED)
    Observable<BookListDataResponse> getSectionListBySectionId(@Query("page") int pageNum, @Query(
            "length") int pageSize, @Query("is_top") int sectionId);

    /**
     * 根据内容mainContentID获取章节列表
     *
     * @return
     */
    @GET(API_CATALOGUE)
    Observable<BookCatalogListResponse> getCatalogList(@QueryMap HashMap<String, Object> parames);

    @GET(API_NEW_CATALOGUE)
    Observable<BookCatalogListResponse> getNewCatalogList(@QueryMap HashMap<String, Object> parames);

    @GET(API_READ)
    Observable<BookCatalogContentResponse> getCatalogContent(@QueryMap Map<String, Object> parames);

    /**
     * 获取书籍详情
     *
     * @return
     */
    @GET(API_CARTOON_INFO)
    Observable<BookModelResponse> getContentDetail(@Query("id") long id);

    /**
     * 获取用户收藏列表
     *
     * @return
     */
    @POST(API_COLLECTION)
    Observable<CollectionResponse> getCollectionByUserId(@Body RequestBody body);


    /**
     * 提交评论
     *
     * @param body
     * @return
     */
    @POST(API_REVIEW)
    Observable<CommonStatusResponse> sendComment(@Body RequestBody body);


    /**
     * 根据主内容id获取当前内容的评论列表
     *
     * @return
     */
    @GET(API_REVIEW_LIST)
    Observable<CommentListResponse> getCommentListByContent(@QueryMap Map<String, Object> parames);

/*************************************************以上是 content 内容模块 *************************************************************************/

//    @POST("data.webapi/subId_query")
//    Flowable<CatalogModelProxy> getCatalog2(@Body RequestBody body);


/******************************************************  User 模块******************************************************************************/
    /**
     * 获取验证码
     *
     * @param body
     * @return
     */
    @POST(API_SEND_CODE)
    Observable<GetCodeResponse> getSecurityCode(@Body RequestBody body);

    /**
     * 修改手机号获取验证码
     *
     * @param body
     * @return
     */
    @POST(API_ALTER_PHONE_SEND_CODE)
    Observable<ResponseModel> getPhoneCode(@Body RequestBody body);

    /**
     * 验证码登录,免注册
     *
     * @param body
     * @return
     */
    @POST(API_MLOGIN)
    Observable<LoginResponse> loginBySecurityCode(@Body RequestBody body);

    /**
     * 获取用户信息.可以是第三方登录后根据第三方平台openid首次获取,也可以是根据登录的后用户token刷新用户信息
     *
     * @return
     */
    @POST(API_GET_USERINFO)
    Observable<UserInfoResponse> getUserInfo();

    /**
     * 获取用户账户余额vip等支付相关信息
     */
    @POST(API_GET_USER_ACCOUNT)
    Observable<PayInfoResponse> getUserPayInfo();

    /**
     * 添加收藏
     *
     * @param body
     * @return
     */
    @POST(API_COLLECT)
    Observable<CommonStatusResponse> collect(@Body RequestBody body);

    /**
     * 取消收藏
     *
     * @param body
     * @return
     */
    @POST(API_COLLECTDEL)
    Observable<CommonStatusResponse> delCollect(@Body RequestBody body);


    /**
     * 删除阅读历史记录
     *
     * @param body
     * @return
     */
    @POST(API_READINGHISTORYDEL)
    Observable<CommonStatusResponse> deleteReadRecord(@Body RequestBody body);


    /**
     * 查询充值记录
     */
    @POST(API_PAYLOG)
    Observable<RecharegeRecordsResponse> getRechargeRecord(@Body RequestBody body);

    /**
     * 查询消费记录
     *
     * @param body
     * @return
     */
    @POST(API_CONSUME)
    Observable<ExpenseSumRecordsResponse> getConsumptionRecord(@Body RequestBody body);

    /**
     * 订阅,购买章节
     *
     * @return
     */
    @GET(API_BUY_BOOK)
    Observable<CommonStatusResponse> subscribe(@QueryMap Map<String, Object> parames);

    /**
     * 获取充值金币价格列表
     *
     * @return
     */
    @GET(API_VOUCHERGOLD)
    Observable<RechargeCoinResponse> getRechargeCoinList();


    /**
     * 微信登录
     *
     * @param body
     * @return
     */
    @POST(API_APP_WXLOGIN)
    Observable<LoginResponse> wxLogin(@Body RequestBody body);

    /**
     * QQ登录
     *
     * @param body
     * @return
     */
    @POST(API_QQ_LOGIN)
    Observable<LoginResponse> qqLogin(@Body RequestBody body);

    /**
     * 微博登录
     *
     * @param body
     * @return
     */
    @POST(API_APP_WEIBO_LOGIN)
    Observable<LoginResponse> wbLogin(@Body RequestBody body);


    /**
     * 打赏
     *
     * @return
     */
    @POST(API_BOOK_REWARD)
    Observable<CommonStatusResponse> doReward(@Body RequestBody body);


    /**
     * 获取当前用户的评论记录
     *
     * @param body
     * @return
     */
    @POST(USER_WEBAPI_QUERY_USER_COMMENT)
    Observable<CommentListResponse> getCommentsRecordByUser(@Body RequestBody body);


    /**
     * 删除评论
     *
     * @param body
     * @return
     */
    @POST(USER_WEBAPI_DELETE_COMMENT)
    Observable<ResponseModel> deleteCommentByIds(@Body RequestBody body);


    /**
     * 上传照片
     *
     * @param file
     * @param productCode
     * @param userId
     * @return
     */
    @Multipart
    @POST(USER_WEBAPI_HEADIMG_UPLOAD)
    Observable<HeadImg> uploadImage(@Part MultipartBody.Part file, @Query("productCode") String productCode, @Query("userId") long userId);


    /**
     * 更新用户信息
     *
     * @param body
     * @return
     */
//    @POST(USER_WEBAPI_ALERT_USERINFO)
//    Observable<UserInfoResponse> updateUserInfo(@Body RequestBody body);


    /**
     * 点赞 漫画内容
     *
     * @param build
     * @return
     */
    @POST(API_DIANZAN)
    Observable<CommonStatusResponse> favorContent(@Body RequestBody build);


    /**
     * 绑定手机号
     *
     * @param body
     * @return
     */
    @POST(USER_WEBAPI_BIND_SECURITY_MOBILE)
    Observable<ResponseModel> bindMobile(@Body RequestBody body);

    /**
     * 修改手机号
     *
     * @param body
     * @return
     */
    @POST(USER_ALTER_MOBILE)
    Observable<LoginResponse> alterMobile(@Body RequestBody body);


    /**
     * 获取用户阅读历史记录列表
     *
     * @return
     */
    @GET(API_READINGHISTORY)
    Observable<CollectionResponse> getHistoryList();


    /**
     * 同步阅读历史
     *
     * @param body
     * @return
     */
    @POST(API_SYNCHRONIZATION)
    Observable<CommonStatusResponse> syncHistory(@Body RequestBody body);

/*********************************************** 用户行为 Behavior 模块*************************************************************************/
    /**
     * 用户行为上报
     *
     * @param body
     * @return
     */
    @POST("http://api.08.biedese.cn/behavior.webapi/user_action_report" + Constants.IDENTIFICATION_IGNORE)
    Observable<ResponseModel> reportAction(@Body RequestBody body);

/*********************************************** 以上是用户行为 Behavior 模块*************************************************************************/


/*********************************************  Task 模块  ****************************************************************/


    /**
     * 金币大赠送
     *
     * @return
     */
    @POST(API_APP_LOGIN_GIVE)
    Observable<CommonStatusResponse> presentGold();


    /**
     * 分享上报
     *
     * @return
     */
    @POST(API_APP_SHARE)
    Observable<CommonStatusResponse> reportShare();

    /**
     * 新手任务领取金币
     *
     * @param body
     * @return
     */
    @POST(API_GETGOLD)
    Observable<ResponseModel> getNewGold(@Body RequestBody body);

    /**
     * 每日任务领取金币
     *
     * @param body
     * @return
     */
    @POST(API_DAYGET)
    Observable<ResponseModel> getDayGold(@Body RequestBody body);

/***************************************************      以上是 Task 模块      *******************************************************************************/

/**************************************************     Product 模块    *****************************************************************/
    /**
     * 根据协议code获取用户协议
     *
     * @param productCode
     * @param protocolCode
     * @return
     */
    @GET("http://api.08.biedese.cn/product.webapi/product_protocol" + Constants.IDENTIFICATION_IGNORE)
    Observable<ProtocalModel> getProductProtocalByProtocolCode(@Query("productCode") String productCode, @Query("protocolCode") String protocolCode);

    /**
     * 获取漫画分类类型
     *
     * @return
     */
    @GET(API_CATEGORY)
    Observable<CategoryResponse> getCategories();

    /**
     * 获取漫画分类类型
     *
     * @return
     */
    @GET(API_CATEGORY)
    Observable<SortListResponse> getSortList(@Query("name") String name);

    /**
     * 分类查询漫画
     *
     * @return
     */
    @GET(API_BOOK_CATEGORY)
    Observable<BookListDataResponse> getBookCategories(@QueryMap Map<String, Object> params);

    /**
     * 分类查询漫画
     *
     * @return
     */
    @GET(API_BOOK_CATEGORY)
    Observable<BookListDataResponse> getBookCategories(@Query("page") int page, @Query("length") int length, @Query("cid") long cid);

    /**
     * 提交反馈
     *
     * @param body
     * @return
     */
    @POST(API_SUGGESTION)
    Observable<ResponseModel> uploadFeedback(@Body RequestBody body);


    /**
     * 产品分渠道升级
     *
     * @param productCode
     * @param channelId
     * @return
     */
    @GET("http://api.08.biedese.cn/product.webapi/channel_update" + Constants.IDENTIFICATION_IGNORE)
    Observable<UpdateModelProxy> getChannelUpdateInfo(@Query("productCode") String productCode, @Query("channelId") String channelId);


    /**
     * 获取分享参数
     *
     * @param build
     * @return
     */
    @POST("http://api.08.biedese.cn/product.webapi/get_share_parameter" + Constants.IDENTIFICATION_IGNORE)
    Observable<ShareParamModel> getShareParam(@Body RequestBody build);


    /**
     * 获取应用内广告
     *
     * @param channelId
     * @param adId
     * @return
     */
    @GET("http://api.08.biedese.cn/ad_api/ad_query_forapp" + Constants.IDENTIFICATION_IGNORE)
    Observable<Push> getAdsPush(@Query("yc") String channelId, @Query("adId") String adId);

    @Streaming
    @GET
    Flowable<ResponseBody> updateApp(@Url String url);

    /**
     * 获取应用消息公告列表
     *
     * @param pageNum
     * @return
     */
    @GET(API_NOTIDICATION_LIST)
    Observable<NotificationListResponse> getNotificationList(@Query("page") int pageNum);

    /**
     * 获取应用消息公告详情
     *
     * @return
     */
    @GET(API_NOTIDICATION_DETAIL)
    Observable<NotificationResponse> getNotificationDetail(@Query("noticeid") long id);

/****************************************************** 以上是 product 模块********************************************************************************/

    /****************************************以下 是Goods 模块 ***************************************************************************/


    /**
     * 兑换码兑换金币
     *
     * @param body
     * @return
     */
    @POST(ACTIVITY_WEBAPI_GOLD_EXCHANGE)
    Observable<ResponseModel> goldExchange(@Body RequestBody body);

    /**
     * 微信下单
     *
     * @param body
     * @return
     */
    @POST(API_WECHAT_ORDER)
    Observable<PrePayOrderResponseWx> prePayWx(@Body RequestBody body);

    /**
     * 支付宝下单
     *
     * @param body
     * @return
     */
    @POST(API_ALIPAY_APP)
    Observable<PrePayOrderResponseAli> prePayAli(@Body RequestBody body);


    /**
     * 汇付宝下单
     *
     * @param body
     * @return
     */
    @POST(API_HUIFUBAO_APP)
    Observable<PrePayOrderResponseHuifubao> prePayHuifubao(@Body RequestBody body);

    /**
     * 获取app配置信息，域名形式获取
     *
     * @return cartoon-novel.jjmh114.cn 测试
     * cartoon-novel.jishusaice.cn 线上
     * cartoon_novel.langd88.cn 线上唯一,专门用来请求获取app配置
     */
    @GET("http://" + Constants.HOST_APP_CONFIG + "/api/get_app_config" + Constants.IDENTIFICATION_IGNORE)
    Observable<AppConfigResponse> getAppConfig();

    @GET("http://" + Constants.HOST_TEST + "/api/get_app_config" + Constants.IDENTIFICATION_IGNORE)
    Observable<AppConfigResponse> getDebugAppConfig();

    /**
     * 获取app配置信息,IP形式获取
     * 测试ip地址:118.126.73.104
     * 生产ip地址:129.211.152.153
     *
     * @return
     */
    @GET("http://129.211.152.153:80/api/get_app_config" + Constants.IDENTIFICATION_IGNORE)
    Observable<AppConfigResponse> getAppConfigByIP();

    /**
     * 获取支付活动弹窗信息
     *
     * @return
     */
    @POST("api/get_alert_info")
    Observable<PayActionResponse> getPayAction();

    /**
     * 获取充值中心数据
     *
     * @return
     */
    @GET("api/app_pay_setting")
    Observable<PaySettingResponse> getPayCenterInfo(@Query("theme_type") String themeType, @Query("type") String type);


    /**
     * 通用的下载文件
     *
     * @param url
     * @return
     */
    @GET
    @Streaming
    Flowable<ResponseBody> downloadFile(@Url String url);

    /**
     * 通用的下载文件
     *
     * @param url
     * @return
     */
    @GET
    @Streaming
    Observable<ResponseBody> downloadChapter(@Url String url);

    /**
     * 获取oss 上传配置
     *
     * @return
     */
    @GET("api/oss_sign")
    Observable<OSSResponse> getOSSConfig();

    @POST("api/update_user")
    Observable<LoginResponse> updateUserInfo(@Body RequestBody requestBody);

    /**
     * 分享成功上报接口
     *
     * @param requestBody
     * @return
     */
    @POST("api/share")
    Observable<CommonStatusResponse> reportShare(@Body RequestBody requestBody);

    @POST("api/article_details")
    Observable<ConsumeDetailListResponse> getConsumeDetail(@Body RequestBody requestBody);

    /**
     * 我的返利列表
     *
     * @param body
     * @return
     */
    @POST("api/rebatelist")
    Observable<RebateListResponse> getRebateList(@Body RequestBody body);

    /**
     * 我的提现
     *
     * @return
     */
    @GET("api/withdraw_list")
    Observable<CashOutListResponse> getCashOutList(@Query("page") int page);

    /**
     * 添加支付宝提现方式
     *
     * @param body
     * @return
     */
    @POST("api/withdrawal_alipay")
    Observable<AddCashOutWayResponse> addCashOutAli(@Body RequestBody body);

    /**
     * 添加银行卡体现方式
     *
     * @param body
     * @return
     */
    @POST("api/withdrawal_bank")
    Observable<AddCashOutWayResponse> addCashOutUnion(@Body RequestBody body);

    /**
     * 获取添加提现方式状态
     *
     * @param body
     * @return
     */
    @POST("api/withd_status")
    Observable<CashOutWayResponse> getCashOutWayStatus(@Body RequestBody body);

    /**
     * 第三方登录的获取验证码(包括验证是否绑定)
     *
     * @param requestBody
     * @return
     */
    @POST("api/binding_code")
    Observable<ResponseModel> getThirdLoginCode(@Body RequestBody requestBody);

    /**
     * 第三方登录绑定手机号
     */
    @POST("api/create_user")
    Observable<LoginResponse> bindPhone(@Body RequestBody requestBody);

    /**
     * 申请提现
     *
     * @param body
     * @return
     */
    @POST("api/withdrawal")
    Observable<CashOutResponse> cashOut(@Body RequestBody body);

    /**
     * 我的购买
     *
     * @param body
     * @return
     */
    @POST("api/my_purchase")
    Observable<BoughtResponse> myBought(@Body RequestBody body);

    /**
     * 获取徒弟列表
     *
     * @return
     */
    @GET("api/my_disciple")
    Observable<ApprenticeListResponse> getApprenticeList(@Query("page") int page, @Query("type") int type);

    /**
     * 获取分享推荐
     *
     * @return
     */
    @GET("api/recommend")
    Observable<ShareRecommendResponse> getShareRecommend();

    /**
     * 通联支付宝
     *
     * @param body
     * @return
     */
    @POST("api/alipay_app")
    Observable<TLPayResponse> getTLPay(@Body RequestBody body);

    /**
     * 通联支付宝订单状态
     *
     * @param body
     * @return
     */
    @POST("api/checkorder")
    Observable<TLPayStatusResponse> getTLPayStatus(@Body RequestBody body);

    /**
     * 消息挂件和自动购买设置
     *
     * @return
     */
    @POST("api/rest")
    Observable<RestResponse> getRest();
    /******************************** 下面为用户在线时长上报相关接口 ************************************/

    /**
     * 获取游客token
     */
    @FormUrlEncoded
    @POST(Constants.REPORT_URL + "api/oauth/token" + Constants.IDENTIFICATION_IGNORE)
    Observable<AccessTokenResponse> getReportToken(@FieldMap Map<String, String> map);

    /**
     * 获取游客token
     */
    @FormUrlEncoded
    @POST(Constants.REPORT_URL_TEST + "api/oauth/token" + Constants.IDENTIFICATION_IGNORE)
    Observable<AccessTokenResponse> getReportTestToken(@FieldMap Map<String, String> map);

    /**
     * 上报注册用户
     */
    @POST(Constants.REPORT_URL + "api/account/app/user_duration_report" + Constants.IDENTIFICATION_IGNORE)
    Observable<ResponseModel> userReport(@Body RequestBody requestBody);

    /**
     * 上报注册用户
     */
    @POST(Constants.REPORT_URL_TEST + "api/account/app/user_duration_report" + Constants.IDENTIFICATION_IGNORE)
    Observable<ResponseModel> userTestReport(@Body RequestBody requestBody);

    /**
     * 上报游客
     */
    @POST(Constants.REPORT_URL + "api/account/app/visitor_duration_report" + Constants.IDENTIFICATION_IGNORE)
    Observable<ResponseModel> visitorReport(@Body RequestBody requestBody);

    /**
     * 上报游客
     */
    @POST(Constants.REPORT_URL_TEST + "api/account/app/visitor_duration_report" + Constants.IDENTIFICATION_IGNORE)
    Observable<ResponseModel> visitorTestReport(@Body RequestBody requestBody);

    /**
     * 获取徒弟列表
     *
     * @return
     */
    @GET(API_GET_STATUS)
    Observable<StatusResponse> getStatus(@Query("appVersion") String appVersion);
}
