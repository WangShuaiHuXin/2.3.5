package com.imapcloud.nest.pojo.dto.reqDto;

import com.imapcloud.nest.pojo.dto.PageInfoDto;
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
public class SysTagReqDto extends PageInfoDto {

    private static final long serialVersionUID = 1L;
    private Long id;

    /**
     * 标签名称，排序
     */
    private String name;

    /**
     * 单位id，单位必须经过检验。查询单位的接口也要校验
     */
    private String unitId;

    /**
     * 排序号
     */
    private Integer seq;

    /**
     * 创建用户id
     */
    private Integer createUserId;

    private LocalDateTime createTime;

    private LocalDateTime modifyTime;

    private String nestId;

    private Integer dataType;

}
