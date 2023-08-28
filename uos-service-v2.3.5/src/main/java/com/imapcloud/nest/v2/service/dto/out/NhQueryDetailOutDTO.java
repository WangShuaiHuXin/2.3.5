package com.imapcloud.nest.v2.service.dto.out;

import com.imapcloud.nest.v2.web.vo.resp.NhOrderDetailRespVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
@Data
public class NhQueryDetailOutDTO {
    private String title;
    private String orgCode;
    private String orgName;
    private LocalDateTime inspectionBeginTime;
    private LocalDateTime inspectionEndTime;
    private String orderId;
    private LocalDateTime createdTime;
    private int orderStatus;
    private int degree;
    private String gdCode;
    private String desc;
    private Long id;
    private int orderType;
    private List<NhQueryDetailOutDTO.OutDtoProcess> process;
    private String creatorName;
    private String verificationMethod;
    private String frequency ;

    private Double longitude;
    private Double latitude;
    @Data
    public static class OutDtoProcess {
        private String desc;
        private String nodeId;
        private String userName;
        private Boolean flag;
        private LocalDateTime operationTime;
        private String remark;
        private String mobile;

    }
}
