package com.imapcloud.nest.v2.web.vo.resp;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 角色页面资源
 *
 * @author boluo
 * @date 2022-05-23
 */
@Data
public class RolePageInfoRespVO implements Serializable {

    private String id;

    private String name;

    private int seq;

    private boolean active;

    private List<RolePageInfoRespVO> childrenList;
}
