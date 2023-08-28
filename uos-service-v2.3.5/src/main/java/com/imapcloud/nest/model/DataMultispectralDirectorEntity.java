package com.imapcloud.nest.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author hc
 * @since 2021-12-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("data_multispectral_director")
public class DataMultispectralDirectorEntity implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * director路径
     */
    private String directorUrl;

    /**
     * 多光谱id
     */
    private Long multispectralId;

    private LocalDateTime createTime;

    /**
     * 创建用户id
     */
    private Integer createUserId;

    private LocalDateTime modifyTime;


}
