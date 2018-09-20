package com.njcool.console.auth.filters;

import com.njcool.console.auth.utils.ShiroFilterUtils;
import com.njcool.console.common.constant.ConsoleStatus;
import com.njcool.console.common.constant.RespBody;
import org.apache.log4j.Logger;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.AccessControlFilter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import static com.njcool.console.auth.utils.ShiroFilterUtils.out;


/**
 * @author xfe
 * @Date 2018/9/13
 * @Desc 认证过滤器
 */
public class AuthenticationFilter extends AccessControlFilter {

    private static final Logger LOG = Logger.getLogger(PermissionFilter.class.getName());

    private String unauthenticatedUrl = "/unauthenticated";        // 未认证转发Url

    private String apiUnauthenticatedUrl = "/unauthenticated";

    /**
     * 判断访问是否被允许
     */
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {
        Subject subject = getSubject(request, response);
        return subject.isAuthenticated();
    }

    /**
     * 访问被拒绝调用方法
     */
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpRequest = null;
        if ((request instanceof HttpServletRequest)) {
            httpRequest = (HttpServletRequest) request;
        } else {
            LOG.error("request is not HttpServletRequest");
        }

        /*if (httpRequest.getServletPath().startsWith("/api") || httpRequest.getServletPath().startsWith("/sys/api")) {
            httpRequest.getRequestDispatcher(apiUnauthenticatedUrl).forward(request, response);
        } else {
            httpRequest.getRequestDispatcher(unauthenticatedUrl).forward(request, response);
        }*/
        //httpRequest.getRequestDispatcher(unauthenticatedUrl).forward(request, response);
        //判断是不是Ajax请求
        //if (ShiroFilterUtils.isAjax(httpRequest)) {
            out(response, new RespBody(ConsoleStatus.RespCode.C101, "登录已失效，请重新登录！"));
            LOG.debug("登录已失效，请重新登录！");
        //}

        return false;
    }

    public void setUnauthenticatedUrl(String unauthenticatedUrl) {
        this.unauthenticatedUrl = unauthenticatedUrl;
    }

    public void setApiUnauthenticatedUrl(String apiUnauthenticatedUrl) {
        this.apiUnauthenticatedUrl = apiUnauthenticatedUrl;
    }
}
