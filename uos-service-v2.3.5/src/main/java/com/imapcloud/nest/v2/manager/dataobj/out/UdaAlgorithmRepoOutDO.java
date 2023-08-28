package com.imapcloud.nest.v2.manager.dataobj.out;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * UDA算法仓库信息
 * @author Vastfy
 * @date 2022/11/2 11:57
 * @since 2.1.4
 */
@Data
public class UdaAlgorithmRepoOutDO implements Serializable {

    /**
     * 仓库ID
     */
    public String storageId;

    /**
     * 仓库名称
     */
    public String name;

    /**
     * 行业下识别功能总数
     */
    public Integer count;

    /**
     * 识别功能子集
     */
    public List<DiscernFunctionInfo> discernFunctionInfos;

    @Data
    public static class DiscernFunctionInfo {
        /**
         * 功能ID
         */
        private String functionId;

        /**
         * 功能名称
         */
        private String name;

        /**
         * 版本号
         */
        private String version;

        /**
         * 排序
         */
        private Integer cOrder;
    }

}
