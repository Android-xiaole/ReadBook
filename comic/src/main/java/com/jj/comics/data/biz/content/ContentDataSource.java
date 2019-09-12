package com.jj.comics.data.biz.content;

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

import io.reactivex.Observable;

public interface ContentDataSource {

    //获取当前分类类型列表
    Observable<CategoryResponse> getCategoryList();

    //获取当前分类类型数据
    Observable<SortListResponse> getSortList(String name);

    //根据分类code获取当前分类下的数据列表
    Observable<BookListDataResponse> getContentListByType(int pageNum, int pageSize, String sectionId,String sort);

    //根据专区id获取当前专区下的数据列表
    Observable<BookListDataResponse> getSectionListBySectionId(int currentPage, int sectionId, boolean evict,String retryTag);

    //根据内容漫画id获取章节列表
    Observable<BookCatalogListResponse> getCatalogList(long id);

    //根据章获取章节内容详情
    Observable<BookCatalogContentResponse> getCatalogContent(long id,long chapterid);

    //获取内容详情,附加点赞数，评论数，收藏数等用户相关信息
    Observable<BookModelResponse> getContentDetail(long id, String retryTag);

    //获取用户收藏列表
    Observable <CollectionResponse> getCollectionByUserId(int pageNum, int pageSize);

    //提交评论
    Observable<CommonStatusResponse> sendComment(long bookId, String commentDetail);

    //根据主内容id获取打赏用户列表
    Observable<RewardListResponse> getRewardRecordByContent(long contentId, int pageNum);

    //根据主内容id获取当前内容评论列表
    Observable<CommentListResponse> getCommentListByContent(long id, int pageNum, int pageSize);

    //获取banner
    Observable<BannerResponse> getBanner(String retryTag);

    //获取最近更新
    Observable<BookListDataResponse> getRecentUpdate(int channelFlag,int pageNum, int i,
                                                     String retryTag);

    //发现页面热门搜索
    Observable<SearchHotKeywordsResponse> getHotSearchKeywords(String retryTag);

    //发现页面大家都在看的漫画数据
    Observable<BookListResponse> getWatchingComicData(String retryTag);


    Observable<BookListResponse> getSearchComicListByKeywords(String keyWord,String retryTag);

    //获取首页推荐
    Observable<BookListRecommondResponse> getRecommond(int channelFlag,String retryTag);

    //获取首页最热分享
    Observable<BookListPopShareResponse> getPopShare(int channelFlag,String retryTag);

    //免费专区
    Observable<BookListDataResponse> getFree(int pageNum, int pageSize, String retryTag);

    //本周限免、下周预告
    Observable<BookListDataResponse> getForFree(int pageNum,int pageSize,String type);

    //必看榜单
    Observable<BookListDataResponse> getRankListByAction(String action,int pageNum, int pageSize,
                                                       String retryTag);
    //我的打赏猜你喜欢
    Observable<BookListResponse> getLikeBookList(String name);
    //详情页的猜你喜欢
    Observable<BookListDataResponse> getLikeBookList(long id,int pageNum,int cid);

    //获取oss 配置
    Observable<OSSResponse> getOSSConfig(String name);

    Observable<BookListDataResponse> getBookCategories(int pageNum, int pageSize, long cid);

}
