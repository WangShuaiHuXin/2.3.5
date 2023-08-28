package com.imapcloud.nest.pojo.dto.reqDto;

import com.imapcloud.nest.pojo.dto.PageInfoDto;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * <p>
 * 任务照片表
 * </p>
 *
 * @author wmin
 * @since 2020-07-16
 */
@Data
public class MissionPhotosReqDto extends PageInfoDto {

    private static final long serialVersionUID=1L;

    private Integer missionRecordId;

    private List<Integer> photoIds;

    private Integer type;

    private Integer defectStatus;

    private Integer identificationType;

    private Integer tagId;

    private String startTimeStr;

    private String endTimeStr;

    private Integer photoId;
}
