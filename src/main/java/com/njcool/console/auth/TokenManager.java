package com.njcool.console.auth;

import com.njcool.console.common.domain.UserDo;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.springframework.stereotype.Component;

/**
 * @author xfe
 * @Date 2018/9/13
 * @Desc
 */
@Component
public class TokenManager {

    /**
     * 登录
     * @param account
     * @return
     */
    public static UserDo login(String account, String password) {
        UsernamePasswordToken token = new UsernamePasswordToken(account, password);
        SecurityUtils.getSubject().login(token);
        return getToken();
    }

    public static void logout() {
        SecurityUtils.getSubject().logout();
    }

    public static UserDo getToken() {
        return (UserDo) SecurityUtils.getSubject().getPrincipal();
    }

    public static Integer getUserId() {
        UserDo userdo;
        if ((userdo = getToken()) != null) {
            return userdo.getId();
        }
        return null;
    }
}
