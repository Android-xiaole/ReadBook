package com.jj.comics.data.biz.content;


import com.jj.base.net.ComicApiImpl;
import com.jj.base.net.NetError;
import com.jj.base.net.RetryFunction2;
import com.jj.comics.common.constants.Constants;
import com.jj.comics.common.net.ComicApi;
import com.jj.comics.common.net.RequestBodyBuilder;
import com.jj.comics.data.model.BannerResponse;
import com.jj.comics.data.model.BookCatalogContentResponse;
import com.jj.comics.data.model.BookCatalogListResponse;
import com.jj.comics.data.model.BookListDataResponse;
import com.jj.comics.data.model.BookListPopShareResponse;
import com.jj.comics.data.model.BookListRecommondResponse;
import com.jj.comics.data.model.BookListResponse;
import com.jj.comics.data.model.BookModelResponse;
import com.jj.comics.data.model.CategoryResponse;
import com.jj.comics.data.model.CollectionResponse;
import com.jj.comics.data.model.CommentListResponse;
import com.jj.comics.data.model.CommonStatusResponse;
import com.jj.comics.data.model.OSSResponse;
import com.jj.comics.data.model.RewardListResponse;
import com.jj.comics.data.model.SearchHotKeywordsResponse;
import com.jj.comics.data.model.SortListResponse;
import com.jj.comics.data.model.UserInfo;
import com.jj.comics.util.LoginHelper;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.rx_cache2.DynamicKey;
import io.rx_cache2.EvictDynamicKey;
import okhttp3.RequestBody;

/**
 * 获取漫画内容的model层
 */
public class ContentRepository implements ContentDataSource {
    private static ContentRepository INSTANCE;

    private ContentRepository() {
    }

    public static ContentRepository getInstance() {
        if (INSTANCE == null) INSTANCE = new ContentRepository();
        return INSTANCE;
    }

    @Override
    public Observable<CategoryResponse> getCategoryList() {
        return ComicApi.getApi().getCategories()
                .retryWhen(new RetryFunction2())
                .compose(ComicApiImpl.<CategoryResponse>getApiTransformer2());
    }

    @Override
    public Observable<SortListResponse> getSortList() {
        return ComicApi.getApi().getSortList()
                .retryWhen(new RetryFunction2())
                .compose(ComicApiImpl.<SortListResponse>getApiTransformer2());
    }

    @Override
    public Observable<BookListDataResponse> getContentListByType(int pageNum, int pageSize, String sectionId, String sort) {
        Map<String, Object> params = new HashMap<>();
        params.put(Constants.RequestBodyKey.PAGE_NUM, pageNum);
        params.put(Constants.RequestBodyKey.PAGE_SIZE, pageSize);
        params.put(Constants.RequestBodyKey.ORDER_BY, sort);
        params.put(Constants.RequestBodyKey.BOOK_TYPE_ID, sectionId);
        return ComicApi.getApi().getBookCategories(params)
                .retryWhen(new RetryFunction2())
                .compose(ComicApiImpl.<BookListDataResponse>getApiTransformer2());
    }

    @Override
    public Observable<BookListDataResponse> getSectionListBySectionId(int currentPage, int sectionId, boolean evict,
                                                                      String retryTag) {
        return ComicApi.getProviders().getSectionListBySectionId(ComicApi.getApi().getSectionListBySectionId(currentPage, 20, sectionId), new DynamicKey(currentPage + "   " + sectionId), new EvictDynamicKey(evict))
                .compose(ComicApiImpl.<BookListDataResponse>getApiTransformer2())
                .retryWhen(new RetryFunction2(retryTag));
    }

    @Override
    public Observable<BookCatalogListResponse> getCatalogList(long id, int pageNum, String sort) {
        HashMap<String, Object> parames = new HashMap();
        parames.put(Constants.RequestBodyKey.ID, id);
        parames.put(Constants.RequestBodyKey.PAGE_NUM, pageNum);
        parames.put(Constants.RequestBodyKey.SORT, sort);
        return ComicApi.getApi().getCatalogList(parames)
                .compose(ComicApiImpl.<BookCatalogListResponse>getApiTransformer2())
                .retryWhen(new RetryFunction2());
    }

