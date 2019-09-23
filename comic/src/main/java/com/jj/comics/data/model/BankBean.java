package com.jj.comics.data.model;


import java.io.Serializable;

public class BankBean implements Serializable {

    /**
     * status : false
     * information : {"account_number":"","opener":"","opening_bank":""}
     */

    private boolean status;
    private InformationBeanX information;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public InformationBeanX getInformation() {
        return information;
    }

    public void setInformation(InformationBeanX information) {
        this.information = information;
    }

    public static class InformationBeanX implements Serializable{
        /**
         * account_number :
         * opener :
         * opening_bank :
         */

        private String account_number;
        private String opener;
        private String opening_bank;

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

        public String getOpening_bank() {
            return opening_bank;
        }

        public void setOpening_bank(String opening_bank) {
            this.opening_bank = opening_bank;
        }
    }
}
