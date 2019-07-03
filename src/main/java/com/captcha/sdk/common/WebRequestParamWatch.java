package com.captcha.sdk.common;


import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * @author xuhu
 * @create 2018/12/21
 */
@Data
public class WebRequestParamWatch {

    private Long startTime;

    private String method;

    private String url;

    /**
     * 平台
     * 简理财，众邦宝，法拍宝，。。。
     */
    private String platform;

    /**
     * 平台环境
     * 开发，测试，uat，生产
     */
    private String platformEnv;

    private Long userId;

    private String ticket;

    private String message;

    private Throwable exception;

    private String paramJsonString;

    public static WebRequestParamWatch start() {
        WebRequestParamWatch paramWatch = new WebRequestParamWatch();
        paramWatch.setStartTime(System.currentTimeMillis());
        return paramWatch;
    }


    public void setRequestParams(HttpServletRequest request) {
        this.method = request.getMethod();
        this.url = request.getRequestURL().toString();
        this.platform = request.getHeader("platform");
        //获取请求头中传递的参数ticket
        this.ticket = StringUtils.isBlank(request.getParameter("ticket"))?request.getHeader("Authorization"):request.getParameter("ticket");
        Enumeration<String> paramNames = request.getParameterNames();
        JSONObject paramJson = new JSONObject();
        while (paramNames.hasMoreElements()) {
            String paramName = paramNames.nextElement();
            paramJson.put(paramName, request.getParameter(paramName));
        }
        this.paramJsonString = paramJson.toJSONString();
    }

    public long processTime() {
        return System.currentTimeMillis() - startTime;
    }

    public String toLogString() {
        return "startTime=" + startTime +
                ", method='" + method + '\'' +
                ", url='" + url + '\'' +
                ", platform='" + platform + '\'' +
                ", platformEnv='" + platformEnv + '\'' +
                ", userId=" + userId +
                ", ticket='" + ticket + '\'' +
                ", message='" + message + '\'' +
                ", paramJsonString=" + paramJsonString ;
    }
}
