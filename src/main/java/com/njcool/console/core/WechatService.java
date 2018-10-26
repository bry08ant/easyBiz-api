package com.njcool.console.core;

import com.njcool.console.common.config.JedisConfig;
import me.chanjar.weixin.open.api.impl.WxOpenInRedisConfigStorage;
import me.chanjar.weixin.open.api.impl.WxOpenMessageRouter;
import me.chanjar.weixin.open.api.impl.WxOpenServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisPool;

import javax.annotation.PostConstruct;

/**
 * @author vino
 * @create 2018-09-07
 **/
public class WechatService extends WxOpenServiceImpl {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private JedisConfig jedisConfig;

    private static JedisPool pool;

    private WxOpenMessageRouter wxOpenMessageRouter;

    @PostConstruct
    public void init() {
        try {
            WxOpenInRedisConfigStorage inRedisConfigStorage =
                    new WxOpenInRedisConfigStorage(getJedisPool());
            inRedisConfigStorage.setComponentAppId("wx588b3dc9e8586e39");
            inRedisConfigStorage.setComponentAppSecret("993ba90af0966bb2b5a62a05b861140d");
            inRedisConfigStorage.setComponentToken("aiyimini_public");
            inRedisConfigStorage.setComponentAesKey("YfPq95J69ST9vrQnRNcL7uxBZfM7iS7R4eABwMZvVCg");
            setWxOpenConfigStorage(inRedisConfigStorage);
            wxOpenMessageRouter = new WxOpenMessageRouter(this);
            wxOpenMessageRouter.rule().handler((wxMpXmlMessage, map, wxMpService, wxSessionManager) -> {
                logger.info("\n接收到 {} 公众号请求消息，内容：{}", wxMpService.getWxMpConfigStorage().getAppId(), wxMpXmlMessage);
                return null;
            }).next();
        } catch (Exception ex) {
            logger.error("wechatService init:" + ex);
        }
    }

    private JedisPool getJedisPool() {
        if (pool == null) {
            synchronized (getClass()) {
                if (pool == null) {
                    pool = jedisConfig.redisPoolFactory();
                }
            }
        }
        return pool;
    }
}