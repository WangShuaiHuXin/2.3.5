package com.imapcloud.nest.utils;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义List分页工具
 * @author hanwl
 */
@Data
public class PageUtil<T> {
    private Integer pageCount;
    private Integer total;
    private List<T> list;

    /**
     * 开始分页
     *
     * @param list
     * @param pageNum  页码
     * @param pageSize 每页多少条数据
     * @return
     */
    public void startPage(List<T> list, Integer pageNum,
                                 Integer pageSize) {
        if (list == null) {
            return;
        }
        if (list.size() == 0) {
            return;
        }

        Integer count = list.size(); // 记录总数
        Integer pageCount = 0; // 页数
        if (count % pageSize == 0) {
            pageCount = count / pageSize;
        } else {
            pageCount = count / pageSize + 1;
        }

        int fromIndex = 0; // 开始索引
        int toIndex = 0; // 结束索引

        if (pageNum != pageCount) {
            fromIndex = (pageNum - 1) * pageSize;
            toIndex = fromIndex + pageSize;
        } else {
            fromIndex = (pageNum - 1) * pageSize;
            toIndex = count;
        }

        List<T> pageList = list.subList(fromIndex, toIndex);
        this.total = count;
        this.pageCount = pageCount;
        this.list = pageList;
    }
}