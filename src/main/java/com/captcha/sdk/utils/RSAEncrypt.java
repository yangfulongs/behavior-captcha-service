package com.captcha.sdk.utils;

import com.alibaba.fastjson.JSON;
import com.captcha.sdk.vo.RequestVo;
import org.apache.tomcat.util.codec.binary.Base64;
import sun.misc.BASE64Decoder;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * @Description: RSA 加密解密
 * @author:zhangwenting
 * @Date :Create in 2019/6/25 10:26
 */
public class RSAEncrypt {
    /**
     * 服务端私钥(重要！不能随意修改)
     */
    public static final String DEFAULT_PRIVATE_KEY = "MIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQC+BHPP4WUFP0lm" +
                                                     "hSk/sZ8VdEIENUAIddFOKjvRG5MsQkaBlsqD16OBsfFCfo/rM+ULcKiqWPSrB+IV" +
                                                     "XQZtrviQbfe7p9kSYZB+dpqAc4xesmA+T+p7fmC+Q6T2iMZUvDNrzQ72m4QZWRBKR" +
                                                     "olYChz7bUyNrzmT6Te3rMQVAEd5odnPZ0IjSPigBrhqLs2eU8lKd6GqkPju1Odcvt" +
                                                     "oWOuYFSy/fatT2asfgVdRRCmSmL6nZpKhLDWOJ4GOswtv//BIamRl9GeDr3QT8ftH" +
                                                     "4AfLmguF4I3GbmfzqNPLE8ErKkp2NFX36+laDfDeNJ9Dp4MJJqRWZM84LSEmVmhou" +
                                                     "Y4bfAgMBAAECggEAWR5bdsb+iyx4b3XKt83Fj9lb0P9sjcs8JE3YiiVFrD9VNBgV2" +
                                                     "Sn8zKo5Pfd/5k8LgIcRA/v+nGxxE5XymuSyPYLl6oQ5Yc6SZ02W/uGylwNP8kmm7s" +
                                                     "6B7xpotxvE8R7newrMlsqvdPf5lOxqI/lySFWlztm8xmteL23+9N6RpzB5xRTBG+4" +
                                                     "5kI6DkDZ9AKMCzG7ydX3XVCpBgiBcUps8RdTpevAC7zar2LXbcnv7PrNgmMj5/dec" +
                                                     "+/SeUykGBkKO7KWD9BhbgS4waa3CxZ85JUB6Qe1iEuJs4HIO0q5Y8FnrT7f7TXQ/n" +
                                                     "HV4JhmnqbX2AGQfUNE9hGXBj44cEAqeoQKBgQD9c6HCbQ4Yn1vsuwf1tfQxdCjHDs" +
                                                     "F4VLOMnQJj+jCW251xhPOJdCT2vJZ4LlA+Of3dt2KxDVtvp1YjKN4gKRhp9xbSCZ/" +
                                                     "hfIJB26v3rMTIU+gZS2TVTXjHpvDuzVNaADPK8IqWCtiC82XI83scUGyfyc8DWEZh" +
                                                     "bndvhAx9MRQXMQKBgQC/7Yt2dhRRNVzDSGQ5a1vxBpd9WSc9FuGiVul1LU80l/dTQ" +
                                                     "47THYPdjfl5919gqOOa19PR/PWRE5dZy3N0BtueN61qufoDfJECtfb0N9D+qOD2vu" +
                                                     "IkEcrTE5WTksJU1nCdnEpRCvq21MhDC24TT4XPitphxLmRHK84haRFQwQbDwKBgQC" +
                                                     "DExrz9vxURY3/xtYd/6SzMrSoBXyO45cbwFNtKwWX7ynkfHKKgpNfOzbCHomHNOyS" +
                                                     "jnWuxQ6llq/YbRk7HqWun4egxjN2JLMkphuwchwu0h31fuU93tv4vOBbT7qsj/A5I" +
                                                     "t85K5C/YGsiWrEvenRCytaRj0SOvNdtx6fB0vDl8QKBgQCZyVyDelADVi/TJlFwlO" +
                                                     "7h7Xc7cFF5qLj5XIzKYJjjupTp+esajL7Zq2ZobWfWSt29eAz5aKjYK4uN3vDO2qP" +
                                                     "lXhWtvAFyA8EqG+YagBMNywIflIodfsg3Y6zpx6HBWuZ31ADUeWi1UUB6QFFeVc0R" +
                                                     "pOAhkQFkHkjkkGk1OmKyzwKBgQCLhPYwzdBd0Q/0L5sW8vaixKcO3JOpRUKWa9Jzn" +
                                                     "7iyKPLeMbHvmrPYTaHgxLM32Ib3ecIUySW23WSh/QTKsu1qCIbQZUE+vK7HB6CUQd" +
                                                     "bx/uqOg7NURYxryZIQY4uOsRVJq2yX3cdjRUPOwKc14dt4ZYLa3Js0zrwkXF8gf7OMYQ==";


