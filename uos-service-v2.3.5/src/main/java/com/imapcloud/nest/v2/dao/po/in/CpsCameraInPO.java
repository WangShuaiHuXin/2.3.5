package com.imapcloud.nest.v2.dao.po.in;

import lombok.Data;

import java.util.List;

@Data
public class CpsCameraInPO {

    @Data
    public static class CpsCameraSetTypeInPO {
        /**
         * 要设置的源类型
         */
        private List<Integer> formats;
        /**
         * 基站id
         */
        private String nestId;
    }

    @Data
    public static class CpsCameraSetZoomInPO {
        /**
         * 变焦类型  1：广角镜头
         * 2：变焦镜头
         * 3：热红外镜头
         */
        private Integer source;
        /**
         * 基站id
         */
        private String nestId;
    }
}
