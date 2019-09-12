package com.jj.comics.ui.sort;

import com.jj.base.mvp.IView;
import com.jj.base.net.NetError;
import com.jj.comics.data.model.BookModel;

import java.util.List;

public class SortListContract {
    interface ISortView extends IView {
        //加载类型的回调
        void fillNoverList(List<BookModel> bookModels, long totalSize);

        //加载列表类型失败
        void getListFail(NetError netError);
    }

    interface ISortPresenter {

        //获取分类数据
        void loadNoverList(int page, int length, long cid);
    }
}
