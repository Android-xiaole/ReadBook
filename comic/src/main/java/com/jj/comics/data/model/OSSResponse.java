package com.jj.comics.data.model;

import com.jj.base.mvp.IModel;
import com.jj.base.net.NetError;

public class OSSResponse implements IModel {


    /**
     * code : 200
     * message : 请求成功
     * data : {"StatusCode":200,"AccessKeyId":"STS.NHEcy1HByQ7VPKzthecc6zFs8","AccessKeySecret":"Ce27J6NJfoDm5TnNNbycGW6RiB3gUwXApDABQvEVNBhv","Expiration":"2019-09-10T02:33:04Z","SecurityToken":"CAISlQJ1q6Ft5B2yfSjIr4vwKMOFpZ1Y5vW9Um3LkGgwb+walYPY2jz2IH9Of3BtCOwftP0ymW1S5vYTlqJ4T55IQ1Dza8J148zAevoB2s+T1fau5Jko1beXewHKeSOZsebWZ+LmNqS/Ht6md1HDkAJq3LL+bk/Mdle5MJqP+/EFA9MMRVv6F3kkYu1bPQx/ssQXGGLMPPK2SH7Qj3HXEVBjt3gb6wZ24r/txdaHuFiMzg+46JdM/dSgf8T0P5AxZcgiDo/s5oEsKPqdihw3wgNR6aJ7gJZD/Tr6pdyHCzFTmU7ZabGNr4wzdVUgO/VnQf4U/eKPnPl5q/HVkJ/s1xFOMOdaXiLSXom8x9HeH+ekJnPPrwZ0ybmnGoABlGxZsq+oxgKbuvk14J1H5pdPXWSR3UHAUGqOjmhn3Aw9d59IWqJf5VDkuGJ1KDw+havqEbJk1gMzeOxrnLNh+a9lUt03yow/rt4hRITuGVxqw7eb5tezGT7Z37ZpaUn4oUVE4FXMq9ssyQRxvMUeSGM668IkiQrtaJU0k6FLAMc="}
     */

    private String code;
    private String message;
    private DataBean data;

    @Override
    public NetError error() {
        return null;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * StatusCode : 200
         * AccessKeyId : STS.NHEcy1HByQ7VPKzthecc6zFs8
         * AccessKeySecret : Ce27J6NJfoDm5TnNNbycGW6RiB3gUwXApDABQvEVNBhv
         * Expiration : 2019-09-10T02:33:04Z
         * SecurityToken : CAISlQJ1q6Ft5B2yfSjIr4vwKMOFpZ1Y5vW9Um3LkGgwb+walYPY2jz2IH9Of3BtCOwftP0ymW1S5vYTlqJ4T55IQ1Dza8J148zAevoB2s+T1fau5Jko1beXewHKeSOZsebWZ+LmNqS/Ht6md1HDkAJq3LL+bk/Mdle5MJqP+/EFA9MMRVv6F3kkYu1bPQx/ssQXGGLMPPK2SH7Qj3HXEVBjt3gb6wZ24r/txdaHuFiMzg+46JdM/dSgf8T0P5AxZcgiDo/s5oEsKPqdihw3wgNR6aJ7gJZD/Tr6pdyHCzFTmU7ZabGNr4wzdVUgO/VnQf4U/eKPnPl5q/HVkJ/s1xFOMOdaXiLSXom8x9HeH+ekJnPPrwZ0ybmnGoABlGxZsq+oxgKbuvk14J1H5pdPXWSR3UHAUGqOjmhn3Aw9d59IWqJf5VDkuGJ1KDw+havqEbJk1gMzeOxrnLNh+a9lUt03yow/rt4hRITuGVxqw7eb5tezGT7Z37ZpaUn4oUVE4FXMq9ssyQRxvMUeSGM668IkiQrtaJU0k6FLAMc=
         */

        private int StatusCode;
        private String AccessKeyId;
        private String AccessKeySecret;
        private String Expiration;
        private String SecurityToken;

        public int getStatusCode() {
            return StatusCode;
        }

        public void setStatusCode(int StatusCode) {
            this.StatusCode = StatusCode;
        }

        public String getAccessKeyId() {
            return AccessKeyId;
        }

        public void setAccessKeyId(String AccessKeyId) {
            this.AccessKeyId = AccessKeyId;
        }

        public String getAccessKeySecret() {
            return AccessKeySecret;
        }

        public void setAccessKeySecret(String AccessKeySecret) {
            this.AccessKeySecret = AccessKeySecret;
        }

        public String getExpiration() {
            return Expiration;
        }

        public void setExpiration(String Expiration) {
            this.Expiration = Expiration;
        }

        public String getSecurityToken() {
            return SecurityToken;
        }

        public void setSecurityToken(String SecurityToken) {
            this.SecurityToken = SecurityToken;
        }
    }
}
