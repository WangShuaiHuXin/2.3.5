package com.imapcloud.nest.service;

import com.imapcloud.nest.model.FixAirLineEntity;
import com.baomidou.mybatisplus.extension.service.IService;
import com.imapcloud.nest.pojo.dto.FixAirLineDto;
import com.imapcloud.nest.utils.RestRes;
import com.imapcloud.sdk.pojo.entity.Mission;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 固定航线表 服务类
 * </p>
 *
 * @author wmin
 * @since 2020-10-10
 */
public interface FixAirLineService extends IService<FixAirLineEntity> {

    /**
     * 导入本地航线
     *
     * @param file
     * @param nestId
     * @param type
     * @return
     */
    RestRes importCloudAirLine(MultipartFile file, Integer nestId, Integer type);

    /**
     * 批量查询所有上次的航线
     *
     * @param nestId
     * @return
     */
    RestRes listCloudAirLine(Integer nestId);

    /**
     * 查询云端航线详情
     *
     * @param id
     * @return
     */
    RestRes findCloudAirLineDetail(Integer id);


    /**
     * 选择航线
     *
     * @param missionId
     * @param nestId
     * @return
     */
    RestRes choiceAirLine(Integer missionId, String nestId);

    /**
     * 查询基站列表
     *
     * @param nestId
     * @return
     */
    RestRes listMissionFromNestByNestId(String nestId);

    /**
     * 查询基站航线
     *
     * @param missionId
     * @return
     */
    RestRes findMissionDetailFromNestByMissionId(String nestId, String missionId);

    /**
     * @param nestId
     * @param missionId
     * @return
     */
    Mission exportAirLineStr(String nestId, String missionId);

    /**
     * 软删除
     *
     * @param id
     * @return
     */
    Integer softDelFixAirLine(Integer id);

    /**
     * 直接上传航线到机巢
     *
     * @param nestId
     * @param file
     * @return
     */
    RestRes uploadAirLineToNest(Integer nestId, Integer airLineType, MultipartFile file);


    /**
     * 基站航线上传到云端航线
     *
     * @param missionMap
     * @param nestId
     * @return
     */
    RestRes uploadNestAirLineToCloudAirLine(Map<String, String> missionMap, Integer nestId);

    /**
     * 批量软删除
     *
     * @param idList
     * @return
     */
    Integer batchSoftDelete(List<Integer> idList);

    /**
     * 复制云端航线
     *
     * @param cloudAirLineId
     * @return
     */
    RestRes copyCloudAirLine(Integer cloudAirLineId);

    /**
     * @param dto
     * @return
     */
    RestRes updateCloudAirLine(FixAirLineDto dto);
}
