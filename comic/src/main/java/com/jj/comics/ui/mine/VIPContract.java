package com.jj.comics.ui.mine;

import android.app.Activity;

import com.jj.base.mvp.IPresenter;
import com.jj.base.mvp.IView;
import com.jj.base.net.NetError;
import com.jj.comics.data.model.GoodsPriceModel;
import com.jj.comics.data.model.ProductPayTypeEnum;
import com.jj.comics.data.model.SelectionWithGood;
import com.jj.comics.data.model.Tasks;
import com.jj.comics.data.model.VIPListResponse;

import java.util.List;

public interface VIPContract {

    interface IVIPView extends IView {
        //获取Vip信息列表的回调
        void fillData(List<VIPListResponse.DataBean> vipListResponses);

        //vip 数据加载失败
        void dataFail(NetError error);

        //支付失败的回调
        void payFail(String msg);

        //支付成功的回调
        void onPaySuccess();

        void dismissLoading();
    }

    interface IVIPPresenter {
        //获取vip列表信息
        void getNewVIPList(boolean evict);

        void payWx(Activity activity, long goodsId, long book_id);

        void payAli(Activity activity, long goodsId, long book_id);

        void payHuifubao(Activity activity, long goodsId, long book_id);
    }
}
