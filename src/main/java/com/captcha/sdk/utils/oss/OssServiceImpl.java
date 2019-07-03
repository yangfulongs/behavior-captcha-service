/**
 * @Title: OSSUtils.java
 * @Package com.snb.fund.util.oss
 * @Description: TODO(用一句话描述该文件做什么)
 * @author: xudd
 * @date: 2019年3月14日 下午5:45:06
 */
package com.captcha.sdk.utils.oss;

import com.aliyun.oss.OSSClient;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.*;


/**
 * @ClassName: OSSUtils
 * @Description:TODO(这里用一句话描述这个类的作用)
 * @author: xudd
 * @date: 2019年3月14日 下午5:45:06
 */
@SuppressWarnings("ALL")
@Component
public class OssServiceImpl implements OssService {

    @Resource
    private Environment environment;

    @SuppressWarnings("AliDeprecation")
    @Override
    public String uploadFile(String objectName, ByteArrayInputStream byteArrayInputStream) {
        String endpoint = environment.getProperty("oss.endpoint");
        String accessKeyId = environment.getProperty("oss.accessKeyId");
        String accessKeySecret = environment.getProperty("oss.accessKeySecret");
        String bucketName = environment.getProperty("oss.bucketName");

        OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
        ossClient.putObject(bucketName, objectName, byteArrayInputStream);
        // 调用ossObject.getObjectContent获取文件输入流，可读取此输入流获取其内容。
        StringBuffer contentBuffer = new StringBuffer();
        // 关闭OSSClient。
        ossClient.shutdown();
        return contentBuffer.toString();

    }


}
