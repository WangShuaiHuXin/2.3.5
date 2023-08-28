package com.imapcloud.nest.pojo.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * <p>
 * 权限信息表
 * </p>
 *
 * @author wmin
 * @since 2020-07-16
 */
@Data
public class SysFunctionDto extends PageInfoDto {

    private Integer id;

    /**
     * 终端名字
     */
    private String name;

    /**
     * 单位id
     */
    private Integer unitId;

    /**
     * 菜单id
     */
    private Integer menuId;

    private Integer functionId;

    /**
     * 创建用户id
     */
    private Integer createUserId;

    private LocalDateTime createTime;

    private LocalDateTime modifyTime;

    /**
     * 是否删除, 0:否,1:是
     */
    private Boolean deleted;

}
