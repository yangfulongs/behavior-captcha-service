package com.captcha.sdk.utils;

import com.captcha.sdk.vo.CaptchaPictureCutResult;
import com.captcha.sdk.vo.CaptchaResponse;
import com.captcha.sdk.vo.CaptchaVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Base64Utils;
import org.springframework.util.StringUtils;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

/**
 * 滑块验证工具类
 *
 * @author: LIUTAO
 * @Description:
 * @Date: Created in 10:57 2018/6/25
 * @Modified By:
 */
@Slf4j
public  class VerifyImageUtil {
    /**
     * = 590;  源文件宽度
     */
    private static int ORI_WIDTH ;
    /**
     * = 360;  源文件高度
     */
    private static int ORI_HEIGHT ;
    /**
     * 抠图坐标x
     */
    private static int X;
    /**
     * 抠图坐标y
     */
    private static int Y;
    /**
     * 模板图宽度
     */
    private static int WIDTH;
    /**
     * 模板图高度
     */
    private static int HEIGHT;
    /**
     * X位置移动百分比
     */
    private static float xPercent;
    /**
     * Y位置移动百分比
     */
    private static float yPercent;


    public static int getX() {
        return X;
    }

    public static int getY() {
        return Y;
    }

    public static float getxPercent() {
        return xPercent;
    }

    public static float getyPercent() {
        return yPercent;
    }

