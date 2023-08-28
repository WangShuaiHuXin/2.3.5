package com.imapcloud.nest.v2.service.dto.in;

import lombok.Data;

import java.util.List;

@Data
public class CpsCameraInDTO {
    @Data
    public static class CpsCameraSetTypeInDTO {
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
    public static class CpsCameraSetZoomInDTO {
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
