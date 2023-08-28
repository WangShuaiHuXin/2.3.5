package com.imapcloud.nest.pojo.dto.dataProblemDTO;

import lombok.Data;

import java.util.List;

/**
 * 删除分析中台照片用
 *
 * @author: zhengxd
 * @create: 2021/6/21
 **/
@Data
public class TagTypePhotoListDTO {
    /**
     * 标签id
     */
    Integer tagId;
    /**
     * 问题类型
     */
    Integer problemSource;
    /**
     * 照片idList
     */
    List<Long> PhotoIdList;
}
