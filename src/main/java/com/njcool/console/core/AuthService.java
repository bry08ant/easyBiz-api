package com.njcool.console.core;

import com.njcool.console.common.domain.PermissionDo;
import com.njcool.console.dao.AuthMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author xfe
 * @Date 2018/9/12
 * @Desc
 */
@Service
public class AuthService {

    @Autowired
    private AuthMapper authDao;

    public List<PermissionDo> queryAllPermissions() {
        return authDao.queryAllPermissions();
    }

    public List<PermissionDo> queryUserPermissionById(Integer userId) {
        return authDao.queryUserPermissionById(userId);
    }
}
