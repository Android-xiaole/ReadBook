package com.jj.comics.ui.mine.rebate;

import com.jj.base.mvp.IView;
import com.jj.base.net.NetError;
import com.jj.comics.data.model.PayInfo;

public interface MyRebateContract {

    interface IMyRebateView extends IView {
        void onGetMyRebateInfo(PayInfo payInfo);

        void onGetMyRebateInfoFail(NetError error);
    }

    interface IMyRebatePresenter {
        void getMyRebateInfo();
    }
}
