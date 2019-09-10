package com.jj.comics.ui.sort;

import com.jj.base.mvp.IView;
import com.jj.base.net.NetError;
import com.jj.comics.data.model.BookModel;
import com.jj.comics.data.model.CategoryResponse;
import com.jj.comics.data.model.SortListResponse;

import java.util.List;

public interface SortContract {

    interface ISortView extends IView {
        //加载类型的回调
        void fillTypeList(List<SortListResponse.DataBean> list);

        //加载列表类型失败
        void getTypeListFail(NetError netError);
    }

    interface ISortPresenter {
        //加载类型
        void loadTypeList();
    }
}
