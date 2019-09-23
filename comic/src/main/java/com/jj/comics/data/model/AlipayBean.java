package com.jj.comics.data.model;

import java.io.Serializable;

public class AlipayBean implements Serializable {
    /**
     * status : false
     * information : {"account_number":"","opener":""}
     */

    private boolean status;
    private AlipayBean.InformationBean information;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public InformationBean getInformation() {
        return information;
    }

    public void setInformation(InformationBean information) {
        this.information = information;
    }

    public static class InformationBean implements Serializable{
        /**
         * account_number :
         * opener :
         */

        private String account_number;
        private String opener;

        public String getAccount_number() {
            return account_number;
        }

        public void setAccount_number(String account_number) {
            this.account_number = account_number;
        }

        public String getOpener() {
            return opener;
        }

        public void setOpener(String opener) {
            this.opener = opener;
        }
    }
}
