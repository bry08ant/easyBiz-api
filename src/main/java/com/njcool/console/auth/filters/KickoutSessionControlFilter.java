package com.njcool.console.auth.filters;

import com.njcool.console.auth.TokenManager;
import com.njcool.console.auth.session.UserSessionRepository;
import com.njcool.console.auth.utils.ShiroFilterUtils;
import com.njcool.console.common.constant.ConsoleStatus;
import com.njcool.console.common.constant.RespBody;
import com.njcool.console.common.domain.UserDo;
import com.njcool.console.common.utils.RedisCache;
import org.apache.log4j.Logger;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.apache.shiro.web.util.WebUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.*;

import static com.njcool.console.auth.utils.ShiroFilterUtils.out;

/**
 * @author xfe
 * @Date 2018/9/13
 * @Desc 相同帐号登录控制
 */
public class KickoutSessionControlFilter extends AccessControlFilter {

    private static final Logger LOG = Logger.getLogger(KickoutSessionControlFilter.class.getName());

    //踢出后跳转的地址
    private String kickoutUrl;

    //同一个帐号最大会话数 默认1
    private int maxSession = 1;

    private UserSessionRepository userSessionRepository;

    private RedisCache cache;

    private int cacheTimeOut = 3600;

    //在线用户
    final static String ONLINE_USER = "ONLINE_USER:";

    //踢出状态，true标示踢出
    final static String KICK_OUT_STATUS = KickoutSessionControlFilter.class.getCanonicalName()+ "_KICK_OUT_STATUS";


    /**
     * 是否允许访问，返回true表示允许
     */
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object o) throws Exception {
        HttpServletRequest httpRequest = ((HttpServletRequest)request);
        Subject subject = getSubject(request, response);
        //如果没有登录 就直接return true
        if(!subject.isAuthenticated() && !subject.isRemembered()) {
            return Boolean.TRUE;
        }
        Session session = subject.getSession();
        Serializable sessionId = session.getId();

        /*
         * 判断是否已经踢出
         * 1.如果是Ajax 访问，那么给予json返回值提示。2.如果是普通请求，直接跳转到登录页
         */
        Boolean marker = (Boolean)session.getAttribute(KICK_OUT_STATUS);
        if (null != marker && marker ) {
            //判断是不是Ajax请求
            //if (ShiroFilterUtils.isAjax(request)) {
               /* resultMap.put("user_status", "300");
                resultMap.put("message", "您已经在其他地方登录，请重新登录！");*/
                out(response, new RespBody(ConsoleStatus.RespCode.C101, "您已经在其他地方登录，请重新登录！"));
                LOG.debug("当前用户已经在其他地方登录，并且是Ajax请求！");
           // }
            return  Boolean.FALSE;
        }

        //获取tokenId
        Integer userId = TokenManager.getUserId();
        String kickOutKey = ONLINE_USER + userId;

        //LinkedHashMap<Integer, Serializable> infoMap = cache.get(ONLINE_USER_STATUS, LinkedHashMap.class);

        Serializable inSessionId = (Serializable)cache.get(kickOutKey);
        if(inSessionId == null) {
            //存储到缓存1个小时（这个时间最好和session的有效期一致或者大于session的有效期）
            cache.set(kickOutKey, sessionId, cacheTimeOut);
        } else if (!inSessionId.equals(sessionId)) {
            Session oldSession = userSessionRepository.getSession(inSessionId);
            if (oldSession != null) {
                //标记session已经踢出
                oldSession.setAttribute(KICK_OUT_STATUS, Boolean.TRUE);
                userSessionRepository.saveSession(oldSession);//更新session
            }
            //存储到缓存1个小时（这个时间最好和session的有效期一致或者大于session的有效期）
            cache.set(kickOutKey, sessionId, cacheTimeOut);
        }
        return Boolean.TRUE;

    }

    /**
     * 表示访问拒绝时是否自己处理，如果返回true表示自己不处理且继续拦截器链执行，返回false表示自己已经处理了（比如重定向到另一个页面）。
     */
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        Subject subject = getSubject(request, response);
        subject.logout();
        //WebUtils.getSavedRequest(request);
        //WebUtils.issueRedirect(request, response, kickoutUrl);
        return Boolean.FALSE;


        /*if(!subject.isAuthenticated() && !subject.isRemembered()) {
            //如果没有登录，直接进行之后的流程
            return Boolean.TRUE;
        }



        Session session = subject.getSession();
        Serializable sessionId = session.getId();
        UserDo userDo = (UserDo) subject.getPrincipal();
        String userId = String.valueOf(userDo.getId());

        //TODO 同步控制
        Deque<Serializable> deque = cache.get(userId);
        if(deque == null) {
            deque = new LinkedList<Serializable>();
            cache.put(userId, deque);
        }

        //如果队列里没有此sessionId，且用户没有被踢出；放入队列
        if(!deque.contains(sessionId) && session.getAttribute("kickout") == null) {
            deque.push(sessionId);
        }

        //如果队列里的sessionId数超出最大会话数，开始踢人
        while(deque.size() > maxSession) {
            // 踢出一开始登录的人
            Serializable kickoutSessionId = deque.removeLast();
            Session kickoutSession = userSessionRepository.getSession(kickoutSessionId);
            if(kickoutSession != null) {
                //设置会话的kickout属性表示踢出了
                kickoutSession.setAttribute("kickout", true);
            }
        }

        //如果被踢出了，直接退出，重定向到踢出后的地址
        if (session.getAttribute("kickout") != null) {

        }
        return Boolean.TRUE;*/
    }

    public void setKickoutUrl(String kickoutUrl) {
        this.kickoutUrl = kickoutUrl;
    }

    public void setMaxSession(int maxSession) {
        this.maxSession = maxSession;
    }

    public void setUserSessionRepository(UserSessionRepository userSessionRepository) {
        this.userSessionRepository = userSessionRepository;
    }

    public void setCache(RedisCache cache) {
        this.cache = cache;
    }

    public void setCacheTimeOut(int cacheTimeOut) {
        this.cacheTimeOut = cacheTimeOut;
    }
}
