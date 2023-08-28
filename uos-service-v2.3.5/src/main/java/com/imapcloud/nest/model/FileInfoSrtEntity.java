package com.imapcloud.nest.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author hc
 * @since 2021-06-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("file_info_srt")
public class FileInfoSrtEntity implements Serializable {

    private static final long serialVersionUID=1L;
    /**
     * ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * srt视频地址
     */
    private String srtUrl;

    /**
     * srt文件地址
     */
    private String fileUrl;

    /**
     * srt文件名
     */
    private String name;

}
