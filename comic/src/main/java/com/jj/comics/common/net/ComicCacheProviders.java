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
import io.rx_cache2.ProviderKey;

public interface ComicCacheProviders {

    /**
     * 获取目录的缓存接口
     * @param cacheKey 由多种参数组成，bookId+pageNum+sort+updateTime，对应缓存每本书的目录
     * @return
     */
    @ProviderKey("CatalogCache")
//    @LifeCache(duration = Constants.CACHE_TIME, timeUnit = TimeUnit.MILLISECONDS)//不设置缓存时间默认永久
    Observable<BookCatalogListResponse> getCatalogList(Observable<BookCatalogListResponse> observable,DynamicKey cacheKey);

    @ProviderKey("BookSectionListCache")
    @LifeCache(duration = Constants.CACHE_TIME, timeUnit = TimeUnit.MILLISECONDS)
    Observable<BookListDataResponse> getSectionListBySectionId(Observable<BookListDataResponse> sectionListBySectionId, DynamicKey dynamicKey, EvictDynamicKey evictDynamicKey);

}
