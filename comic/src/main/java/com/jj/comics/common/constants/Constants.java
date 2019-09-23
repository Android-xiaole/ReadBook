package com.jj.comics.common.constants;

import com.jj.base.BaseApplication;
import com.jj.base.utils.ResourceUtil;
import com.jj.base.utils.SharedPref;

public class Constants {
    public static final int SOURCE_ID = 3;
    public static final String DB_NAME = "comic.db";
    public static final String MCH_ID = "11000";//商户号
    public static final String PRODUCT_CODE = "jjmanhua2";//新产品
    public static String CHANNEL_ID = ResourceUtil.getAppRes(BaseApplication.getApplication(), "channel_id", "").toString();//老后台渠道号

    public static String OPEN_INSTALL_URL = "http://share.sou89.cn/download.html?";
    public static String CONTENT_URL = "http://share.sou89.cn/?";
    public static String CHANNEL_ID_PHP() {
        return SharedPref.getInstance().getString("CHANNEL_ID_PHP", "");
    }//新后台渠道ID（由{#CHANNEL_ID}转换后得到）

    //QQ登录相关参数
    public static String QQ_APPID() {
        return SharedPref.getInstance().getString("QQ_APPID", "");
    }//101571758

    //微博登录相关参数
    public static String WEIBO_APP_ID() {
        return SharedPref.getInstance().getString("WEIBO_APP_ID", "");
    }//82040142

    public static final String REDIRECT_URL = "http://api.weibo.com/oauth2/default.html";
    public static final String SCOPE = "";

    //微信登录相关参数
    //猫爪wx852297c06ef6ba77  //小猫爪wxc0bcd7b1f2639665
    public static String WX_APP_ID_LOGIN() {
        return SharedPref.getInstance().getString("WX_APP_ID_LOGIN", "");
    }//wx852297c06ef6ba77

    public static String WX_APP_ID_PAY() {
        return SharedPref.getInstance().getString("WX_APP_ID_PAY", "");
    }//wxc0bcd7b1f2639665

    //友盟相关参数
    public static final String UMENG_APPKEY = "5d6ddc203fc19593b4000cd4";
    public static final String UMENG_MESSAGE_SECRET = "7e609230d58fae2e255602b6b89a5163";
    //小米推送相关参数
    public static final String MI_APPID = "2882303761517940112";
    public static final String MI_APPKEY = "5661794063112";

    //华为推送相关参数
    public static final String HUAWEI_APPID = "100635135";

    public static boolean DEBUG = ResourceUtil.getAppBooleanRes(BaseApplication.getApplication(), "is_debug",
            true);
    //接口统计开关
    public static boolean OPEN_STATISTICS = true;
    //更新APP dialog开关
    public static boolean SHOW_UPDATE_DIALOG = true;
    public static final int NET_TIMEOUT = 10 * 1000;
    public static final int LOADING_RES = 0;
    public static final int LOAD_ERROR_RES = 0;

    public static final int CACHE_TIME = 20 * 1000 * 60; //RxCache缓存时间 单位ms
    public static final int CACHE_SIZE = 50; //RxCache缓存大小 单位MB
    public static final String CACHE_PATH = BaseApplication.getApplication().getCacheDir().getAbsolutePath();

    //    public static String HOST = "cartoon-novel.jishusaice.cn";
    public static final String HOST_TEST = "cartoon-novel.ds7j0i.cn";//测试环境域名
    public static final String HOST_APP_CONFIG = "cartoon_novel.langd88.cn";//获取app配置
    //cartoon-novel.txread.net
    // cartoon-novel.jishusaice.cn
    //cartoon-novel.jjmh114.cn
    //中台域名
    public static final String HOST_PRODUCT = "api.08.biedese.cn";

    public static String BASEURL() {
        return SharedPref.getInstance().getString("BASEURL", "http://cartoon-novel.jishusaice.cn/");
    }

    public static final String IDENTIFICATION_IGNORE = "#url_ignore";//此标识忽略动态替换baseurl配置
    //    public static final String QCORD_IMG_URL = "http://manhua.dfdy5.com/static/qrcode_for_gh_f19bdc003746_258.jpg";
    public static final String QCORD_IMG_URL = "https://open.weixin.qq.com/qr/code?username=zui_yanqing";
    public static final String MEDIA_TYPE_JSON = "application/json; charset=utf-8";
    public static final String PLATFORM = "android";


    public static long LAST_READING_BOOK = 0;

