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
public class CaptchaPictureUseCacheVo implements Serializable {

    private String token;

    private String pictureKey;

    /**
     * 0：初始值， 1：校验成功， 2： 校验失败
     */
    private Integer result = 0;

    private String percent;
}
