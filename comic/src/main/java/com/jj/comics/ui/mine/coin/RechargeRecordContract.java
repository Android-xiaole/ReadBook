package com.jj.comics.ui.mine.coin;

import com.jj.base.mvp.IView;
import com.jj.base.net.NetError;
import com.jj.comics.data.model.RechargeRecordModel;

import java.util.List;

public interface RechargeRecordContract {

    interface IRechargeRecordView extends IView {
        //加载消费记录列表的回调
        void fillData( List<RechargeRecordModel> rechargeRecords);

        //加载消费记录列表失败的回调
        void getDataFail(NetError netError);
    }

    interface IRechargeRecordPresenter {
        //加载消费列表
        void loadData(int currentPage);
    }
}
