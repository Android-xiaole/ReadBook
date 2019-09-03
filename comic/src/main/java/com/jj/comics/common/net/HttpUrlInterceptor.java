package com.jj.comics.common.net;

import android.text.TextUtils;
import android.util.LruCache;

import com.jj.comics.common.constants.Constants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 动态替换baseurl的拦截器
 */

public class HttpUrlInterceptor implements Interceptor {

    private LruCache<String, String> mCache;

    public HttpUrlInterceptor() {
        mCache = new LruCache<>(100);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        Request.Builder builder = request.newBuilder();
        String url = request.url().toString();
        //如果url中包含忽略动态替换url的标识，不处理改url,只进行标识裁剪，还原成原始url
        if (url.contains(Constants.IDENTIFICATION_IGNORE)){
            return chain.proceed(pruneIdentification(builder,url));
        }
        HttpUrl newUrl = HttpUrl.parse(Constants.BASEURL());
        HttpUrl oldUrl = request.url();
        Request newRequest = builder.url(parseUrl(newUrl, oldUrl)).build();

        Response response = chain.proceed(newRequest);
        return response;
    }

    private HttpUrl parseUrl(HttpUrl newUrl, HttpUrl oldUrl) {
        if (null == newUrl) return oldUrl;

        HttpUrl.Builder builder = oldUrl.newBuilder();

        if (TextUtils.isEmpty(mCache.get(getKey(newUrl, oldUrl)))) {
            for (int i = 0; i < oldUrl.pathSize(); i++) {
                //当删除了上一个 index, PathSegment 的 item 会自动前进一位, 所以 remove(0) 就好
                builder.removePathSegment(0);
            }

            List<String> newPathSegments = new ArrayList<>();
            newPathSegments.addAll(newUrl.encodedPathSegments());
            newPathSegments.addAll(oldUrl.encodedPathSegments());

            for (String PathSegment : newPathSegments) {
                builder.addEncodedPathSegment(PathSegment);
            }
        } else {
            builder.encodedPath(mCache.get(getKey(newUrl, oldUrl)));
        }

        HttpUrl httpUrl = builder
                .scheme(newUrl.scheme())
                .host(newUrl.host())
                .port(newUrl.port())
                .build();

        if (TextUtils.isEmpty(mCache.get(getKey(newUrl, oldUrl)))) {
            mCache.put(getKey(newUrl, oldUrl), httpUrl.encodedPath());
        }
        return httpUrl;
    }

    private String getKey(HttpUrl newUrl, HttpUrl oldUrl) {
        return newUrl.encodedPath() + oldUrl.encodedPath();
    }

    /**
     * 将 {@code IDENTIFICATION_IGNORE} 从 Url 地址中修剪掉
     *
     * @param newBuilder {@link Request.Builder}
     * @param url        原始 Url 地址
     * @return 被修剪过 Url 地址的 {@link Request}
     */
    private Request pruneIdentification(Request.Builder newBuilder, String url) {
        String[] split = url.split(Constants.IDENTIFICATION_IGNORE);
        StringBuffer buffer = new StringBuffer();
        for (String s : split) {
            buffer.append(s);
        }
        return newBuilder
                .url(buffer.toString())
                .build();
    }
}
