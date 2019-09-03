package com.jj.comics.ui.mine.pay;

import com.jj.base.mvp.IView;
import com.jj.base.net.NetError;
import com.jj.comics.data.model.ExpenseSumRecordModel;

import java.util.List;

public interface PayRecordContract {

    interface IPayRecordView extends IView {
        //加载支付记录数据的回调
        void fillData(List<ExpenseSumRecordModel> expenseRecords);

        //获取支付记录数据失败的回调
        void getDataFail(NetError netError);
    }

    interface IPayRecordPresenter {
        //获取支付数据
        void loadData(int currentPage);
    }
}
