package com.captcha.sdk.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author wangdan
 * @create 2019/06/25
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CaptchaPictureCutResult implements Serializable {

    /**
     * 原图片
     */
    private byte[] oriCopyImages;

    /**
     * 滑块图片
     */
    private byte[] slideImages;

    /**
     * X位置移动百分比
     */
    private Float percent;
}
