package com.njcool.console.auth.utils;

import com.njcool.console.common.constant.RespBody;
import com.njcool.console.common.utils.JsonUtils;
import org.apache.log4j.Logger;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.PrintWriter;
import java.util.Map;

/**
 * @author xfe
 * @Date 2018/9/13
 * @Desc filter工具类
 */
public class ShiroFilterUtils {
    private static final Logger LOG = Logger.getLogger(ShiroFilterUtils.class.getName());

    //登录页面
    public static final String LOGIN_URL = "/u/login.shtml";
    //踢出登录提示
    public final static String KICKED_OUT = "/open/kickedOut.shtml";
    //没有权限提醒
    public final static String UNAUTHORIZED = "/ui/unauthorized.shtml";
    /**
     * 是否是Ajax请求
     * @param request
     * @return
     */
    public static boolean isAjax(ServletRequest request) {
        return "XMLHttpRequest".equalsIgnoreCase(((HttpServletRequest) request).getHeader("X-Requested-With"));
    }

    /**
     * response 输出JSON
     * @param result
     */
    public static void out(ServletResponse response, RespBody result){

        PrintWriter out = null;
        try {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("text/javascript; charset=utf-8");
            out = response.getWriter();
            out.println(JsonUtils.objectToString(result));
        } catch (Exception e) {
            LOG.error("输出JSON报错。", e);
        } finally {
            if(null != out) {
                out.flush();
                out.close();
            }
        }
    }
}
