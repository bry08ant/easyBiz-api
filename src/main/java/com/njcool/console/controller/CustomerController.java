package com.njcool.console.controller;

import com.njcool.console.common.constant.RespBody;
import com.njcool.console.common.domain.CustomerDo;
import com.njcool.console.common.domain.PageDo;
import com.njcool.console.core.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * @author xfe
 * @Date 2018/9/21
 * @Desc
 */
@RestController
@RequestMapping("/ui/api/")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    /**
     * 查询客户列表
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "queryCustomerPageData",method = RequestMethod.POST)
    @ResponseBody
    public RespBody<PageDo<CustomerDo>> queryCustomerPageData(HttpServletRequest request, HttpServletResponse response) {
        Integer pageSize = Integer.valueOf(request.getParameter("pageSize"));
        Integer currentPage = Integer.valueOf(request.getParameter("currentPage"));
        Map<String,Object> params = new HashMap<>();
        return new RespBody(customerService.queryCustomerPageData(params, currentPage, pageSize));
    }

    /**
     * 查询新增用户数据
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "queryAddCustomerData",method = RequestMethod.POST)
    @ResponseBody
    public RespBody queryAddCustomerData(HttpServletRequest request, HttpServletResponse response) {
        String dateRange = request.getParameter("dateRange");
        return new RespBody(customerService.queryAddCustomerData(dateRange));
    }


}
