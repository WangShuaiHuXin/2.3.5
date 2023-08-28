package com.imapcloud.nest.v2.manager.dataobj.in;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class DataSynInDO {

    private Integer taskType;

    private Integer taskId;

    private Integer missionRecordId;

    private Integer airLineId;

    private String nestUuid;

}
