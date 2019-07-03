package com.captcha.sdk;

import com.captcha.sdk.service.CaptchaService;
import com.captcha.sdk.service.impl.CaptchaServiceImpl;
import com.captcha.sdk.utils.VerifyImageUtil;
import com.captcha.sdk.vo.CaptchaVo;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CaptchaApplicationTests {

	@Autowired(required=true)
	private StringRedisTemplate stringRedisTemplate;
	@Autowired
	private CaptchaService captchaService;

	@Test
	public void contextLoads() {
	}

	@Test
	public void testImg() throws Exception {
		Map<String, Object> pictureMap;
		File templateFile;  //模板图片
		File targetFile;  //
		Random random = new Random();
		int templateNo = random.nextInt(4) + 1;
		int targetNo = random.nextInt(20) + 1;

		InputStream stream = getClass().getClassLoader().getResourceAsStream("static/templates/" + templateNo + ".png");
		templateFile = new File(templateNo + ".png");
		FileUtils.copyInputStreamToFile(stream, templateFile);

		stream = getClass().getClassLoader().getResourceAsStream("static/targets/" + targetNo + ".jpg");
		targetFile = new File(targetNo + ".jpg");
//		BufferedImage imageTemplate = ImageIO.read(stream);
//		System.out.println("源文件：长度："+imageTemplate.getWidth());
//		System.out.println("源文件：宽度："+imageTemplate.getHeight());
		FileUtils.copyInputStreamToFile(stream, targetFile);
//		pictureMap = VerifyImageUtil.pictureTemplatesCut(templateFile, targetFile, "png", "jpg",imageTemplate.getWidth(),imageTemplate.getHeight());
//		pictureMap = VerifyImageUtil.pictureTemplatesCut(templateFile, targetFile, "png", "jpg");
//		byte[] oriCopyImages = pictureMap.get("oriCopyImage");
//		byte[] newImages = pictureMap.get("slideImages");

//		FileOutputStream fout = new FileOutputStream("C:/Users/admin/Desktop/oriCopyImage.png");
//		//将字节写入文件
//		try {
//			fout.write(oriCopyImages);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		fout.close();
//
//		FileOutputStream newImageFout = new FileOutputStream("C:/Users/admin/Desktop/newImage.png");
//		//将字节写入文件
//		newImageFout.write(newImages);
//		newImageFout.close();
	}

	@Test
	public void test2() throws IOException {
//		File picture = new File("C:/Users/aflyun/Pictures/Camera Roll/7.jpg");

//		InputStream	stream = getClass().getClassLoader().getResourceAsStream("static/targets/7.jpg");
//		targetFile = new File("static/targets/7.jpg);
//		BufferedImage sourceImg =ImageIO.read(new FileInputStream(picture));
//		BufferedImage sourceImg =ImageIO.read(stream);
//		System.out.println(sourceImg.getHeight()+"@@@@@@");

	}

	@Test
	public void test3()throws Exception{
		File templateFile;  //模板图片
		File targetFile;  //
		Random random = new Random();
		int templateNo = random.nextInt(4) + 1;
		int targetNo = random.nextInt(20) + 1;

		InputStream stream = getClass().getClassLoader().getResourceAsStream("static/templates/" + templateNo + ".jpg");
		templateFile = new File(templateNo + ".jpg");
		FileUtils.copyInputStreamToFile(stream, templateFile);

		stream = getClass().getClassLoader().getResourceAsStream("static/targets/" + targetNo + ".jpg");
//		InputStream stream2 = getClass().getClassLoader().getResourceAsStream("static/targets/" + targetNo + ".jpg");
		targetFile = new File(targetNo + ".jpg");
        BufferedImage bufferedImage = getBufferedImage(targetNo);
        //源文件宽度
        int width = bufferedImage.getWidth();
        //源文件长度
        int height = bufferedImage.getHeight();
        FileUtils.copyInputStreamToFile(stream, targetFile);
//		CaptchaVo vo = new CaptchaVo();
//        CaptchaVo captchaVo = VerifyImageUtil.pictureTemplatesCut(templateFile, targetFile, "png", "jpg", width, height, vo);
//        String uniqueFlag = UUID.randomUUID().toString().replace("-", "");
//        captchaVo.setUniqueFlag(uniqueFlag);
////		stringRedisTemplate.opsForValue().set(uniqueFlag,String.valueOf(captchaVo.getXPercent()));
//
//        Boolean expire = stringRedisTemplate.expire(uniqueFlag, 30, TimeUnit.MINUTES);
//        System.out.println(expire+"~~~~");
//		System.out.println(captchaVo.getOriCopyImages());
//		System.out.println(captchaVo.getSlideImages());
//
//		System.out.println("uniqueFlag:"+uniqueFlag);
	}

	private  BufferedImage getBufferedImage(int targetNo){
		InputStream stream = getClass().getClassLoader().getResourceAsStream("static/targets/" + targetNo + ".jpg");
		BufferedImage imageTemplate = null;
		try {
			imageTemplate = ImageIO.read(stream);
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			if(stream != null){
				try {
					stream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return imageTemplate;
	}

	@Test
	public void testImg5() throws Exception {
		captchaService.getTwoImages("ios");
	}
//    @Test
//    public void testImg4() throws Exception {
//        Map<String,Object> pictureMap;
//        File templateFile;  //模板图片
//        File targetFile;  //
//        Random random = new Random();
//        int templateNo = random.nextInt(4) + 1;
//        int targetNo = random.nextInt(20) + 1;
//
//        InputStream stream = getClass().getClassLoader().getResourceAsStream("static/templates/" + templateNo + ".jpg");
//        templateFile = new File(templateNo + ".jpg");
//        FileUtils.copyInputStreamToFile(stream, templateFile);
//
//        stream = getClass().getClassLoader().getResourceAsStream("static/targets/" + targetNo + ".jpg");
//        targetFile = new File(targetNo + ".jpg");
//        BufferedImage bufferedImage = getBufferedImage(targetNo);
//        int width = bufferedImage.getWidth();
//        //源文件长度
//        int height = bufferedImage.getHeight();
////		BufferedImage imageTemplate = ImageIO.read(stream);
////		System.out.println("源文件：长度："+imageTemplate.getWidth());
////		System.out.println("源文件：宽度："+imageTemplate.getHeight());
//        FileUtils.copyInputStreamToFile(stream, targetFile);
////		pictureMap = VerifyImageUtil2.pictureTemplatesCut(templateFile, targetFile, "jpg", "jpg");
//		pictureMap = VerifyImageUtil.pictureTemplatesCut(templateFile, targetFile, "jpg", "jpg",width,height);
//		Object oriCopyImages = pictureMap.get("oriCopyImage");
//		Object newImages = pictureMap.get("newImage");
//
//		FileOutputStream fout = new FileOutputStream("C:/Users/admin/Desktop/oriCopyImage.jpg");
//		//将字节写入文件
////		try {
//////			fout.write(oriCopyImages);
////		} catch (IOException e) {
////			e.printStackTrace();
////		}
////		fout.close();
//
//		FileOutputStream newImageFout = new FileOutputStream("C:/Users/admin/Desktop/newImage.jpg");
//		//将字节写入文件
////		newImageFout.write(newImages);
//		newImageFout.close();
//    }

    @Test
    public void test6() {
        captchaService.createPictures();
    }

}

