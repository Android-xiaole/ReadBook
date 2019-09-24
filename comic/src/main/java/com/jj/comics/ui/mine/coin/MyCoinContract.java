package com.jj.comics.ui.mine.coin;

import com.jj.base.mvp.IView;
import com.jj.comics.data.model.PayInfo;

public class MyCoinContract {

    interface IMyCoinView extends IView {
        void onGetUserPayInfo(PayInfo payInfo);
    }

    interface IMyCoinPresenter {
        void getUserPayInfo();
    }
}
