package com.imapcloud.nest.v2.service.dto.in;
import com.geoai.common.core.bean.PageInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @Classname RegionQueryInDTO
 * @Description 区域分页查询信息
 * @Date 2022/8/11 10:51
 * @Author Carnival
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Data
public class UosRegionPageInDTO extends PageInfo{

    private String regionName;
}
