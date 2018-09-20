package com.njcool.console.auth;

import com.njcool.console.common.domain.PermissionDo;
import com.njcool.console.core.AuthService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;
import java.util.List;

/**
 * @author xfe
 * @Date 2018/9/12
 * @Desc
 */
public class FilterChainManagerImpl {

    private static final Logger LOG = Logger.getLogger(FilterChainManagerImpl.class.getName());

    // 注意/r/n前不能有空格
    private static final String CRLF = "\r\n";

    private String filterChainDefinitions = "";//过滤链定义字符串

    public static final String PERMISSION_STRING = "perms[\"{0}\"]";

    @Autowired
    private AuthService authService;

    /**
     * 加载过滤配置信息
     * @return
     */
    public String loadFilterChainDefinitions() {
        StringBuffer sb = new StringBuffer();
        filterChainDefinitions = filterChainDefinitions.replaceAll("\\t", "").replaceAll(" ", "");
        List<PermissionDo> permissionList = authService.queryAllPermissions();
        for (PermissionDo permission : permissionList) {
            if (!StringUtils.isEmpty(permission.getUrl()) && permission.getCode() != null) {
                String perms = MessageFormat.format(PERMISSION_STRING, permission.getCode());
                sb.append(permission.getUrl().replace("=", "-")).append(" = ").append(perms).append(CRLF); // 组装成过滤链定义字符穿
            }
        }
        String filterChains = filterChainDefinitions + sb.toString();
        LOG.info(String.format("filterChainDefinitions:{%s}", filterChains));
        return filterChains;
    }

    /**
     * 重新构建权限过滤器
     * 一般在修改了用户角色、用户等信息时，需要再次调用该方法
     */
    // 此方法加同步锁
    public synchronized void reloadFilterChains() {
		/*ShiroFilterFactoryBean shiroFilterFactoryBean = (ShiroFilterFactoryBean) SpringContextUtil.getBean("shiroFilterFactoryBean");
        AbstractShiroFilter shiroFilter = null;
        try {
            shiroFilter = (AbstractShiroFilter) shiroFilterFactoryBean.getObject();
        } catch (Exception e) {
            LOG.error("getShiroFilter from shiroFilterFactoryBean error!", e);
            throw new RuntimeException("get ShiroFilter from shiroFilterFactoryBean error!");
        }

        PathMatchingFilterChainResolver filterChainResolver = (PathMatchingFilterChainResolver) shiroFilter.getFilterChainResolver();
        DefaultFilterChainManager manager = (DefaultFilterChainManager) filterChainResolver.getFilterChainManager();

        // 清空老的权限控制
        manager.getFilterChains().clear();

        shiroFilterFactoryBean.getFilterChainDefinitionMap().clear();
        shiroFilterFactoryBean.setFilterChainDefinitions(loadFilterChainDefinitions());

        // 重新构建生成
        Map<String, String> chains = shiroFilterFactoryBean.getFilterChainDefinitionMap();
        for (Map.Entry<String, String> entry : chains.entrySet()) {
            String url = entry.getKey();
            String chainDefinition = entry.getValue().trim().replace(" ", "");
            manager.createChain(url, chainDefinition);
        }*/

    }

    public void setFilterChainDefinitions(String filterChainDefinitions) {
        this.filterChainDefinitions = filterChainDefinitions;
    }

    public void setAuthService(AuthService authService) {
        this.authService = authService;
    }
}
