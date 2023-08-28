package com.imapcloud.nest.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 文件
 *
 * @author: zhengxd
 * @create: 2020/12/15
 **/
@Data
@EqualsAndHashCode(callSuper = false)
@TableName(value = "file_info")
public class FileInfoEntity {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String fileName;

    private String filePath;

    private String uploadPath;

    private Long fileSize;

    @TableLogic
    private Boolean deleted;

}
