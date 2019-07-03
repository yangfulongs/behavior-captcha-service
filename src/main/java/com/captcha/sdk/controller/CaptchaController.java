package com.captcha.sdk.controller;

import com.alibaba.fastjson.JSON;
import com.captcha.sdk.common.WebApiException;
import com.captcha.sdk.common.config.ApolloGlobalCache;
import com.captcha.sdk.common.config.RedisKeysConfig;
import com.captcha.sdk.enums.IpCheckEnum;
import com.captcha.sdk.enums.ResultCode;
import com.captcha.sdk.enums.SwitchEnum;
import com.captcha.sdk.service.CaptchaService;
import com.captcha.sdk.service.RedisService;
import com.captcha.sdk.utils.HttpReqUtil;
import com.captcha.sdk.utils.RSAEncrypt;
import com.captcha.sdk.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @auther: yangfulong
 * @Date: 2019/1/16 19:32
 * @Description:
 */
@CrossOrigin
@RestController
@RequestMapping("/captcha")
@Slf4j
public class CaptchaController {

    private final static String PRECISION = "0.03";

    @Autowired
    private CaptchaService  captchaService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedisService redisService;

    @GetMapping("/slide/imgs")
    public CaptchaVo getSliderVerifyCode(@RequestParam String encryContent) throws Exception {
        // RSA解密
        String jsonContent = decryptEncryContent(encryContent);
        log.info("encryContent={},解密后为{}",encryContent,jsonContent);
        int endIndex = jsonContent.lastIndexOf("}");
        CaptchaRequest request ;
        try{
             request = JSON.parseObject(JSON.parse(jsonContent.substring(0,endIndex+1)).toString(),CaptchaRequest.class);
        }catch (Exception e){
            log.error("请求参数信息有误",e);
            throw new WebApiException(ResultCode.PARAM_IS_INVALID,"请求滑块验证参数无效!");
        }

        // 校验时间戳
        if(request.getTimeStamp()==null || System.currentTimeMillis() - request.getTimeStamp() > 60000 * 10){
            log.info("请求时间相差太大{}",request.getTimeStamp()==null?null:System.currentTimeMillis() - request.getTimeStamp());
            throw new WebApiException(ResultCode.PARAM_IS_INVALID,"请求滑块验证参数无效!");
        }

        //校验ip
        checkIp(request.getAppId(), IpCheckEnum.BLACK_LIST.getValue());

        log.info("channel {}", request.getChannel());
        String token = UUID.randomUUID().toString().replace("-","");
        //选择图片，返回前端
        CaptchaVo captchaVo = captchaService.getCaptchaPictures(token);
        return captchaVo;
    }

    @GetMapping("/slide/verify")
    public CheckStatusVo checkSlide(@RequestParam String encryContent) throws Exception{
        // RSA解密
        String jsonContent = decryptEncryContent(encryContent);
        log.info("encryContent={},解密后为{}",encryContent,jsonContent);
        int endIndex = jsonContent.lastIndexOf("}");
        RequestVo requestVo  ;
        try{
            requestVo = JSON.parseObject(JSON.parse(jsonContent.substring(0,endIndex+1)).toString(),RequestVo.class);
        }catch (Exception e){
            log.error("请求参数信息有误",e);
            throw new WebApiException(ResultCode.PARAM_IS_INVALID,"请求滑块验证参数无效!");
        }

        // 校验时间戳
        if(requestVo.getTimeStamp() == null || System.currentTimeMillis() - requestVo.getTimeStamp() > 60000 * 10){
            log.info("请求时间相差太大{}",requestVo.getTimeStamp()==null?null:System.currentTimeMillis() - requestVo.getTimeStamp());
            throw new WebApiException(ResultCode.PARAM_IS_INVALID,"请求滑块验证参数无效!");
        }
        //校验ip
        checkIp(requestVo.getAppId(),IpCheckEnum.BLACK_LIST.getValue());
        String uniqueFlag = requestVo.getUniqueFlag();
        String xPercent = requestVo.getXPercent();
        String channel = requestVo.getChannel();
        CheckStatusVo checkStatusVo = new CheckStatusVo();
        //先查询缓存中是否保存获取的图片的token和图片信息， 否 返回失败
        CaptchaPictureUseCacheVo captchaPictureUseCacheVo = redisService.geCaptchaPictureUseRecord(uniqueFlag);
        log.info("滑块验证码校验参数, captchaPictureUseCacheVo:{}", captchaPictureUseCacheVo);
        if (captchaPictureUseCacheVo == null || !new Integer(0).equals(captchaPictureUseCacheVo.getResult())) {
            checkStatusVo.setStatus(1);
            return checkStatusVo;
        }
        log.info("滑块验证码校验参数，channel={}，uniqueFlag={},xPercent={}", channel,uniqueFlag,xPercent);

        BigDecimal xPerDecimal = new BigDecimal(captchaPictureUseCacheVo.getPercent());
        BigDecimal xPercentDecimal = new BigDecimal(xPercent);
        BigDecimal abs = xPerDecimal.subtract(xPercentDecimal).abs();
        if (abs.compareTo(new BigDecimal(PRECISION)) <= 0) {
            checkStatusVo.setStatus(0);
            captchaPictureUseCacheVo.setResult(1);
            redisService.cacheCaptchaPictureUserRecord(uniqueFlag, captchaPictureUseCacheVo);
        } else {
            //返回失败
            checkStatusVo.setStatus(1);
            redisService.deleteCaptchaPictureUserRecord(uniqueFlag);
            log.info("移动端验证失败，删除滑块验证码缓存captchaPictureUseCacheVo={}",captchaPictureUseCacheVo);
        }
        return checkStatusVo;
    }


