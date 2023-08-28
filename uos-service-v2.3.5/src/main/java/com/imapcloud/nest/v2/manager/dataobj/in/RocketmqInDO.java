package com.imapcloud.nest.v2.manager.dataobj.in;

import com.imapcloud.nest.enums.RoleIdenValueEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * rocketmq
 *
 * @author boluo
 * @date 2023-01-31
 */
@ToString
public final class RocketmqInDO {

    private RocketmqInDO() {

    }

    @Getter
    @AllArgsConstructor
    public enum MqTypeEnum {

        /**
         * 文件删除
         */
        FILE_DELETE(),
        POWER_TASK(),
        ;
    }

    @Data
    public static class MqInfo implements Serializable {

        private MqTypeEnum type;

        private Object data;
    }

    @Data
    public static class TaskMqInfo implements Serializable {

        private String dataId;

        private String taskKey;

        private long num;

//        /**
//         * 任务图片记录ID
//         */
//        private List<String> detailIdList;
//
        /**
         * 识别类型
         */
        private RoleIdenValueEnum roleIdenValueEnum;
//
//        /**
//         * 单位code
//         */
//        private String orgCode;
//
//        /**
//         * 识别功能
//         */
//        private List<String> functionIdList;
    }

    @Data
    public static class BusinessParamInfo {

        private String detailId;

        /**
         * 飞行数据id
         */
        private String dataId;
    }
}
