package com.captcha.sdk.enums;

public enum WebResultEnum {

    SUCCESS(0, "校验成功"),

    ERROR_TOKEN_TIMEOUT(1, "唯一标识失效，请重新获取"),
    ERROR_PARAM(2, "参数异常"),
    ERROR_SYSTEM(3, "系统繁忙，请稍后重试"),
    CHECK_FAIL(4, "校验失败");


    private int code;

    private String message;

    WebResultEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
