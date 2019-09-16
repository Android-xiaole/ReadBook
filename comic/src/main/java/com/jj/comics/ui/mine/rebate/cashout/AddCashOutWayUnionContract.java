package com.jj.comics.ui.mine.rebate.cashout;

import com.jj.base.mvp.IView;

public interface AddCashOutWayUnionContract {

    interface IAddCashOutWayUnionView extends IView {
        void onAddComplete(boolean succ,String msg);
    }

    interface IAddCashOutWayUnionPresenter {
        void addUnion(String account_number,String opener,String opening_bank);
    }
}
