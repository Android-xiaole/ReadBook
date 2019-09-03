package com.jj.comics.ui.recommend;

import com.jj.base.mvp.IView;
import com.jj.base.net.NetError;
import com.jj.comics.data.model.BookModel;

import java.util.List;

public interface RecommendLoadMoreContract {

    interface View extends IView {
        //填充获取漫画列表数据的回调
        void fillData(List<BookModel> listBeans);

        //获取漫画列表失败的回调
        void getDataFail(NetError netError);
    }

    interface Presenter {
        //根据sectionId获取漫画列表
        void getSectionDataListBySectionId(int currentPage, int sectionId, boolean evict);
    }
}
