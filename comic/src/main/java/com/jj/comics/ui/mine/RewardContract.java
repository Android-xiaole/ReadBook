package com.jj.comics.ui.mine;

import com.jj.base.mvp.IView;
import com.jj.base.net.NetError;
import com.jj.comics.data.model.BookModel;
import com.jj.comics.data.model.RewardHistoryResponse;

import java.util.List;

public interface RewardContract {

    interface IRewardView extends IView {
        //刷新猜你喜欢列表的回调
        void fillHotData(List<BookModel> bookModelList);

        //刷新猜你喜欢列表失败的回调
        void refreshFail(NetError netError);

        //获取打赏记录列表的回调
        void fillRewardData(RewardHistoryResponse reward);
    }

    interface IRewardPresenter  {
        //刷新猜你喜欢列表
        void refreshHot(int page);

        //获取打赏记录列表
        void getRewardsHistory(boolean evict);
    }
}
