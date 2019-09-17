package com.jj.comics.ui.mine.apprentice;

import com.jj.base.mvp.IView;
import com.jj.base.net.NetError;
import com.jj.comics.data.model.ApprenticeModel;

import java.util.List;

public interface TuziContract {

    interface ITuziView extends IView {
        void onGetData(List<ApprenticeModel> list);

        void onGetDataFail(NetError error);
    }

    interface ITuziPresenter {
        void getData(int page,int type);
    }
}
