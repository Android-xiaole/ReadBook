package com.jj.comics.ui.bookshelf.history;

import com.jj.base.mvp.IView;
import com.jj.base.net.NetError;
import com.jj.comics.data.model.BookModel;

import java.util.List;

public interface HistoryContract {

    interface IHistoryView extends IView {
        //加载历史记录列表的回调
        void onLoadHistoryList(List<BookModel> data);

        //加载历史记录列表失败的回调
        void onLoadHistoryListFail(NetError error);

        //加载为你推荐列表的回调
        void onLoadRecommendList(List<BookModel> bookModelList);

        //加载为你推荐列表失败的回调
        void getFooterDataFail(NetError error);

        //删除完成的回调
        void onDeleteComplete();
    }

    interface IHistoryPresenter {
        //获取历史记录列表
        void getHistoryList();

        //获取为你推荐列表
        void loadRecommendData();

        //上传历史记录
        void uploadRecord();

        //阅读漫画
        void toRead(final BookModel bookModel, final long chapterid);

        //删除历史记录
        void deleteHistory(List<BookModel> delete);
    }
}
