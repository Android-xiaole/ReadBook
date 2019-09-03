package com.jj.comics.ui.find;

import com.jj.base.mvp.IView;
import com.jj.base.net.NetError;
import com.jj.comics.data.model.BookModel;
import com.jj.comics.data.model.CategoryResponse;

import java.util.List;

public interface FindBookContract {

    interface IFindView extends IView {
        //加载类型的回调
        void fillType(List<CategoryResponse.DataBean> list);

        //加载数据列表的回调
        void fillData(long total_num,List<BookModel> listBeans);

        //加载数据列表失败的回调
        void getDataFail(NetError netError);

        //加载列表类型失败
        void getTypeFail(NetError netError);
    }

    interface IFindPresenter {
        //加载类型
        void loadType();

        //加载数据列表
        void loadList(int pageNum, long type1Code, String sort);
    }
}
