package com.njcool.console.common.constant;

/**
 * @author xfe
 * @Date 2018/9/13
 * @Desc
 */
public class ConsoleStatus {

    public enum RespCode {
        C000("000"),
        C200("200"),
        C101("101"),//登录异常码
        C102("102"),
        C110("110"),// 登录失效
        C111("111"),// 账号被踢
        C112("112"),// 权限不足
        ;

        private String code;

        RespCode (String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }

    }
}
