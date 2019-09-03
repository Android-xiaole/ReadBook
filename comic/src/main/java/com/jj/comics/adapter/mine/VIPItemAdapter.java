package com.jj.comics.adapter.mine;

import android.content.Context;
import android.view.View;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jj.comics.R;
import com.jj.comics.data.model.GoodsPriceModel;
import com.jj.comics.ui.mine.VIPPresenter;

import java.util.List;

/**
 * 会员中心二级vip列表适配器
 */
public class VIPItemAdapter extends BaseMultiItemQuickAdapter<GoodsPriceModel, BaseViewHolder> {

    private Context mContext;
    //VIP P层实现类用于吊起支付
    private VIPPresenter mVIPPresenter;

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public VIPItemAdapter(VIPPresenter ivip, Context context, List<GoodsPriceModel> data) {
        super(data);
        mContext = context;
        mVIPPresenter = ivip;
        //添加二级布局
        addItemType(1, R.layout.comic_vip_adapter_item);
    }

    /**
     * 填充数据
     *
     * @param helper
     * @param item 数据model
     */
    @Override
    protected void convert(BaseViewHolder helper, final GoodsPriceModel item) {
        helper.setText(R.id.comic_vip_adapter_item_name, item.getTitle1());
        helper.setText(R.id.comic_vip_adapter_item_desc, item.getInfo());
        helper.setText(R.id.comic_vip_adapter_item_price, item.getPrice2());
        helper.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomDialog(v, item);
            }
        });
        helper.getView(R.id.comic_vip_adapter_item_buy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomDialog(v, item);
            }
        });
    }

    /**
     * 支付对话框
     *
     *
     * @param view
     */
    private void showBottomDialog(View view, final GoodsPriceModel goodsPriceModel) {
//        final BottomPayDialog bottomPayDialog = new BottomPayDialog();
//        bottomPayDialog.showBottomPop((Activity) mContext, view, new BottomPayDialog.AliPayOnClickListener() {
//            @Override
//            public void onClick(View v) {
//                bottomPayDialog.dismiss();
//                if (goodsPriceModel != null) {
//                    ((VIPActivity) mContext).showProgress();
////                    mVIPPresenter.goPay((Activity) mContext,ProductPayTypeEnum.AliPay, goodsPriceModel);
//                }
//            }
//        }, new BottomPayDialog.WeChatOnClickListener() {
//            @Override
//            public void onClick(View v) {
//                bottomPayDialog.dismiss();
//                if (goodsPriceModel != null) {
//                    ((VIPActivity) mContext).showProgress();
////                    mVIPPresenter.goPay((Activity) mContext,ProductPayTypeEnum.WeChat, goodsPriceModel);
//                }
//            }
//        }, new BottomPayDialog.CancelOnClickListener() {
//            @Override
//            public void onClick(View v) {
//                bottomPayDialog.dismiss();
//            }
//        });
    }


}
