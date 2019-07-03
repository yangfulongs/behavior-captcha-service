/*
 *company:jlc
 *author:admin
 *date:2019/6/2611:28
 *desc:{}
 **/


package com.captcha.sdk.schedule;

import com.captcha.sdk.common.config.ApolloGlobalCache;
import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import java.util.Objects;
import java.util.Set;

/**
 * @author:xudd
 * @date:2019/6/26 -11:28
 * @desc:
 **/
@Configuration
@EnableScheduling
@Slf4j
public class ApolloLocalCacheScheduleTask {

    /**或直接指定时间间隔，例如：5分钟*/
    @Scheduled(initialDelay=1000, fixedRate=5000*60)
    private void configureTasks() {
        Config config = ConfigService.getConfig("appInfo");
        if(Objects.isNull(config)){
            log.error("定时任外拉取apollo的namespace的appInfo失败");
            return;
        }
        Set<String> propertyNamesSet=config.getPropertyNames();
        if(Objects.isNull(propertyNamesSet)){
            log.error("获取的apollo的参数名称列表为空");
            return;
        }
        log.info("拉取apollo数据定时任务执行{}",propertyNamesSet);
        for(String p:propertyNamesSet){
            if(p.startsWith(ApolloGlobalCache.APPID_PREFIX)){
                ApolloGlobalCache.appIdAndKeyMap.put(p,config.getProperty(p,""));
            }else if(p.startsWith(ApolloGlobalCache.APPID_IP_CHECK_PREFIX)){
                ApolloGlobalCache.IPCheckSwitchMap.put(p,config.getProperty(p,"0"));
            }else if(p.startsWith(ApolloGlobalCache.APPID_IP_WHITE_LIST_RREFIX)){
                ApolloGlobalCache.IPWhiteListMap.put(p,config.getProperty(p,""));
            }
            else if(p.startsWith(ApolloGlobalCache.APPID_IP_USED_COUNT_MAX_PREFIX)){
                ApolloGlobalCache.IPUsedCountMaxMap.put(p,config.getProperty(p,"5"));
            }else if(p.startsWith(ApolloGlobalCache.APPID_IP_USED_VALID_TIME_PREFIX)){
                ApolloGlobalCache.IPUsedValidTimeMap.put(p,config.getProperty(p,"1"));
            }

        }

    }
}
