package com.jj.sdk;

import android.content.Context;

import androidx.annotation.NonNull;

import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.cache.MemorySizeCalculator;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.request.RequestOptions;

@GlideModule
public final class MyAppGlideModule extends AppGlideModule {
    @Override
    public void applyOptions(@NonNull Context context, @NonNull GlideBuilder builder) {
        MemorySizeCalculator.Builder memorySizeCalculator = new MemorySizeCalculator.Builder(context).setMemoryCacheScreens(1).setBitmapPoolScreens(1);
        builder.setMemorySizeCalculator(memorySizeCalculator);

        RequestOptions requestOptions = new RequestOptions();
//        requestOptions.format(DecodeFormat.PREFER_RGB_565);
        builder.setDefaultRequestOptions(requestOptions);
    }

    @Override
    public boolean isManifestParsingEnabled() {
        return false;
    }
}
