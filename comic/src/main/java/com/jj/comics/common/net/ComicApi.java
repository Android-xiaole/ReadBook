package com.jj.comics.common.net;
import android.os.Environment;
import com.jj.base.BaseApplication;
import com.jj.base.net.ComicApiImpl;
import com.jj.comics.common.constants.Constants;
import java.io.File;
import io.rx_cache2.internal.RxCache;
import io.victoralbertos.jolyglot.GsonSpeaker;

public class ComicApi {
    private static ComicService mComicService;
    private static ComicCacheProviders mComicCache;

    public static ComicService getApi() {
        if (mComicService == null)
            synchronized (ComicApi.class) {
                if (mComicService == null)
                    mComicService = ComicApiImpl.getInstance().getRetrofit(Constants.BASEURL(), true).create(ComicService.class);
            }
        return mComicService;
    }

    public static ComicCacheProviders getProviders() {
        if (mComicCache == null)
            synchronized (ComicApi.class) {
                if (mComicCache == null) {
                    mComicCache = new RxCache.Builder()
                            .setMaxMBPersistenceCache(Constants.CACHE_SIZE)
                            .persistence(getCacheFile(), new GsonSpeaker())
                            .using(ComicCacheProviders.class);
                }
            }
        return mComicCache;
    }

    private static File getCacheFile() {
        File cacheDir = BaseApplication.getApplication().getCacheDir();
        if (cacheDir != null) {
            if (!cacheDir.exists()) cacheDir.mkdirs();
            return cacheDir;
        }
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            File externalCacheDir = BaseApplication.getApplication().getExternalCacheDir();
            if (externalCacheDir != null) {
                if (!externalCacheDir.exists()) externalCacheDir.mkdirs();
                return externalCacheDir;
            }
        }
        return new File("");
    }

}
