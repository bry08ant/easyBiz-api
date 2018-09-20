package com.njcool.console.auth.session;

import com.njcool.console.common.utils.RedisCache;
import com.njcool.console.common.utils.SerializeUtil;
import org.apache.log4j.Logger;
import org.apache.shiro.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;

/**
 * @author xfe
 * @Date 2018/9/12
 * @Desc
 */
@Component
public class UserSessionRepository {
    private static final Logger LOG = Logger.getLogger(UserSessionRepository.class.getName());

    public static final String REDIS_SHIRO_SESSION = "console-session:";
    //这里有个小BUG，因为Redis使用序列化后，Key反序列化回来发现前面有一段乱码，解决的办法是存储缓存不序列化
    public static final String REDIS_SHIRO_ALL = "console-session:*";
    private static final int SESSION_VAL_TIME_SPAN = 18000;

    @Autowired
    private RedisCache redisCache;

    public void saveSession(Session session) {
        if (session == null || session.getId() == null)
            throw new NullPointerException("session is empty");
        try {
            //byte[] key = SerializeUtil.serialize(buildRedisSessionKey(session.getId()));
            String key = buildRedisSessionKey(session.getId());

            //不存在才添加。
            if(null == session.getAttribute(UserSessionManager.SESSION_STATUS)) {
                //Session 踢出自存存储。
                SessionStatus sessionStatus = new SessionStatus();
                session.setAttribute(UserSessionManager.SESSION_STATUS, sessionStatus);
            }

            //byte[] value = SerializeUtil.serialize(session);
            long sessionTimeOut = session.getTimeout() / 1000;
            //Long expireTime = sessionTimeOut + SESSION_VAL_TIME_SPAN + (5 * 60);
            Long expireTime = sessionTimeOut;
            redisCache.set(key, session, expireTime.intValue());
        } catch (Exception e) {
            LOG.error(String.format("save session error，id:[%s]",session.getId()), e);
        }
    }

    public void deleteSession(Serializable id) {
        if (id == null) {
            throw new NullPointerException("session id is empty");
        }
        try {
            //getJedisManager().deleteByKey(DB_INDEX, SerializeUtil.serialize(buildRedisSessionKey(id)));
            redisCache.del(buildRedisSessionKey(id));
        } catch (Exception e) {
            LOG.error(String.format("删除session出现异常，id:[%s]", id), e);
        }
    }


    public Session getSession(Serializable id) {
        if (id == null)
            throw new NullPointerException("session id is empty");
        Session session = null;
        try {
            //byte[] value = getJedisManager().getValueByKey(DB_INDEX, SerializeUtil.serialize(buildRedisSessionKey(id)));
            session = (Session)redisCache.get(buildRedisSessionKey(id));
            //session = SerializeUtil.deserialize(value.getBytes(), Session.class);
        } catch (Exception e) {
            LOG.error(String.format("获取session异常，id:[%s]",id),e);
        }
        return session;
    }

    public Collection<Session> getAllSessions() {
        Collection<Session> sessions = null;
        try {
            Set<String> keySet = redisCache.keys(REDIS_SHIRO_ALL);
            if (keySet != null && keySet.size() > 0) {
                for (String _key : keySet) {
                    Session obj = (Session) redisCache.get(_key);
                    if(obj instanceof Session)
                    {
                        sessions.add(obj);
                    }
                }
            }
        } catch (Exception e) {
            LOG.error("获取全部session异常",e);
        }

        return sessions;
    }

    private String buildRedisSessionKey(Serializable sessionId) {
        return REDIS_SHIRO_SESSION + sessionId;
    }

}
