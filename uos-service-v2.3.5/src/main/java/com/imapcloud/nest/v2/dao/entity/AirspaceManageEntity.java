package com.imapcloud.nest.v2.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.geoai.common.mp.entity.GenericEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

/**
 * @Classname AirspaceManageEntity
 * @Description 空域管理实体类
 * @Date 2023/3/8 18:17
 * @Author Carnival
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("airspace_manage")
public class AirspaceManageEntity extends GenericEntity {

    private String airspaceId;

    private String airspaceName;

    private String orgCode;

    private String orgName;

    private String uavCode;

    private Integer uavCount;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Double altitude;

    private Double aglAltitude;

    private String address;

    private String airCoor;

    private Integer airCoorCount;

    private Integer isApproval;

    private String photoUrl;

    private String approvalFileUrl;

    /*中科天网*/
    private Integer applicantType;

    private Integer missionType;


}
