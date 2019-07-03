package com.captcha.sdk.service;


import com.captcha.sdk.vo.CaptchaPictureCacheVo;
import com.captcha.sdk.vo.CaptchaPictureUseCacheVo;

/**
 * @auther: yangfulong
 * @Date: 2019/2/21 09:26
 * @Description:
 */
public interface RedisService {

    void cacheCaptchaInfo(String uniqueFlag, String xPercent);

    String getCaptchaInfo(String uniqueFlag);

    void cacheCaptchaPathInfo(String key, CaptchaPictureCacheVo captchaPictureCacheVo);

    CaptchaPictureCacheVo getCaptchaPicureInfo(String key);

    /**
     * 缓存图形验证码使用记录
     * @param token
     * @param captchaPictureUseCacheVo
     */
    void cacheCaptchaPictureUserRecord(String token, CaptchaPictureUseCacheVo captchaPictureUseCacheVo);

    /**
     * 获取相同token图片验证码使用记录
     * @return
     */
    CaptchaPictureUseCacheVo geCaptchaPictureUseRecord(String token);

    /**
     * 删除相同token图片验证码使用记录
     * @param token
     */
    void deleteCaptchaPictureUserRecord(String token);
}
