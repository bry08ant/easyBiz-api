package com.njcool.console.dao;

import com.njcool.console.common.domain.CustomerDo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author xfe
 * @Date 2018/9/24
 * @Desc
 */
@Component
public interface CustomerMapper {

    /**
     * 查询所有客户的数目
     * @param condition
     * @return
     */
    int queryCustomerTotal(@Param(value = "condition") Map<String,Object> condition);

    /**
     * 分页查询客户信息
     * @param condition
     * @param offset
     * @param limit
     * @return
     */
    List<CustomerDo> queryCustomerListByPage(@Param(value = "condition") Map<String,Object> condition, @Param(value = "offset") int offset, @Param(value = "limit") int limit);
}
