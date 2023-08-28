package com.imapcloud.nest.v2.service.dto.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName DataAnalysisBaseQueryCriteriaPO.java
 * @Description DataAnalysisBaseQueryCriteriaPO
 * @createTime 2022年07月13日 15:04:00
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataAnalysisDetailSaveInDTO {

    private Long centerBaseId;

    private Long centerDetailId;

    private Long photoId;

    private String photoName;

    private String thumImagePath;

    private String imagePath;

    private BigDecimal longitude;

    private BigDecimal latitude;

    private Integer originalWidth;

    private Integer originalHeight;

    private Integer srcDataType;

    private Integer photoState;

    private Integer pushState;

    /**
     * 图片类型 0 可见  1 红外
     */
    private Integer picType;

    private Long tagId;

    private Long taskId;

    private Long missionId;

    private Long missionRecordId;

    private String nestId;

    private String orgId;

    private LocalDateTime photoCreateTime;

    private List<DataAnalysisMarkSaveInDTO> dataAnalysisMarkSaveInDTOList;

}
