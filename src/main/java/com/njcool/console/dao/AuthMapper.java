package com.njcool.console.dao;

import com.njcool.console.common.domain.PermissionDo;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author xfe
 * @Date 2018/9/12
 * @Desc
 */
@Component
public interface AuthMapper {

    List<PermissionDo> queryAllPermissions();

    List<PermissionDo> queryUserPermissionById(Integer userId);
}