    //    public static final String TOKEN_KEY = "token";
    public static final String SERVICE_PROTOCAL = "service_agreement";
//    public static final String NIGHT_BRIGHTNESS_KEY = "night_brightness";
//    public static final String DAY_BRIGHTNESS_KEY = "day_brightness";
//    public static final String BRIGHTNESS_MODE_KEY = "brightnessMode";//亮度key  true为日间模式
//    public static final String READ_MODE_KEY = "readMode";//亮度key  true为日间模式

//    public static final String SP_NO_DIALOG_FOR_DELETE_COMMENT = "SP_NO_DIALOG_FOR_DELETE_COMMENT";


    public static final String ACTIVITY_TASK_CODE = "activity_tasks";
    public static final String FREE_GOLD_TASK_CODE = "free_gold";
    public static final String DAILY_TASK_CODE = "daily_task";
    public static final String NEWBIE_TASK_CODE = "newbie_task";
    public static final String VIP_GIFTS_TASK_CODE = "vip_gifts";
    public static final String COMPLETE_ALL_TASKS = "complete_all_tasks";

    //bugly appid和debug配置
    public static final String BUGLY_APPID = "242baaf3a5";
    public static final boolean BUGLY_APPID_DEBUG = false;

    //activity间相互传值的key
    public static class IntentKey {

        public static final String BOOK_ID = "bookId";
        public static final String BOOK_CHAPTER_ID = "chapterId";
        public static final String BOOK_CATALOG_MODEL = "catalogModel";
        public static final String BOOK_MODEL = "bookModel";
        public static final String BOOK_INFO = "bookInfo";
        public static final String BOOK_PRICE = "bookPrice";

        public static final String INDEX = "index";
        public static final String ID = "id";
        public static final String KEY = "key";
        public static final String MODEL = "catalogModel";
        public static final String MAIN_CONTENT = "mainContent";
        public static final String RESULT_STATE = "RESULT_STATE";
        public static final String BUNDLE = "bundle";
        public static final String SUB_COUNT = "subCount";
        public static final String OBJECT_ID = "objectId";
        public static final String LAYOUT_ID = "layoutId";
        public static final String COUNT = "count";
        public static final String SECTION_ID = "section_id";
        public static final String CODE = "code";
        public static final String TITLE = "title";
        public static final String FROM = "from";
        public static final String ACTION = "action";

        public static final String MESSAGE_SUM = "message_sum";
        public static final String PAY_INFO = "pay_info";
        public static final String COIN = "coin";
        public static final String ALL_REBATE = "all_rebate";
        public static final String CASH_OUT_RESULT = "cash_out_result";

        public static final String PAY_TYPE = "type";

        //login
        public static final String LOGIN_TYPE = "type";
        public static final String LOGIN_OPENID = "openid";
        //添加提现方式
        public static final String CASH_OUT_ALI = "ali";
        public static final String CASH_OUT_BANK = "bank";
        public static final String CASH_OUT_WAY = "way";

    }

    //SharedPreferences存值key
    public static class SharedPrefKey {
        //第一次打开应用
        public static final String FIRST_OPEN = "first_open";
        public static final String REMIND_PAY_RECORD = "remind_pay_record";
        //同意产品协议
        public static final String AGREE_KEY = "AGREE";
        //用户token
        public static final String TOKEN = "token";
        //分享图片
        public static final String SHARE_IMG_KEY = "SHARE_IMG";
        public static final String SHARE_HOST_KEY = "SHARE_HOST";
        public static final String SHARE_HOST = "jjxs518.cn";
        public static final String FEED_BAAK = "FEED_BAAK";

        public static final String BRIGHTNESS_MODE_KEY = "brightnessMode";//亮度key  true为日间模式
        public static final String AUTO_BUY = "auto_buy";
        public static final String SWITCH_GPRS_READ_REMIND = "gprs_read_remind";
        public static final String SWITCH_ACCEPT_NOTIFICATION = "accept_notification";
        public static final String SWITCH_VOLUME_PAGE = "volume_page";
        public static final String SP_NO_DIALOG_FOR_DELETE_COMMENT = "SP_NO_DIALOG_FOR_DELETE_COMMENT";
        public static final String AD_PUSH_ID = "ad_push_id";
        public static final String MAIN_PUSH = "main_push";
        public static final String INVITE_CODE = "invite_code";//通过openinstall传过来的邀请码
    }

