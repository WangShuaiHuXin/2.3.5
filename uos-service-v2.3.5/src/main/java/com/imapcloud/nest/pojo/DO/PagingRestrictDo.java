package com.imapcloud.nest.pojo.DO;

import com.imapcloud.nest.common.core.Pagination;
import lombok.Builder;
import lombok.Getter;

/**
 * @author Vastfy
 * @date 2022/04/20 14:24
 * @since 1.8.9
 */
@Getter
@Builder
public class PagingRestrictDo {

    private long limit;

    private long offset;

    public static PagingRestrictDo transfer(Pagination pagination){
        return PagingRestrictDo.builder()
            .limit(pagination.getPageSize())
            .offset((long) (pagination.getCurPage() - 1) * pagination.getPageSize())
            .build();
    }

}
