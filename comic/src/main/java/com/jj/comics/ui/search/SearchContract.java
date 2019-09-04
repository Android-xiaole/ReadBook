package com.jj.comics.ui.search;

import com.jj.base.mvp.IView;
import com.jj.comics.data.model.BookModel;
import com.jj.comics.data.model.SearchHotKeywordsResponse;
import com.jj.comics.data.model.SearchModel;

import java.util.List;

public interface SearchContract {

    interface ISearchView extends IView {

        //填充热门搜索的数据
        void fillHotSearchKeywords(SearchHotKeywordsResponse response);

        void onComplete();

        //搜索历史
        void fillKeyData(List<SearchModel> searchModels);
    }

    interface ISearchPresenter {
        void loadRecentData();

        //获取热门搜索的数据
        void getHotSearchKeywords();

        //插入搜索记录
        void dealKey(SearchModel model);
    }
}
