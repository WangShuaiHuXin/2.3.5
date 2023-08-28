package com.imapcloud.nest.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 文件视频实体类
 *
 * @author zhengxd
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("file_info_srt_video")
public class FileInfoSrtVideoEntity {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("correlation_uuid")
    private String correlationUuid;

    @TableField("srt_id")
    private Integer srtId;

    @TableField("video_id")
    private Integer videoId;

}
