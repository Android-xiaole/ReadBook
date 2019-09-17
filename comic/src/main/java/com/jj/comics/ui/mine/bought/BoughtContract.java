package com.jj.comics.ui.mine.bought;

import com.jj.base.mvp.IView;
import com.jj.base.net.NetError;
import com.jj.comics.data.model.BookModel;
import com.jj.comics.data.model.BoughtResponse;

import java.util.List;

public interface BoughtContract {

    interface IBoughtView extends IView {
        void onLoadBoughtList(List<BoughtResponse.DataBeanX.BoughtModel> data);

        void onLoadBoughtListFail(NetError error);
    }

    interface IBoughtPresenter {
        void getBoughtList(int page);
    }
}
