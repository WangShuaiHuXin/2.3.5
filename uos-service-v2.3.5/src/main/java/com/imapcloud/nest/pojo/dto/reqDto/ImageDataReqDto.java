package com.imapcloud.nest.pojo.dto.reqDto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.imapcloud.nest.pojo.dto.PageInfoDto;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 数据管理dto
 *
 * @author: zhengxd
 * @create: 2020/12/2
 **/
@Data
public class ImageDataReqDto extends PageInfoDto {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 文件名称
     */
    private String name;

    /**
     * 压缩包名称
     */
    private String zipName;

    /**
     * 压缩包存储路径
     */
    private String zipUrl;

    /**
     * 压缩包大小
     */
    private String zipSize;

    /**
     * 缩略图名称
     */
    private String thumbnailName;

    /**
     * 缩略图url
     */
    private String thumbnailUrl;

    /**
     * xml文件名称
     */
    private String xmlName;

    /**
     * xml文件url
     */
    private String xmlUrl;

    /**
     * 地区code数组
     */
    private String region;

    /**
     * 数据拍摄的时间
     */
    private LocalDateTime recordTime;

    private String startTime;

    private String endTime;

    /**
     * 单位编码
     */
    private String orgCode;

    /**
     * 单位名称
     */
    private String unitName;

    /**
     * 经度
     */
    private Double log;

    /**
     * 纬度
     */
    private Double lat;

    /**
     * 创建用户id
     */
    private Integer createUserId;

    private LocalDateTime createTime;

    private LocalDateTime modifyTime;

    /**
     * 是否删除, 0:否,1:是
     */
    private Boolean deleted;

}
