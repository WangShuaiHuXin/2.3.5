package com.imapcloud.nest.service.event;

import com.imapcloud.nest.common.event.AbstractEvent;
import com.imapcloud.sdk.pojo.BaseResult3;
import lombok.Getter;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName UploadFileEvent.java
 * @Description UploadFileEvent
 * @createTime 2022年03月31日 17:11:00
 */
@Getter
public class UploadMediaEvent extends AbstractEvent<BaseResult3> {


    private Integer recordId;

    private Integer missionId;

    private String nestUuid;

    private Boolean success;

    private String env;

    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public UploadMediaEvent(BaseResult3 source) {
        super(source);
    }

    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public UploadMediaEvent(BaseResult3 source,boolean success , Integer recordId ,Integer missionId , String nestUuid) {
        super(source);
        this.success = success;
        this.recordId = recordId;
        this.missionId = missionId;
        this.nestUuid = nestUuid;
    }
    public UploadMediaEvent(BaseResult3 source,boolean success , Integer recordId ,Integer missionId , String nestUuid,String env) {
        super(source);
        this.success = success;
        this.recordId = recordId;
        this.missionId = missionId;
        this.nestUuid = nestUuid;
        this.env=env;
    }

}
