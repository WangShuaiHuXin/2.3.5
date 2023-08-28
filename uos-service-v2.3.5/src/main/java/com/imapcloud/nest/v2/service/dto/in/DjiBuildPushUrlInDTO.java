package com.imapcloud.nest.v2.service.dto.in;

import lombok.Data;


/**
 * @author wmin
 */
@Data
public class DjiBuildPushUrlInDTO {

    /**
     * 基站id
     */
    private String nestId;

    /**
     * 1 - 基站sn
     * 2 - 无人机sn
     */
    private Integer snType;

    /**
     * 无人机sn
     */
    private String uavSn;

    private String serverId;

    public enum SnTypeEnum {
        DOCK(1),
        UAV(2)
        ;
        private Integer val;

        SnTypeEnum(Integer val) {
            this.val = val;
        }

        public Integer getVal() {
            return val;
        }
    }
}
