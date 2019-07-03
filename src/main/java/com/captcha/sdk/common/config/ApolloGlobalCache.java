/*
 *company:jlc
 *author:admin
 *date:2019/6/2611:21
 *desc:{}
 **/


package com.captcha.sdk.common.config;

import java.util.HashMap;
import java.util.Map;

/**
 * @author:xudd
 * @date:2019/6/26 -11:21
 * @desc:
 **/
public class ApolloGlobalCache {

    public static final String APPID_PREFIX="appId.";

    public static final String APPID_IP_CHECK_PREFIX="ip.check.switch.";
    public static final String APPID_IP_WHITE_LIST_RREFIX="ip.white.list.";
    public static final String APPID_IP_USED_COUNT_MAX_PREFIX="ip.used.count.max.";
    public static final String APPID_IP_USED_VALID_TIME_PREFIX="ip.used.valid.time.";

    /***应用ID和密钥**/
    public static Map<String,String> appIdAndKeyMap= new HashMap<>();
     /**应用ID和开关**/
    public static Map<String,String> IPCheckSwitchMap= new HashMap<>();

    /**应用IP白名单**/
    public static Map<String,String> IPWhiteListMap= new HashMap<>();
    /**应用IP最大次数限制**/
    public static Map<String,String> IPUsedCountMaxMap= new HashMap<>();
    /**应用IP限制时间*/
    public static Map<String,String> IPUsedValidTimeMap= new HashMap<>();

}
