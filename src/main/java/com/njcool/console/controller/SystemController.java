package com.njcool.console.controller;

import com.njcool.console.auth.TokenManager;
import com.njcool.console.common.constant.ConsoleStatus;
import com.njcool.console.common.constant.RespBody;
import com.njcool.console.common.domain.UserDo;
import com.njcool.console.core.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author xfe
 * @Date 2018/9/12
 * @Desc 系统相关接口
 */
@CrossOrigin
@RestController
@RequestMapping("/ui/sys/")
public class SystemController {

    /*@Autowired
    private TokenManager tokenManager;*/

    /**
     * ajax登录请求
     * @return
     */
    @RequestMapping(value="login",method = RequestMethod.POST)
    @ResponseBody
    public RespBody login(@RequestBody Map<String, String> params, HttpServletRequest request, HttpServletResponse response) {
//        String account = request.getParameter("account");
//        String password = request.getParameter("password");
        String account = params.get("account");
        String password = params.get("password");

        try {
            TokenManager.login(account, password);
            Serializable sessionId = SecurityUtils.getSubject().getSession().getId();
            return new RespBody(ConsoleStatus.RespCode.C000, "登录成功", sessionId);
        } catch (LockedAccountException e){ // 锁定的帐号
            return new RespBody(ConsoleStatus.RespCode.C101, "用户名被冻结");
        }catch (ExcessiveAttemptsException e) { //登录失败次数过多
            return new RespBody(ConsoleStatus.RespCode.C101, "账号已锁，请明天再来或联系管理员解锁！");
        } catch (UnknownAccountException e) { //错误的帐号
            return new RespBody(ConsoleStatus.RespCode.C101, "错误的帐号");
        } catch (ExpiredCredentialsException e) { //过期的凭证
            return new RespBody(ConsoleStatus.RespCode.C101, "过期的凭证");
        } catch (DisabledAccountException e) { //禁用的账号
            return new RespBody(ConsoleStatus.RespCode.C101, "禁用的账号");
        } catch (IncorrectCredentialsException e) { // 用户名/密码错误
            return new RespBody(ConsoleStatus.RespCode.C101, e.getMessage());
        } catch (AccountException e) { // 用户名/密码错误
            return new RespBody(ConsoleStatus.RespCode.C101, "用户名或密码错误");
        } catch (UnauthorizedException e) {
            return new RespBody(ConsoleStatus.RespCode.C101, "您没有得到相应的授权");
        }
    }

    @RequestMapping(value="logout",method = RequestMethod.POST)
    @ResponseBody
    public RespBody logout(HttpServletRequest request, HttpServletResponse response) {
        TokenManager.logout();
        return new RespBody();
    }

    @RequestMapping(value="notLogin",method = RequestMethod.POST)
    @ResponseBody
    public RespBody notLogin(HttpServletRequest request, HttpServletResponse response) {
        return new RespBody(ConsoleStatus.RespCode.C101, "用户未登录");
    }


}
