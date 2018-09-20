package com.njcool.console.common.constant;

/**
 * @author xfe
 * @Date 2018/9/13
 * @Desc
 */
public class RespBody<T> {
    private String code = "000";

    private String desc = "success";

    private T data;

    public RespBody() {

    }

    public RespBody(ConsoleStatus.RespCode code) {
        this.code = code.getCode();
    }

    public RespBody(ConsoleStatus.RespCode code, String desc) {
        this.code = code.getCode();
        this.desc = desc;
    }

    public RespBody(ConsoleStatus.RespCode code, String desc, T data) {
        this.code = code.getCode();
        this.desc = desc;
        this.data = data;
    }

    public RespBody(T data) {
        this.data = data;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
