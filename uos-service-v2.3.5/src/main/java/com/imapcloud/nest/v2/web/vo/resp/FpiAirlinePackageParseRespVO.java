package com.imapcloud.nest.v2.web.vo.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.util.List;

/**
 * 精细巡检航线数据包信息
 * @author Vastfy
 * @date 2023/02/21 09:52
 * @since 2.2.3
 */
@ApiModel("精细巡检航线数据包信息")
@Data
public class FpiAirlinePackageParseRespVO implements Serializable {

    @ApiModelProperty(value = "数据包ID", position = 1, required = true)
    private Integer zipId;

    @ApiModelProperty(value = "数据包名称", position = 2, required = true)
    private String zipName;

    @ApiModelProperty(value = "杆塔信息列表", position = 3, required = true)
    private List<TowerInfo> towerList;

    @ApiModel("杆塔信息")
    @Data
    public static class TowerInfo implements Serializable{

        @ApiModelProperty(value = "杆塔ID", position = 1, required = true)
        private Integer towerId;
        @ApiModelProperty(value = "杆塔名称", position = 2, required = true)
        private String towerName;
        @ApiModelProperty(value = "杆塔位置", position = 3, required = true)
        private List<String> position;
    }

}
