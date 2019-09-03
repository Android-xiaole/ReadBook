package com.jj.comics.common.net;

import com.jj.comics.common.constants.Constants;
import com.jj.comics.data.model.BookCatalogListResponse;
import com.jj.comics.data.model.BookListDataResponse;
import com.jj.comics.data.model.BookListRecommondResponse;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.rx_cache2.DynamicKey;
import io.rx_cache2.EvictDynamicKey;
import io.rx_cache2.LifeCache;

public interface ComicCacheProviders {
    @LifeCache(duration = Constants.CACHE_TIME, timeUnit = TimeUnit.MILLISECONDS)
    Observable<BookListRecommondResponse> getRecommendData(Observable<BookListRecommondResponse> flowable, DynamicKey userName,
                                          EvictDynamicKey evictDynamicKey);

    @LifeCache(duration = Constants.CACHE_TIME, timeUnit = TimeUnit.MILLISECONDS)
    Observable<BookCatalogListResponse> getCatalogList(Observable<BookCatalogListResponse> observable,DynamicKey userName, EvictDynamicKey evictDynamicKey);

    @LifeCache(duration = Constants.CACHE_TIME, timeUnit = TimeUnit.MILLISECONDS)
    Observable<BookListDataResponse> getSectionListBySectionId(Observable<BookListDataResponse> sectionListBySectionId, DynamicKey dynamicKey, EvictDynamicKey evictDynamicKey);

}
