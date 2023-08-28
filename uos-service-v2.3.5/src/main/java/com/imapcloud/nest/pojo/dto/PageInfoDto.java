package com.imapcloud.nest.pojo.dto;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author kings
 * @date 2020/9/2 0002 16:10
 */
@Data
public class PageInfoDto {

    private Integer pageSize;

    private Integer pageNo;

    private static final int DEFAULT_PAGE_NO = 1;
    private static final int DEFAULT_PAGE_SIZE = 15;
    private static final int MAX_PAGE_SIZE = 999;
    //private int currentPage;  这个对于的就是pageNo


    private static int getDefaultPageNo() {
        return DEFAULT_PAGE_NO;
    }

    private static int getDefaultPageSize() {
        return DEFAULT_PAGE_SIZE;
    }

    private static int getMaxPageSize() {
        return MAX_PAGE_SIZE;
    }

    public Integer getCurrentPageNo() {
        if (pageNo == null) {
            return PageInfoDto.getDefaultPageNo();
        } else {
            return pageNo <= 0  ? PageInfoDto.getDefaultPageNo() : pageNo;
        }

    }

    public Integer getCurrentPageSize() {
        if (pageSize == null) {
            return PageInfoDto.getDefaultPageSize();
        } else {
            return pageSize <= 0 || pageSize > PageInfoDto.getMaxPageSize() ? PageInfoDto.getMaxPageSize() : pageSize;
        }
    }

    public Integer getCurrentMAxPageSize() {
        return PageInfoDto.getMaxPageSize();
    }
}
