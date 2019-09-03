package com.jj.comics.ui.search;

import com.jj.base.mvp.IView;
import com.jj.comics.data.model.BookModel;

import java.util.List;

public interface SearchResultContract {

    interface ISearchResultView extends IView {

        //填充搜索结果数据
        void fillSearchComicListByKeywords(List<BookModel> contentList);

        //填充大家都在看的数据
        void fillWatchingComicData(List<BookModel> contentList);

        void onComplete();
    }

    interface ISearchResultPresenter {

        //获取搜索结果数据
        void getSearchComicListByKeywords(String keyWord);

        //获取大家都在看的数据
        void getWatchingComicData();
    }
}
