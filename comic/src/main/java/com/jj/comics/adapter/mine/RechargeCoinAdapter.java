package com.jj.comics.adapter.mine;

import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jj.base.log.LogUtil;
import com.jj.base.ui.BaseActivity;
import com.jj.base.utils.CommonUtil;
import com.jj.comics.R;
import com.jj.comics.R2;
import com.jj.comics.data.model.PayCenterInfoResponse;
import com.jj.comics.data.model.ProductPayTypeEnum;
import com.jj.comics.data.model.RechargeCoinResponse;
import com.jj.comics.ui.mine.pay.PayActivity;
import com.jj.comics.util.reporter.ActionReporter;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Collection;
import java.util.List;

import androidx.annotation.NonNull;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class RechargeCoinAdapter extends BaseMultiItemQuickAdapter<PayCenterInfoResponse.PayCenterInfo, BaseViewHolder> {

    private int selectIndex = 0;

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public RechargeCoinAdapter(List<PayCenterInfoResponse.PayCenterInfo> data) {
        super(data);
        addItemType(2, R.layout.comic_pay_vip_item);
        addItemType(1, R.layout.comic_pay_shubi_item);
        setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                setSelectIndex(position);
            }
        });
    }

    public void setSelectIndex(int selectIndex) {
        if (selectIndex != this.selectIndex) {
            if (mContext instanceof BaseActivity)
                ActionReporter.reportAction(ActionReporter.Event.CHANGE_AMOUNT, "recharge",
                        getData().get(selectIndex).getPrice() + "", null);
            notifyItemChanged(selectIndex + getHeaderLayoutCount());
            notifyItemChanged(this.selectIndex + getHeaderLayoutCount());
            this.selectIndex = selectIndex;
        }
    }

    @Override
    protected void convert(BaseViewHolder helper, PayCenterInfoResponse.PayCenterInfo item) {
        helper.itemView.setSelected(false);
        helper.getView(R.id.pay_item_message).setSelected(false);
        helper.getView(R.id.pay_item_coin).setSelected(false);
        boolean selected = getData().indexOf(item) == selectIndex;
//        int price = getPrice(item.getPrice());
        switch (item.getItemType()) {
            case 1:
                helper.setText(R.id.pay_item_coin, item.getPrice() + "元");
                helper.getView(R.id.pay_item_coin).setSelected(selected);
                if (item.isDouble()) {
                    //首充
                    double price = Double.parseDouble(item.getPrice());
                    if (price >20){
                        helper.setVisible(R.id.pay_item_double, true);
                    }else{
                        helper.setVisible(R.id.pay_item_double, false);
                    }
                    helper.setText(R.id.pay_item_send, String.format(mContext.getString(R.string.comic_pay_present), item.getGiveegold()));
                    helper.getView(R.id.pay_item_send).setSelected(selected);
                    helper.setText(R.id.pay_item_message, String.format(mContext.getString(R.string.comic_pay_message_new), item.getEgold()));
                    helper.getView(R.id.pay_item_message).setSelected(selected);
                } else {
                    helper.setText(R.id.pay_item_send, String.format(mContext.getString(R.string.comic_pay_present), item.getGiveegold()));
                    helper.getView(R.id.pay_item_send).setSelected(selected);
                    helper.setGone(R.id.pay_item_double, false);
                    helper.setText(R.id.pay_item_message, String.format(mContext.getString(R.string.comic_pay_message_new), item.getEgold()));
                    helper.getView(R.id.pay_item_message).setSelected(selected);
                }
                break;
            default://默认都显示2类型,就像这个面又长又宽
                helper.setText(R.id.pay_item_coin, item.getPrice() + "元");
                helper.getView(R.id.pay_item_coin).setSelected(selected);

                helper.setText(R.id.pay_item_message, item.getTitle());
                helper.getView(R.id.pay_item_message).setSelected(selected);

                helper.setText(R.id.pay_item_info, item.getDescription());
        }
        helper.itemView.setSelected(selected);
    }

    private int getPrice(String price) {
        if (TextUtils.isEmpty(price)) return 0;
        try {
            return (int) Math.floor(Float.parseFloat(price));
        } catch (Exception e) {
            return 0;
        }
    }

    private float getPrice2(String price) {
        if (TextUtils.isEmpty(price)) return 0f;
        try {
            return Float.parseFloat(price);
        } catch (Exception e) {
            return 0f;
        }
    }

    public PayCenterInfoResponse.PayCenterInfo getSelect() {
        if (CommonUtil.checkValid(getData().size(), selectIndex))
            return getData().get(selectIndex);
        return null;
    }

}
