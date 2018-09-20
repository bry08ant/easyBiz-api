package com.njcool.console.common.domain;

import java.util.List;

/**
 * @author xfe
 * @Date 2018/9/18
 * @Desc 菜单实体类
 */
public class MenuDo {
    private Integer id;

    private String title;

    private String code;

    private String icon;

    private String name;

    private String href;

    private Integer sort;

    private Integer parentId;

    private List<MenuDo> children;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public List<MenuDo> getChildren() {
        return children;
    }

    public void setChildren(List<MenuDo> children) {
        this.children = children;
    }
}
