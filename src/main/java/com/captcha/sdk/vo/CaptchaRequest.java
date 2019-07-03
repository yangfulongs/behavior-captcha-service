package com.captcha.sdk.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author wangdan
 * @create 2019/06/25
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CaptchaRequest implements Serializable {

    @NotBlank(message = "appId不能为空")
    private String appId;

    @NotBlank(message = "channel不能为空")
    private String channel;

    @NotBlank(message = "sign不能为空")
    private String sign;

    @NotBlank(message = "timeStamp不能为空")
    private Long timeStamp;
}
