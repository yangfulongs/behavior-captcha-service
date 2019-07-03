package com.captcha.sdk.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CaptchaPictureCacheVo implements Serializable {

    /**
     * 原始图片地址
     */
    private String oriPath;

    /**
     * 滑块图片地址
     */
    private String slidePath;

    /**
     * 滑块横坐标所占比例
     */
    private String xPercent;

    /**
     * 原始大图地址
     */
    private String oldPath;
}
