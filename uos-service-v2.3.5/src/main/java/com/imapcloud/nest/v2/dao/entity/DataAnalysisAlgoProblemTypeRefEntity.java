package com.imapcloud.nest.v2.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.geoai.common.mp.entity.GenericEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * dataAnalysisAlgoProblemTypeRef
 * 由 mr 使用 AutoGenerator 自动生成
 * </p>
 *
 * @author mr
 * @since 2022-11-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
// 算法问题类型关联表
@TableName("data_analysis_algo_problem_type_ref")
public class DataAnalysisAlgoProblemTypeRefEntity extends GenericEntity {

    // uos问题类型ID
    private Long typeId;

    // uda场景ID
    private String storageId;

    // uda场景名称
    private String storageName;

    // uda识别功能ID
    private String functionId;

    // uda识别功能名称
    private String functionName;

    // uda问题类型配置ID
    private String typeRelationId;

    // uda问题类型配置名称
    private String typeRelationName;
}
