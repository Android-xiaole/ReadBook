package com.jj.comics.ui.list.rank;

import com.jj.base.mvp.IView;
import com.jj.base.net.NetError;
import com.jj.comics.data.model.BookModel;

import java.util.List;

public interface CommonRankContract {

    interface ICommonRankView extends IView {
        //填充获取漫画列表数据的回调
        void fillData(List<BookModel> listBeans);

        //获取漫画列表失败的回调
        void getDataFail(NetError netError);
    }

    interface ICommonRankPresenter {
        //获取榜单列表
        void getRankListByAction(int currentPage, String sectionId, boolean evict);
    }
}
