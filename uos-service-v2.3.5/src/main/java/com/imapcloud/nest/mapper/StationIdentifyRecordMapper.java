package com.imapcloud.nest.mapper;

import com.imapcloud.nest.model.StationIdentifyRecordEntity;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.imapcloud.nest.model.StationInfraredRecordEntity;
import com.imapcloud.nest.pojo.dto.DefectPhotoDTO;
import com.imapcloud.nest.pojo.dto.StationDevicePhotoDTO;
import com.imapcloud.nest.pojo.dto.StationIdentifyPhotoDTO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 变电站的设备出现的缺陷记录 Mapper 接口
 * </p>
 *
 * @author wmin
 * @since 2020-12-29
 */
public interface StationIdentifyRecordMapper extends BaseMapper<StationIdentifyRecordEntity> {


    /**
     * 根据设备uuid获取该设备的图片（分页）
     *
     * @param name
     * @param missionRecordId 
     * @return
     */
    IPage<StationIdentifyPhotoDTO> getPhotoListPage(@Param("page") IPage<StationIdentifyPhotoDTO> page, @Param("tagId") Integer tagId, @Param("name") String name, @Param("missionRecordId")String missionRecordId,@Param("defectStatus")Integer defectStatus);

    /**
     * 根据设备uuid获取该设备的表计读数的全部数值
     *
     * @param photoName
     * @return
     */
    List<StationDevicePhotoDTO> getAllPhotoMeterNum(@Param("tagId") Integer tagId, @Param("photoName") String photoName,
                                                    @Param("startTime") String startTime, @Param("endTime") String endTime);

    /**
     * 获取表计读数的图片idList
     *
     * @return
     */
    @Select("SELECT photo_id FROM station_identify_record WHERE meter_num >0")
    List<Long> getMeterPhotoIdList();

    /**
     * 获取该用户的缺陷、表记照片（筛选用户有的机巢、用户有的tag的照片,超管获取全部）
     *
     * @param userId
     * @return
     */
    List<DefectPhotoDTO> getDefectListByUser(@Param("userId") Long userId, @Param("type") Integer type,
                                             @Param("startTime") String startTime, @Param("endTime") String endTime,
                                             @Param("orgCode") String orgCode);


    /**
     * 获取缺陷的问题数量曲线
     *
     * @param userId
     * @param startTime
     * @param endTime
     * @param type
     * @return
     */
    List<DefectPhotoDTO> getDefectNum(@Param("userId") Long userId, @Param("startTime") String startTime, @Param("endTime") String endTime,
                                      @Param("type") Integer type, @Param("orgCode") String orgCode);


}
