package com.imapcloud.sdk.pojo.entity;

import cn.hutool.core.collection.CollectionUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class PublishMsgDO {
    private String code;
    private Object param;
    private Object from;
    private String traceId;
    private List<Integer> which;

    public Integer getWhichVal(){
        return CollectionUtil.isNotEmpty(this.getWhich()) ? this.getWhich().get(0) : null;
    }
}
