package com.imapcloud.nest.pojo.dto.reqDto;

import com.imapcloud.nest.pojo.dto.PageInfoDto;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * <p>
 * 用户信息表
 * </p>
 *
 * @author wmin
 * @since 2020-07-16
 */
@Data
public class SysUnitReqDto{

    private static final long serialVersionUID=1L;
    private Integer id;

    /**
     * 部门名字
     */
    private String name;

    /**
     * 创建用户id
     */
    private Integer createUserId;

    private LocalDateTime createTime;

    private LocalDateTime modifyTime;

    /**
     * 是否删除, 0:否,1:是
     */
    private Boolean deleted;

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
            return getDefaultPageNo();
        } else {
            return pageNo <= 0  ? getDefaultPageNo() : pageNo;
        }

    }

    public Integer getCurrentPageSize() {
        if (pageSize == null) {
            return getDefaultPageSize();
        } else {
            return pageSize <= 0 || pageSize > getMaxPageSize() ? getMaxPageSize() : pageSize;
        }
    }

}
