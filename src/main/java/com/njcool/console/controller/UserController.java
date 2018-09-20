package com.njcool.console.controller;

import com.njcool.console.common.constant.RespBody;
import com.njcool.console.common.domain.UserDo;
import com.njcool.console.core.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author xfe
 * @Date 2018/9/12
 * @Desc
 */
@CrossOrigin
@RestController
@RequestMapping("/ui/api/")
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = "list",method = RequestMethod.POST)
    @ResponseBody
    public List<UserDo> queryUserList(HttpServletRequest request, HttpServletResponse response) {
        return userService.queryUserList();
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
