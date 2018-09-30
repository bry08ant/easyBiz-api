package com.njcool.console.core;

import com.njcool.console.auth.TokenManager;
import com.njcool.console.common.constant.ConsoleStatus;
import com.njcool.console.common.constant.RespBody;
import com.njcool.console.common.domain.PageDo;
import com.njcool.console.common.domain.UserDo;
import com.njcool.console.dao.UserMapper;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * @author xfe
 * @Date 2018/9/12
 * @Desc
 */
@Service
public class UserService {
    
    @Autowired
    private UserMapper userDao;

    /**
     * 分页查询用户信息
     * @param condition
     * @param currentPage
     * @param pageSize
     * @return
     */
    public PageDo<UserDo> queryAccountPageData(Map<String,Object> condition, int currentPage, int pageSize) {
        int offset = (currentPage - 1) * pageSize;
        condition.put("type", TokenManager.getToken().getType());
        int total = userDao.queryAccountTotal(condition);
        List<UserDo> userList = userDao.queryAccountListByPage(condition, offset, pageSize);
        return new PageDo(userList, total);
    }

    /**
     * 根据用户号码查询用户信息
     * @param telephone
     * @return
     */
    public UserDo getUserByTelephone(String telephone) {
        return userDao.getUserByTelephone(telephone);
    }

    /**
     * 更新用户登录时间
     * @param userId
     * @return
     */
    public int updateUserLoginTime(Integer userId) {
        return userDao.updateUserLoginTime(userId);
    }

    /**
     * 获取用户信息
     * @return
     */
    public UserDo getUserInfo () {
        return userDao.getUserByUserId(TokenManager.getUserId());
    }

    /**
     * 更新账户密码
     * @param oldPwd
     * @param newPwd
     * @return
     */
    public RespBody updateAccountPassword (String oldPwd, String newPwd) {
        Integer userId = TokenManager.getUserId();
        String password = userDao.getUserPassword(userId);
        if (!password.equals(getMd5Pwd(oldPwd))) {
            return  new RespBody(ConsoleStatus.RespCode.C102, "原始密码错误");
        }

        if (userDao.updateAccountPassword(userId, getMd5Pwd(newPwd)) < 1) {
            return  new RespBody(ConsoleStatus.RespCode.C102, "密码修改失败");
        }
        // 清空登录信息
        TokenManager.logout();
        return new RespBody();
    }

    /**
     * 获取账户信息
     * @return
     */
    public Object getAccountInfo() {

        return null;
    }

    /**
     * MD%加密操作
     * @param password
     * @return
     */
    private String getMd5Pwd (String password) {
        // ByteSource salt = ByteSource.Util.bytes(xxx.getUsername());//以账号作为盐值
        SimpleHash hash = new SimpleHash("MD5", password, null,1);
        return hash.toString();
    }


}