    @Override
    public Observable<BookCatalogContentResponse> getCatalogContent(long id, long chapterid) {
        Map<String, Object> parames = new HashMap<>();
        parames.put(Constants.RequestBodyKey.BOOK_ID, id);
        parames.put(Constants.RequestBodyKey.CHAPTER_ID, chapterid);
        return ComicApi.getApi().getCatalogContent(parames)
                .compose(ComicApiImpl.<BookCatalogContentResponse>getApiTransformer2())
                .retryWhen(new RetryFunction2());
    }

    @Override
    public Observable<BookModelResponse> getContentDetail(long id, String retryTag) {
        return ComicApi.getApi().getContentDetail(id)
                .compose(ComicApiImpl.<BookModelResponse>getApiTransformer2())
                .retryWhen(new RetryFunction2(retryTag));
    }

    @Override
    public Observable<CollectionResponse> getCollectionByUserId(int pageNum, int pageSize) {
        RequestBody body = new RequestBodyBuilder()
                .addProperty(Constants.RequestBodyKey.PAGE_NUM, 1)
                .build();

        return ComicApi.getApi().getCollectionByUserId(body)
                .compose(ComicApiImpl.<CollectionResponse>getApiTransformer2())
                .retryWhen(new RetryFunction2());
    }

    @Override
    public Observable<CommonStatusResponse> sendComment(long bookId, String commentDetail) {
        UserInfo loginUser = LoginHelper.getOnLineUser();
        if (loginUser == null) {
            return Observable.error(new NetError("请先登录", NetError.AuthError));
        }
        RequestBody body = new RequestBodyBuilder()
                .addProperty(Constants.RequestBodyKey.BOOK_ID, bookId)
                .addProperty(Constants.RequestBodyKey.COMMENT_CONTENT, commentDetail)
                .build();

        return ComicApi.getApi().sendComment(body)
                .retryWhen(new RetryFunction2())
                .compose(ComicApiImpl.<CommonStatusResponse>getApiTransformer2());
    }

    @Override
    public Observable<RewardListResponse> getRewardRecordByContent(long contentId, int pageNum) {
        Map<String, Object> parames = new HashMap<>();
        parames.put(Constants.RequestBodyKey.ID, contentId);
        parames.put(Constants.RequestBodyKey.PAGE_NUM, pageNum);
        return ComicApi.getApi().getRewardRecordByContent(parames)
                .retryWhen(new RetryFunction2())
                .compose(ComicApiImpl.<RewardListResponse>getApiTransformer2());
    }

    @Override
    public Observable<CommentListResponse> getCommentListByContent(long id, int pageNum, int pageSize) {
        Map<String, Object> parames = new HashMap<>();
        parames.put(Constants.RequestBodyKey.BOOK_ID, id);
        parames.put(Constants.RequestBodyKey.PAGE_NUM, pageNum);
        parames.put(Constants.RequestBodyKey.PAGE_SIZE, pageSize);
        return ComicApi.getApi().getCommentListByContent(parames)
                .retryWhen(new RetryFunction2())
                .compose(ComicApiImpl.<CommentListResponse>getApiTransformer2());
    }

    @Override
    public Observable<SearchHotKeywordsResponse> getHotSearchKeywords(String retryTag) {
        return ComicApi.getApi().getHotSearchKeywords()
                .compose(ComicApiImpl.<SearchHotKeywordsResponse>getApiTransformer2())
                .retryWhen(new RetryFunction2(retryTag));
    }


    @Override
    public Observable<BookListResponse> getWatchingComicData(String retryTag) {
        return ComicApi.getApi().getWatchingComicData()
                .compose(ComicApiImpl.<BookListResponse>getApiTransformer2())
                .retryWhen(new RetryFunction2(retryTag));
    }

