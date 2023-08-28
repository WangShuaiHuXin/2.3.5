package com.imapcloud.nest.common.constant;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName MessageConstant.java
 * @Description MessageConstant
 * @createTime 2022年04月18日 17:16:00
 */
public interface MessageConstant {

    interface Job {
        String JOB_NAME_STR = "jobName";

        String JOB_GROUP_NAME_STR = "jobGroupName";

        String PUB_MESSAGE_GROUP = "pubMessageGroup";

        String VALUES = "values";
    }

    /**
     *  NOT_READ - 0  ,READ - 1
     */
    interface ReadStatus {
        Integer NOT_READ = 0;
        Integer READ = 1;
    }


}
