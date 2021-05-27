package com.tbossgroup.utils;

/**
 * Created by daixun on 17-3-23.
 */

public class RespBean<T> {

    private RspInfoBean rspInfo;
    private T rspData;

    public RspInfoBean getRspInfo() {
        return rspInfo;
    }

    public void setRspInfo(RspInfoBean rspInfo) {
        this.rspInfo = rspInfo;
    }

    public T getRspData() {
        return rspData;
    }

    public void setRspData(T rspData) {
        this.rspData = rspData;
    }

    public static class RspInfoBean {

        private String rspType;
        private int rspCode;
        private String rspDesc;

        public String getRspType() {
            return rspType;
        }

        public void setRspType(String rspType) {
            this.rspType = rspType;
        }

        public int getRspCode() {
            return rspCode;
        }

        public void setRspCode(int rspCode) {
            this.rspCode = rspCode;
        }

        public String getRspDesc() {
            return rspDesc;
        }

        public void setRspDesc(String rspDesc) {
            this.rspDesc = rspDesc;
        }
    }
}
