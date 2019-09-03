package com.jj.comics.ui.recommend;

import com.jj.base.mvp.IView;
import com.jj.base.net.NetError;
import com.jj.comics.data.model.BookModel;

import java.util.List;

public interface FreeComicContract {

    interface IRecentlyView extends IView {
        //填充数据
        void fillData(List<BookModel> bookModel);

        //获取数据失败
        void getDataFail(NetError error);

        void collectSuccess(BookModel bookModel);
    }

    interface IRecentlyPresenter {
        //获取数据
        void loadData(int pageNum,String type);

        //收藏
        void collect(BookModel bookModel);
    }
}
