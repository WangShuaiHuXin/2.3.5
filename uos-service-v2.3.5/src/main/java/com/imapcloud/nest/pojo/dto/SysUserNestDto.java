package com.imapcloud.nest.pojo.dto;


import lombok.Data;

import java.time.LocalDateTime;

/**
 * <p>
 * 用户信息表
 * </p>
 *
 * @author wmin
 * @since 2020-07-16
 */
@Data
public class SysUserNestDto extends PageInfoDto {

    private Integer id;

    /**
     * 用户id
     */
    private Integer userId;

    /**
     * 机槽id
     */
    private Integer nestId;

    private LocalDateTime createTime;

    private LocalDateTime modifyTime;

    /**
     * 是否删除, 0:否,1:是
     */
    private Boolean deleted;

    /**
     * 区域id
     */
    private Integer regionId;

    /**
     * 用户名称
     */
    private String account;
}
