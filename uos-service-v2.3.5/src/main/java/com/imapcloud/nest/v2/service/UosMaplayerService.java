package com.imapcloud.nest.v2.service;

import com.imapcloud.nest.v2.service.dto.in.MaplayerQueryDTO;
import com.imapcloud.nest.v2.service.dto.out.MaplayerInfoOutDTO;
import com.imapcloud.nest.v2.web.vo.req.MaplayerQueryReqVO;
import com.imapcloud.nest.v2.web.vo.resp.MaplayerInfoRespVO;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * UOS地图图层业务接口定义
 * @author Vastfy
 * @date 2022/9/27 11:36
 * @since 2.1.0
 */
public interface UosMaplayerService {

    /**
     * 设置地图图层展示状态
     * @param maplayerId 图层ID
     * @param status    展示状态
     * @param managed    为true时，代表上级单位在操作图层数据，即设置图层所属单位的用户查看地图时是否对该地图可见
     */
    void setUserOrgMaplayerDisplayStatus(Long maplayerId, Integer status, boolean managed);

    /**
     * 设置地图图层预加载状态
     * @param maplayerId 图层ID
     * @param status    预加载状态
     * @param managed    为true时，代表上级单位在操作图层数据，即设置图层所属单位的用户查看地图时是否预加载该图层
     */
    void setUserOrgMaplayerPreloadStatus(Long maplayerId, Integer status, boolean managed);

    /**
     * 删除图层
     * @param maplayerId    图层ID
     */
    boolean deleteMaplayer(Long maplayerId);

    /**
     * 查询可见的地图图层数据
     * @param condition 查询条件
     * @return  地图图层数据
     */
    List<MaplayerInfoOutDTO> fetchVisibleMaplayerInfos(MaplayerQueryDTO condition);

    /**
     * 查询允许展示的地图图层数据
     * @return  地图图层数据
     */
    List<MaplayerInfoOutDTO> listDisplayedMaplayerInfos(Integer type);

}
