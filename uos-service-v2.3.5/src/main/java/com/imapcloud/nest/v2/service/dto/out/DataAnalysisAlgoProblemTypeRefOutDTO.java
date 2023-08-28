package com.imapcloud.nest.v2.service.dto.out;

import com.imapcloud.nest.v2.web.vo.resp.TopicProblemRespVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 专题dto
 *
 * @author boluo
 * @date 2022-07-14
 */

@ToString
@Data
public class DataAnalysisAlgoProblemTypeRefOutDTO {

    public DataAnalysisAlgoProblemTypeRefOutDTO() {

    }
    
    // uos问题类型ID
    public Long typeId;

    // uda场景ID
    public String storageId;

    // uda场景名称
    public String storageName;

    // uda识别功能ID
    public String functionId;

    // uda识别功能名称
    public String functionName;

    // uda问题类型配置ID
    public String typeRelationId;

    // uda问题类型配置名称
    public String typeRelationName;
}
