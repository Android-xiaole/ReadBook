package com.jj.comics.data.model;

import com.jj.base.net.NetError;
import com.jj.base.utils.CommonUtil;

import java.util.List;

/**
 * 打赏礼物列表
 */
public class RewardGiftsResponse extends ResponseModel {

    private List<DataBean> data;

    @Override
    public NetError error() {
        if (code != 200){
            return super.error();
        }else{
            if (CommonUtil.checkEmpty(data)){
                return NetError.noDataError();
            }else{
                return null;
            }
        }
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }


    public static class DataBean {
        /**
         * type : neckpillow
         * caption : 颈枕
         * price : 366
         * imgfile : http://ossmh.jj1699.cn/gifts/jingzhen.png
         * desc : 366豪气值
         */

        private String type;
        private String caption;
        private int price;
        private String imgfile;
        private String desc;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getCaption() {
            return caption;
        }

        public void setCaption(String caption) {
            this.caption = caption;
        }

        public int getPrice() {
            return price;
        }

        public void setPrice(int price) {
            this.price = price;
        }

        public String getImgfile() {
            return imgfile;
        }

        public void setImgfile(String imgfile) {
            this.imgfile = imgfile;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }
    }
}
