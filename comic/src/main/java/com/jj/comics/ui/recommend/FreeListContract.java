package com.jj.comics.ui.recommend;

import com.jj.base.mvp.IView;
import com.jj.base.net.NetError;
import com.jj.comics.data.model.BookModel;

import java.util.List;

public interface FreeListContract {

    interface IFreeListView extends IView {
        //加载免费专区列表回调
        void fillData(List<BookModel> bookModelList);

        //加载列表请求失败回调
        void getDataFail(NetError netError);
    }

    interface IFreeListPresenter {
        //加载免费专区列表
        void loadData(int currentPage, boolean evict);
    }
}
