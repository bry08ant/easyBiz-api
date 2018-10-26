package com.njcool.console.common.config;

import me.chanjar.weixin.open.api.impl.WxOpenInRedisConfigStorage;
import me.chanjar.weixin.open.api.impl.WxOpenMessageRouter;
import me.chanjar.weixin.open.api.impl.WxOpenServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;

/**
 * @author xfe
 * @Date 2018/10/26
 * @Desc
 */
@Configuration
public class WxOpenConfig {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${wx.open.component.appId}")
    private String appId;

    @Value("${wx.open.component.appSecret}")
    private String appSecret;

    @Value("${wx.open.component.token}")
    private String token;

    @Value("${wx.open.component.aesKey}")
    private String aesKey;

    @Bean
    public WxOpenServiceImpl wxOpenInRedisConfigStorage(JedisPool redisPoolFactory) {
        WxOpenServiceImpl wxOpenService = new WxOpenServiceImpl();
        WxOpenInRedisConfigStorage inRedisConfigStorage = new WxOpenInRedisConfigStorage(redisPoolFactory);
        inRedisConfigStorage.setComponentAppId(appId);
        inRedisConfigStorage.setComponentAppSecret(appSecret);
        inRedisConfigStorage.setComponentToken(token);
        inRedisConfigStorage.setComponentAesKey(aesKey);
        wxOpenService.setWxOpenConfigStorage(inRedisConfigStorage);

        new WxOpenMessageRouter(wxOpenService).rule().handler((wxMpXmlMessage, map, wxMpService, wxSessionManager) -> {
            logger.info("\n接收到 {} 公众号请求消息，内容：{}", wxMpService.getWxMpConfigStorage().getAppId(), wxMpXmlMessage);
            return null;
        }).next();

        return wxOpenService;
    }
}
