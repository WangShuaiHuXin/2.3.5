package com.imapcloud.nest.pojo.dto.reqDto;

import com.imapcloud.nest.pojo.dto.PageInfoDto;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * <p>
 * 用户信息表
 * </p>
 *
 * @author wmin
 * @since 2020-07-16
 */
@Data
public class MissionRecordsReqDto extends PageInfoDto {

    private static final long serialVersionUID=1L;

    /**
     * 飞行架次id
     */

    @NotEmpty(message = "{geoai_uos_error_parameter}")
    private List<Integer> missionRecordIds;

    private String nestId;

    //0为最近7天。1为一个月
    private Integer type;

    private String startTime;
    private String endTime;

    //数据类型
    private Integer dataType;
}
