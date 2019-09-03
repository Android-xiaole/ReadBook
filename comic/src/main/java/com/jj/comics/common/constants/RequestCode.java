package com.jj.comics.common.constants;

public class RequestCode {
    public static final int LOGIN_REQUEST_CODE = 2001;
    public static final int REGISTER_REQUEST_CODE = LOGIN_REQUEST_CODE + 1;
    public static final int READ_REQUEST_CODE = REGISTER_REQUEST_CODE + 1;
    public static final int SUBSCRIBE_REQUEST_CODE = READ_REQUEST_CODE + 1;
    public static final int PAY_REQUEST_CODE = SUBSCRIBE_REQUEST_CODE + 1;
    public static final int WEB_REQUEST_CODE = PAY_REQUEST_CODE + 1;
    public static final int OPEN_PIC_REQUEST_CODE = WEB_REQUEST_CODE + 1;
    public static final int CROP_IMG_REQUEST_CODE = OPEN_PIC_REQUEST_CODE + 1;
    public static final int VIP_REQUEST_CODE = OPEN_PIC_REQUEST_CODE + 1;
    public static final int WELFARE_REQUEST_CODE = VIP_REQUEST_CODE + 1;
    public static final int REWARD_REQUEST_CODE = WELFARE_REQUEST_CODE + 1;
    public static final int SETTING_REQUEST_CODE = REWARD_REQUEST_CODE + 1;
    public static final int Coupon_REQUEST_CODE = SETTING_REQUEST_CODE + 1;
    public static final int RICH_REQUEST_CODE = Coupon_REQUEST_CODE + 1;
    public static final int COMMENT_REQUEST_CODE = RICH_REQUEST_CODE + 1;
    public static final int FEEDBACK_REQUEST_CODE = RICH_REQUEST_CODE + 1;//我的反馈跳转到意见反馈页面
    public static final int MINE_REQUEST_CODE = RICH_REQUEST_CODE + 1;//我的页面跳转到意见反馈页面
}
