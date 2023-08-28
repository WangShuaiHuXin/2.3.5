package com.imapcloud.nest.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("nest_external_equip")
@Builder
public class NestExternalEquipEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 基站uuid
     */
    private String nestUuid;

    /**
     * 基站id
     */
    @Deprecated
    private Integer nestId;

    private String baseNestId;
    /**
     * 设备uuid
     */
    private String externalEquipUuid;

    /**
     * 设备号
     */
    private String externalEquipNo;

    /**
     * 设备类型
     */
    private Integer externalEquipType;


    /**
     * 创建用户id
     */
    private Long creatorId;

    private LocalDateTime createTime;

    private LocalDateTime modifyTime;

    /**
     * 是否删除, 0:否,1:是
     */
    private Boolean deleted;

}
