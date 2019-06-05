package com.example.noone.model;

/**
 * FileName: AsrFinishJsonData
 * Author: Administrators
 * Time: 2018/12/12 9:37
 * Desc: TODO
 */
public class AsrFinishJsonData {

    private String error;
    private String sub_error;
    private String desc;
    private OriginResult origin_result;

    public AsrFinishJsonData() {
    }

    public AsrFinishJsonData(String error, String sub_error, String desc, OriginResult origin_result) {
        this.error = error;
        this.sub_error = sub_error;
        this.desc = desc;
        this.origin_result = origin_result;
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

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public OriginResult getOrigin_result() {
        return origin_result;
    }

    public void setOrigin_result(OriginResult origin_result) {
        this.origin_result = origin_result;
    }

    @Override
    public String toString() {
        return "AsrFinishJsonData{" +
                "error='" + error + '\'' +
                ", sub_error='" + sub_error + '\'' +
                ", desc='" + desc + '\'' +
                ", origin_result=" + origin_result +
                '}';
    }
}
