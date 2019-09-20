package com.jj.novelpro.app;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import androidx.multidex.MultiDex;

import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.load.model.LazyHeaders;
import com.fm.openinstall.OpenInstall;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jj.base.BaseApplication;
import com.jj.base.imageloader.ILFactory;
import com.jj.base.imageloader.ImageProvider;
import com.jj.base.log.LogUtil;
import com.jj.base.net.ApiSubscriber2;
import com.jj.base.net.ComicApiImpl;
import com.jj.base.net.NetError;
import com.jj.base.net.NetProvider;
import com.jj.base.net.RequestHandler;
import com.jj.base.utils.SharedPref;
import com.jj.comics.common.constants.Constants;
import com.jj.comics.common.net.HttpUrlInterceptor;
import com.jj.comics.common.net.gsonconvert.CommonGsonConverterFactory;
import com.jj.comics.data.biz.pruduct.ProductRepository;
import com.jj.comics.data.model.ShareParamModel;
import com.jj.comics.util.LoginHelper;
import com.jj.comics.util.SharedPreManger;
import com.jj.comics.util.eventbus.EventBusHelper;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;
import com.tencent.bugly.beta.interfaces.BetaPatchListener;
import com.tencent.bugly.beta.upgrade.UpgradeStateListener;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;

import cn.jpush.android.api.JPushInterface;
import io.reactivex.functions.Consumer;
import io.reactivex.plugins.RxJavaPlugins;
import me.jessyan.autosize.AutoSizeConfig;
import me.jessyan.autosize.unit.UnitsManager;
import okhttp3.CookieJar;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Converter;

import static com.jj.base.net.ComicApiImpl.STRING;

public class ComicApplication extends BaseApplication {
    private String img;
    private String host;

    // 友盟推送图标
    @Override
    public void init() {
//        img = getString(R.string.share_default_img);
        initBugly();
        //openinstall
        if (isMainProcess()) {
            OpenInstall.init(this);
        }
        Beta.autoCheckUpgrade = false;
        if (Constants.DEBUG) {
            ARouter.openLog();     // Print log
            ARouter.openDebug();   // Turn on debugging mode (If you are running in InstantRun mode, you must turn on debug mode! Online version needs to be closed, otherwise there is a security risk)
        }
        ARouter.init(this); // As early as possible, it is recommended to initialize in the Application
        //Glide初始化
        ILFactory.getLoader().init(new ImageProvider() {
            @Override
            public int getLoadingResId() {
                return Constants.LOADING_RES;
            }

            @Override
            public int getLoadErrorResId() {
                return Constants.LOAD_ERROR_RES;
            }

            @Override
            public LazyHeaders configHeader() {
                return null;
            }
        });

        initHttp();

        initUmeng();
        configUnits();

        getShareParams();

        //处理Rxjava全局捕获异常，防止下游终止订阅之后，上游有未处理的异常导致崩溃
        if (!Constants.DEBUG) {//如果是调试模式就不开启，这样方便排查BUG
            RxJavaPlugins.setErrorHandler(new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception {
                    LogUtil.e(this.getClass().getName() + ":Rxjava全局捕获的异常-" + throwable.getMessage());
                }
            });
        }

        // 初始化 EventBus
        EventBusHelper.init();
        //初始化极光推送
        JPushInterface.init(this);

//        DoraemonKit.install(getApplication());

