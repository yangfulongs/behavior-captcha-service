package com.captcha.sdk.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author yang
 * @create 2018/12/12
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CaptchaVo implements Serializable {

    /**
     * 带滑块的图片
     */
    private String oriCopyImages;
    /**
     * 滑块图片
     */
    private String slideImages;
    /**
     * 唯一标识
     */
    private String  uniqueFlag;

    /**
     * 渠道来源
     */
    private String channel;

    /**
     * 底图
     */
    private String oriImages;
}
