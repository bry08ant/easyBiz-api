package com.njcool.console.common.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xfe
 * @Date 2018/9/24
 * @Desc
 */
public class PageDo<T> {

    public int total;

    public List<T> data;

    public PageDo () {
        this.total = 0;
        this.data = new ArrayList<T>();
    }

    public PageDo(List<T> data, int total) {
        this.data = data;
        this.total = total;
    }

}
