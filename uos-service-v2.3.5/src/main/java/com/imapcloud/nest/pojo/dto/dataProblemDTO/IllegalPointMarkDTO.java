package com.imapcloud.nest.pojo.dto.dataProblemDTO;

import com.imapcloud.nest.model.DefectInfoEntity;
import lombok.Data;

import java.util.List;

/**
 * 违建识别标记DTO
 *
 * @author: zhengxd
 * @create: 2021/6/29
 **/
@Data
public class IllegalPointMarkDTO {

    private Integer illegalPointId;

    private Long photoId;

    private Integer problemSource;

    private List<DefectInfoEntity> defectInfoEntityList;
}
