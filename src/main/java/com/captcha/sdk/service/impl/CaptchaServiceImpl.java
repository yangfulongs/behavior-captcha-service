package com.captcha.sdk.service.impl;

import com.captcha.sdk.enums.WebResultEnum;
import com.captcha.sdk.service.CaptchaService;
import com.captcha.sdk.service.RedisService;
import com.captcha.sdk.utils.VerifyImageUtil;
import com.captcha.sdk.utils.oss.OssService;
import com.captcha.sdk.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Random;
import java.util.UUID;

/**
 * @auther: yangfulong
 * @Date: 2019/2/21 09:27
 * @Description:
 */
@Service
@Slf4j
public class CaptchaServiceImpl implements CaptchaService {

    private final static String PRECISION = "0.05";

    private final static String CAPTCHA_NAME_PREFIX = "picture_";

    private final static String CAPTCHA_FOLDER = "captcha/";

    //TODO 这个值需要修改
    private final static Integer CAPTCHA_PICUTRE_COUNT = 338;

    //TODO 这个值需要修改
    private final static Integer TARGET_COUNT = 27;

    private final static Integer PICTURE_NUM = 14;

    @Autowired
    private RedisService redisService;
    @Autowired
    private OssService ossService;
    @Autowired
    private Environment environment;

    @Deprecated
    @Override
    public CaptchaVo getTwoImages(String channel) throws Exception {

        File templateFile;
        File targetFile;
        Random random = new Random();
        int start = 7;
        int end = 10;
        int templateNo = (int) (Math.random() * (end - start) + start);
        int targetNo = random.nextInt(6) + 1;

        InputStream stream = getClass().getClassLoader().getResourceAsStream("static/templates/" + templateNo + ".jpg");
        templateFile = new File(templateNo + ".jpg");
        if (templateFile.exists()) {
            templateFile.delete();
        }
        FileUtils.copyInputStreamToFile(stream, templateFile);
        stream = getClass().getClassLoader().getResourceAsStream("static/targets/" + targetNo + ".jpg");
        targetFile = new File(targetNo + ".jpg");
        if (targetFile.exists()) {
            targetFile.delete();
        }
        FileUtils.copyInputStreamToFile(stream, targetFile);
        BufferedImage bufferedImage = getBufferedImage(targetNo);
        //源文件宽度
        int width = bufferedImage.getWidth();
        //源文件长度
        int height = bufferedImage.getHeight();

        CaptchaResponse response = new CaptchaResponse();
        CaptchaVo captchaVo = new CaptchaVo();
        CaptchaResponse vo = VerifyImageUtil.pictureTemplatesCut(templateFile, targetFile, "jpg", "jpg", width, height, response);
        //唯一标识
        String uniqueFlag = UUID.randomUUID().toString().replace("-", "");
        //        vo.setUniqueFlag(uniqueFlag);
        redisService.cacheCaptchaInfo(uniqueFlag, String.valueOf(vo.getXPercent()));
        log.info("uniqueFlag：{},xPercent：{}", vo.getUniqueFlag(), vo.getXPercent());
        captchaVo.setUniqueFlag(uniqueFlag);
        captchaVo.setSlideImages(response.getSlideImages());
        captchaVo.setOriCopyImages(response.getOriCopyImages());
        return captchaVo;
    }

    @Override
    public CheckStatusVo checkSlide(String uniqueFlag, String xPercent) {

        CheckStatusVo vo = new CheckStatusVo();
        log.info("uniqueFlag：{},xPercent：{}", uniqueFlag, xPercent);
        if (StringUtils.isEmpty(uniqueFlag) && StringUtils.isEmpty(xPercent)) {
            vo.setStatus(WebResultEnum.ERROR_PARAM.getCode());
            return vo;
        }
        String xPer = redisService.getCaptchaInfo(uniqueFlag);
        if (StringUtils.isEmpty(xPer)) {
            vo.setStatus(WebResultEnum.ERROR_TOKEN_TIMEOUT.getCode());
            return vo;
        }
        BigDecimal xPerDecimal = new BigDecimal(xPer);
        BigDecimal xPercentDecimal = new BigDecimal(xPercent);
        BigDecimal abs = xPerDecimal.subtract(xPercentDecimal).abs();
        if (abs.compareTo(new BigDecimal(PRECISION)) <= 0) {
            vo.setStatus(WebResultEnum.SUCCESS.getCode());
            return vo;
        } else {
            vo.setStatus(WebResultEnum.CHECK_FAIL.getCode());
            return vo;
        }
    }

