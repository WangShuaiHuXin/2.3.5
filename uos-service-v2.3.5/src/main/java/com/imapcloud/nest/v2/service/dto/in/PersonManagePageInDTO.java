package com.imapcloud.nest.v2.service.dto.in;

import com.geoai.common.core.bean.PageInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @Classname PersonManagePageInDTO
 * @Description 人员管理分页信息类
 * @Date 2023/3/28 13:59
 * @Author Carnival
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PersonManagePageInDTO extends PageInfo implements Serializable {

    private String orgCode;

    private String name;
}
