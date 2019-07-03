package com.captcha.sdk.service.impl;

import com.captcha.sdk.service.RedisService;
import com.captcha.sdk.vo.CaptchaPictureCacheVo;
import com.captcha.sdk.vo.CaptchaPictureUseCacheVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class RedisServiceImpl implements RedisService{

    @Value("${timeOut}")
    private String timeOut;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void cacheCaptchaInfo(String uniqueFlag, String xPercent) {
        stringRedisTemplate.opsForValue().set(uniqueFlag, xPercent, Long.valueOf(timeOut),TimeUnit.MINUTES);
    }

    @Override
    public String getCaptchaInfo(String uniqueFlag) {
        return stringRedisTemplate.opsForValue().get(uniqueFlag);
    }

    @Override
    public void cacheCaptchaPathInfo(String key, CaptchaPictureCacheVo captchaPictureCacheVo) {
        redisTemplate.opsForValue().set(key, captchaPictureCacheVo);
    }

    @Override
    public CaptchaPictureCacheVo getCaptchaPicureInfo(String key) {
        return (CaptchaPictureCacheVo) redisTemplate.opsForValue().get(key);
    }

    @Override
    public void cacheCaptchaPictureUserRecord(String token, CaptchaPictureUseCacheVo captchaPictureUseCacheVo) {
        redisTemplate.opsForValue().set(token, captchaPictureUseCacheVo, 5, TimeUnit.MINUTES);
    }

    @Override
    public CaptchaPictureUseCacheVo geCaptchaPictureUseRecord(String token) {
        return (CaptchaPictureUseCacheVo) redisTemplate.opsForValue().get(token);
    }

    @Override
    public void deleteCaptchaPictureUserRecord(String token) {
        redisTemplate.delete(token);
    }
}
