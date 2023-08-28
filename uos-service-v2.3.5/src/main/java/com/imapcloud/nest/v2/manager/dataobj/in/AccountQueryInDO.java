package com.imapcloud.nest.v2.manager.dataobj.in;

import com.geoai.common.core.bean.PageInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 账号查询条件
 * @author Vastfy
 * @date 2022/4/25 15:12
 * @since 1.0.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AccountQueryInDO extends PageInfo {

    private String username;

    private Integer accountStatus;

    private String mobile;

    private String orgCode;

}
