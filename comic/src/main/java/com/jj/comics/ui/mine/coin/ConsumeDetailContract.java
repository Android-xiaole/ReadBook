package com.jj.comics.ui.mine.coin;

import com.jj.base.mvp.IView;
import com.jj.base.net.NetError;
import com.jj.comics.data.model.ConsumeDetailListResponse;

import java.util.List;


public class ConsumeDetailContract {
    interface IConsumeDetailView extends IView {
        void getDataFail(NetError error);

        void fillData(List<ConsumeDetailListResponse.DataBeanX.ConsumeDetail> contentList);
    }

    interface IConsumeDetailPresenter {
        void getConsumeDetail(long bookid);
    }
}
