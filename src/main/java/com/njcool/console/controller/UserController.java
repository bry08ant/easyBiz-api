package com.njcool.console.controller;

import com.njcool.console.common.constant.RespBody;
import com.njcool.console.common.domain.PageDo;
import com.njcool.console.common.domain.UserDo;
import com.njcool.console.core.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author xfe
 * @Date 2018/9/12
 * @Desc 用户管理相关
 */
@CrossOrigin
@RestController
@RequestMapping("/ui/api/")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 分页查询账号信息
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "queryAccountPageData",method = RequestMethod.POST)
    @ResponseBody
    public RespBody<PageDo<UserDo>> queryAccountPageData(HttpServletRequest request, HttpServletResponse response) {
        Integer pageSize = Integer.valueOf(request.getParameter("pageSize"));
        Integer currentPage = Integer.valueOf(request.getParameter("currentPage"));
        Map<String,Object> params = new HashMap<>();
        return new RespBody(userService.queryAccountPageData(params, currentPage, pageSize));
    }

    /**
     * 获取登录用户信息
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "getUserInfo",method = RequestMethod.POST)
    @ResponseBody
    public RespBody getUserInfo(HttpServletRequest request, HttpServletResponse response) {
        return new RespBody(userService.getUserInfo());
    }
}
