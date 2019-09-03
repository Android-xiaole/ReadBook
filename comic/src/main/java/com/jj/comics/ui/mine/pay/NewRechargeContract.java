package com.jj.comics.ui.mine.pay;

import com.jj.base.mvp.IView;

public interface NewRechargeContract {

    interface INewRechargeView extends IView {

    }

    interface INewRechargePresenter {
        void loadData(int currentPage);
    }
}
