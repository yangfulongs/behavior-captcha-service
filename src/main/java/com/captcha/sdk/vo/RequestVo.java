package com.captcha.sdk.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yang
 * @create 2018/12/12
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestVo{

    /**
     * 唯一标识
     */
    private String  uniqueFlag;

    /**
     * 滑块横坐标所占比例
     */
    private String xPercent;

    /**
     * 渠道
     */
    private String channel;

    /**
     * 请求时间戳
     */
    private Long timeStamp;

    /**
     * appId
     */
    private String appId;

    /**
     * sign签名
     */
    private String sign;

}
