package com.jj.comics.ui.detail.subdetail;


import com.jj.base.mvp.IView;
import com.jj.base.net.NetError;
import com.jj.comics.data.model.BookCatalogModel;

import java.util.List;

public interface CatalogContract {

    interface ICatalogView extends IView {
        //加载目录列表的回调
        void onLoadCatalogList(List<BookCatalogModel> catalogModels);

        //加载目录列表失败的回调
        void onLoadCatalogListFail(NetError netError);
    }

    interface ICatalogPresenter {
        //获取目录列表
        void getCatalogList(long id, int pageNum,String sort);
    }
}
