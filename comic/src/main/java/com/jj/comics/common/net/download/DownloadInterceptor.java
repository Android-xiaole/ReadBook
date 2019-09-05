package com.jj.comics.common.net.download;

import com.jj.base.utils.FileUtil;

import java.io.File;
import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class DownloadInterceptor implements Interceptor {

    private DownloadProgressListener listener;
//    private long startPos;

    public DownloadInterceptor(DownloadProgressListener listener, File file) {
        this.listener = listener;
//        startPos = FileUtil.readFile(file);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        //断点续传，暂时先不做这个功能
//        Request request = chain.request().newBuilder().addHeader("Range", "bytes=" + startPos + "-").build();
        Response originalResponse = chain.proceed(chain.request());
        return originalResponse.newBuilder()
                .body(new ProgressResponseBody(originalResponse, listener))
                .build();
    }
}
