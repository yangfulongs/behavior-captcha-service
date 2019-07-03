/**
 * @Title: OssService.java
 * @Package com.snb.fund.util.oss
 * @Description: TODO(用一句话描述该文件做什么)
 * @author: xudd
 * @date: 2019年3月14日 下午6:14:22
 */
package com.captcha.sdk.utils.oss;

import java.io.ByteArrayInputStream;

/**
 * @ClassName: OssService
 * @Description:TODO(这里用一句话描述这个类的作用)
 * @author: xudd
 * @date: 2019年3月14日 下午6:14:22
 */
public interface OssService {
    String uploadFile(String objectName, ByteArrayInputStream byteArrayInputStream);
}