        //听云SDK初始化，暂时用不到了
//        NBSAppAgent.setLicenseKey("63bff81cf51840e194014f59bf50435a").withLocationServiceEnabled(true).start(this.getApplicationContext());

    }

    private void initHttp() {
        final Gson gson = new GsonBuilder()
                //配置你的Gson
                .setDateFormat(Constants.DateFormat.YMDHMS)
                .registerTypeHierarchyAdapter(String.class, STRING)//设置解析的时候null转成""
                .create();
        //配置HttpClient
        ComicApiImpl.registerProvider(new NetProvider() {
            @Override
            public Interceptor[] configInterceptors() {
                HttpUrlInterceptor httpUrlInterceptor = new HttpUrlInterceptor();
                return new Interceptor[]{httpUrlInterceptor};
            }

            @Override
            public Converter.Factory[] configConverterFactories() {
                return new Converter.Factory[]{CommonGsonConverterFactory.create(gson)};
            }

            @Override
            public void configHttps(OkHttpClient.Builder builder) {

            }

            @Override
            public CookieJar configCookie() {
                return null;
            }

            @Override
            public RequestHandler configHandler() {
                return new RequestHandler() {
                    @Override
                    public Request onBeforeRequest(Request request, Interceptor.Chain chain) {
                        Request.Builder newRequest = request.newBuilder()
                                .header(Constants.RequestBodyKey.SOURCEID, Constants.SOURCE_ID + "")
                                .header(Constants.RequestBodyKey.CHANNEL_ID, Constants.CHANNEL_ID)
                                .header(Constants.RequestBodyKey.DEVICE, "android");
                        if (LoginHelper.getOnLineUser() != null) {
                            newRequest.header(Constants.RequestBodyKey.TOKEN, "Bearer " + SharedPreManger.getInstance().getToken());
                        }
                        return newRequest.build();
                    }
                };
            }

            @Override
            public long configConnectTimeoutMills() {
                return Constants.NET_TIMEOUT;
            }

            @Override
            public long configReadTimeoutMills() {
                return Constants.NET_TIMEOUT;
            }

            @Override
            public boolean configLogEnable() {
                return Constants.DEBUG;
            }
        });
    }

    public void getShareParams() {
        ProductRepository.getInstance().getShareParam(getApplication().getClass().getName())
                .subscribe(new ApiSubscriber2<ShareParamModel>() {
                    @Override
                    public void onNext(ShareParamModel shareParamModel) {

                        if (shareParamModel.getShareWays() != null && shareParamModel.getShareWays().size() > 0) {
                            ShareParamModel.ShareParam shareParam = shareParamModel.getShareWays().get(0);
                            if (!TextUtils.isEmpty(shareParam.getDefaultImgUrl())) {
                                img = shareParam.getDefaultImgUrl();
                            }
                            if (!TextUtils.isEmpty(shareParam.getDefaultShareLinkHost())) {
                                host = shareParam.getDefaultShareLinkHost();
                            }
                        }
                        SharedPref.getInstance(getApplicationContext()).putString(Constants.SharedPrefKey.SHARE_IMG_KEY, img);
                        SharedPref.getInstance(getApplicationContext()).putString(Constants.SharedPrefKey.SHARE_HOST_KEY, host);
                    }

                    @Override
                    protected void onFail(NetError error) {
                        SharedPref.getInstance(getApplicationContext()).putString(Constants.SharedPrefKey.SHARE_IMG_KEY, img);
                        SharedPref.getInstance(getApplicationContext()).putString(Constants.SharedPrefKey.SHARE_HOST_KEY, host);
                    }
                });
    }


    private void initUmeng() {
        //场景类型设置
        /**
         * EScenarioType.E_UM_NORMAL 普通统计场景，如果您在埋点过程中没有使用到
         U-Game统计接口，请使用普通统计场景。
         EScenarioType.E_UM_GAME 游戏场景 ，如果您在埋点过程中需要使用到U-Game
         统计接口，则必须设置游戏场景，否则所有的U-Game统计接口不会生效。
         */
        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);
        //禁止默认的页面统计功能
        UMConfigure.init(this,
                Constants.UMENG_APPKEY,
                Constants.CHANNEL_ID,
                UMConfigure.DEVICE_TYPE_PHONE,null);
        UMConfigure.setLogEnabled(Constants.DEBUG);
        UMConfigure.setEncryptEnabled(false);
        // 选用LEGACY_AUTO页面采集模式
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.LEGACY_MANUAL);

    }

    /**
     * 注意!!! 布局时的实时预览在开发阶段是一个很重要的环节, 很多情况下 Android Studio 提供的默认预览设备并不能完全展示我们的设计图
     * 所以我们就需要自己创建模拟设备, 以下链接是给大家的福利, 按照链接中的操作可以让预览效果和设计图完全一致!
     *
     * @see <a href="https://github.com/JessYanCoding/AndroidAutoSize/blob/master/README-zh.md#preview">dp、pt、in、mm 这四种单位的模拟设备创建方法</a>
     * <p>
     * v0.9.0 以后, AndroidAutoSize 强势升级, 将这个方案做到极致, 现在支持5种单位 (dp、sp、pt、in、mm)
     * {@link UnitsManager} 可以让使用者随意配置自己想使用的单位类型
     * 其中 dp、sp 这两个是比较常见的单位, 作为 AndroidAutoSize 的主单位, 默认被 AndroidAutoSize 支持
     * pt、in、mm 这三个是比较少见的单位, 只可以选择其中的一个, 作为 AndroidAutoSize 的副单位, 与 dp、sp 一起被 AndroidAutoSize 支持
     * 副单位是用于规避修改 {@link DisplayMetrics#density} 所造成的对于其他使用 dp 布局的系统控件或三方库控件的不良影响
     * 您选择什么单位, 就在 layout 文件中用什么单位布局
     * <p>
     * 两个主单位和一个副单位, 可以随时使用 {@link UnitsManager} 的方法关闭和重新开启对它们的支持
     * 如果您想完全规避修改 {@link DisplayMetrics#density} 所造成的对于其他使用 dp 布局的系统控件或三方库控件的不良影响
     * 那请调用 {@link UnitsManager#setSupportDP}、{@link UnitsManager#setSupportSP} 都设置为 {@code false}
     * 停止对两个主单位的支持 (如果开启 sp, 对其他三方库控件影响不大, 也可以不关闭对 sp 的支持)
     * 并调用 {@link UnitsManager#setSupportSubunits} 从三个冷门单位中选择一个作为副单位
     * 三个单位的效果都是一样的, 按自己的喜好选择, 比如我就喜欢 mm, 翻译为中文是妹妹的意思
     * 然后在 layout 文件中只使用这个副单位进行布局, 这样就可以完全规避修改 {@link DisplayMetrics#density} 所造成的不良影响
     * 因为 dp、sp 这两个单位在其他系统控件或三方库控件中都非常常见, 但三个冷门单位却非常少见
     */
    private void configUnits() {
        AutoSizeConfig.getInstance()

                //是否让框架支持自定义 Fragment 的适配参数, 由于这个需求是比较少见的, 所以须要使用者手动开启
                //如果没有这个需求建议不开启
                .setCustomFragment(true)

                //是否屏蔽系统字体大小对 AndroidAutoSize 的影响, 如果为 true, App 内的字体的大小将不会跟随系统设置中字体大小的改变
                //如果为 false, 则会跟随系统设置中字体大小的改变, 默认为 false
                .setExcludeFontScale(true)

        //屏幕适配监听器
//                .setOnAdaptListener(new onAdaptListener() {
//                    @Override
//                    public void onAdaptBefore(Object target, Activity activity) {
//                        //使用以下代码, 可支持 Android 的分屏或缩放模式, 但前提是在分屏或缩放模式下当用户改变您 App 的窗口大小时
//                        //系统会重绘当前的页面, 经测试在某些机型, 某些情况下系统不会重绘当前页面, ScreenUtils.getScreenSize(activity) 的参数一定要不要传 Application!!!
//                        AutoSizeConfig.getInstance().setScreenWidth(ScreenUtils.getScreenSize(activity)[0]);
//                        AutoSizeConfig.getInstance().setScreenHeight(ScreenUtils.getScreenSize(activity)[1]);
//                        LogUtils.d(String.format(Locale.ENGLISH, "%s onAdaptBefore!", target.getClass().getName()));
//                    }
//
//                    @Override
//                    public void onAdaptAfter(Object target, Activity activity) {
//                        LogUtils.d(String.format(Locale.ENGLISH, "%s onAdaptAfter!", target.getClass().getName()));
//                    }
//                })

        //是否打印 AutoSize 的内部日志, 默认为 true, 如果您不想 AutoSize 打印日志, 则请设置为 false
//                .setLog(false)

        //是否使用设备的实际尺寸做适配, 默认为 false, 如果设置为 false, 在以屏幕高度为基准进行适配时
        //AutoSize 会将屏幕总高度减去状态栏高度来做适配
        //设置为 true 则使用设备的实际屏幕高度, 不会减去状态栏高度
//                .setUseDeviceSize(true)

        //是否全局按照宽度进行等比例适配, 默认为 true, 如果设置为 false, AutoSize 会全局按照高度进行适配
//                .setBaseOnWidth(false)

        //设置屏幕适配逻辑策略类, 一般不用设置, 使用框架默认的就好
//                .setAutoAdaptStrategy(new AutoAdaptStrategy())
        ;
    }

    /**
     * 初始化bugly
     */
    private void initBugly() {
        setStrictMode();
        // 设置是否开启热更新能力，默认为true
        Beta.enableHotfix = true;
        // 设置是否自动下载补丁
        Beta.canAutoDownloadPatch = true;
        // 设置是否提示用户重启
        Beta.canNotifyUserRestart = true;
        // 设置是否自动合成补丁
        Beta.canAutoPatch = true;

        /**
         *  全量升级状态回调
         */
        Beta.upgradeStateListener = new UpgradeStateListener() {
            @Override
            public void onUpgradeFailed(boolean b) {

            }

            @Override
            public void onUpgradeSuccess(boolean b) {

            }

            @Override
            public void onUpgradeNoVersion(boolean b) {
            }

            @Override
            public void onUpgrading(boolean b) {
            }

            @Override
            public void onDownloadCompleted(boolean b) {

            }
        };

        /**
         * 补丁回调接口，可以监听补丁接收、下载、合成的回调
         */
        Beta.betaPatchListener = new BetaPatchListener() {
            @Override
            public void onPatchReceived(String patchFileUrl) {
//                Toast.makeText(getApplicationContext(), patchFileUrl, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDownloadReceived(long savedLength, long totalLength) {
//                Toast.makeText(getApplicationContext(), String.format(Locale.getDefault(),
//                        "%s %d%%",
//                        Beta.strNotificationDownloading,
//                        (int) (totalLength == 0 ? 0 : savedLength * 100 / totalLength)), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDownloadSuccess(String patchFilePath) {
//                Toast.makeText(getApplicationContext(), patchFilePath, Toast.LENGTH_SHORT).show();
//                Beta.applyDownloadedPatch();
            }

            @Override
            public void onDownloadFailure(String msg) {
//                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onApplySuccess(String msg) {
//                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onApplyFailure(String msg) {
//                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPatchRollback() {
//                Toast.makeText(getApplicationContext(), "onPatchRollback", Toast.LENGTH_SHORT).show();
            }
        };

        long start = System.currentTimeMillis();
        Bugly.setAppChannel(this, Constants.CHANNEL_ID);


        // 这里实现SDK初始化，appId替换成你的在Bugly平台申请的appId,调试时将第三个参数设置为true
        //bugly初始化
        Bugly.init(getApplicationContext(), Constants.BUGLY_APPID, Constants.BUGLY_APPID_DEBUG);
//        Bugly.setIsDevelopmentDevice(getApplicationContext(), true);//注册bugly开发设备
        long end = System.currentTimeMillis();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        // you must install multiDex whatever tinker is installed!
        MultiDex.install(base);

        // 安装tinker
        Beta.installTinker();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if (level == TRIM_MEMORY_UI_HIDDEN) {
            ILFactory.getLoader().clearMemoryCache(this);
        }
        ILFactory.getLoader().trimMemory(this, level);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        ILFactory.getLoader().clearMemoryCache(this);
    }

    @TargetApi(9)
    protected void setStrictMode() {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyLog().build());
    }

    public boolean isMainProcess() {
        int pid = android.os.Process.myPid();
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager.getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return getApplicationInfo().packageName.equals(appProcess.processName);
            }
        }
        return false;
    }
}
