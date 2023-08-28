package com.imapcloud.nest.pojo.dto.dataProblemDTO;

import lombok.Data;

import java.util.List;

/**
 * 删除数据用的DTO
 *
 * @author: zhengxd
 * @create: 2021/6/22
 **/
@Data
public class DeleteDataDTO {

    private Integer problemSource;
    private List<Long> photoIdList;

    // 用来删除点云正射在违建违章模块用的
    private List<Integer> dataIdList;
}