package com.imapcloud.nest.v2.service.dto.in;

import com.imapcloud.nest.enums.RoleIdenValueEnum;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * 电力AI任务识别
 *
 * @author boluo
 * @date 2023-03-01
 */
@ToString
public final class PowerTaskInDTO {

    private PowerTaskInDTO() {}

    @Data
    public static class AutoTaskInDTO {

        private String dataId;

        private List<String> detailIdList;

        private String orgCode;

        private RoleIdenValueEnum roleIdenValueEnum;
    }

    @Data
    public static class AddTaskInDTO {

        private String dataId;

        private List<String> detailIdList;

        private String orgCode;

        /**
         * 识别功能
         */
        private List<String> functionIdList;

        /**
         * 是否是系统自动AI识别
         */
        private Boolean system;

        /**
         * 添加任务的用户ID，为空表示给单位下的所有用户发送
         */
        private String accountId;

        /**
         * 识别类型
         */
        private RoleIdenValueEnum roleIdenValueEnum;
    }
}