    @Override
    public Observable<BookListResponse> getSearchComicListByKeywords(String keyWord, String retryTag) {
        return ComicApi.getApi().getSearchComicListByKeywords(keyWord)
                .compose(ComicApiImpl.<BookListResponse>getApiTransformer2())
                .retryWhen(new RetryFunction2(retryTag));
    }

    @Override
    public Observable<BookListRecommondResponse> getRecommond(int channelFlag, String retryTag) {
        return ComicApi.getApi().getRecommond(channelFlag)
                .compose(ComicApiImpl.<BookListRecommondResponse>getApiTransformer2())
                .retryWhen(new RetryFunction2(retryTag));
    }

    @Override
    public Observable<BookListPopShareResponse> getPopShare(int channelFlag, String retryTag) {
        return ComicApi.getApi().getPopShare(channelFlag)
                .compose(ComicApiImpl.<BookListPopShareResponse>getApiTransformer2())
                .retryWhen(new RetryFunction2(retryTag));
    }

    @Override
    public Observable<BannerResponse> getBanner(String retryTag) {
        return ComicApi.getApi().getBanner()
                .compose(ComicApiImpl.<BannerResponse>getApiTransformer2())
                .retryWhen(new RetryFunction2(retryTag));
    }

    @Override
    public Observable<BookListDataResponse> getRecentUpdate(int channelFlag, int pageNum,
                                                            int pageSize,
                                                            String retryTag) {
        return ComicApi.getApi().getRecentUpdate(channelFlag, pageNum, pageSize)
                .compose(ComicApiImpl.<BookListDataResponse>getApiTransformer2())
                .retryWhen(new RetryFunction2(retryTag));
    }

    @Override
    public Observable<BookListDataResponse> getFree(int pageNum, int pageSize, String retryTag) {
        return ComicApi.getApi().getFree(pageNum, pageSize)
                .compose(ComicApiImpl.<BookListDataResponse>getApiTransformer2())
                .retryWhen(new RetryFunction2(retryTag));
    }

    @Override
    public Observable<BookListDataResponse> getForFree(int pageNum, int pageSize, String type) {
        Map<String, Object> parames = new HashMap<>();
        parames.put(Constants.RequestBodyKey.PAGE_NUM, pageNum);
        parames.put(Constants.RequestBodyKey.PAGE_SIZE, pageSize);
        parames.put(Constants.RequestBodyKey.COMMON_TYPE, type);
        return ComicApi.getApi().getForFree(parames)
                .compose(ComicApiImpl.<BookListDataResponse>getApiTransformer2())
                .retryWhen(new RetryFunction2());
    }

    @Override
    public Observable<BookListDataResponse> getRankListByAction(String action, int pageNum,
                                                                int pageSize, String retryTag) {
        return ComicApi.getApi().getRankListByAction(action, pageNum, pageSize)
                .compose(ComicApiImpl.<BookListDataResponse>getApiTransformer2())
                .retryWhen(new RetryFunction2(retryTag));
    }

    @Override
    public Observable<BookListResponse> getLikeBookList(String name) {
        return ComicApi.getApi().getLikeBookList()
                .compose(ComicApiImpl.<BookListResponse>getApiTransformer2())
                .retryWhen(new RetryFunction2(name));
    }

    @Override
    public Observable<BookListDataResponse> getLikeBookList(long id, int pageNum, int cid) {
        Map<String, Object> parames = new HashMap<>();
        parames.put(Constants.RequestBodyKey.BOOK_ID, id);
        parames.put(Constants.RequestBodyKey.PAGE_NUM, pageNum);
        parames.put(Constants.RequestBodyKey.BOOK_TYPE_ID, cid);
        return ComicApi.getApi().getLikeBookList(parames)
                .compose(ComicApiImpl.<BookListDataResponse>getApiTransformer2())
                .retryWhen(new RetryFunction2());
    }


    /**
     * oss 文件上传获取token
     *
     * @param name
     * @return
     */
    @Override
    public Observable<OSSResponse> getOSSConfig(String name) {
        return ComicApi.getApi().getOSSConfig()
                .compose(ComicApiImpl.<OSSResponse>getApiTransformer2())
                .retryWhen(new RetryFunction2(name));
    }



}
