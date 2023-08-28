package com.imapcloud.nest.pojo.dto;

import com.geoai.common.web.util.MessageUtils;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class NestBaseStateDto {
    /**
     * 舱门状态
     */
    private Integer cabin = -1;
    /**
     * 平台状态
     */
    private Integer lift = -1;
    /**
     * 归中装填
     */
    private Integer square = -1;

    public String getTips() {
        String tips = MessageUtils.getMessage("geoai_uos_NestBaseStateDto_01");
        StringBuilder sb = new StringBuilder(MessageUtils.getMessage("geoai_uos_NestBaseStateDto_06"));
        if (this.cabin != 1) {
            sb.append(MessageUtils.getMessage("geoai_uos_NestBaseStateDto_02"));
        }
        if (this.lift != 1) {
            sb.append(MessageUtils.getMessage("geoai_uos_NestBaseStateDto_03"));
        }
        if (this.square != 1) {
            sb.append(MessageUtils.getMessage("geoai_uos_NestBaseStateDto_04"));
        }
        sb.append(MessageUtils.getMessage("geoai_uos_NestBaseStateDto_05"));
        return tips + sb.toString();
    }
}
