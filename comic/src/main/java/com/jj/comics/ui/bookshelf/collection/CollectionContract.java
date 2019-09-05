package com.jj.comics.ui.bookshelf.collection;

import com.jj.base.mvp.IView;
import com.jj.base.net.NetError;
import com.jj.comics.data.model.BookModel;
import com.jj.comics.data.model.CollectionResponse;

import java.util.List;

public interface CollectionContract {

    interface ICollectionView extends IView {
        //加载收藏列表的回调
        void onLoadCollectionList(List<BookModel> bookModels);

        //加载收藏列表收藏失败的回调
        void onLoadCollectionListFail(NetError error);

        //接在收藏列表结束的回调
        void onLoadCollectionListEnd();

        //加载为你推荐列表的回调
        void onLoadRecommendList(List<BookModel> bookModelList);

        //加载为你推荐列表失败的回调
        void onLoadRecommendListFail(NetError error);

        //删除完成的回调
        void onDeleteComplete();
    }


    interface ICollectionPresenter{
        //获取收藏列表
        void getCollectionList(int pageNum, int pageSize);

        //获取为你推荐列表
        void loadRecommendData();

        //续看
        void toRead(final BookModel bookModel, final long chapterid);

        //移除收藏
        void removeShelf(List<BookModel> delete);
    }
}
