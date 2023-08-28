package com.imapcloud.nest.pojo.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 表计读数DTO
 *
 * @author: zhengxd
 * @create: 2021/1/5
 **/
@Data
public class MeterReadDTO {
    /**
     * 标签id
     */
    private Integer tagId;
    /**
     * 图片名称
     */
    private String photoName;

    private String startTime;
    private String endTime;

    /**
     * 图片idList
     */
    private List<Long> photoIdList;

    /**
     * 表计识别类型（1-智巡接口方案；2-本地包识别方案）
     */
    private Integer type;

}
