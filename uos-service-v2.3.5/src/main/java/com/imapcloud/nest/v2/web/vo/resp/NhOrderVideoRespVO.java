package com.imapcloud.nest.v2.web.vo.resp;

import com.imapcloud.nest.v2.service.dto.out.NhOrderVideoOutDTO;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@ApiModel("南海任务工单-查询视频数据")
public class NhOrderVideoRespVO implements Serializable {
    private Long total;
    private List<NhOrderVideoRespVO.videoVo> infoList;

    @Data
    public static class videoVo implements Serializable {
        private Long id;
        private String name;
        private String alias;
        private String videoUrl;
        private Integer type;
        private Integer recordStatus;
        private Integer missionId;
        private Integer missionRecordsId;
        private String execId;
        private Integer createUserId;
        private LocalDateTime createTime;
        private LocalDateTime modifyTime;
        private boolean deleted;
        private boolean physicalDeleted;
        private Double lat;
        private Double lng;
        private String unitId;
        private String orgCode;
        private Integer tagVersion;
        private String srtJson;
        private String srtUrl;
    }
}
