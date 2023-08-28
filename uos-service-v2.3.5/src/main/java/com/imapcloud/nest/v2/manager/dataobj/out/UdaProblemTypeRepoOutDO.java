package com.imapcloud.nest.v2.manager.dataobj.out;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * UDA算法仓库信息
 * @author Vastfy
 * @date 2022/11/2 11:57
 * @since 2.1.4
 */
@Data
public class UdaProblemTypeRepoOutDO implements Serializable {
    @ApiModelProperty(value = "配置ID")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long typeRelationId;

    @ApiModelProperty(value = "行业场景")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long storageId;

    @ApiModelProperty(value = "识别功能")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long functionId;

    @ApiModelProperty(value = "问题类型名称")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private String problemTypeName;

    @ApiModelProperty(value = "行业场景")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private String storageName;

    @ApiModelProperty(value = "识别功能")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private String functionName;

}
