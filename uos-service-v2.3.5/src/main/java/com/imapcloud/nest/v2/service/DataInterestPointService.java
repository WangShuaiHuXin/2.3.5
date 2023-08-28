package com.imapcloud.nest.v2.service;

import com.geoai.common.core.bean.PageResultInfo;
import com.imapcloud.nest.v2.service.dto.in.DataInterestPointInDTO;
import com.imapcloud.nest.v2.service.dto.in.DataInterestPointPageInDTO;
import com.imapcloud.nest.v2.service.dto.out.DataInterestPointOutDTO;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @Classname DataInterestPointService
 * @Description 全景兴趣点接口
 * @Date 2022/9/26 10:18
 * @Author Carnival
 */
public interface DataInterestPointService {

    /**
     * 兴趣点-新增
     */
    Boolean addPoint(DataInterestPointInDTO dto);

    /**
     * 兴趣点-批量删除
     */
    Boolean deletePoints(List<String> pointIds);
    /**
     * 兴趣点-修改
     */
    Boolean updatePoint(String pointId, DataInterestPointInDTO dto);

    /**
     *  兴趣点-分页查询
     */
    PageResultInfo<DataInterestPointOutDTO> queryPointPage(DataInterestPointPageInDTO dto);

    /**
     *  兴趣点-全量查询
     */
    List<DataInterestPointOutDTO> queryAllPoints(String orgCode, String pointName, String tagId);

    /**
     *  兴趣点-指定查询-返回字段较全
     */
    DataInterestPointOutDTO queryPoint(String pointId);
}
