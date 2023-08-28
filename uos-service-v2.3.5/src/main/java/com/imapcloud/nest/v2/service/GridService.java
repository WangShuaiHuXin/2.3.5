package com.imapcloud.nest.v2.service;

import com.imapcloud.nest.pojo.dto.unifyAirLineDto.GridData;
import com.imapcloud.nest.v2.service.dto.in.GridInDTO;
import com.imapcloud.nest.v2.service.dto.in.GridManageInDTO;
import com.imapcloud.nest.v2.service.dto.out.GridOutDTO;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Classname GridRegionService
 * @Description 网格区域API
 * @Date 2022/12/7 14:25
 * @Author Carnival
 */
public interface GridService {


    /**
     * 上传网格区域
     */
    String uploadGridRegion(MultipartFile file);

    /**
     * 保存网格区域
     */
    Boolean saveGridRegion(GridInDTO.RegionInDTO dto);

    /**
     * 更改网格区域
     */
    Boolean updateGridRegion(GridInDTO.RegionInDTO dto, String gridRegionId);

    /**
     * 根据单位获取网格区域
     */
    List<GridOutDTO.RegionOutDTO> queryGridRegion(String orgCode);

    /**
     * 根据管理网格获取数据网格
     */
    List<GridOutDTO.GridDataBatchDTO> queryGridData(List<String> gridManageIds, String orgCode);

    /**
     * 保存数据网格
     */
    Boolean saveGridData(List<GridData> gridData, String girdManageId, String orgCode);

    /**
     * 保存航线管理网格
     */
    void setGridManage(GridManageInDTO gridManageInDTO);

    /**
     * 根据航线ID获取网格边界
     */
    String getGridBoundsByTaskId(Integer taskId);

    /**
     * 根据航线ID获取网格边界
     */
    String getGridManageByTaskId(Integer taskId);

    /**
     * 航线删除时调用此方法
     */
    void delGridManageTask(List<Integer> taskIdList);

    /**
     * 查询网格列表，根据边界值
     */
    String findGridIdByLngAndLat(BigDecimal lng, BigDecimal lat, Long missionRecordsId);

    /**
     * 根据管理网格获取最新的航线
     */
    Integer getTaskIdByGridManagerId(String gridManageId, String orgCode);

    /**
     * 管理网格与单位关联
     */
    String setGridManageOrgCode(List<GridInDTO.GridManageOrgCodeDTO> list);

    /**
     * 管理网格重置
     */
    void cancelGridManageOrgCode(List<String> gridManageIds);


    /**
     * 删除管理网格
     */
    void delGridManage(List<String> gridManageIds);

    /**
     * 根据区域网格获取管理网格
     */
    List<String> queryGridManageIdsByGridRegionId(String gridRegionId);

    /**
     * 获取管理网格所属单位和航线
     */
    List<GridOutDTO.OrgAndTaskOutDTO> queryOrgCodeAndTask(String gridManageId);

    /**
     * 根据管理网格查找所属区域网格
     */
    List<GridInDTO.GridManageOrgCodeDTO> findOrgAndManageByManageIds(List<String> gridManageIds);
}
