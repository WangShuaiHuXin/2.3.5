package com.imapcloud.nest.pojo.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ListTaskListDtoParam {
    private String nestId;
    private Integer tagId;
    private Integer currPage;
    private Integer pageSize;
    private boolean paging;
    private Integer taskType;
}
