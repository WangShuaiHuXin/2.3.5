package com.imapcloud.nest.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

/**
 * 文件块
 *
 * @author: zhengxd
 * @create: 2020/12/15
 **/
@Data
@EqualsAndHashCode(callSuper = false)
@TableName(value = "chunk_info")
public class ChunkInfoEntity {
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 块编号，从1开始
     */
    @TableField(value = "chunk_number")
    private Long chunkNumber;

    /**
     * 相对路径
     */
    @TableField(value = "chunk_url")
    private String chunkUrl;

    /**
     * 块大小
     */
    @TableField(value = "chunk_size")
    private Long chunkSize;

    /**
     * 文件类型(1-全景；2-正射；3-点云)
     */
    @TableField(value = "type")
    private Integer type;

    /**
     * 文件标识
     */
    @TableField(value = "identifier")
    private String identifier;

    /**
     * 文件名
     */
    @TableField(value = "file_name")
    private String fileName;


    /**
     * 总块数
     */
    @TableField(value = "total_chunks")
    private Integer totalChunks;

    /**
     * 总大小
     */
    @TableField(value = "total_size")
    private Long totalSize;

    /**
     * 单位id
     * @deprecated 2.0.0，由orgCode字段替代
     */
    @Deprecated
    private Integer unitId;

    /**
     * 单位编码
     */
    private String orgCode;

    @TableField(exist = false)
    private String unitName;

    /**
     * 创建用户id
     */
    private Integer createUserId;

    private LocalDateTime createTime;

    private LocalDateTime modifyTime;

    /**
     * 是否删除, 0:否,1:是
     */
    @TableLogic
    private Boolean deleted;

    /**
     * 块内容
     */
    @TableField(exist = false)
    private MultipartFile chunkFile;



    public static final String IDENTIFIER = "identifier";

    public static final String FILE_NAME = "file_name";
    public static final String TYPE = "type";
    public static final String CREATE_TIME = "create_time";

}
