package com.imapcloud.nest.v2.service.dto.out;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 精细巡检航线数据包信息
 * @author Vastfy
 * @date 2023/02/21 09:52
 * @since 2.2.3
 */
@Data
public class FpiAirlinePackageParseOutDTO implements Serializable {

    private String zipId;

    private String zipName;

    private List<TowerInfo> towerList;

    @Data
    public static class TowerInfo implements Serializable{

        private Integer towerId;
        private String towerName;
        private List<String> position;
        private String dataUrl;
        private String txtUrl;
    }

}