    //获取网络数据 传参key字段名
    //首页漫画推荐
    public static final String AD_ID_132 = "132";
    //首页弹窗提醒
    public static final String AD_ID_133 = "133";
    //客服联系方式
    public static final String AD_ID_129 = "129";

    public static class RequestBodyKey {
        /**
         * -------------------php api key-------------------------
         */
        public static final String SOURCEID = "sourceid";//来源：猫爪-3，金桔-4
        public static final String DEVICE = "device";//操作系统 android
        public static final String CHANNEL_ID = "channelid";//渠道ID
        public static final String TOKEN = "Authorization";//token
        public static final String PACKAGE = "app_package_no";//token

        /* login key */
        public static final String LOGIN_PHONE_NUMBER = "phone_number";
        public static final String LOGIN_CODE = "code";
        public static final String LOGIN_TYPE = "type";
        public static final String LOGIN_INVITE_CODE = "invitecode";
        public static final String LOGIN_OPENID = "openid";

        public static final String ID = "id";
        public static final String ARTICLE_ID = "articleid";

        public static final String PAGE_NUM = "page";
        public static final String PAGE_SIZE = "length";

        public static final String GIFT_NUM = "num";
        public static final String GIFT_TYPE = "type";
        public static final String BOOK_ID = "book_id";
        public static final String CHAPTER_ID = "chapter_id";
        public static final String BOOK_TYPE_ID = "cid";//该本漫画所属分类的id
        public static final String COMMON_TYPE = "type";
        public static final String COMMENT_TYPE_ADD = "add";//点赞评论
        public static final String COMMENT_TYPE_SUB = "sub";//取消点赞
        public static final String COMMENT_CONTENT = "content";
        public static final String SORT_ASC = "asc";
        public static final String SORT_DESC = "desc";
        public static final String TYPE_THIS_WEEK = "this_week";
        public static final String TYPE_NEXT_WEEK = "next_week";
        public static final String ORDER_BY = "order_by";


