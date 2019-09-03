package com.jj.comics.ui.search;

import com.jj.base.mvp.IView;
import com.jj.comics.data.model.BookModel;
import com.jj.comics.data.model.SearchHotKeywordsResponse;

import java.util.List;

public interface SearchContract {

    interface ISearchView extends IView {

        //填充热门搜索的数据
        void fillHotSearchKeywords(SearchHotKeywordsResponse response);

        //填充大家都在看的数据
        void fillWatchingComicData(List<BookModel> contentList);

        void onComplete();

    }

    interface ISearchPresenter {

        //获取热门搜索的数据
        void getHotSearchKeywords();

        //获取大家都在看的数据
        void getWatchingComicData();

    }
}
