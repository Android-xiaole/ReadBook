package com.jj.comics.common.net.download;

import com.jj.base.net.ComicApiImpl;
import com.jj.base.net.ComicInterceptor;
import com.jj.base.net.LogInterceptor;
import com.jj.base.net.NetProvider;
import com.jj.base.net.RequestHandler;
import com.jj.base.ui.BaseActivity;
import com.jj.base.utils.FileUtil;
import com.jj.comics.common.net.ComicService;
import com.jj.comics.common.constants.Constants;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;

import java.io.File;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import androidx.lifecycle.Lifecycle;
import io.reactivex.FlowableSubscriber;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.CookieJar;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class DownLoadManager {
    private ComicService downloadRetrofitApiService;
    private static DownLoadManager sInstace = null;

    public static DownLoadManager builder() {
        if (sInstace == null) {
            synchronized (DownLoadManager.class) {
                sInstace = new DownLoadManager();
            }
        }
        return sInstace;
    }

    private ComicService getDownloadService(DownloadProgressListener listener, File file) {
        NetProvider provider = ComicApiImpl.getCommonProvider();
        DownloadInterceptor downloadInterceptor = new DownloadInterceptor(listener, file);
        OkHttpClient.Builder builder = new OkHttpClient.Builder().connectTimeout(provider.configConnectTimeoutMills() != 0
                ? provider.configConnectTimeoutMills()
                : ComicApiImpl.connectTimeoutMills, TimeUnit.MILLISECONDS)
                .readTimeout(provider.configReadTimeoutMills() != 0
                        ? provider.configReadTimeoutMills() : ComicApiImpl.readTimeoutMills, TimeUnit.MILLISECONDS)
                .addNetworkInterceptor(downloadInterceptor)
                .retryOnConnectionFailure(true);

        CookieJar cookieJar = provider.configCookie();
        if (cookieJar != null) {
            builder.cookieJar(cookieJar);
        }
        provider.configHttps(builder);

        RequestHandler handler = provider.configHandler();
        if (handler != null) {
            builder.addInterceptor(new ComicInterceptor(handler));
        }

        //这里不能添加额外配置的拦截器，会导致下载异常
//        Interceptor[] interceptors = provider.configInterceptors();
//        if (interceptors != null && interceptors.length > 0) {
//            for (Interceptor interceptor : interceptors) {
//                builder.addInterceptor(interceptor);
//            }
//        }

        if (provider.configLogEnable()) {
            LogInterceptor logInterceptor = new LogInterceptor();
            builder.addInterceptor(logInterceptor);
        }
        Retrofit.Builder retrofitBuilder = new Retrofit.Builder()
                .baseUrl(Constants.BASEURL())
                .client(builder.build());
        if (provider != null) {
            Converter.Factory[] factories = provider.configConverterFactories();
            if (factories != null)
                for (int i = 0; i < factories.length; i++) {
                    retrofitBuilder.addConverterFactory(factories[i]);
                }
        }
        retrofitBuilder.addConverterFactory(GsonConverterFactory.create());
        retrofitBuilder.addCallAdapterFactory(RxJava2CallAdapterFactory.create());
        downloadRetrofitApiService = retrofitBuilder.build().create(ComicService.class);
        return downloadRetrofitApiService;
    }

    //下载
    public void downApkFile(BaseActivity activity, FlowableSubscriber flowableSubscriber, String url, DownloadProgressListener listener, final File file) {
        getDownloadService(listener, file);
        downloadRetrofitApiService.updateApp(url)
                .unsubscribeOn(Schedulers.io())
                .map(new Function<ResponseBody, InputStream>() {
                    @Override
                    public InputStream apply(ResponseBody body) throws Exception {
                        return body.byteStream();
                    }
                })
                .observeOn(Schedulers.computation())
                .doOnNext(new Consumer<InputStream>() {
                    @Override
                    public void accept(InputStream inputStream) throws Exception {
                        FileUtil.writeFile(inputStream, file);
                    }
                }).subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.<InputStream>autoDisposable(AndroidLifecycleScopeProvider.from(activity.getLifecycle(), Lifecycle.Event.ON_DESTROY)))
                .subscribe(flowableSubscriber);
    }

}
