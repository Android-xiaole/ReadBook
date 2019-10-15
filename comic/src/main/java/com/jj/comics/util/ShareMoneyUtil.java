package com.jj.comics.util;

public class ShareMoneyUtil {
    public static String getShareMoney (String totalSize,String first_commission_rate,
                                        String second_commission_rate,String share_price) {
        String ret = "分享预计赚￥";
        long size = 0;
        try {
            size = Long.parseLong(totalSize);
        } catch (NumberFormatException e) {
            e.printStackTrace();

        }
        double first = 0;
        try {
            first = Double.parseDouble(first_commission_rate);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        double second = 0;
        try {
            second = Double.parseDouble(second_commission_rate);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        double rate = 0;
        try {
            rate = Double.parseDouble(share_price);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        double sum = 0;
        try {
            sum = size * rate * (first + second);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ret + String.format("%.2f",sum);
    }
}
