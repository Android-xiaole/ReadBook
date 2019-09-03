package com.jj.comics.data.model;

import com.jj.base.BaseApplication;
import com.jj.base.mvp.IModel;
import com.jj.base.net.NetError;
import com.jj.base.utils.PackageUtil;
import com.jj.base.utils.ResourceUtil;
import com.jj.comics.common.constants.Constants;
import com.jj.comics.util.RegularUtil;

public class UpdateModelProxy implements IModel {

    /**
<<<<<<< .working
     * productChannelUpdate : {"productCode":"manhua","productDownUrl":"http://download.jjmh18.cn/apk/App_release_chile_2019-03-04_1.1.0.apk","productVersion":"2","productUpdateDesc":"猫爪漫画原生应用池乐正式包下载渠道\r\nv1.1.1","isUpdate":"0"}
||||||| .merge-left.r3326
     * productChannelUpdate : {"productCode":"manhua","productDownUrl":"http://download.jjmh18.cn/apk/App_release_chile_2019-03-04_1.1.0.apk","productVersion":"2","productUpdateDesc":"猫爪漫画原生应用池乐正式包下载渠道\r\nv1.1.1","isUpdate":"0"}
=======
     * productChannelUpdate : {"productCode":"manhua","productDownUrl":"http://download.jjmh18.cn/apk/App_release_chile_2019-03-04_1.1.0.apk","productVersion":"2","productUpdateDesc":"猫爪漫画原生应用正式包下载渠道\r\nv1.1.1","isUpdate":"0"}
>>>>>>> .merge-right.r3328
     */

    private UpdateModel productChannelUpdate;

    /**
     * 不用做error特殊处理
     *
     * @return
     */
    @Override
    public NetError error() {
        if (productChannelUpdate == null)
            return NetError.noDataError();
        if (!RegularUtil.isNumeric(productChannelUpdate.productVersion)) {
            String channel_id = Constants.CHANNEL_ID;
            return new NetError("平台未配置渠道号为： " + channel_id + "  的升级信息", NetError.OtherError);
        } else {
            int versionCode = PackageUtil.getPackageInfo().versionCode;
            if (versionCode >= Integer.parseInt(productChannelUpdate.productVersion)) {
                return new NetError("当前版本： " + versionCode + "  已是最新版本", NetError.OtherError);
            }
        }
        return null;
    }

    public class UpdateModel implements IModel {

        private String productCode;
        private String productDownUrl;
        private String productVersion;
        private String productUpdateDesc;
        private String isUpdate;
        /**
<<<<<<< .working
         * productChannelUpdate : {"productCode":"manhua","productDownUrl":"http://download.jjmh18.cn/apk/App_release_chile_2019-03-04_1.1.0.apk","productVersion":"2","productUpdateDesc":"猫爪漫画原生应用池乐正式包下载渠道\r\nv1.1.1","isUpdate":"0"}
||||||| .merge-left.r3326
         * productChannelUpdate : {"productCode":"manhua","productDownUrl":"http://download.jjmh18.cn/apk/App_release_chile_2019-03-04_1.1.0.apk","productVersion":"2","productUpdateDesc":"猫爪漫画原生应用池乐正式包下载渠道\r\nv1.1.1","isUpdate":"0"}
=======
         * productChannelUpdate : {"productCode":"manhua","productDownUrl":"http://download.jjmh18.cn/apk/App_release_chile_2019-03-04_1.1.0.apk","productVersion":"2","productUpdateDesc":"猫爪漫画原生应用正式包下载渠道\r\nv1.1.1","isUpdate":"0"}
>>>>>>> .merge-right.r3328
         */

        private UpdateModel productChannelUpdate;

        public String getProductCode() {
            return this.productCode;
        }

        public void setProductCode(String productCode) {
            this.productCode = productCode;
        }

        public String getProductDownUrl() {
            return this.productDownUrl;
        }

        public void setProductDownUrl(String productDownUrl) {
            this.productDownUrl = productDownUrl;
        }

        public String getProductVersion() {
            return this.productVersion;
        }

        public void setProductVersion(String productVersion) {
            this.productVersion = productVersion;
        }

        public String getProductUpdateDesc() {
            return this.productUpdateDesc;
        }

        public void setProductUpdateDesc(String productUpdateDesc) {
            this.productUpdateDesc = productUpdateDesc;
        }

        public String getIsUpdate() {
            return this.isUpdate;
        }

        public void setIsUpdate(String isUpdate) {
            this.isUpdate = isUpdate;
        }

        /**
         * 不用做error特殊处理
         *
         * @return
         */
        @Override
        public NetError error() {
            if (!RegularUtil.isNumeric(productVersion)) {
                String channel_id = Constants.CHANNEL_ID;
                return new NetError("平台未配置渠道号为： " + channel_id + "  的升级信息", NetError.OtherError);
            } else {
                int versionCode = PackageUtil.getPackageInfo().versionCode;
                if (versionCode < Integer.parseInt(productVersion)) {
                    return new NetError("当前版本： " + versionCode + "  已是最新版本", NetError.OtherError);
                }
            }
            return null;
        }
    }

    public UpdateModel getProductChannelUpdate() {
        return productChannelUpdate;
    }

    public void setProductChannelUpdate(UpdateModel productChannelUpdate) {
        this.productChannelUpdate = productChannelUpdate;
    }
}

