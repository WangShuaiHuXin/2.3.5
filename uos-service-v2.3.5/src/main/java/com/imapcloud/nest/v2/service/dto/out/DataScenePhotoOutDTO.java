package com.imapcloud.nest.v2.service.dto.out;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * 现场取证
 *
 * @author boluo
 * @date 2022-08-05
 */
@ToString
public class DataScenePhotoOutDTO {

    private DataScenePhotoOutDTO() {

    }

    @Data
    public static class PushOut {

        private List<String> failedList;

        /**
         * 成功列表 业务ID
         */
        private List<String> successList;

        private int successNum;

        private int ignoredNum;

    }
}
