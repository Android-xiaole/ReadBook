package com.jj.comics.ui.mine.rebate.cashout;

import com.jj.base.mvp.IView;
import com.jj.comics.data.model.CashOutWayResponse;

public interface CashOutWayContract {
    interface ICashOutWayView extends IView {
        void onGetCashOutWayStatus(CashOutWayResponse cashOutWayResponse);
    }

    interface ICashOutWayPresenter {
        void getWayStatus();
    }
}
