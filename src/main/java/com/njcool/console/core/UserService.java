package com.njcool.console.core;

import com.njcool.console.auth.TokenManager;
import com.njcool.console.common.domain.PageDo;
import com.njcool.console.common.domain.UserDo;
import com.njcool.console.dao.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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



    public UserDo getUserByUserId(Integer userId) {
        return userDao.getUserByUserId(userId);
    }

}
