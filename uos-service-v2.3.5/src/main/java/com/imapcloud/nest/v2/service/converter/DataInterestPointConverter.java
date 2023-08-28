package com.imapcloud.nest.v2.service.converter;

import com.imapcloud.nest.v2.dao.entity.DataInterestPointEntity;
import com.imapcloud.nest.v2.service.dto.out.DataInterestPointOutDTO;
import org.mapstruct.Mapper;

/**
 * @Classname DataInterestPointConverter
 * @Description 全景兴趣点转化类
 * @Date 2022/9/26 14:08
 * @Author Carnival
 */
@Mapper(componentModel = "spring")
public interface DataInterestPointConverter {

    DataInterestPointOutDTO convert(DataInterestPointEntity entity);
}
