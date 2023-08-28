package com.imapcloud.nest.pojo.dto.dataProblemDTO;

import com.imapcloud.nest.model.DataOrthoEntity;
import lombok.Data;

/**
 * 标签正射列表DTO
 *
 * @author: zhengxd
 * @create: 2021/6/25
 **/
@Data
public class TagOrthoDTO extends DataOrthoEntity {

    /**
     * 正射id
     */
    private Long orthoId;
    /**
     * 标签id
     */
    private String tagName;

    private String shpUrl;


}
