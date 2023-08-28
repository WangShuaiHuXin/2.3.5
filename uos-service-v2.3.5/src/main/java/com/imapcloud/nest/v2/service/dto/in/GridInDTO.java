package com.imapcloud.nest.v2.service.dto.in;


import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @Classname GridInDTO
 * @Description 区域网格InDTO
 * @Date 2022/12/6 19:55
 * @Author Carnival
 */
@ToString
public class GridInDTO {

    @Data
    public static class RegionInDTO {

        String gridRegionCoor;

        String gridRegion;

        Integer sideLen;

        String orgCode;

        List<GridManageInDTO> gridManageList;
    }


    @Data
    public static class GridManageInDTO {

        Double west;

        Double south;

        Double east;

        Double north;

        Integer line;

        Integer col;
    }

    @Data
    public static class GridManageOrgCodeDTO {

        private String gridManageId;

        private String orgCode;
    }

    @Data
    public static class GridCancelRelInDTO {

        private String gridManageId;

        private String orgCode;
    }
}
