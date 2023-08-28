package com.imapcloud.nest.v2.service.dto.out;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName MissionPhotoPointOutDTO.java
 * @Description MissionPhotoPointOutDTO
 * @createTime 2022年09月16日 11:23:00
 */
@Data
@Accessors(chain = true)
public class TaskOutDTO implements Serializable {


        /**
         * taskId
         */
        private String id;
        /**
         * 任务名称
         */
        private String name;

        /**
         * 描述，巡检作业周期
         */
        private String description;

        /**
         * 任务类型Id
         */
        private Integer type;

        private String baseNestId;

        /**
         * 该条记录被复制的次数
         */
        private Integer copyCount;

        /**
         * 航线种类，0 - 机巢航线，1 - 易飞终端航线
         */
        private Integer mold;

        /**
         * 单位编码
         */
        private String orgCode;

        /**
         * 变电站规划的次级类别0是本地任务，1是动态任务
         */
        private Integer subType;

        private Integer dataType;

        private String tagId;

}
