package com.njcool.console.auth;

import com.njcool.console.common.domain.PermissionDo;
import com.njcool.console.common.domain.UserDo;
import com.njcool.console.core.AuthService;
import com.njcool.console.core.UserService;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author xfe
 * @Date 2018/9/12
 * @Desc 认证授权域
 */
public class AuthShiroRealm extends AuthorizingRealm {

    private static final Logger LOG = LoggerFactory.getLogger(AuthShiroRealm.class);

    @Autowired
    private UserService userService;

    @Autowired
    private AuthService authService;

    /**
     *  认证信息.(身份验证) : Authentication 是用来验证用户身份
     * @param token
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        UsernamePasswordToken t = (UsernamePasswordToken) token;
        UserDo user = userService.getUserByTelephone(t.getUsername());
        if (user == null) {
            throw new AccountException("帐号/密码不正确！");
        } else if (user.getStatus() == 0) {
            throw new DisabledAccountException("帐号已经禁止登录！");
        }
        //String principal = user.getTelephone();
        String password  = user.getPassword();
        //ByteSource credentialsSalt = ByteSource.Util.bytes(user.getTelephone());

        SimpleAuthenticationInfo authcInfo = new SimpleAuthenticationInfo(user, password, this.getName());
        //authcInfo.setCredentialsSalt(ByteSource.Util.bytes(t.getCredentials()));
        userService.updateUserLoginTime(user.getId());
        return authcInfo;
    }

    /**
     * 授权
     * @param principals
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        SimpleAuthorizationInfo authzInfo = null;
        UserDo userDo = (UserDo) super.getAvailablePrincipal(principals);
        /*UserDo user = null;
        try {
            user = userService.getUserByUserName(userDo.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        // 加载角色和权限
        if (null != userDo) {
            authzInfo = new SimpleAuthorizationInfo();

            //查询出用户角色列表
            /*List<RoleBean> roleList = roleService.queryRoleByUserId(user.getId());
            boolean rootRole = false;
            for (RoleBean role : roleList) {
                if (role.getRoleType().equals(RoleBean.RoleType.root)) { //root角色
                    rootRole = true;
                    break;
                }
            }*/

            //查询出用户权限列表
            List<PermissionDo> permList = authService.queryUserPermissionById(userDo.getId());
            /*if (rootRole) {
                permList = permissionService.queryAllPermissions(null);  //查询系统所有权限
            } else {
                permList = permissionService.queryByUserId(user.getId());  //查询用户拥有的权限
            }*/


            //添加角色到授权信息对象
           /* for (RoleBean role : roleList) {
                authzInfo.addRole(role.getCode());
            }*/

            //添加权限到授权信息对象
            for (PermissionDo perm : permList) {
                if (!perm.getUrl().trim().isEmpty() && !perm.getCode().trim().isEmpty()) {
                    authzInfo.addStringPermission(perm.getCode());
                }
            }
        }
        return authzInfo;
    }

}
