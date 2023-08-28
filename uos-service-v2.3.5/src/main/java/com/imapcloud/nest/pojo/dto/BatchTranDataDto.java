package com.imapcloud.nest.pojo.dto;


import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class BatchTranDataDto implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer lastIndex;
    private List<Integer> recordIdList;

    public Integer readyRecordId() {
        if (this.lastIndex == this.getRecordIdList().size() - 1) {
            return -1;
        }
        this.lastIndex++;
        return recordIdList.get(lastIndex);
    }

    public Integer currRecordId() {
        if (this.lastIndex == -1) {
            return -1;
        }
        return recordIdList.get(this.lastIndex);
    }

}
