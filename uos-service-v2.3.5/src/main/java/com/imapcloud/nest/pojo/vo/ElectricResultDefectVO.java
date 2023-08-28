package com.imapcloud.nest.pojo.vo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.imapcloud.nest.model.MissionPhotoEntity;
import lombok.Data;

import java.util.Date;

/**
 * 缺陷管理表VO类
 *
 * @author: zhengxd
 * @create: 2020/5/28
 **/
@Data
public class ElectricResultDefectVO extends MissionPhotoEntity {
    /**
     * 成果标记
     */
    //@ApiModelProperty(value = "成果标记")
    private ResultsTagVO resultsTagVO;
    /**
     * 缩略图路径
     */
    //@ApiModelProperty(value = "缩略图路径")
    private String picThumbnailPath;

    //@ApiModelProperty(value = "缺陷等级")
    private String confidenceRate;

    //@ApiModelProperty(value = "缺陷等级")
    private String defectLevel;

    //@ApiModelProperty(value = "缺陷内容")
    private String defectContent;

    //@ApiModelProperty(value = "缺陷类别")
    private String defectName;

    //@ApiModelProperty(value = "缺陷大类")
    private String typeName;
}
