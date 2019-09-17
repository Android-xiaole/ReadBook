package com.jj.comics.ui.mine.rebate.cashout;

import com.jj.base.mvp.IView;

public interface DoCashOutContract {
    interface IDoCashOutView extends IView {
        void cashOutComplete(boolean succ,String msg);
    }

    interface IDoCashOutPresenter {
        void cashOut(int type,float money);
    }
}
