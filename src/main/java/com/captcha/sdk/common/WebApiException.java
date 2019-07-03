package com.captcha.sdk.common;

import com.captcha.sdk.enums.ResultCode;
import lombok.Data;

@Data
public class WebApiException extends RuntimeException {

    /**
     * 错误码
     */
    private int code;

    /**
     * 返回前端内容
     */
    private String message;

    /**
     * 错误详细信息
     */
    private String info;

    public WebApiException(int code, String msgInfo) {
        this.code = code;
        this.message = msgInfo;
        this.info = msgInfo;
    }

    public WebApiException(int code, String message, String info) {
        this.code = code;
        this.message = message;
        this.info = info;
    }

    public WebApiException(ResultCode resultCode, String info) {
        this.code = resultCode.code();
        this.message = resultCode.message();
        this.info = info;
    }

}
