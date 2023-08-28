package com.imapcloud.nest.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * mqtt日志解析列表
 * </p>
 *
 * @author hc
 * @since 2022-02-18
 */
@Accessors(chain = true)
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("mqtt_log_records")
public class MqttLogRecordsEntity implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 日志来源
     */
    private Integer source;

    /**
     * 文件路径
     */
    private String path;

    /**
     * 文件MD5值
     */
    private String md5;

    /**
     * 是否解析完成
     */
    private Integer finished;

    private LocalDateTime createTime;

    private LocalDateTime modifyTime;


}
