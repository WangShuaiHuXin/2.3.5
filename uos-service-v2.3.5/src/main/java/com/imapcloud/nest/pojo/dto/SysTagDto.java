package com.imapcloud.nest.pojo.dto;

import com.imapcloud.nest.model.SysTagEntity;
import lombok.Data;

import java.util.List;

/**
 * <p>
 * 权限信息表
 * </p>
 *
 * @author wmin
 * @since 2020-07-16
 */
@Data
public class SysTagDto extends SysTagEntity {

    public List<SysTagEntity> sysTagEntityList;


}
