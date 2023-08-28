package com.imapcloud.nest.v2.manager.dataobj.in;

import com.geoai.common.core.bean.PageInfo;
import lombok.Data;

import java.io.Serializable;

/**
 * @Classname UosOrgQueryReqVO
 * @Description 单位管理分页查询信息
 * @Date 2022/8/12 15:20
 * @Author Carnival
 */
@Data
public class OrgPageQueryDO extends PageInfo implements Serializable {

    private String orgName;

}
