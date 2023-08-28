package com.imapcloud.nest.v2.service.dto.in;

import com.geoai.common.core.bean.PageInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

/**
 * @Classname DataInterestPointPageInDTO
 * @Description 全景兴趣点分页类
 * @Date 2022/9/26 11:03
 * @Author Carnival
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Data
public class DataInterestPointPageInDTO extends PageInfo implements Serializable {

    private String pointName;

    private Integer pointType;

    private String orgCode;

    private String tagId;
}
