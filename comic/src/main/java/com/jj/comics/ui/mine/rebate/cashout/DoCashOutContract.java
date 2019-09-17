package com.jj.comics.ui.mine.rebate.cashout;

import com.jj.base.mvp.IView;
import com.jj.comics.data.model.CashOutResponse;

public interface DoCashOutContract {
    interface IDoCashOutView extends IView {
        void cashOutComplete(CashOutResponse.DataBean dataBean);

        void cashOutFail(String message);
    }

    interface IDoCashOutPresenter {
        void cashOut(int type,float money);
    }
}
