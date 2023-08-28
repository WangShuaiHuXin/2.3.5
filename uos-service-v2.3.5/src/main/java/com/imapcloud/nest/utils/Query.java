package com.imapcloud.nest.utils;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.base.CaseFormat;
import com.imapcloud.nest.common.xss.SQLFilter;
import com.imapcloud.sdk.utils.StringUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * 查询参数
 *  @author: zhengxd
 *  @create: 2020/8/12
 */
public class Query<T> {
    /**
     * 当前页码
     */
    public static final String PAGE = "page";
    /**
     * 每页显示记录数
     */
    public static final String LIMIT = "limit";
    /**
     * 排序字段
     */
    public static final String ORDER_FIELD = "sidx";
    /**
     * 排序方式
     */
    public static final String ORDER = "order";
    /**
     *  升序
     */
    public static final String ASC = "asc";
    public IPage<T> getPage(Map<String, Object> params) {
        return this.getPage(params, null, false);
    }

    public IPage<T> getPage(Map<String, Object> params, String defaultOrderField, boolean isAsc) {
        //分页参数
        long curPage = 1;
        long limit = 10;

        if(params.get(PAGE) != null){
            curPage = Long.parseLong(params.get(PAGE).toString());
        }
        if(params.get(LIMIT) != null){
            limit = Long.parseLong(params.get(LIMIT).toString());
        }

        //分页对象
        Page<T> page = new Page<>(curPage, limit);

        //分页参数
        params.put(PAGE, page);

        //排序字段
        //防止SQL注入（因为sidx、order是通过拼接SQL实现排序的，会有SQL注入风险）
        String orderField = SQLFilter.sqlInject((String)params.get(ORDER_FIELD));
        String order = (String)params.get(ORDER);

        //前端字段排序
        if(StringUtils.isNotEmpty(orderField) && StringUtils.isNotEmpty(order)){
            if(ASC.equalsIgnoreCase(order)) {
                return page.addOrder(OrderItem.asc(orderField));
            }else {
                return page.addOrder(OrderItem.desc(orderField));
            }
        }


        //默认排序
        if(isAsc) {
            page.addOrder(OrderItem.asc(defaultOrderField));
        }else {
            page.addOrder(OrderItem.desc(defaultOrderField));
        }

        return page;
    }

    public IPage<T> getPage(Map<String, Object> params,String asc , String desc) {
        Page<T> page = (Page<T>) this.getPage(params, null, false);
        if(StringUtil.isEmpty(asc) && StringUtil.isEmpty(desc)){
            return page;
        }
        asc = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, asc);
        desc = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, desc);
        String handleAsc = SQLFilter.sqlInject(asc)
                ,handleDesc = SQLFilter.sqlInject(desc);
        if(StringUtil.isNotEmpty(handleAsc)){
            page.addOrder(OrderItem.asc(handleAsc));
        }
        if(StringUtil.isNotEmpty(handleDesc)){
            page.addOrder(OrderItem.desc(handleDesc));
        }
        return page;
    }
}
