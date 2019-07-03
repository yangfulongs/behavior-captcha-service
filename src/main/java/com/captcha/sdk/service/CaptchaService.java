package com.captcha.sdk.service;

import com.captcha.sdk.vo.CaptchaPictureUseCacheVo;
import com.captcha.sdk.vo.CaptchaVo;
import com.captcha.sdk.vo.CheckStatusVo;

/**
 * @auther: yangfulong
 * @Date: 2019/2/21 09:26
 * @Description:
 */
public interface CaptchaService {

    /**
     * 获取图片信息
     * @param channel
     * @return
     * @throws Exception
     */
    CaptchaVo getTwoImages(String channel) throws Exception;

    /**
     * 根据唯一标识和滑块移动百分比验证
     * @param uniqueFlag
     * @param xPercent
     * @return
     */
    CheckStatusVo checkSlide(String uniqueFlag, String xPercent);

    /**
     * 预先处理图片
     */
    void createPictures();

    /**
     * 获取图形验证码图片地址
     * @return
     */
    CaptchaVo getCaptchaPictures(String token);
}
