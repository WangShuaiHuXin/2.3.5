package com.imapcloud.nest.v2.web.vo.req;

import lombok.Data;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName DJITaskReqVO.java
 * @Description DJITaskReqVO
 * @createTime 2022年07月25日 16:08:00
 */
@Data
public class DJITaskReqVO {

    @Data
    public static class DJITaskFileReqVO {

        private String fileName;

        private String fileMD5;

    }

}