    /**
     * 服务端公钥(重要！不能随意修改)
     */
    public static final String DEFAULT_PUBLIC_KEY ="MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAvgRzz+FlBT9JZoUpP7Gf" +
                                                   "FXRCBDVACHXRTio70RuTLEJGgZbKg9ejgbHxQn6P6zPlC3Coqlj0qwfiFV0Gba74" +
                                                   "kG33u6fZEmGQfnaagHOMXrJgPk/qe35gvkOk9ojGVLwza80O9puEGVkQSkaJWAoc" +
                                                   "+21Mja85k+k3t6zEFQBHeaHZz2dCI0j4oAa4ai7NnlPJSnehqpD47tTnXL7aFjrm" +
                                                   "BUsv32rU9mrH4FXUUQpkpi+p2aSoSw1jieBjrMLb//wSGpkZfRng690E/H7R+AHy" +
                                                   "5oLheCNxm5n86jTyxPBKypKdjRV9+vpWg3w3jSfQ6eDCSakVmTPOC0hJlZoaLmOG" +
                                                   "3wIDAQAB";


    /**
     * 私钥
     */
    private RSAPrivateKey privateKey;

    /**
     * 公钥
     */
    private RSAPublicKey publicKey;

    /**
     * 字节数据转字符串专用集合
     */
    private static final char[] HEX_CHAR= {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    /**
     * 获取私钥
     * @return 当前的私钥对象
     */
    public RSAPrivateKey getPrivateKey() {
        return privateKey;
    }

    /**
     * 获取公钥
     * @return 当前的公钥对象
     */
    public RSAPublicKey getPublicKey() {
        return publicKey;
    }

    /**
     * 随机生成密钥对
     */
    public void genKeyPair(){
        KeyPairGenerator keyPairGen= null;
        try {
            keyPairGen= KeyPairGenerator.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        keyPairGen.initialize(1024, new SecureRandom());
        KeyPair keyPair= keyPairGen.generateKeyPair();
        this.privateKey= (RSAPrivateKey) keyPair.getPrivate();
        this.publicKey= (RSAPublicKey) keyPair.getPublic();
    }


    /**
     * 从字符串中加载公钥
     * @param publicKeyStr 公钥数据字符串
     * @throws Exception 加载公钥时产生的异常
     */
    public void loadPublicKey(String publicKeyStr) throws Exception{
        try {
            BASE64Decoder base64Decoder= new BASE64Decoder();
            byte[] buffer= base64Decoder.decodeBuffer(publicKeyStr);
            KeyFactory keyFactory= KeyFactory.getInstance("RSA");
            X509EncodedKeySpec keySpec= new X509EncodedKeySpec(buffer);
            this.publicKey= (RSAPublicKey) keyFactory.generatePublic(keySpec);
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("无此算法");
        } catch (InvalidKeySpecException e) {
            throw new Exception("公钥非法");
        } catch (IOException e) {
            throw new Exception("公钥数据内容读取错误");
        } catch (NullPointerException e) {
            throw new Exception("公钥数据为空");
        }
    }


    public void loadPrivateKey(String privateKeyStr) throws Exception{
        try {
            BASE64Decoder base64Decoder= new BASE64Decoder();
            byte[] buffer= base64Decoder.decodeBuffer(privateKeyStr);
            PKCS8EncodedKeySpec keySpec= new PKCS8EncodedKeySpec(buffer);
            KeyFactory keyFactory= KeyFactory.getInstance("RSA");
            this.privateKey= (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("无此算法");
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
            throw new Exception("私钥非法");
        } catch (IOException e) {
            throw new Exception("私钥数据内容读取错误");
        } catch (NullPointerException e) {
            throw new Exception("私钥数据为空");
        }
    }

    /**
     * 字节数据转十六进制字符串
     * @param data 输入数据
     * @return 十六进制内容
     */
    public static String byteArrayToString(byte[] data){
        StringBuilder stringBuilder= new StringBuilder();
        for (int i=0; i<data.length; i++){
            //取出字节的高四位 作为索引得到相应的十六进制标识符 注意无符号右移
            stringBuilder.append(HEX_CHAR[(data[i] & 0xf0)>>> 4]);
            //取出字节的低四位 作为索引得到相应的十六进制标识符
            stringBuilder.append(HEX_CHAR[(data[i] & 0x0f)]);
            if (i<data.length-1){
                stringBuilder.append(' ');
            }
        }
        return stringBuilder.toString();
    }


    /**
     * 加密过程
     * @param plainTextData 明文数据
     * @return
     * @throws Exception 加密过程中的异常信息
     */
    public byte[] encrypt(byte[] plainTextData) throws Exception{
        try {
            loadPublicKey(RSAEncrypt.DEFAULT_PUBLIC_KEY);
        } catch (Exception e) {
            //System.err.println(e.getMessage());
        }
        Cipher cipher= null;
        try {
            cipher= Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, getPublicKey());
            byte[] output= cipher.doFinal(plainTextData);
            return output;
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("无此加密算法");
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
            return null;
        }catch (InvalidKeyException e) {
            throw new Exception("加密公钥非法,请检查");
        } catch (IllegalBlockSizeException e) {
            throw new Exception("明文长度非法");
        } catch (BadPaddingException e) {
            throw new Exception("明文数据已损坏");
        }
    }

    /**
     * 解密过程
     * @param cipherData 密文数据
     * @return 明文
     * @throws Exception 解密过程中的异常信息
     */
    public byte[] decrypt(byte[] cipherData) throws Exception{
        // 加载私钥
        try {
            loadPrivateKey(RSAEncrypt.DEFAULT_PRIVATE_KEY);
        } catch (Exception e) {
          //  System.err.println(e.getMessage());
        }
        Cipher cipher= null;
        try {
            cipher= Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, getPrivateKey());
            byte[] output= cipher.doFinal(cipherData);
            return output;
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("无此解密算法");
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
            return null;
        }catch (InvalidKeyException e) {
            throw new Exception("解密私钥非法,请检查");
        } catch (IllegalBlockSizeException e) {
            throw new Exception("密文长度非法");
        } catch (BadPaddingException e) {
            throw new Exception("密文数据已损坏");
        }
    }

    // 测试
    public static void main(String[] args) {
        RSAEncrypt rsaEncrypt = new RSAEncrypt();
        //加载公钥
        try {
            rsaEncrypt.loadPublicKey(RSAEncrypt.DEFAULT_PUBLIC_KEY);
            System.out.println("加载公钥成功");
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.err.println("加载公钥失败");
        }
        // 加载私钥
        try {
            rsaEncrypt.loadPrivateKey(RSAEncrypt.DEFAULT_PRIVATE_KEY);
            System.out.println("加载私钥成功");
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.err.println("加载私钥失败");
        }
        System.out.println("私钥长度："+rsaEncrypt.getPrivateKey().toString().length());
        System.out.println("公钥长度："+rsaEncrypt.getPublicKey().toString().length());
        try {
            // 加密
            String encryptStr= "{\"xPercent\":0.6402778,\"uniqueFlag\":\"4ff67a14-5623-4cf8-9b79-b70c86d211dd\",\"appId\":\"jianming\",\"channel\":\"app\",\"timeStamp\":1561601433503}bHz08Quqrrrrrrrrrrrrrr";
			byte[] cipher = rsaEncrypt.encrypt(encryptStr.getBytes());
			System.out.println("1:"+cipher);
			System.out.println(byteArrayToString(cipher));
            System.out.println(Base64.encodeBase64String(cipher));
            int endIndex = encryptStr.indexOf("}");
            System.out.println(endIndex);
            RequestVo requestVo = JSON.parseObject(JSON.parse(encryptStr.substring(0,endIndex+1)).toString(),RequestVo.class);
            System.out.println(requestVo.getTimeStamp());

			String strs = "OTGdiimF+737Gu7yiRN0nBqJNZ7uxZQLdX4tDSIwtAo5LUO6g+mam+vn0zhovstIaSX4E2+d50XqXsAvMOosGRObVeKNzjVfh73/ijAFnMA3QHi5ziwar26EoenugMcoa5shf+7JtHhg1O9/BY2gacmE6cnGQgH8hSCSftQxAVUjCvIBH5DYiqA2a03wIRU71dfwo2WXIULSLE/nAKDkQ4GMq/BP/Aac81wlEAlNiJYcNZ4dO4CYCcC0Fpp7MWv1fA3KeGkGg73oVPI18lPu2aeK4xmU7JVMXspnIgF3MPQZ/ugcsttrqXUHheit+/uYm18Z5r/ceixC428HBJP7EQ==";
            // 解密
            strs = strs.replaceAll(" ", "+");
           byte[] plainText = rsaEncrypt.decrypt(Base64.decodeBase64(strs));

			System.out.println("cipher:["+Base64.decodeBase64(strs)+"]");
			System.out.println("plainText:["+plainText+"]");

			System.out.println(byteArrayToString(plainText));
			String str = new String(plainText,"UTF-8");
			System.out.println(str);

          /*  RequestVo requestVo = JSON.parseObject(JSON.parse(str).toString(),RequestVo.class);
            System.out.println(requestVo.getXPercent());*/
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
