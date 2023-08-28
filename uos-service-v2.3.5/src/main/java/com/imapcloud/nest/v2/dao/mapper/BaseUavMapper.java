package com.imapcloud.nest.v2.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.geoai.common.mp.entity.PagingRestrictDo;
import com.geoai.common.mp.mapper.IPageMapper;
import com.imapcloud.nest.v2.dao.entity.BaseUavEntity;
import com.imapcloud.nest.v2.dao.po.BaseUavEntityExt;
import com.imapcloud.nest.v2.dao.po.UavQueryCriteriaPO;
import com.imapcloud.nest.v2.dao.po.out.AppStreamOutPO;
import com.imapcloud.nest.v2.dao.po.out.BaseAppUavOutPO;
import com.imapcloud.nest.v2.dao.po.out.BaseNestUavOutPO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * <p>
 * 无人机信息表 Mapper 接口
 * </p>
 *
 * @author wmin
 * @since 2022-08-17
 */
public interface BaseUavMapper extends BaseMapper<BaseUavEntity>,
        IPageMapper<BaseUavEntityExt, UavQueryCriteriaPO, PagingRestrictDo> {

    /**
     * 通过无人机ID更新数据
     *
     * @param baseUavEntity 基地无人机实体
     * @return int
     */
    int updateByUavId(BaseUavEntity baseUavEntity);

    BaseUavEntity selectUavByNestId(@Param("nestId") String nestId,@Param("which") Integer which);

    String selectUavTypeByNestId(@Param("nestId") String nestId, @Param("which") Integer which);

    BaseUavEntity selectUavByAppId(String appId);

    List<BaseNestUavOutPO> batchSelectUavAndNestId(List<String> nestIdList);

    String selectTypeByNestUuid(@Param("nestUuid") String nestUuid, @Param("which") Integer which);


    List<String> selectUavStreamByUav(String uavSn , String uavId);

    /**
     * 删除通过无人机id
     *
     * @param uavIdList 无人机id列表
     * @param accountId 帐户id
     * @return int
     */
    int deleteByUavIdList(@Param("uavIdList") List<String> uavIdList, @Param("accountId") String accountId);

    String selectUavStreamIdByNestId(@Param("nestId") String nestId,@Param("which") Integer which);

    List<AppStreamOutPO> selectBatchStreamIdAndAppIdByAppIds(List<String> appIdList);

    String selectTypeByDeviceId(String deviceId);

    String selectUavStreamIdByAppId(String appId);

    List<BaseAppUavOutPO> batchSelectUavAndAppId(List<String> appIdList);

    @Update("UPDATE base_uav SET deleted = #{deleted} WHERE uav_id = #{uavId}")
    int updateDeletedByUavId(String uavId, Integer deleted);

    String selectUavStreamIdByDeviceId(String deviceId);

    void updateStreamIdByUavId(List<BaseUavEntity> list);

    List<BaseNestUavOutPO> listUavs(String nestId);
}
