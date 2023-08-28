package com.imapcloud.nest.v2.service.converter;

import com.imapcloud.nest.v2.dao.entity.DataAnalysisMarkEntity;
import com.imapcloud.nest.v2.dao.po.out.DataAnalysisDetailMarkOutPO;
import com.imapcloud.nest.v2.service.dto.in.DataAnalysisMarkDrawInDTO;
import com.imapcloud.nest.v2.service.dto.in.DataAnalysisMarkSaveInDTO;
import com.imapcloud.nest.v2.service.dto.in.DataAnalysisResultInDTO;
import com.imapcloud.nest.v2.service.dto.out.DataAnalysisMarkDrawOutDTO;
import com.imapcloud.nest.v2.service.dto.out.DataAnalysisMarkOutDTO;
import com.imapcloud.nest.v2.service.dto.out.DataAnalysisMarkPageOutDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

/**
 * 账号信息转换器
 * @author Vastfy
 * @date 2022/4/25 16:09
 * @since 1.0.0
 */
@Mapper(componentModel = "spring")
public interface DataAnalysisMarkConverter {

    DataAnalysisMarkConverter INSTANCES = Mappers.getMapper(DataAnalysisMarkConverter.class);

    /**
     *  转换
     * @param out
     * @return
     */
    DataAnalysisMarkDrawInDTO convertDraw(DataAnalysisMarkEntity out);

    /**
     *  转换
     * @param out
     * @return
     */
    @Mappings({
            @Mapping(source = "addrImagePath",target = "addrImagePath",defaultValue = "")
            ,@Mapping(source = "markImagePath",target = "resultImagePath")
            ,@Mapping(source = "missionRecordId",target = "missionRecordsId")
    })
    DataAnalysisMarkDrawInDTO convertDraw(DataAnalysisDetailMarkOutPO out);

    /**
     *  转换TODO
     * @param in
     * @return
     */
    @Mappings({
//            @Mapping(target = "markId",expression = "java(in.getMarkId()!=null?in.getMarkId():com.geoai.common.core.util.BizIdUtils.snowflakeId())")
            @Mapping(target = "markId",expression = "java(com.imapcloud.nest.v2.common.utils.AuditUtils.getSnowId(in.getMarkId()))")
            ,@Mapping(target = "creatorId",expression = "java(com.imapcloud.nest.v2.common.utils.AuditUtils.getAudit())")
            ,@Mapping(target = "modifierId",expression = "java(com.imapcloud.nest.v2.common.utils.AuditUtils.getAudit())")
            ,@Mapping(source = "existMark",target = "existMark",defaultValue = "true")
            ,@Mapping(source = "thumImagePath",target = "thumImagePath",defaultValue = "")
            ,@Mapping(source = "addrImagePath",target = "addrImagePath",defaultValue = "")
            ,@Mapping(target = "markImagePath",defaultValue = "",ignore = true)
            ,@Mapping(target = "markNo",defaultValue = "0")
            ,@Mapping(target = "markState",defaultValue = "0")
    })
    DataAnalysisMarkEntity convert(DataAnalysisMarkSaveInDTO in);


    /**
     *  转换
     * @param out
     * @return
     */
    DataAnalysisMarkPageOutDTO convert(DataAnalysisMarkEntity out);

    /**
     *  转换
     * @param out
     * @return
     */
    DataAnalysisMarkOutDTO convertQuery(DataAnalysisMarkEntity out);

    /**
     *  转换
     * @param out
     * @return
     */
    DataAnalysisMarkOutDTO convertQuery(DataAnalysisDetailMarkOutPO out);

    /**
     *  转换
     * @param out
     * @return
     */
    @Mappings({
            @Mapping(target = "modifierId",expression = "java(com.imapcloud.nest.v2.common.utils.AuditUtils.getAudit())")
    })
    DataAnalysisMarkEntity convert(DataAnalysisMarkDrawOutDTO out);


    /**
     *  转换
     * @param out
     * @return
     */
    DataAnalysisResultInDTO.InsertInfoIn convertForResult(DataAnalysisMarkEntity out);

    /**
     *  转换
     * @param in
     * @return
     */
    @Mappings({
            @Mapping(source = "originalImagePath",target = "imagePath")
            ,@Mapping(source = "addr",target = "addr",defaultValue = "")
            ,@Mapping(target = "longitude",expression = "java(in.getLongitude()==null?in.getPicLongitude():in.getLongitude())")
            ,@Mapping(target = "latitude",expression = "java(in.getLatitude()==null?in.getPicLatitude():in.getLatitude())")
            ,@Mapping(source = "aiMark",target = "aiMark",defaultValue = "false")
    })
    DataAnalysisResultInDTO.InsertInfoIn convertForResult(DataAnalysisMarkDrawInDTO in);

}
