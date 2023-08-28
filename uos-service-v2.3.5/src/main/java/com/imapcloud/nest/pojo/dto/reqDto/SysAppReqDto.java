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
public class SysAppReqDto extends PageInfoDto {

    private static final long serialVersionUID=1L;
    private Integer id;

    /**
     * 终端名字
     */
    private String name;

    /**
     * 图传地址
     */
    private String picSendUrl;

    /**
     * 单位id
     */
    private String unitId;

    /**
     * 创建用户id
     */
    private Integer createUserId;

    private Integer showStatus;

    private String appId;

    private LocalDateTime createTime;

    private LocalDateTime modifyTime;

    /**
     * 是否删除, 0:否,1:是
     */
    private Boolean deleted;
}
