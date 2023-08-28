package com.imapcloud.nest.v2.manager.dataobj.in;

import lombok.Data;

import java.util.List;

/**
 * 页面资源
 *
 * @author boluo
 * @date 2022-05-23
 */
@Data
public class ResourcePageListInDO {

    private List<String> roleIdList;
}
