package com.njcool.console.core;

import com.njcool.console.common.domain.CustomerDo;
import com.njcool.console.common.domain.PageDo;
import com.njcool.console.dao.CustomerMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author xfe
 * @Date 2018/9/24
 * @Desc
 */
@Service
public class CustomerService {

    @Autowired
    private CustomerMapper customerDao;

    /**
     * 分页查询客户信息
     * @param condition
     * @param currentPage
     * @param pageSize
     * @return
     */
    public PageDo<CustomerDo> queryCustomerPageData(Map<String,Object> condition, int currentPage, int pageSize) {
        int offset = (currentPage - 1) * pageSize;
        int total = customerDao.queryCustomerTotal(condition);
        List<CustomerDo> customerList = customerDao.queryCustomerListByPage(condition, offset, pageSize);
        return new PageDo(customerList, total);
    }

    /**
     * 查询新增的用户数
     * @param dateRange
     * @return
     */
    public List<Map<String, Object>> queryAddCustomerData(String dateRange) {
        return new ArrayList<>();
    }
}
