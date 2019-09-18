package com.jj.comics.adapter.mine;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.jj.comics.ui.dialog.BottomPayDialog;
import com.jj.comics.R;
import com.jj.comics.data.model.VIPListResponse;
import com.jj.comics.ui.mine.VIPActivity;
import com.jj.comics.ui.mine.VIPPresenter;

import java.util.List;

/**
 * 会员列表适配器
 */
public class VIPAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {
    private Context mContext;
    private VIPPresenter mVIPPresenter;
    public static final int TYPE_LEVEL_0 = 0;
    public static final int TYPE_LEVEL_1 = 1;

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public VIPAdapter(VIPPresenter ivip, Context context, List<MultiItemEntity> data) {
        super(data);
        mContext = context;
        mVIPPresenter = ivip;
        //加载一级列表布局
        addItemType(TYPE_LEVEL_0, R.layout.comic_vip_type_item);
        addItemType(TYPE_LEVEL_1, R.layout.comic_vip_adapter_item);
    }

    /**
     * 填充数据
     *
     * @param helper viewholder
     * @param item   item 数据
     */
    @Override
    protected void convert(BaseViewHolder helper, final MultiItemEntity item) {
        switch (helper.getItemViewType()) {
            case TYPE_LEVEL_0:
                VIPListResponse.DataBean dataBean = (VIPListResponse.DataBean) item;
                helper.setText(R.id.comic_vip_item_title, dataBean.getDuration() + "天会员");
                break;
            case TYPE_LEVEL_1:
                final VIPListResponse.DataBean.ListBean listBean = (VIPListResponse.DataBean.ListBean) item;
                helper.setText(R.id.comic_vip_adapter_item_name, listBean.getVip_level() == 1 ? "VIP会员" : "SVIP会员");
                helper.setText(R.id.comic_vip_adapter_item_desc, listBean.getDescription());
                helper.setText(R.id.comic_vip_adapter_item_price, listBean.getPrice());
                helper.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showBottomDialog(v, listBean.getId());
                    }
                });
                helper.getView(R.id.comic_vip_adapter_item_buy).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showBottomDialog(v, listBean.getId());
                    }
                });
                break;
        }
    }

    /**
     * 支付对话框
     *
     * @param view
     */
    private void showBottomDialog(View view, final long goodsid) {
        final BottomPayDialog bottomPayDialog = new BottomPayDialog();
        bottomPayDialog.showBottomPop((Activity) mContext, view, new BottomPayDialog.AliPayOnClickListener() {
            @Override
            public void onClick(View v) {
                bottomPayDialog.dismiss();
                ((VIPActivity) mContext).showProgress();
                mVIPPresenter.payAli((Activity) mContext, goodsid, 1);
            }
        }, new BottomPayDialog.WeChatOnClickListener() {
            @Override
            public void onClick(View v) {
                bottomPayDialog.dismiss();
                ((VIPActivity) mContext).showProgress();
                mVIPPresenter.payWx((Activity) mContext, goodsid, 1);
            }
        }, new BottomPayDialog.CancelOnClickListener() {
            @Override
            public void onClick(View v) {
                bottomPayDialog.dismiss();
            }
        });
    }
}
