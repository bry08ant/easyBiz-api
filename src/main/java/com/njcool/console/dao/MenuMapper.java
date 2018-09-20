package com.njcool.console.dao;

import com.njcool.console.common.domain.MenuDo;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author xfe
 * @Date 2018/9/18
 * @Desc
 */
@Component
public interface MenuMapper {

    List<MenuDo> queryUserMenus(Integer userId);

}
