package com.njcool.console.auth;

import com.njcool.console.common.utils.RedisCache;
import com.njcool.console.common.utils.TimeUtils;
import org.apache.log4j.Logger;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;

/**
 * @author xfe
 * @Date 2018/9/16
 * @Desc 登录失败重试次数
 */
public class RetryLimitCredentialsMatcher extends HashedCredentialsMatcher {

    private static final Logger LOG = Logger.getLogger(RetryLimitCredentialsMatcher.class.getName());

    private RedisCache logoInRetryCache;

    private static String LOCK_USER_CACHE_KEY = "LOCK_USER_";

    public RetryLimitCredentialsMatcher () {}

    @Override
    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
        String telephone = (String) token.getPrincipal();
        String cacheKey = LOCK_USER_CACHE_KEY + telephone;

        //retry count + 1
        long retryCount = logoInRetryCache.incr(cacheKey, 1);
        if (retryCount == 1) {
            logoInRetryCache.expire(cacheKey, TimeUtils.getMillSecOnMoment(23, 59, 59, 999));
        }

        if (retryCount <= 5) {
            if (!super.doCredentialsMatch(token, info)) {
                if (retryCount == 5) {
                    //LOG.warn("telephone: " + telephone + " tried to login more than 5 times in period");
                    throw new ExcessiveAttemptsException("telephone: " + telephone + " tried to login more than 5 times in period");
                }
                throw new IncorrectCredentialsException("用户名或密码错误，还剩"+(5 - retryCount)+"次机会");
            } else {
                logoInRetryCache.del(cacheKey);
                return true;
            }
        }

        LOG.warn("telephone: " + telephone + " tried to login more than 5 times in period");
        throw new ExcessiveAttemptsException("telephone: " + telephone + " tried to login more than 5 times in period");

    }

    public void setLogoInRetryCache(RedisCache logoInRetryCache) {
        this.logoInRetryCache = logoInRetryCache;
    }
}
