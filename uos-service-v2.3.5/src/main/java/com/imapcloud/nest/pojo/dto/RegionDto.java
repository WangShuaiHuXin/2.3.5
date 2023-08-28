package com.imapcloud.nest.pojo.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 架次表
 * </p>
 *
 * @author wmin
 * @since 2020-07-16
 */
@Data
public class RegionDto extends PageInfoDto{


    private Integer id;

    /**
     * 区域名称
     */
    @NotBlank(message = "区域名不能为空")
    private String name;

    /**
     * 简单描述
     */
    private String description;

    /**
     * 创建用户id
     */
    private Long createUserId;

    private LocalDateTime createTime;

    private LocalDateTime modifyTime;

    /**
     * 是否删除, 0:否,1:是
     */
    private Boolean deleted;

    private List<Integer> regionIds;

    private Integer nestCount;  //机巢数


}
