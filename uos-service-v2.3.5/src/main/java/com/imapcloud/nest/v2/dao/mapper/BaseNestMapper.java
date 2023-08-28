package com.imapcloud.nest.v2.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.geoai.common.mp.entity.PagingRestrictDo;
import com.geoai.common.mp.mapper.IPageMapper;
import com.imapcloud.nest.v2.dao.entity.BaseNestEntity;
import com.imapcloud.nest.v2.dao.po.in.BaseNestInPO;
import com.imapcloud.nest.v2.dao.po.out.BaseNestOutPO;
import com.imapcloud.nest.v2.service.dto.out.BaseNestInfoOutDTO;
import com.imapcloud.nest.v2.manager.dataobj.out.BaseNestOutDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 机巢信息表 Mapper 接口
 * </p>
 *
 * @author wmin
 * @since 2022-08-17
 */
@Mapper
public interface BaseNestMapper extends BaseMapper<BaseNestEntity>, IPageMapper<BaseNestOutPO.ListOutPO, BaseNestInPO.ListInPO, PagingRestrictDo> {

    /**
     * 查询基站uuid
     *
     * @param nestId 巢id
     * @return {@link String}
     */
    @Select("SELECT uuid FROM base_nest WHERE nest_id = #{nestId} and deleted=0")
    String selectUuidByNestId(String nestId);

    List<BaseNestEntity> batchSelects(List<String> nestIdList);

    /**
     * 基站基本信息
     *
     * @param baseNestEntity 实体
     * @return int
     */
    int insertNestBase(BaseNestEntity baseNestEntity);

    /**
     * 通过基站ID更新
     *
     * @param baseNestEntity
     * @return int
     */
    int updateByNestId(BaseNestEntity baseNestEntity);

    BaseNestEntity selectNestByMissionId(Integer missionId);

    List<BaseNestEntity> selectNestByKeyword(@Param("accountId") Long accountId, @Param("keyword") String keyword);

    /**
     * 逻辑删除
     *
     * @param nestId    巢id
     * @param modifierId 帐户id
     * @return int
     */
    int deleteByNestId(@Param("nestId") String nestId, @Param("modifierId") String modifierId);

    List<BaseNestEntity> selectByOrgCode(String orgCode);

    BaseNestInfoOutDTO findNestByUavId(String streamId);

    String findDJIStreamId(String nestId);

    List<BaseNestOutDO.BaseNestUavInfoOutDO> getNestUavInfoByIds(@Param("ids")List<String> nestIdList);

    List<BaseNestOutPO.ListOutPO> selectAllCondition(@Param("criteria") BaseNestInPO.ListInPO listInPo);

}
