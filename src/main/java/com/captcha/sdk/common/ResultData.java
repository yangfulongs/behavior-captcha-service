package com.captcha.sdk.common;

import com.captcha.sdk.enums.ResultCode;
import lombok.Data;

import java.io.Serializable;

/**
 *
 */
@Data
public class ResultData implements Serializable {

    private static final long serialVersionUID = -3948389268046368059L;

    private Integer code;

    private String msg;

    private Object data;

    public ResultData() {}

    public ResultData(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static ResultData success() {
        ResultData result = new ResultData();
        result.setResultCode(ResultCode.SUCCESS);
        return result;
    }

    public static ResultData success(Object data) {
        ResultData result = new ResultData();
        result.setResultCode(ResultCode.SUCCESS);
        result.setData(data);
        return result;
    }

    public static ResultData failure(ResultCode resultCode) {
        ResultData result = new ResultData();
        result.setResultCode(resultCode);
        return result;
    }

    public static ResultData failure(ResultCode resultCode, Object data) {
        ResultData result = new ResultData();
        result.setResultCode(resultCode);
        result.setData(data);
        return result;
    }

    public void setResultCode(ResultCode code) {
        this.code = code.code();
        this.msg = code.message();
    }
}