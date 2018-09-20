package com.njcool.console.dao;

import com.njcool.console.common.domain.UserDo;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author xfe
 * @Date 2018/9/12
 * @Desc
 */
@Component
public interface UserMapper {

    /**
     * 根据用户号码查询用户信息
     * @param telephone
     * @return
     */
    UserDo getUserByTelephone(String telephone);

    /**
     * 设置用户上次登录时间
     * @param userId
     * @return
     */
    int updateUserLoginTime(Integer userId);

    UserDo getUserByUserId(Integer userId);

    List<UserDo> queryUserList();
}
