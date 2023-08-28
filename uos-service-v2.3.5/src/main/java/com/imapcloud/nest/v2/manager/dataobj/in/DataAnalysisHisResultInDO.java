package com.imapcloud.nest.v2.manager.dataobj.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DataAnalysisHisResultInDO implements Serializable {
    private String missionId;

    private String orgId;

    private String topicProblemId;

    private String beginTIme;

    private String endTime;

}