    /** *
     * @author: xudd
     * @desc:服务器间内部验证，移动端禁用
     * @date: 11:54 2019/6/27
     * @param:
     * @return:
     **/
    @PostMapping("/slide/innerVerify")
    public CheckStatusVo innerVerify(@RequestBody RequestVo requestVo){
        log.info("服务器端验证滑块验证码校验参数requestVo={}",requestVo);
        CheckStatusVo checkStatusVo = new CheckStatusVo();
        if(Objects.isNull(requestVo)||StringUtils.isEmpty(requestVo.getUniqueFlag())
                ||StringUtils.isEmpty(requestVo.getAppId())){
            checkStatusVo.setStatus(1);
            return checkStatusVo;
        }
        log.info("服务器端验证进入白名单验证appId={},uniqueFlag={}",requestVo.getAppId(),requestVo.getUniqueFlag());
        checkIp(requestVo.getAppId(),IpCheckEnum.WHITE_LIST.getValue());

        CaptchaPictureUseCacheVo captchaPictureUseCacheVo = redisService.geCaptchaPictureUseRecord(requestVo.getUniqueFlag());
        if (captchaPictureUseCacheVo == null || !new Integer(1).equals(captchaPictureUseCacheVo.getResult())) {
            checkStatusVo.setStatus(1);
            return checkStatusVo;
        }
            checkStatusVo.setStatus(0);
            redisService.deleteCaptchaPictureUserRecord(requestVo.getUniqueFlag());
            log.info("服务器端验证成功，删除滑块验证码缓存uniqueFlag{}.",requestVo.getUniqueFlag());
        return checkStatusVo;
    }



    @PostMapping("/slide/create-pictures")
    public void createPictures() {
        captchaService.createPictures();
    }

    private String decryptEncryContent(String encryContent) throws Exception {
        if(StringUtils.isEmpty(encryContent)){
            throw new WebApiException(ResultCode.PARAM_IS_BLANK,"滑块验证传参为空!");
        }
        String jsonContent = null;
        encryContent = encryContent.replaceAll(" ", "+");
        RSAEncrypt rsaEncrypt = new RSAEncrypt();
        byte[] x = rsaEncrypt.decrypt(Base64.decodeBase64(encryContent));
        jsonContent = new String(x, "UTF-8");
        return jsonContent;
    }


    private void checkIp(String appId,int ipCheckType){
        Map<String, String> ipCheckSwitchMap = ApolloGlobalCache.IPCheckSwitchMap;
        int appIpCheckSwitch = Integer.parseInt(ipCheckSwitchMap.getOrDefault(ApolloGlobalCache.APPID_IP_CHECK_PREFIX + appId, "0"));
        log.info("滑块校验ip,appId={},appIpCheckSwitch={}",appId,appIpCheckSwitch);
        if(appIpCheckSwitch == SwitchEnum.OFF.getValue()){
            return;
        }
        HttpServletRequest httpServletRequest =
                ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String ip = HttpReqUtil.getIpAddr(httpServletRequest);
        if(ipCheckType == IpCheckEnum.WHITE_LIST.getValue()){
            Map<String, String> ipWhiteListMap = ApolloGlobalCache.IPWhiteListMap;
            String ipWhiteList =  ipWhiteListMap.getOrDefault(ApolloGlobalCache.APPID_IP_WHITE_LIST_RREFIX + appId ,"");
            log.info("滑块校验白名单ip，reqIp={},ipWhiteList={}",ip,ipWhiteList);
            if(StringUtils.isEmpty(ipWhiteList)){
                throw new WebApiException(ResultCode.FAIL,"whiteList is empty");
            }
            if(!ipWhiteList.contains(ip)){
                throw new WebApiException(ResultCode.FAIL,"网络繁忙,请稍后再试");
            }
        }else {
            Map<String, String> ipUsedCountMaxMap = ApolloGlobalCache.IPUsedCountMaxMap;
            Map<String, String> ipUsedValidTimeMap = ApolloGlobalCache.IPUsedValidTimeMap;
            Long ipUsedCountMax = Long.parseLong(ipUsedCountMaxMap.getOrDefault(ApolloGlobalCache.APPID_IP_USED_COUNT_MAX_PREFIX + appId,"5"));
            Long ipUsedValidTime = Long.parseLong(ipUsedValidTimeMap.getOrDefault(ApolloGlobalCache.APPID_IP_USED_VALID_TIME_PREFIX + appId,"1"));
            String blackIps = stringRedisTemplate.opsForValue().get(RedisKeysConfig.REGISTER_BLACK_IP_LIST);
            log.info("滑块校验黑名单ip，reqIp={},blackIps={}",ip,blackIps);
            if(!StringUtils.isEmpty(blackIps) && blackIps.contains(ip)){
                throw new WebApiException(ResultCode.FAIL,"网络繁忙,请稍后再试");
            }
            String ipUsedCountKey = RedisKeysConfig.IP_USED_COUNT + ip;
            long ipUsedCount = stringRedisTemplate.opsForValue().increment(ipUsedCountKey, 1L);
            if(ipUsedCount == 1L){
                stringRedisTemplate.expire(ipUsedCountKey, ipUsedValidTime, TimeUnit.MINUTES);
            }
            if (ipUsedCount >= Long.valueOf(ipUsedCountMax)) {
                stringRedisTemplate.opsForValue().append(RedisKeysConfig.REGISTER_BLACK_IP_LIST,ip + ",");
                throw new WebApiException(ResultCode.FAIL,"网络繁忙,请稍后再试");
            }
        }
    }
}
