package com.imapcloud.nest.v2.manager.dataobj.in;

import com.geoai.common.core.bean.PageInfo;
import com.imapcloud.nest.enums.RoleIdenValueEnum;
import com.imapcloud.nest.v2.manager.dataobj.BaseInDO;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 电力部件库信息表
 *
 * @author boluo
 * @date 2022-11-24
 */
@Data
public class PowerComponentInfoInDO extends BaseInDO {

    /**
     * 部件库id
     */
    private String componentId;

    /**
     * 部件名称
     */
    private String componentName;

    /**
     * 部件图片
     */
    private String componentPicture;

    /**
     * 部件图片名称
     */
    private String componentPictureName;

    /**
     * 单位code
     */
    private String orgCode;

    /**
     * 设备类型
     */
    private String equipmentType;

    /**
     * 备注
     */
    private String description;

    /**
     * 识别类型
     */
    private List<RoleIdenValueEnum> roleIdenValueEnumList;

    @Data
    public static class ListInfoInDO extends PageInfo {

        private String orgCode;

        private List<RoleIdenValueEnum> roleIdenValueEnumList;

        private String equipmentType;

        private String componentName;

        private LocalDateTime start;

        private LocalDateTime end;
    }
}