        //首页男女频切换标识
        public static final int CONTENT_CHANNEL_FLAG_ALL = 0;
        public static final int CONTENT_CHANNEL_FLAG_MAN = 1;
        public static final int CONTENT_CHANNEL_FLAG_WOMAN = 2;
        /**
         * ------------------ java api key-------------------------
         */
        public static final String IMEI = "imei";
        public static final String IMSI = "imsi";
        public static final String PLATFORM = "platform";
        //        public static final String TOKEN = "token";
        public static final String PRODUCT_CODE = "productCode";
        //        public static final String CHANNEL_ID = "channelId";
        public static final String CONTENT_ID = "contentId";
        public static final String IS_MAIN = "isMain";
        public static final String USER_ID = "userId";
        public static final String TASK_CODE = "taskCode";
        public static final String EXPENSE_TYPE = "expenseType";
        public static final String OBJECT_ID = "objectId";
        public static final String CURRENCY_CNT = "currencyCnt";
        public static final String EXPENSE_TOKEN = "expenseToken";
        public static final String GOODS = "goods";
        public static final String GOODS_DESC = "goodsDesc";
        public static final String MAIN_ID = "mainId";
        public static final String SUB_ID = "subId";
        public static final String CURR_SUB_SEQ = "currSubSeq";
        public static final String ADD_SUB_SEQ = "addSubSeq";
        public static final String OBJECT_TYPE = "objectType";
        public static final String APPRAISE_TYPE = "appraiseType";
        public static final String COMMENT_DETAIL = "commentDetail";
        public static final String SORT = "sort";
        public static final String SORT_TYPE = "sortType";
        public static final String TYPE_1_CODE = "type1Code";
        public static final String TYPE_2_CODE = "type2Code";
        public static final String PAGE_sIZE = "pagesize";
        public static final String IS_MIAN = "isMian";
        public static final String REQUEST_LIST = "requestList";
        public static final String OBJECT_SUB_ID = "objectSubId";
        public static final String BROWSE_TIME = "browseTime";
        public static final String INFO = "info";
        public static final String RECORD = "record";
        public static final String BROWSE_RECORD_IDS = "browseRecordIds";
        public static final String REWARD_CODE = "rewardCode";
        public static final String REWARD_COUNT = "rewardCount";
        public static final String SECTION_ID = "is_top";
        public static final String PAGE_CODE = "pageCode";
        public static final String MOBILE = "mobile";
        public static final String ACCOUNT = "account";
        public static final String PASSWORD = "password";
        public static final String SECURITY_CODE = "securityCode";
        public static final String OPEN_ID_WAY = "openidWay";
        public static final String OPEN_ID_KEY = "openidKey";
        public static final String OPEN_ID = "openid";
        public static final String APP_NAME = "appName";
        public static final String AMOUNT_CURRENCY = "amountCurrency";
        public static final String WAP_NAME = "wapName";
        public static final String WAP_URL = "wapUrl";
        public static final String AMOUNT = "amount";
        public static final String PRICING_CURRENCY = "pricingCurrency";
        public static final String PRODUCT_ORDER_NO = "productOrderNo";
        public static final String EXTRA3 = "extra3";
        public static final String GOODS_ID = "goodsId";
        public static final String EXTRA = "extra";
        public static final String IS_TEST = "isTest";
        public static final String SUBJECT = "subject";
        public static final String SUBJECT_DESC = "subjectDesc";
        public static final String PAY_TYPE = "payType";
        public static final String PLUGIN_CODE = "pluginCode";
        public static final String PN = "pn";
        public static final String IP = "ip";
        public static final String APP_VERSION = "appVersion";
        public static final String MOBILE_FACTORY = "mobileFactory";
        public static final String MOBILE_TYPE = "mobileType";
        public static final String CLIENT_INFO = "clientInfo";
        public static final String MCH_ID = "mchId";
        public static final String SCENE_TYPE_ENUM = "sceneTypeEnum";
        public static final String SCENE_INFO = "sceneInfo";
        public static final String PRODUCT_REQUEST_ORDER = "productRequestOrder";
        public static final String SECURITY_MOBILE = "securityMobile";
        public static final String NEW_MOBILE = "newMobile";
        public static final String EXCHANGE_CODE = "exchangeCode";
        public static final String USER = "user";
        public static final String COMMENT_IDS = "commentIds";
        public static final String FEEDBACK_DESC = "content";
        public static final String CONTACT1 = "contact1";
        public static final String CONTACT2 = "contact2";
        public static final String CONTACT3 = "contact3";
        public static final String CONTACT4 = "contact4";
        public static final String REWARD = "Reward";
        public static final String CONTENT_NAME_LIKE = "contentNameLike";
        public static final String CONTENT_IDS = "contentIds";
        public static final String AVATAR = "avatar";
        public static final String NICKNAME = "nickname";
        public static final String SEX = "sex";
        public static final String PHONE_NUMBER = "phone_number";
        public static final String CODE = "code";
    }

    //时间格式
    public static class DateFormat {
        public static final String YMDHMS = "yyyy-MM-dd HH:mm:ss";
        public static final String _YMDHMS = "yyyyMMddHHmmss";
        public static final String YMDHM = "yyyy-MM-dd HH:mm";
        public static final String YMD = "yyyy-MM-dd";
        public static final String HM = "HH:mm";
    }

    //任务上报参数
    public static class TaskCode {
        public static final String COLLECT = "collect";
        public static final String LIKE = "like";
        public static final String COMMENT = "comment";
        public static final String NEW_COMMENT = "new_comment";
        public static final String GIVE_REWARD = "give_reward";
        public static final String ACTIVITY_TASK_CODE = "activity_tasks";
        public static final String FREE_GOLD_TASK_CODE = "free_gold";
        public static final String DAILY_TASK_CODE = "daily_task";
        public static final String NEWBIE_TASK_CODE = "newbie_task";
        public static final String VIP_GIFTS_TASK_CODE = "vip_gifts";
        public static final String SVIP_DAILY = "SVIP_daily";
        public static final String OPEN_90_UP_VIP = "open_90_up_vip";
        public static final String SIGN_IN = "signin";
        public static final String SIGN_CHECK = "check";
        public static final String ACTIVE_VIP = "active_vip";
        public static final String COMPLETE_ALL_TASKS = "complete_all_tasks";
        public static final String SHARE = "share";
        public static final String INVITE = "invite";
        public static final String READING = "reading";
        public static final String TYPE = "type";
    }

