package com.imapcloud.nest.pojo.dto.dataProblemDTO;

import com.imapcloud.nest.model.DataPointCloudEntity;
import lombok.Data;

/**
 * 标签点云列表DTO
 *
 * @author: zhengxd
 * @create: 2021/6/25
 **/
@Data
public class TagPointCloudDTO extends DataPointCloudEntity {
    /**
     * 点云id
     */
    private Long pointCloudId;
    /**
     * 标签id
     */
    private String tagName;
}
