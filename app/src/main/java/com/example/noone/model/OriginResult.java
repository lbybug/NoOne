package com.example.noone.model;

/**
 * FileName: OriginResult
 * Author: Administrators
 * Time: 2018/12/12 9:37
 * Desc: TODO
 */
public class OriginResult {

    private String error;
    private String sub_error;
    private String sn;
    private String desc;

    public OriginResult() {
    }

    public OriginResult(String error, String sub_error, String sn, String desc) {
        this.error = error;
        this.sub_error = sub_error;
        this.sn = sn;
        this.desc = desc;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getSub_error() {
        return sub_error;
    }

    public void setSub_error(String sub_error) {
        this.sub_error = sub_error;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "OriginResult{" +
                "error='" + error + '\'' +
                ", sub_error='" + sub_error + '\'' +
                ", sn='" + sn + '\'' +
                ", desc='" + desc + '\'' +
                '}';
    }
}
