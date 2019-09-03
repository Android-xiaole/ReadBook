package com.jj.comics.ui.recommend;

import com.jj.base.mvp.IView;
import com.jj.comics.data.model.RichManModel;

import java.util.List;

public interface RichManRankContract {

    interface IRichManRankView extends IView {

        //获取到打赏排行数据的回调
        void onFetchData(List<RichManModel> rewardTotalList);

        //获取到实时打赏数据的回调
        void onGetRewardNowData(List<RichManModel> rewardRecordByAllUser);
    }

    interface IRichManRankPresenter {
        //获取打赏排行
        void getRickManRankList();

        //获取实时打赏
        void getRewardNow(int pageNum);
    }
}
