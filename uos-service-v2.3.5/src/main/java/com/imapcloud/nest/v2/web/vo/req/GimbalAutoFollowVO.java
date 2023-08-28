package com.imapcloud.nest.v2.web.vo.req;

import com.imapcloud.nest.common.annotation.NestId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("基站扩展查询条件（固件）")
@Data
public class GimbalAutoFollowVO {

    @NestId
    @ApiModelProperty(value = "基站id", example = "12324546657")
    private String nestId;
    @ApiModelProperty(value = "目标区域左上角起始点的X坐标", example = "0.5")
    private Float startX;
    @ApiModelProperty(value = "目标区域左上角起始点的Y坐标", example = "0.5")
    private Float startY;
    @ApiModelProperty(value = "目标区域右下角终点的X坐标", example = "0.5")
    private Float endX;
    @ApiModelProperty(value = "目标区域右下角终点的Y坐标", example = "0.5")
    private Float endY;
    @ApiModelProperty(value = "是否在图传画面绘制目标区域方框", example = "false")
    private Boolean showFrame;
    @ApiModelProperty(value = "是否调整机头朝向", example = "false")
    private Boolean moveHeading;
    @ApiModelProperty(value = "调整位置后的变焦类型", example = "1")
    private Integer zoomType;
    @ApiModelProperty(value = "调整位置后的变焦倍率", example = "0.5")
    private Float zoomRatio;
}