    //友盟事件相关参数
    public static class UMEventId {
        //访问个人中心
        public static final String ACCESS_USER_CENTER = "accessUserCenter";
        //访问金币中心
        public static final String ACCESS_GOLD_CENTER = "accessGoldCenter";
        //访问会员中心
        public static final String ACCESS_USER_PAY = "accessUserPay";
        //微博登录
        public static final String WB_LOGIN = "wb_login";
        //QQ登录
        public static final String QQ_LOGIN = "login_qq";
        //QQ好友分享
        public static final String QQ_SHARE = "QQShare";
        //微信好友分享
        public static final String WECHAT_FRIEND_SHARE = "WeChatFriendShare";
        //点击banner
        public static final String CLICK_BANNER = "clickBanner";
        //访问充值中心
        public static final String ACCESS_REHARGE_CENTER = "accessRehargeCenter";
        //微博分享
        public static final String WEIBO_SHARE = "WeiboShare";
        //微信朋友圈分享
        public static final String WECHAT_MOMENTS_SHARE = "WeChatMomentsShare";
        //手机号登录
        public static final String PHONE_LOGIN = "phone_login";
        //UID登录
        public static final String UID_LOGIN = "uid_login";
        //微信登录
        public static final String WX_LOGIN = "wx_login";
        //QQ空间分享
        public static final String QZONE_SHARE = "QZoneShare";
        //榜单点击事件
        public static final String RANK = "rank";
        //分类列表点击事件
        public static final String CLASSIFY = "classify";
        //本周头牌点击事件
        public static final String WEEK_TOP = "WeekTop";
        //宅男专区点击事件
        public static final String OTAKU_DISTRICT = "OtakuDistrict";
        //少女恋爱点击事件
        public static final String GRIL_LOVE = "GrilLove";
        //抢看新作点击事件
        public static final String NEW_COMIC = "NewComic";
        //惊悚悬疑点击事件
        public static final String TERROR_COMIC = "TerrorComic";
        //进入漫画详情页
        public static final String ENTER_DETAIL = "enter_detail";
        //进入漫画阅读页
        public static final String ENTER_READ = "enter_read";
        //退出漫画阅读界面
        public static final String EXIT_READ = "exit_read";
        //点击详情页收藏按钮
        public static final String COLLECT_DETAIL = "collect_detail";
        //点击详情页打赏按钮
        public static final String REWARD_DETAIL = "reward_detail";
        //点击详情页评论按钮
        public static final String COMMENT_DETAIL = "comment_detail";
        //点击阅读页收藏按钮
        public static final String COLLECT_READ = "collect_read";
        //点击阅读页打赏按钮
        public static final String REWARD_READ = "reward_read";
        //点击阅读页评论按钮
        public static final String COMMENT_READ = "comment_read";
        //点击阅读页点赞按钮
        public static final String ARISE_READ = "arise_read";
        //点击充值中心立即充值
        public static final String CLICK_RECHARGE_CENTER_PAY = "click_recharge_center_pay";
        //付费弹窗点击立即阅读
        public static final String CLICK_SUBSCRIBE_CONFIRM = "click_subscribe_confirm";
        //付费弹窗点击关闭
        public static final String CLICK_SUBSCRIBE_CLOSE = "click_subscribe_close";
        //订阅时余额不足点击充值
        public static final String CLICK_SUBSCRIBE_PAY = "click_subscribe_pay";

        //充值中心点击跳转会员中心
        public static final String CLICK_GO_VIP_FROM_RECHARGE = "click_go_vip_from_recharge";
        //点击搜索
        public static final String CLICK_SEARCH = "click_search";
        //内容阅读时间
        public static final String READ_TIME = "read_time";
        //点击购买vip
        public static final String CLICK_BUY_VIP = "click_buy_vip";
        //阅读页弹窗收藏
        public static final String COLLECT_READ_POP = "collect_read_pop";
        //加载图片失败
        public static final String IMG_DOWNLOAD_FAIL = "img_download_fail";
        public static final String PAY_TYPE = "pay_type";
        public static final String PAY_RESULT = "pay_result";
        public static final String HAS_ALIPAY = "has_alipay";
        public static final String USER_INSTALL_ALIPAY = "user_install_alipay";
    }

    // oss相关配置
    public static final String OSS_ENDPOINT = "oss-cn-shanghai.aliyuncs.com";
    public static final String BUCKET_NAME = "fanlixiaoshuo";
    public static final String BUCKET_URL = "https://fanlixiaoshuo.oss-cn-shanghai.aliyuncs.com/";

    public static class CASH_OUT_WAY {
        public static final int ALIPAY = 1;
        public static final int UNION = 2;
    }

    public static class APPRENTICE {
        public static final int TUZI = 1;
        public static final int TUSUN = 2;
    }
}
