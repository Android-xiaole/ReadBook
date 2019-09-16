package com.jj.comics.ui.mine.rebate.detail;

import com.jj.base.mvp.IView;
import com.jj.base.net.NetError;
import com.jj.comics.data.model.RebateListResponse;

import java.util.List;

public interface RebateContract {
    interface IRebateView extends IView {
        void onGetRebateListSucc(List<RebateListResponse.DataBeanX.RebateModel> list);

        void onGetRebateListFail(NetError error);
    }

    interface IRebatePresenter {
        void getRebateList(int page);
    }
}
