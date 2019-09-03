package com.jj.comics.ui.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StrikethroughSpan;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jj.base.dialog.BaseFragmentDialog;
import com.jj.base.imageloader.ILFactory;
import com.jj.comics.R;
import com.jj.comics.data.model.BookCatalogModel;
import com.jj.comics.data.model.PayInfo;
import com.jj.comics.util.DateHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import static android.view.KeyEvent.KEYCODE_BACK;

public class RechargeNotifyDialog extends BaseFragmentDialog {

    private boolean isAutoPay;
    private DialogUtilForComic.OnDialogClickWithSelect onDialogClick;

    private TextView tv_alert_pay_remaining;
    private ImageView btn_alert_pay_now;

    private PayInfo payInfo;
    private BookCatalogModel catalogModel;

    public void show(FragmentManager manager, PayInfo payInfo, BookCatalogModel catalogModel, boolean isAutoPay,
                     DialogUtilForComic.OnDialogClickWithSelect onDialogClick) {
        super.show(manager);
        this.payInfo = payInfo;
        this.catalogModel = catalogModel;
        this.isAutoPay = isAutoPay;
        this.onDialogClick = onDialogClick;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(getContext(), R.style.comic_Dialog_no_title);
        dialog.setContentView(R.layout.comic_bg_alert_pay);

        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KEYCODE_BACK && dialog != null) {
                    dialog.dismiss();
                    getActivity().finish();
                }
                return true;
            }
        });
        TextView tv_alert_pay_desc = dialog.findViewById(R.id.tv_alert_pay_desc);
        TextView tv_alert_pay_vip = dialog.findViewById(R.id.tv_alert_pay_vip);
        tv_alert_pay_remaining = dialog.findViewById(R.id.tv_alert_pay_remaining);
        RelativeLayout btn_alert_pay_auto = dialog.findViewById(R.id.btn_alert_pay_auto);
        btn_alert_pay_now = dialog.findViewById(R.id.btn_alert_pay_now);
        ImageView btn_alert_pay_close = dialog.findViewById(R.id.btn_alert_pay_close);
        final ImageView iv_alert_pay_auto = dialog.findViewById(R.id.iv_alert_pay_auto);

        if (payInfo != null) {
            iv_alert_pay_auto.setSelected(isAutoPay);
            boolean isVip = payInfo.getIs_vip() == 1;
            String real = catalogModel.getSaleprice() + "";
            String desc = "解锁本章节仅需要 " + catalogModel.getSaleprice() + " 金币";
            if (isVip) {
                real = (int) (catalogModel.getSaleprice() * catalogModel.getVip_discount()) + " ";
                desc = "解锁本章节仅需要 " + real + catalogModel.getSaleprice() + " 金币";
            }
            SpannableString descSpan = new SpannableString(desc);
            RelativeSizeSpan sizeSpanDesc = new RelativeSizeSpan(1.7f);
            ForegroundColorSpan colorSpanDesc =
                    new ForegroundColorSpan(getContext().getResources().getColor(R.color.comic_ff4723));
            descSpan.setSpan(sizeSpanDesc, 9, 9 + real.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            descSpan.setSpan(colorSpanDesc, 9, 9 + real.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            if (isVip) {
                tv_alert_pay_vip.setVisibility(View.VISIBLE);
                StrikethroughSpan strikethroughSpan = new StrikethroughSpan();
                RelativeSizeSpan sizeSpanDesc2 = new RelativeSizeSpan(0.8f);
                descSpan.setSpan(strikethroughSpan, 9 + real.length(), 9 + (real + catalogModel.getSaleprice()).length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                descSpan.setSpan(sizeSpanDesc2, 9 + real.length(), 9 + (real + catalogModel.getSaleprice()).length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                // TODO: 2019/7/18 待使用
                String vipDes = "VIP" + (int) (catalogModel.getVip_discount() * 10) + "折优惠(会员于" + DateHelper.formatSec(payInfo.getVip_enddate() * 1000) + "到期)";
                SpannableString vipSpan = new SpannableString(vipDes);
                RelativeSizeSpan vipSpanDesc = new RelativeSizeSpan(1.2f);
                ForegroundColorSpan vipColorSpanDesc =
                        new ForegroundColorSpan(getContext().getResources().getColor(R.color.comic_ff7b23));
                vipSpan.setSpan(vipSpanDesc, 0, 7, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                vipSpan.setSpan(vipColorSpanDesc, 0, 7, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                tv_alert_pay_vip.setText(vipSpan);
            }

            tv_alert_pay_desc.setText(descSpan);
            //设置用户金币余额以及按钮显示状态
            setCoinAndBtnStatus(payInfo);
        }

        btn_alert_pay_auto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAutoPay) {
                    iv_alert_pay_auto.setSelected(false);
                } else {
                    iv_alert_pay_auto.setSelected(true);
                }
                isAutoPay = !isAutoPay;
                if (onDialogClick != null) onDialogClick.onSelected(isAutoPay);
            }
        });

        btn_alert_pay_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (onDialogClick != null) onDialogClick.onRefused();
            }
        });

        btn_alert_pay_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onDialogClick != null) onDialogClick.onConfirm();
            }
        });
        return dialog;
    }

    /**
     * 设置金币余额
     * 设置按钮显示状态
     */
    public void setCoinAndBtnStatus(PayInfo payInfo) {
        //设置按钮显示状态
        if (payInfo.getIs_vip() == 1){
            ILFactory.getLoader().loadResource(btn_alert_pay_now, (int) (catalogModel.getSaleprice() * catalogModel.getVip_discount()) > payInfo.getTotal_egold() ? R.drawable.btn_comic_subscribe_pay_now : R.drawable.btn_comic_subscribe_yuedu, null);
        }else{
            ILFactory.getLoader().loadResource(btn_alert_pay_now, catalogModel.getSaleprice() > payInfo.getTotal_egold() ? R.drawable.btn_comic_subscribe_pay_now : R.drawable.btn_comic_subscribe_yuedu, null);
        }

        //设置金币余额
        String remaining = "金币余额 " + payInfo.getTotal_egold();
        SpannableString remainingSpan = new SpannableString(remaining);
        RelativeSizeSpan sizeSpanRemaining = new RelativeSizeSpan(0.9f);
        ForegroundColorSpan colorSpanRemaining =
                new ForegroundColorSpan(getContext().getResources().getColor(R.color.comic_ff4723));
        remainingSpan.setSpan(sizeSpanRemaining, 4, 5 + (payInfo.getTotal_egold() + "").length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        remainingSpan.setSpan(colorSpanRemaining, 4, 5 + (payInfo.getTotal_egold() + "").length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv_alert_pay_remaining.setText(remainingSpan);
    }

}
