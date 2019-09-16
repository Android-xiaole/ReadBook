package com.jj.comics.ui.mine.rebate.cashout;

import com.jj.base.mvp.IView;

public interface AddCashOutWayAliContract {

    interface IAddCashOutWayAliView extends IView {
        void onAddComplete(boolean succ,String msg);

    }

    interface IAddCashOutWayAliPresenter {
        void addAli(String account,String name);
    }
}
