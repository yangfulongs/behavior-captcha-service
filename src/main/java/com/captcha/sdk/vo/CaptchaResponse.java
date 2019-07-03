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
public class CaptchaResponse implements Serializable {

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
     * 滑块x坐标
     */
    private Integer slideX;
    /**
     * 滑块Y坐标
     */
    private Integer slideY;
    /**
     * 滑块横坐标所占比例
     */
    private float xPercent;
    /**
     * 滑块纵坐标所占比例
     */
    private float yPercent;

    private String channel;

}
