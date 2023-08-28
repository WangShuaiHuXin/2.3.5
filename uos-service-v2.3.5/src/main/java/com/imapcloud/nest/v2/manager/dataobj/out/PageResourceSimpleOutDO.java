package com.imapcloud.nest.v2.manager.dataobj.out;

import lombok.Data;

import java.io.Serializable;

/**
 * 页面资源简要信息
 * @author Vastfy
 * @date 2022/08/16 16:12
 * @since 2.0.0
 */
@Data
public class PageResourceSimpleOutDO implements Serializable {

    private String appType;

    private String permId;

    private Integer permType;

    private String permCode;

    private String permName;

}
