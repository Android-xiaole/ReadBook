package com.jj.comics.ui.mine.rebate.cashout;

import com.jj.base.mvp.IView;

public interface CashOutWayContract {
    interface ICashOutWayView extends IView {
        void onGetCashOutWayStatus(boolean ali,boolean union);
    }

    interface ICashOutWayPresenter {
        void getWayStatus();
    }
}