    @Override
    public void createPictures() {
        int start = 27;
        int end = 30;
        File templateFile;
        File targetFile;
        int count = 1;

        for (int targetNo = 1; targetNo < TARGET_COUNT; targetNo++) {
            for (int pictureNumber = 1; pictureNumber < PICTURE_NUM; pictureNumber++) {
                int templateNo = (int) (Math.random() * (end - start) + start);
                InputStream stream = getClass().getClassLoader().getResourceAsStream("static/templates/" + templateNo + ".jpg");
                templateFile = new File(templateNo + ".jpg");
                if (templateFile.exists()) {
                    templateFile.delete();
                }
                try {
                    FileUtils.copyInputStreamToFile(stream, templateFile);
                } catch (Exception ex) {
                    continue;
                }
                stream = getClass().getClassLoader().getResourceAsStream("static/targets/" + targetNo + ".jpg");

                targetFile = new File(targetNo + ".jpg");
                if (targetFile.exists()) {
                    targetFile.delete();
                }
                try {
                    FileUtils.copyInputStreamToFile(stream, targetFile);
                } catch (Exception ex) {
                    log.error("处理原始图，读取流出现异常， error:{}", ex);
                    continue;
                }
                BufferedImage bufferedImage = getBufferedImage(targetNo);
                //源文件宽度
                int width = bufferedImage.getWidth();
                //源文件长度
                int height = bufferedImage.getHeight();
                CaptchaResponse response = new CaptchaResponse();
                CaptchaPictureCutResult result;
                try {
                    result = VerifyImageUtil.pictureTemplatesCutByPercent(templateFile, targetFile, "jpg", "jpg", width, height, pictureNumber * 5);
                } catch (Exception ex) {
                    log.error("初始化图片，切图出现异常， error:{}", ex);
                    continue;
                }
                //上传原图片到OSS
                String oriPictureName = CAPTCHA_FOLDER + "slide/ori_" + System.currentTimeMillis() + ".jpg";
                ossService.uploadFile(oriPictureName, new ByteArrayInputStream(result.getOriCopyImages()));
                //上传滑块图片到OSS
                String slideName = CAPTCHA_FOLDER + "slide/slide_" + System.currentTimeMillis() + ".jpg";
                ossService.uploadFile(slideName, new ByteArrayInputStream(result.getSlideImages()));
                String oldPicture = CAPTCHA_FOLDER + "public/" + "old_" + targetNo + ".jpg";
                //将图片名称记录到redis， 图片名称用时间戳 key= picture_count value= captchaPictureCacheVo
                String key = CAPTCHA_NAME_PREFIX + count;
                CaptchaPictureCacheVo captchaPictureCacheVo = new CaptchaPictureCacheVo();
                captchaPictureCacheVo.setOriPath(oriPictureName);
                captchaPictureCacheVo.setSlidePath(slideName);
                captchaPictureCacheVo.setOldPath(oldPicture);
                captchaPictureCacheVo.setXPercent(String.valueOf(result.getPercent()));
                redisService.cacheCaptchaPathInfo(key, captchaPictureCacheVo);
                log.info("captchaPictureCacheVo:{}", captchaPictureCacheVo);
                //计数器+1
                count++;
                try {
                    stream.close();
                } catch (Exception ex) {
                    log.error("初始化图片，关闭流出现异常, error:{}", ex);
                    continue;
                }
            }
        }


    }

    @Override
    public CaptchaVo getCaptchaPictures(String token) {
        Random random = new Random();
        int picture_number = random.nextInt(CAPTCHA_PICUTRE_COUNT) + 1;
        String captchaPicturePathKey = CAPTCHA_NAME_PREFIX + picture_number;
        CaptchaPictureCacheVo captchaPictureCacheVo = redisService.getCaptchaPicureInfo(captchaPicturePathKey);
        if (captchaPictureCacheVo != null) {
            //redis中保存图片使用记录， 使用次数
            CaptchaPictureUseCacheVo captchaPictureUseCacheVo = new CaptchaPictureUseCacheVo();
            captchaPictureUseCacheVo.setPictureKey(captchaPicturePathKey);
            captchaPictureUseCacheVo.setToken(token);
            captchaPictureUseCacheVo.setPercent(captchaPictureCacheVo.getXPercent());
            redisService.cacheCaptchaPictureUserRecord(token, captchaPictureUseCacheVo);
            CaptchaVo captchaVo = new CaptchaVo();
            String endpoint = environment.getProperty("oss.endpoint");
            String bucketName = environment.getProperty("oss.bucketName");
            String ossPathPrefix = "https://" + bucketName + "." + endpoint + "/";
            captchaVo.setOriCopyImages(ossPathPrefix + captchaPictureCacheVo.getOriPath());
            captchaVo.setSlideImages(ossPathPrefix + captchaPictureCacheVo.getSlidePath());
            captchaVo.setOriImages(ossPathPrefix + captchaPictureCacheVo.getOldPath());
            captchaVo.setUniqueFlag(token);
            return captchaVo;
        }
        return null;
    }

    private BufferedImage getBufferedImage(int targetNo) {
        InputStream stream = getClass().getClassLoader().getResourceAsStream("static/targets/" + targetNo + ".jpg");
        BufferedImage imageTemplate = null;
        try {
            imageTemplate = ImageIO.read(stream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return imageTemplate;
    }

}
