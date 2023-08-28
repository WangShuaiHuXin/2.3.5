package com.imapcloud.nest.v2.manager.dataobj;

import lombok.Data;

/**
 * @Classname GridManageDO
 * @Description GridManageDO
 * @Date 2023/1/3 10:10
 * @Author Carnival
 */
@Data
public class GridManageDO {

    private String gridManageId;

    private Double west;

    private Double south;

    private Double east;

    private Double north;

}
