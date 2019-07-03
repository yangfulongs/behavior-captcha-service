package com.captcha.sdk.common.config;


import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.io.Serializable;
import java.time.Duration;

/**
 * @author lizengqiang
 * @Description
 * @date 2018/4/14 16:28
 */
@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, String> redisStringTemplate(LettuceConnectionFactory factory) {
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<String, String>();
        redisTemplate.setConnectionFactory(factory);
        // key序列化方式;（不然会出现乱码;）,但是如果方法上有Long等非String类型的话，会报类型转换错误；
        // 所以在没有自己定义key生成策略的时候，以下这个代码建议不要这么写，可以不配置或者自己实现ObjectRedisSerializer
        // 或者JdkSerializationRedisSerializer序列化方式;
        // Long类型不可以会出现异常信息;
        RedisSerializer<String> redisSerializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(redisSerializer);
        redisTemplate.setHashKeySerializer(redisSerializer);
        return redisTemplate;
    }

    @Bean
    public RedisTemplate<Serializable, Serializable> redisTemplate(
            LettuceConnectionFactory redisConnectionFactory) {
        RedisTemplate<Serializable, Serializable> redisTemplate = new RedisTemplate<Serializable, Serializable>();
        //key序列化方式;（不然会出现乱码;）,但是如果方法上有Long等非String类型的话，会报类型转换错误；
        //所以在没有自己定义key生成策略的时候，以下这个代码建议不要这么写，可以不配置或者自己实现 ObjectRedisSerializer
        //或者JdkSerializationRedisSerializer序列化方式;
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new JdkSerializationRedisSerializer());
        redisTemplate.setHashValueSerializer(new JdkSerializationRedisSerializer());
        //以上4条配置可以不用
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        return redisTemplate;
    }

    @Bean
    public LettuceConnectionFactory redisConnectionFactory(Environment environment) {
        RedisStandaloneConfiguration standaloneConfig =
                new RedisStandaloneConfiguration(
                        environment.getProperty("redis.master.host"),
                        environment.getProperty("redis.port", Integer.class, 6379));
        standaloneConfig.setPassword(RedisPassword.of(environment.getProperty("redis.password")));
        standaloneConfig.setDatabase(environment.getProperty("redis.default.db", Integer.class, 4));
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setMaxTotal(environment.getProperty("redis.pool.maxTotal", Integer.class, 300));
        config.setMaxIdle(environment.getProperty("redis.pool.maxIdle", Integer.class, 50));
        config.setMaxWaitMillis(environment.getProperty("redis.pool.maxWaitMillis", Long.class, 10000L));
        config.setTestOnBorrow(environment.getProperty("redis.pool.testOnBorrow", Boolean.class, false));
        LettuceClientConfiguration.LettuceClientConfigurationBuilder builder = LettucePoolingClientConfiguration.builder().poolConfig(config);
        builder.commandTimeout(Duration.ofMillis(environment.getProperty("redis.timeout", Long.class, 10000L)));
        return new LettuceConnectionFactory(standaloneConfig, builder.build());
    }
}
