package com.jj.base.imageloader;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.jj.sdk.GlideApp;
import com.jj.sdk.GlideRequest;
import com.jj.sdk.GlideRequests;
import com.umeng.analytics.MobclickAgent;

import java.io.File;

import androidx.annotation.Nullable;

public class GlideLoader implements ILoader {
    private static final int RES_NONE = -1;
    private ImageProvider mImageProvider;

    @Override
    public void init(ImageProvider imageProvider) {
        mImageProvider = imageProvider;
    }

    public ImageProvider getImageProvider() {
        return mImageProvider;
    }

    @Override
    public void loadNet(ImageView target, String url, RequestOptions options) {
        Object model = mImageProvider == null || mImageProvider.configHeader() == null ? url : new GlideUrl(url, mImageProvider.configHeader());
        load(getRequestManager(target.getContext()).load(model).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> t, boolean isFirstResource) {
                MobclickAgent.onEvent(target.getContext(), "img_download_fail",url);
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                return false;
            }
        }), target, options);
    }

    @Override
    public void loadNet(Context context, String url, RequestOptions options, SimpleTarget target) {
        final GlideRequest request = getRequestManager(context).asFile().load(mImageProvider == null || mImageProvider.configHeader() == null ? url : new GlideUrl(url, mImageProvider.configHeader()));
        if (options == null) {
            options = new RequestOptions().placeholder(mImageProvider == null ? RES_NONE : mImageProvider.getLoadingResId())
                    .error(mImageProvider == null ? RES_NONE : mImageProvider.getLoadErrorResId());
        }

        if (options.getErrorId() == 0 && mImageProvider != null && mImageProvider.getLoadErrorResId() > 0) {
            options.error(mImageProvider.getLoadErrorResId());
        }
        if (options.getPlaceholderId() == 0 && mImageProvider != null && mImageProvider.getLoadingResId() > 0) {
            options.placeholder(mImageProvider.getLoadingResId());
        }
        request.apply(options)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .listener(new RequestListener() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target target, boolean isFirstResource) {
                        MobclickAgent.onEvent(context,"img_download_fail",url);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Object resource, Object model, Target target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                })
                .into(target);
    }

    @Override
    public void loadDrawable(Context context, String url, RequestOptions options, Target<Drawable> target) {
        getRequestManager(context)
                .asDrawable()
                .load(mImageProvider == null || mImageProvider.configHeader() == null ? url : new GlideUrl(url, mImageProvider.configHeader()))
                .apply(options)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        MobclickAgent.onEvent(context,"img_download_fail",url);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                })
                .into(target);
    }

    @Override
    public void loadFile(Context context, String url, RequestOptions options, Target<File> target) {
        getRequestManager(context).asFile().load(url).apply(options).into(target);
    }

    @Override
    public File down(Context context, String url, RequestOptions options) {
        try {
            return getRequestManager(context).asFile()
                    .load(mImageProvider == null || mImageProvider.configHeader() == null ? url : new GlideUrl(url, mImageProvider.configHeader()))
                    .apply(options)
                    .submit().get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void loadResource(ImageView target, int resId, RequestOptions options) {
        load(getRequestManager(target.getContext()).load(resId), target, options);
    }

    @Override
    public void loadAssets(ImageView target, String assetName, RequestOptions options) {
        load(getRequestManager(target.getContext()).load("file:///android_asset/" + assetName), target, options);
    }

    @Override
    public void loadFile(ImageView target, File file, RequestOptions options) {
        load(getRequestManager(target.getContext()).load(file), target, options);
    }

    @Override
    public void loadUri(ImageView target, Uri uri, RequestOptions options) {
        load(getRequestManager(target.getContext()).load(uri), target, options);
    }

    @Override
    public void clearMemoryCache(Context context) {
        Glide.get(context).clearMemory();
    }

    @Override
    public void clearDiskCache(Context context) {
        Glide.get(context).clearDiskCache();
    }

    @Override
    public void trimMemory(Context context, int level) {
        Glide.get(context).trimMemory(level);
    }

    @Override
    public void clear(Context context, View view) {
        getRequestManager(context).clear(view);
    }

    @Override
    public void resume(Context context) {
        getRequestManager(context).resumeRequests();
    }

    @Override
    public void pause(Context context) {
        getRequestManager(context).pauseRequests();
    }

    private GlideRequests getRequestManager(Context context) {
        if (context instanceof Activity) {
            return GlideApp.with((Activity) context);
        }
        return GlideApp.with(context);
    }

    private void load(GlideRequest<Drawable> request, ImageView target, RequestOptions options) {
        if (options == null) {
            options = new RequestOptions().placeholder(mImageProvider == null ? RES_NONE : mImageProvider.getLoadingResId())
                    .error(mImageProvider == null ? RES_NONE : mImageProvider.getLoadErrorResId());
        }
        if (options.getErrorId() == 0 && mImageProvider != null && mImageProvider.getLoadErrorResId() > 0) {
            options.error(mImageProvider.getLoadErrorResId());
        }
        if (options.getPlaceholderId() == 0 && mImageProvider != null && mImageProvider.getLoadingResId() > 0) {
            options.placeholder(mImageProvider.getLoadingResId());
        }
        request.apply(options)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .into(target);
    }
}
