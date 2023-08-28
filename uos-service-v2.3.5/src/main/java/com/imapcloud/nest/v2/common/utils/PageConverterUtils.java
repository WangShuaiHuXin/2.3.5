package com.imapcloud.nest.v2.common.utils;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.geoai.common.core.bean.PageResultInfo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

/**
 * 分页转换工具类
 *
 * @author Vastfy
 * @date 2022/5/25 10:22
 * @since 2.0.0
 */
public abstract class PageConverterUtils {

    private PageConverterUtils(){}

    public static <T extends Serializable> IPage<T> convertToOld(PageResultInfo<T> newPage){
        IPage<T> oldPage = new Page<>();
        if(Objects.nonNull(newPage)){
            oldPage.setTotal(newPage.getTotal());
            oldPage.setRecords(new ArrayList<>(newPage.getRecords()));
        }
        return oldPage;
    }

}
