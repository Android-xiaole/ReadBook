package com.jj.comics.ui.mine.rebate.detail;

import com.jj.base.mvp.IView;
import com.jj.base.net.NetError;
import com.jj.comics.data.model.CashOutListResponse;

import java.util.List;

public interface CashOutContract {
    interface ICashOutView extends IView {
        void onGetDataSucc(List<CashOutListResponse.DataBeanX.CashOutModel> data);

        void onGetDataFail(NetError error);
    }


    interface ICashOutPresenter {
        void getCashOutList(int page);
    }
}
