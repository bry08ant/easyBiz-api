package com.njcool.console.common.config;

import com.njcool.console.auth.AuthShiroRealm;
import com.njcool.console.auth.FilterChainManagerImpl;
import com.njcool.console.auth.RetryLimitCredentialsMatcher;
import com.njcool.console.auth.cache.CustomCacheManager;
import com.njcool.console.auth.filters.AuthenticationFilter;
import com.njcool.console.auth.filters.KickoutSessionControlFilter;
import com.njcool.console.auth.filters.PermissionFilter;
import com.njcool.console.auth.session.CustomWebSessionManager;
import com.njcool.console.auth.session.UserSessionDAO;
import com.njcool.console.auth.session.UserSessionRepository;
import com.njcool.console.common.utils.RedisCache;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.mgt.eis.JavaUuidSessionIdGenerator;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import javax.servlet.Filter;
import java.util.HashMap;

/**
 * @author xfe
 * @Date 2018/9/12
 * @Desc Shiro安全配置 集群配置
 */
@Configuration
public class ShiroConfig {

    /**
     * Shiro过滤器 核心
     * @param securityManager
     * @return
     */
    @Bean
    public ShiroFilterFactoryBean shiroFilter(SecurityManager securityManager, FilterChainManagerImpl filterChainManager,KickoutSessionControlFilter kickoutSessionFilter,AuthenticationFilter authenticationFilter,PermissionFilter permissionFilter) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);

        //配置shiro默认登录界面地址，前后端分离中登录界面跳转应由前端路由控制，后台仅返回json数据
        shiroFilterFactoryBean.setLoginUrl("/unauth");
        // 登录成功后要跳转的链接
        shiroFilterFactoryBean.setSuccessUrl("/index");
        //未授权界面;
        shiroFilterFactoryBean.setUnauthorizedUrl("/unauthorized");
        shiroFilterFactoryBean.setFilterChainDefinitions(filterChainManager.loadFilterChainDefinitions());
        shiroFilterFactoryBean.setFilters(new HashMap<String,Filter>(){
            {
                put("authc", authenticationFilter);
                put("perms", permissionFilter);
                put("kickout", kickoutSessionFilter);
            }
        });
        return shiroFilterFactoryBean;
    }

    /**
     * 凭证匹配器
     * （由于我们的密码校验交给Shiro的SimpleAuthenticationInfo进行处理了
     * ）
     *
     * @return
     */
    @Bean
    public HashedCredentialsMatcher hashedCredentialsMatcher(RedisCache RedisCache) {
        RetryLimitCredentialsMatcher hashedCredentialsMatcher = new RetryLimitCredentialsMatcher();
        hashedCredentialsMatcher.setLogoInRetryCache(RedisCache);
        hashedCredentialsMatcher.setHashAlgorithmName("md5");//散列算法:这里使用MD5算法;
        hashedCredentialsMatcher.setHashIterations(1);//散列的次数，比如散列两次，相当于 md5(md5(""));
        return hashedCredentialsMatcher;
    }

    @Bean
    public AuthShiroRealm authShiroRealm(HashedCredentialsMatcher hashedCredentialsMatcher) {
        AuthShiroRealm authShiroRealm = new AuthShiroRealm();
        authShiroRealm.setCredentialsMatcher(hashedCredentialsMatcher);
        return authShiroRealm;
    }

    /**
     * 安全管理器
     * @return
     */
    @Bean
    public SecurityManager securityManager(AuthShiroRealm authShiroRealm, CustomCacheManager cacheManager, CustomWebSessionManager sessionManager) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(authShiroRealm);
        // 自定义session管理 使用redis
        securityManager.setSessionManager(sessionManager);
        // 自定义缓存实现 使用redis
        securityManager.setCacheManager(cacheManager);
        return securityManager;
    }


    /**
     * 自定义sessionManager
     * @param userSessionDAO
     * @param sessionIdCookie
     * @return
     */
    @Bean
    public CustomWebSessionManager sessionManager(UserSessionDAO userSessionDAO, SimpleCookie sessionIdCookie) {
        CustomWebSessionManager mySessionManager = new CustomWebSessionManager();
        //相隔多久检查一次session的有效性   毫秒
        mySessionManager.setSessionValidationInterval(600000);
        //会话超时: 1小时 （单位:毫秒）
        mySessionManager.setGlobalSessionTimeout(3600000);
        //session存储的实现
        mySessionManager.setSessionDAO(userSessionDAO);
        //会话Cookie模板
        mySessionManager.setSessionIdCookie(sessionIdCookie);
        //是否删除过期会话，默认也是开启
        mySessionManager.setDeleteInvalidSessions(true);
        //是否开启会话验证器，默认开启
        mySessionManager.setSessionValidationSchedulerEnabled(true);
        //mySessionManager.setSessionValidationScheduler(sessionValidationScheduler);

        /*<!-- session 监听，可以多个。 -->
        <property name="sessionListeners">
            <list>
                <ref bean="userSessionListener"/>
            </list>
        </property>*/

        return mySessionManager;
    }

    /**
     * 会话Cookie模板
     *
     * sessionIdCookie是sessionManager创建会话Cookie的模板：
     *
     * sessionIdCookie.name：设置Cookie名字，默认为JSESSIONID；
     *
     * sessionIdCookie.domain：设置Cookie的域名，默认空，即当前访问的域名；
     *
     * sessionIdCookie.path：设置Cookie的路径，默认空，即存储在域名根下；
     *
     * sessionIdCookie.maxAge：设置Cookie的过期时间，秒为单位，默认-1表示关闭浏览器时过期Cookie；
     *
     * sessionIdCookie.httpOnly：如果设置为true，则客户端不会暴露给客户端脚本代码，使用HttpOnly cookie有助于减少某些类型的跨站点脚本攻击；此特性需要实现了Servlet 2.5 MR6及以上版本的规范的Servlet容器支持；
     *
     * sessionManager.sessionIdCookieEnabled：是否启用/禁用Session Id Cookie，默认是启用的；如果禁用后将不会设置Session Id Cookie，即默认使用了Servlet容器的JSESSIONID，且通过URL重写（URL中的“;JSESSIONID=id”部分）保存Session Id。
     *
     * @return
     */
    @Bean
    public SimpleCookie sessionIdCookie() {
        //设置Cookie名字，默认为JSESSIONID
        SimpleCookie simpleCookie = new SimpleCookie("BIZ-JSESSIONID");
        // 如果设置为true，则客户端不会暴露给客户端脚本代码，使用HttpOnly cookie有助于减少某些类型的跨站点脚本攻击；
        // 此特性需要实现了Servlet 2.5 MR6及以上版本的规范的Servlet容器支持*/
        simpleCookie.setHttpOnly(true);
        //cookie的有效时间 设置Cookie 的过期时间，秒为单位，默认-1 表示关闭浏览器时过期Cookie
        simpleCookie.setMaxAge(-1);
        /* <!-- 设置Cookie的域名，默认空，即当前访问的域名
                <property name="domain" value=".itboy.net"/> -->
        <!-- 设置Cookie 名字，默认为JSESSIONID
                <property name="name" value="xxx"/> -->
        <!-- 设置Cookie 的路径，默认空，即存储在域名根下
                <property name="path" value="xxx"/> -->*/
        return simpleCookie;
    }

    /**
     * 认证数据库存储
     * @return
     */
    @Bean
    public FilterChainManagerImpl filterChainDefinitions() {
        FilterChainManagerImpl filterChainManager = new FilterChainManagerImpl();
        StringBuffer sb = new StringBuffer();
        sb.append("/ui/sys/login = anon\n\t");
        sb.append("/ui/sys/logout = anon\n\t");
        sb.append("/ui/sys/notLogin = anon\n\t");
        sb.append("/ui/api/** = authc,kickout\n\t");
        filterChainManager.setFilterChainDefinitions(sb.toString());
        return filterChainManager;

        /*//注意过滤器配置顺序 不能颠倒
        //配置退出 过滤器,其中的具体的退出代码Shiro已经替我们实现了，登出后跳转配置的loginUrl
        filterChainDefinitionMap.put("/logout", "logout");
        // 配置不会被拦截的链接 顺序判断
        filterChainDefinitionMap.put("/static/**", "anon");
        filterChainDefinitionMap.put("/ajaxLogin", "anon");
        filterChainDefinitionMap.put("/ui/sys/login", "anon");
        filterChainDefinitionMap.put("/ui/**", "authc");*/
    }

    /**
     * 会话验证调度器
     * @return
     */
  /*  @Bean
    public ExecutorServiceSessionValidationScheduler sessionValidationScheduler(ValidatingSessionManager sessionManager) {
        ExecutorServiceSessionValidationScheduler scheduler = new ExecutorServiceSessionValidationScheduler();
        //间隔多少时间检查，不配置是60分钟
        scheduler.setInterval(600000);
        scheduler.setSessionManager(sessionManager);
        return scheduler;
    }*/

    /**
     * RedisSessionDAO shiro sessionDao层的实现 通过redis
     * <p>
     * 使用的是shiro-redis开源插件
     */
    @Bean
    public UserSessionDAO redisSessionDAO(UserSessionRepository userSessionRepository) {
        UserSessionDAO redisSessionDAO = new UserSessionDAO();
        JavaUuidSessionIdGenerator javaUuidSessionIdGenerator = new JavaUuidSessionIdGenerator();
        redisSessionDAO.setUserSessionRepository(userSessionRepository);
        redisSessionDAO.setSessionIdGenerator(javaUuidSessionIdGenerator);
        return redisSessionDAO;
    }

    /**
     * 开启shiro aop注解支持.
     * 使用代理方式;所以需要开启代码支持;
     *
     * @param securityManager
     * @return
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }

    @Bean
    @DependsOn("lifecycleBeanPostProcessor")
    public DefaultAdvisorAutoProxyCreator authorizationAttributeSourceAdvisor() {
        return new DefaultAdvisorAutoProxyCreator();
    }

    /**
     * 保证实现了Shiro内部lifecycle函数的bean执行
     * @return
     */
    @Bean(name = "lifecycleBeanPostProcessor")
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    @Bean
    public KickoutSessionControlFilter kickoutSessionFilter(UserSessionRepository userSessionRepository, RedisCache redisCache) {
        KickoutSessionControlFilter kickoutSessionControlFilter = new KickoutSessionControlFilter();
        kickoutSessionControlFilter.setUserSessionRepository(userSessionRepository);
        kickoutSessionControlFilter.setCache(redisCache);
        kickoutSessionControlFilter.setCacheTimeOut(3600);
        kickoutSessionControlFilter.setKickoutUrl("http://localhost:8081/biz/");
        return kickoutSessionControlFilter;
    }

    @Bean
    public AuthenticationFilter authenticationFilter () {
        AuthenticationFilter authenticationFilter = new AuthenticationFilter();
        authenticationFilter.setApiUnauthenticatedUrl("/ui/sys/notLogin");
        authenticationFilter.setUnauthenticatedUrl("/ui/sys/notLogin");
        return authenticationFilter;
    }

    @Bean
    public PermissionFilter permissionFilter () {
        return new PermissionFilter();
    }

    /**
     * 注册全局异常处理
     * @return
     */
  /*  @Bean(name = "exceptionHandler")
    public HandlerExceptionResolver handlerExceptionResolver() {
        return new MyExceptionHandler();
    }*/
}