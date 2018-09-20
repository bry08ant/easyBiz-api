package com.njcool.console.core;

import com.njcool.console.auth.TokenManager;
import com.njcool.console.common.domain.MenuDo;
import com.njcool.console.dao.MenuMapper;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xfe
 * @Date 2018/9/18
 * @Desc
 */
@Service
public class MenuService {

    private static final Integer ROOT_MENU_ID = 0;

    @Autowired
    private MenuMapper menuDao;

    public List<MenuDo> queryUserMenus() {
        List<MenuDo> menuDoList = menuDao.queryUserMenus(TokenManager.getUserId());
        MenuDo rootMenu = new MenuDo();
        rootMenu.setId(ROOT_MENU_ID);
        analysisMenuList(menuDoList, rootMenu);
        return rootMenu.getChildren();
    }


    private void analysisMenuList(List<MenuDo> menuDoList, MenuDo parentMenu) {
        if (CollectionUtils.isEmpty(parentMenu.getChildren())) {
            parentMenu.setChildren(new ArrayList<MenuDo>());
        }

        for (MenuDo menuDo : menuDoList) {
            if (menuDo.getParentId().equals(parentMenu.getId())) {
                parentMenu.getChildren().add(menuDo);
                analysisMenuList(menuDoList, menuDo);
            }
        }
    }
}