    /**
     * 根据模板切图
     * @param templateFile
     * @param targetFile
     * @param templateType
     * @param targetType
     * @return
     * @throws Exception
     */
    public static CaptchaResponse pictureTemplatesCut(File templateFile, File targetFile, String templateType, String targetType, int oriWidth, int oriHeight, CaptchaResponse captchaVo) throws Exception {
        Map<String, Object> pictureMap = new HashMap<>();
        // 文件类型
        String templateFiletype = templateType;
        String oriFiletype = targetType;
        //源文件宽度
        ORI_WIDTH = oriWidth;
        //源文件高度
        ORI_HEIGHT = oriHeight;
        log.info("源文件宽度：{},源文件高度：{}", ORI_WIDTH,ORI_HEIGHT);
        if (StringUtils.isEmpty(templateFiletype) || StringUtils.isEmpty(oriFiletype)) {
            throw new RuntimeException("file type is empty");
        }
        // 源文件流
        File Orifile = targetFile;
        InputStream oriIs = new FileInputStream(Orifile);
        // 模板图
        BufferedImage imageTemplate = ImageIO.read(templateFile);
        WIDTH = imageTemplate.getWidth();
        HEIGHT = imageTemplate.getHeight();
        generateCutoutCoordinates();
        // 最终图像
        BufferedImage newImage = new BufferedImage(WIDTH, HEIGHT, imageTemplate.getType());
        Graphics2D graphics = newImage.createGraphics();
        graphics.setBackground(Color.white);

        int bold = 5;
        // 获取感兴趣的目标区域
        BufferedImage targetImageNoDeal = getTargetArea(X, Y, WIDTH, HEIGHT, oriIs, oriFiletype);
        // 根据模板图片抠图
        newImage = DealCutPictureByTemplate(targetImageNoDeal, imageTemplate, newImage);
        // 设置“抗锯齿”的属性
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setStroke(new BasicStroke(bold, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
        graphics.drawImage(newImage, 0, 0, null);
        graphics.dispose();
        //新建流
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        //利用ImageIO类提供的write方法，将bi以png图片的数据模式写入流。
        ImageIO.write(newImage, "png", os);
        byte[] slideImages = os.toByteArray();
        String slideImagesString = Base64Utils.encodeToString(slideImages);

        pictureMap.put("slideImages", slideImagesString);
        captchaVo.setSlideImages(slideImagesString);
        // 源图生成遮罩
        BufferedImage oriImage = ImageIO.read(Orifile);
        byte[] oriCopyImages = DealOriPictureByTemplate(oriImage, imageTemplate, X, Y);
        String oriCopyImagesString = Base64Utils.encodeToString(oriCopyImages);
        captchaVo.setOriCopyImages(oriCopyImagesString);
        captchaVo.setXPercent(xPercent);
        return captchaVo;
    }

    /**
     * 按照比例便宜直接生成图片
     * @param templateFile
     * @param targetFile
     * @param templateType
     * @param targetType
     * @param oriWidth
     * @param oriHeight
     * @return
     * @throws Exception
     */
    public static CaptchaPictureCutResult pictureTemplatesCutByPercent(File templateFile, File targetFile, String templateType, String targetType, int oriWidth, int oriHeight, int percent) {
        Map<String, Object> pictureMap = new HashMap<>();
        // 文件类型
        String templateFiletype = templateType;
        String oriFiletype = targetType;
        //源文件宽度
        ORI_WIDTH = oriWidth;
        //源文件高度
        ORI_HEIGHT = oriHeight;
        // 源文件流
        File Orifile = targetFile;
        ByteArrayOutputStream os = null;
        Graphics2D graphics = null;
        CaptchaPictureCutResult captchaPictureCutResult = null;
        log.info("源文件宽度：{},源文件高度：{}", ORI_WIDTH, ORI_HEIGHT);
        if (StringUtils.isEmpty(templateFiletype) || StringUtils.isEmpty(oriFiletype)) {
            log.info("初始化图形验证码-切图出现异常");
            throw new RuntimeException("file type is empty");
        }
        try {
            InputStream oriIs = new FileInputStream(Orifile);
            // 模板图
            BufferedImage imageTemplate = ImageIO.read(templateFile);
            WIDTH = imageTemplate.getWidth();
            HEIGHT = imageTemplate.getHeight();
            generateCutoutCoordinates(percent);
            // 最终图像
            BufferedImage newImage = new BufferedImage(WIDTH, HEIGHT, imageTemplate.getType());
            graphics = newImage.createGraphics();
            graphics.setBackground(Color.white);
            int bold = 5;
            // 获取感兴趣的目标区域
            BufferedImage targetImageNoDeal = getTargetArea(X, Y, WIDTH, HEIGHT, oriIs, oriFiletype);
            // 根据模板图片抠图
            newImage = DealCutPictureByTemplate(targetImageNoDeal, imageTemplate, newImage);
            // 设置“抗锯齿”的属性
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics.setStroke(new BasicStroke(bold, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
            graphics.drawImage(newImage, 0, 0, null);
            graphics.dispose();
            //新建流
            os = new ByteArrayOutputStream();
            //利用ImageIO类提供的write方法，将bi以png图片的数据模式写入流。
            ImageIO.write(newImage, "png", os);
            byte[] slideImages = os.toByteArray();
            captchaPictureCutResult = new CaptchaPictureCutResult();
            captchaPictureCutResult.setSlideImages(slideImages);
            // 源图生成遮罩
            BufferedImage oriImage = ImageIO.read(Orifile);
            byte[] oriCopyImages = DealOriPictureByTemplate(oriImage, imageTemplate, X, Y);
            captchaPictureCutResult.setOriCopyImages(oriCopyImages);
            captchaPictureCutResult.setPercent(xPercent);
            return captchaPictureCutResult;
        } catch (Exception ex) {
            log.error("初始化图形验证码，切图发生异常, error:{}", ex);
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                if (graphics != null) {
                    graphics.dispose();
                }
            } catch (Exception ex) {
                log.error("初始化图形验证码，关闭流发生异常, error:{}", ex);
            }
        }
        return captchaPictureCutResult;
    }

    /**
     * 抠图后原图生成
     * @param oriImage
     * @param templateImage
     * @param x
     * @param y
     * @return
     * @throws Exception
     */
    private static byte[] DealOriPictureByTemplate(BufferedImage oriImage, BufferedImage templateImage, int x,
                                                   int y) throws Exception {
        // 源文件备份图像矩阵 支持alpha通道的rgb图像
        BufferedImage ori_copy_image = new BufferedImage(oriImage.getWidth(), oriImage.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
        // 源文件图像矩阵
        int[][] oriImageData = getData(oriImage);
        // 模板图像矩阵
        int[][] templateImageData = getData(templateImage);

        //copy 源图做不透明处理
        for (int i = 0; i < oriImageData.length; i++) {
            for (int j = 0; j < oriImageData[0].length; j++) {
                int rgb = oriImage.getRGB(i, j);
                int r = (0xff & rgb);
                int g = (0xff & (rgb >> 8));
                int b = (0xff & (rgb >> 16));
                //无透明处理
                rgb = r + (g << 8) + (b << 16) + (255 << 24);
                ori_copy_image.setRGB(i, j, rgb);
            }
        }

        for (int i = 0; i < templateImageData.length; i++) {
            for (int j = 0; j < templateImageData[0].length - 5; j++) {
                int rgb = templateImage.getRGB(i, j);
                //对源文件备份图像(x+i,y+j)坐标点进行透明处理
                if (rgb != 16777215 && rgb <= 0) {
                    int rgb_ori = ori_copy_image.getRGB(x + i, y + j);
                    int r = (0xff & rgb_ori);
                    int g = (0xff & (rgb_ori >> 8));
                    int b = (0xff & (rgb_ori >> 16));
                    rgb_ori = r + (g << 8) + (b << 16) + (150 << 24);
                    int color = converRgbToArgb(136, 136, 136);
//                    ori_copy_image.setRGB(x + i, y + j, rgb_ori);
                    ori_copy_image.setRGB(x + i, y + j, color);

                }
            }
        }
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        //利用ImageIO类提供的write方法，将bi以png图片的数据模式写入流。
        ImageIO.write(ori_copy_image, "png", os);
        byte b[] = os.toByteArray();
        return b;
    }


    /**
     * 根据模板图片抠图
     * @param oriImage
     * @param templateImage
     * @return
     */
    private static BufferedImage DealCutPictureByTemplate(BufferedImage oriImage, BufferedImage templateImage,
                                                          BufferedImage targetImage) throws Exception {
        // 源文件图像矩阵
        int[][] oriImageData = getData(oriImage);
        // 模板图像矩阵
        int[][] templateImageData = getData(templateImage);
        // 模板图像宽度
        for (int i = 0; i < templateImageData.length; i++) {
            // 模板图片高度
            for (int j = 0; j < templateImageData[0].length; j++) {
                // 如果模板图像当前像素点不是白色 copy源文件信息到目标图片中
                int rgb = templateImageData[i][j];
                if (rgb != 16777215 && rgb <= 0) {
                    targetImage.setRGB(i, j, oriImageData[i][j]);
                }
            }
        }
        return targetImage;
    }


    /**
     * 获取目标区域
     * @param x            随机切图坐标x轴位置
     * @param y            随机切图坐标y轴位置
     * @param targetWidth  切图后目标宽度
     * @param targetHeight 切图后目标高度
     * @param ois          源文件输入流
     * @throws Exception
     */
    private static BufferedImage getTargetArea(int x, int y, int targetWidth, int targetHeight, InputStream ois,
                                               String filetype) throws Exception {
        Iterator<ImageReader> imageReaderList = ImageIO.getImageReadersByFormatName(filetype);
        ImageReader imageReader = imageReaderList.next();
        // 获取图片流
        ImageInputStream iis = ImageIO.createImageInputStream(ois);
        // 输入源中的图像将只按顺序读取
        imageReader.setInput(iis, true);

        ImageReadParam param = imageReader.getDefaultReadParam();
        Rectangle rec = new Rectangle(x, y, targetWidth, targetHeight);
        param.setSourceRegion(rec);
        BufferedImage targetImage = imageReader.read(0, param);
        return targetImage;
    }

    /**
     * 生成图像矩阵
     * @param
     * @return
     * @throws Exception
     */
    private static int[][] getData(BufferedImage bimg) throws Exception {
        int[][] data = new int[bimg.getWidth()][bimg.getHeight()];
        for (int i = 0; i < bimg.getWidth(); i++) {
            for (int j = 0; j < bimg.getHeight(); j++) {
                data[i][j] = bimg.getRGB(i, j);
            }
        }
        return data;
    }

    /**
     * 随机生成抠图坐标
     */
    private static Map<String,Object> generateCutoutCoordinates() {
        Random random = new Random();
        int widthDifference = ORI_WIDTH - WIDTH;
        int heightDifference = ORI_HEIGHT - HEIGHT;
        int middleWidth= ORI_WIDTH/2;
        if (widthDifference <= 0) {
            X = 5;

        } else {
            X = random.nextInt(ORI_WIDTH - WIDTH-middleWidth)+middleWidth;
            log.info("随机生成抠图X坐标,{}",X);
        }

        if (heightDifference <= 0) {
            Y = 0;
        } else {
            Y = random.nextInt(ORI_HEIGHT - HEIGHT) + 5;
        }
        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMaximumFractionDigits(2);

        float actualWidth = (float) ORI_WIDTH - (float) WIDTH;
        xPercent = Float.parseFloat(numberFormat.format((float) X / actualWidth));
        yPercent = Float.parseFloat(numberFormat.format((float) Y / ORI_HEIGHT));

        Map<String, Object> map = new HashMap<>();
        map.put("X", X);
        map.put("Y", Y);
        map.put("xPercent", xPercent);
        map.put("yPercent", yPercent);
        return map;
    }

    private static Map<String, Object> generateCutoutCoordinates(int percent) {
        Random random = new Random();
        int widthDifference = ORI_WIDTH - WIDTH;
        int heightDifference = ORI_HEIGHT - HEIGHT;
        int middleWidth = ORI_WIDTH / 2;
        int offset = 60;
        if (widthDifference <= 0) {
            X = 5;

        } else {
            //X = ((ORI_WIDTH - WIDTH - middleWidth) * percent / 100) + middleWidth;
            X = (ORI_WIDTH - WIDTH) * percent / 100 + WIDTH + offset;
            log.info("随机生成抠图X坐标,{}", X);
        }

        if (heightDifference <= 0) {
            Y = 0;
        } else {
            Y = random.nextInt(ORI_HEIGHT - HEIGHT)+5;
        }
        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMaximumFractionDigits(2);

        float actualWidth  = (float) ORI_WIDTH-(float)WIDTH;
        xPercent = Float.parseFloat(numberFormat.format((float) X /actualWidth));
        yPercent = Float.parseFloat(numberFormat.format((float) Y /ORI_HEIGHT ));

        Map<String,Object> map = new HashMap<>();
        map.put("X",X);
        map.put("Y",Y);
        map.put("xPercent",xPercent);
        map.put("yPercent",yPercent);
        return map;
    }

    /**
     * base64字符串和图片互转
     * @param imagedata
     * @return
     * @throws Exception
     */
    private static String imageToBase64( byte[] imagedata ) throws  Exception{
        BASE64Encoder encoder = new BASE64Encoder();
        String base64Image=encoder.encodeBuffer(imagedata).trim();
        base64Image = base64Image.replaceAll("\n", "").replaceAll("\r", "");
        return base64Image;
    }

    // rgb转int
    public static int converRgbToArgb(int r,int g,int b){
        int color = ((0xFF << 24)|(r << 16)|(g << 8)|b);
        return color;
    }


}


