package com.njcool.console.controller;

import com.njcool.console.common.constant.RespBody;
import com.njcool.console.core.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author xfe
 * @Date 2018/9/18
 * @Desc 权限、菜单相关
 */
@RestController
@RequestMapping("/ui/api/")
public class AuthController {

    @Autowired
    private MenuService menuService;

    /**
     * 获取当前用户的菜单
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value="queryUserMenus",method = RequestMethod.POST)
    @ResponseBody
    public RespBody queryUserMenus(HttpServletRequest request, HttpServletResponse response) {

        return new RespBody(menuService.queryUserMenus());
    }
}
