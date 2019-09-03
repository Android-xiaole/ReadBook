package com.jj.base.net;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.jj.base.BaseApplication;
import com.jj.base.R;
import com.jj.base.mvp.IModel;

import org.reactivestreams.Publisher;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.functions.Function;
import okhttp3.CookieJar;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by wanglei on 2016/12/24.
 */

public class ComicApiImpl {
    private static NetProvider sProvider = null;

    private Map<String, NetProvider> providerMap = new HashMap<>();
    private Map<String, Retrofit> retrofitMap = new HashMap<>();
    private Map<String, OkHttpClient> clientMap = new HashMap<>();

    public static final long connectTimeoutMills = 10 * 1000l;
    public static final long readTimeoutMills = 10 * 1000l;

    private static ComicApiImpl instance;

    private ComicApiImpl() {

    }

    public static ComicApiImpl getInstance() {
        if (instance == null) {
            synchronized (ComicApiImpl.class) {
                if (instance == null) {
                    instance = new ComicApiImpl();
                }
            }
        }
        return instance;
    }


    public static <S> S get(String baseUrl, Class<S> service) {
        return getInstance().getRetrofit(baseUrl, true).create(service);
    }

    public static void registerProvider(NetProvider provider) {
        ComicApiImpl.sProvider = provider;
    }

    public static void registerProvider(String baseUrl, NetProvider provider) {
        getInstance().providerMap.put(baseUrl, provider);
    }


    public Retrofit getRetrofit(String baseUrl, boolean useRx) {
        return getRetrofit(baseUrl, null, useRx);
    }


    /**
     * 关于retrfit添加多个解析器的说明：https://blog.csdn.net/K_Hello/article/details/81318856
     * @param baseUrl
     * @param provider
     * @param useRx
     * @return
     */
    public Retrofit getRetrofit(String baseUrl, NetProvider provider, boolean useRx) {
        if (TextUtils.isEmpty(baseUrl)) {
            throw new IllegalStateException("baseUrl can not be null");
        }
        if (retrofitMap.get(baseUrl) != null) return retrofitMap.get(baseUrl);

        if (provider == null) {
            provider = providerMap.get(baseUrl);
            if (provider == null) {
                provider = sProvider;
            }
        }
        checkProvider(provider);
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(getClient(baseUrl, provider))
                .callbackExecutor(executorService);

//        Gson gson = new GsonBuilder()
//                //配置你的Gson
//                .setDateFormat(BaseApplication.getApplication().getString(R.string.base_YMDHMS_date_format))
//                .registerTypeHierarchyAdapter(String.class, STRING)//设置解析的时候null转成""
//                .create();
//        builder.addConverterFactory(GsonConverterFactory.create(gson));

        if (provider != null) {
            Converter.Factory[] factories = provider.configConverterFactories();
            if (factories != null)
                for (int i = 0; i < factories.length; i++) {
                    builder.addConverterFactory(factories[i]);
                }
        }

        if (useRx) {
            builder.addCallAdapterFactory(RxJava2CallAdapterFactory.create());
        }

        Retrofit retrofit = builder.build();
        retrofitMap.put(baseUrl, retrofit);
        providerMap.put(baseUrl, provider);

        return retrofit;
    }

    /**
     * 自定义TypeAdapter ,null对象将被解析成空字符串
     */
    public static final TypeAdapter<String> STRING = new TypeAdapter<String>() {
        public String read(JsonReader reader) {
            try {
                if (reader.peek() == JsonToken.NULL) {
                    reader.nextNull();
                    return ""; // 原先是返回null，这里改为返回空字符串
                }
                return reader.nextString();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "";
        }

        public void write(JsonWriter writer, String value) {
            try {
                if (value == null) {
                    writer.nullValue();
                    return;
                }
                writer.value(value);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private OkHttpClient getClient(String baseUrl, NetProvider provider) {
        if (TextUtils.isEmpty(baseUrl)) {
            throw new IllegalStateException("baseUrl can not be null");
        }
        if (clientMap.get(baseUrl) != null) return clientMap.get(baseUrl);

        checkProvider(provider);

        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        builder.connectTimeout(provider.configConnectTimeoutMills() != 0
                ? provider.configConnectTimeoutMills()
                : connectTimeoutMills, TimeUnit.MILLISECONDS);
        builder.readTimeout(provider.configReadTimeoutMills() != 0
                ? provider.configReadTimeoutMills() : readTimeoutMills, TimeUnit.MILLISECONDS);

        CookieJar cookieJar = provider.configCookie();
        if (cookieJar != null) {
            builder.cookieJar(cookieJar);
        }
        provider.configHttps(builder);

        RequestHandler handler = provider.configHandler();
        if (handler != null) {
            builder.addInterceptor(new ComicInterceptor(handler));
        }

        Interceptor[] interceptors = provider.configInterceptors();
        if (interceptors != null && interceptors.length > 0) {
            for (Interceptor interceptor : interceptors) {
                builder.addInterceptor(interceptor);
            }
        }

        if (provider.configLogEnable()) {
            LogInterceptor logInterceptor = new LogInterceptor();
            builder.addInterceptor(logInterceptor);
        }

        OkHttpClient client = builder.build();
        clientMap.put(baseUrl, client);
        providerMap.put(baseUrl, provider);

        return client;
    }


    private void checkProvider(NetProvider provider) {
        if (provider == null) {
            throw new IllegalStateException("must register provider first");
        }
    }

    public static NetProvider getCommonProvider() {
        return sProvider;
    }

    public Map<String, Retrofit> getRetrofitMap() {
        return retrofitMap;
    }

    public Map<String, OkHttpClient> getClientMap() {
        return clientMap;
    }

    public static void clearCache() {
        getInstance().retrofitMap.clear();
        getInstance().clientMap.clear();
    }

    /**
     * Flowable异常处理变换
     *
     * @return
     */
    public static <T extends IModel> FlowableTransformer<T, T> getApiTransformer() {

        return new FlowableTransformer<T, T>() {
            @Override
            public Publisher<T> apply(Flowable<T> upstream) {
                return upstream.flatMap(new Function<T, Publisher<T>>() {
                    @Override
                    public Publisher<T> apply(T model) throws Exception {
                        if (model == null) {
                            return Flowable.error(NetError.noDataError());
                        } else if (model.error() != null) {
                            return Flowable.error(model.error());
                        } else {
                            return Flowable.just(model);
                        }
                    }
                });
            }
        };
    }

    /**
     * Observable异常处理变换
     *
     * @return
     */
    public static <T extends IModel> ObservableTransformer<T, T> getApiTransformer2() {

        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(Observable<T> upstream) {
                return upstream.flatMap(new Function<T, ObservableSource<T>>() {
                    @Override
                    public ObservableSource<T> apply(T model) throws Exception {
                        if (model == null) {
                            return Observable.error(NetError.noDataError());
                        } else if (model.error() != null) {
                            return Observable.error(model.error());
                        } else {
                            return Observable.just(model);
                        }
                    }
                });
            }
        };
    }


}
