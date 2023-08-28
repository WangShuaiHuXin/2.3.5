package com.imapcloud.nest.v2.service.dto.out;

import com.imapcloud.sdk.pojo.constant.NestStateEnum;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 基站基本信息
 *
 * @author Vastfy
 * @date 2022/07/08 11:12
 * @since 1.9.7
 */
@Data
public class NestBasicOutDTO implements Serializable {

    /**
     * 基站编号
     */
    private String id;

    /**
     * 基站唯一ID
     */
    private String uuid;

    /**
     * 基站名称
     */
    private String name;

    /**
     * 基站状态【参见CPS状态】
     */
    private Integer state;

    /**
     * 基础基站类型
     */
    private String baseState;

    /**
     * 基站类型【0：G600；1：S100_V1；2：G900；3：T50；4：CAR；5：S100_V2；6：S110；7：I_CREST2；8：未知】
     */
    private Integer type;

    /**
     * 是否展示监控【0：不展示；1：展示】
     */
    private Integer showStatus;

    /**
     * @since 2.3.2
     */
    private List<String> uavIds;

}
