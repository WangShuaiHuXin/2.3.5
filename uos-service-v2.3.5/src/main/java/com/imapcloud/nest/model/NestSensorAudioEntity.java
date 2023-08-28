package com.imapcloud.nest.model;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 机巢喊话器的音频表
 * </p>
 *
 * @author zheng
 * @since 2021-04-06
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("nest_sensor_audio")
public class NestSensorAudioEntity implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 机巢id
     */
    @Deprecated
    private String nestId;

    private String baseNestId;
    /**
     * 机巢类型(1-mini1代；2-M300；5-mini2代)
     */
    private Integer nestType;

    /**
     * 音频名称
     */
    private String audioName;

    /**
     * 音频路径
     */
    private String audioUrl;

    /**
     * 音频文件PCM格式路径
     */
    private String pcmUrl;

    /**
     * 音频大小
     */
    private String audioSize;

    /**
     * 音频时长
     */
    private String audioTime;

    /**
     * 音频在机巢的index
     */
    private Integer audioIndex;

    /**
     * 创建用户id
     */
    @Deprecated
    private Integer createUserId = 0;
    private Long creatorId;

    private LocalDateTime createTime;

    private LocalDateTime modifyTime;

    /**
     * 是否删除, 0:否,1:是
     */
    @TableLogic
    private Boolean deleted;


}
