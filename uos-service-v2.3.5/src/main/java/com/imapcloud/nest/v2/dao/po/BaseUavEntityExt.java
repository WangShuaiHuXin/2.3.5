package com.imapcloud.nest.v2.dao.po;

import com.imapcloud.nest.v2.dao.entity.BaseUavEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 无人机扩展信息
 *
 * @author Vastfy
 * @date 2023/4/20 9:06
 * @since 2.3.2
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Data
public class BaseUavEntityExt extends BaseUavEntity {

    private String nestId;

    private String nestName;
    private String nestUuid;
    private Integer nestType;

}
