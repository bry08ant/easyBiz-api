package com.njcool.console.dao;

import com.njcool.console.common.domain.UserDo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author xfe
 * @Date 2018/9/12
 * @Desc
 */
@Component
public interface UserMapper {

    /**
     * 查询所有客户的数目
     * @param condition
     * @return
     */
    int queryAccountTotal(@Param(value = "condition") Map<String,Object> condition);

    /**
     * 分页查询客户信息
     * @param condition
     * @param offset
     * @param limit
     * @return
     */
    List<UserDo> queryAccountListByPage(@Param(value = "condition") Map<String,Object> condition, @Param(value = "offset") int offset, @Param(value = "limit") int limit);

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

    /**
     * 根据id查询用户信息
     * @param userId
     * @return
     */
    UserDo getUserByUserId(Integer userId);

    /**
     * 获取用户密码
     * @param userId
     * @return
     */
    String getUserPassword(Integer userId);

    /**
     * 更新账号的密码
     * @param userId
     * @param newPwd
     * @return
     */
    int updateAccountPassword(@Param(value = "userId") Integer userId, @Param(value = "newPwd") String newPwd);

}
