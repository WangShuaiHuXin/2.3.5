package com.imapcloud.nest.v2.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.geoai.common.mp.entity.GenericEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName TaskFileEntity.java
 * @Description TaskFileEntity
 * @createTime 2022年07月08日 15:41:00
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("task_file")
public class TaskFileEntity extends GenericEntity {

    /**
     * 大疆航线主键
     */
    private String taskFileId;

    /**
     * 大疆航线url
     */
    private String fileUrl;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 文件md5
     */
    private String fileMd5;

    /**
     * 航线id
     */
    private String taskId;


    /**
     * 架次id
     */
    private String missionId;


}
