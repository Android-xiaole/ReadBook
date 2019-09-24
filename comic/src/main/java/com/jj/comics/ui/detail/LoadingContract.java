package com.jj.comics.ui.detail;

import com.jj.base.mvp.IView;
import com.jj.base.net.NetError;
import com.jj.comics.data.model.BookCatalogModel;
import com.jj.comics.data.model.BookModel;

public interface LoadingContract {

    interface View extends IView{
        void loadSuccess(BookCatalogModel catalogModel);
        void loadFail(NetError error);
    }

    interface Presenter{
        //跳转到阅读页面
        void toRead(BookModel bookModel, long chapterid);
    }
}
