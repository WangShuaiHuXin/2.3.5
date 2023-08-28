package com.imapcloud.nest.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.geoai.common.mp.entity.PagingRestrictDo;
import com.geoai.common.mp.mapper.IPageMapper;
import com.imapcloud.nest.model.NestEntity;
import com.imapcloud.nest.pojo.dto.NestAppNameHttpUrlDTO;
import com.imapcloud.nest.pojo.dto.NestDetailsInfoDto;
import com.imapcloud.nest.pojo.dto.NestDto;
import com.imapcloud.nest.v2.dao.po.NestQueryCriteriaPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * <p>
 * 机巢信息表 Mapper 接口
 * </p>
 *
 * @author wmin
 * @since 2020-07-02
 */
@Mapper
public interface NestMapper extends BaseMapper<NestEntity>, IPageMapper<NestEntity, NestQueryCriteriaPO, PagingRestrictDo> {

    /**
     * 查询所有机巢的uuid,name
     */
    @Select("select id,name from nest where deleted = 0")
    List<NestEntity> selectAllIdAndName();

    /**
     * 通过id查询机巢的uuid
     */
    @Select("select uuid from nest where id = #{id} and deleted = 0")
    String selectUuidById(@Param("id") Integer id);

    @Select("select id from nest where deleted = 0 and uuid = #{uuid}")
    Integer selectIdByUuid(String uuid);

    /**
     * 软删除
     */
    @Update("update nest set deleted = 1 where id = #{id}")
    int softDelete(@Param("id") Integer id);

    /**
     * 通过机巢id获取机巢的全部信息和飞机信息
     */
    @Deprecated
    NestDetailsInfoDto getByNestId(@Param("nestId") String id);

    @Deprecated
    NestDetailsInfoDto getByNestUuId(@Param("nestUuid") String nestUuid);

    /**
     * 根据架次id获取飞机的图片地址
     */
    @Select("SELECT rtmp_url FROM nest WHERE id = (SELECT nest_id FROM task WHERE id = (SELECT task_id FROM mission WHERE id = #{missionId}))")
    String getRtmpUrlByMissionId(Integer missionId);

    @Select("SELECT type FROM nest WHERE uuid = #{uuid} and deleted = 0")
    Integer selectTypeByUuid(String uuid);

    /**
     * 根据机巢uuid获取机巢的无人机型号
     */
    @Select("SELECT code FROM aircraft WHERE nest_id = (SELECT id FROM nest WHERE uuid = #{uuid} AND deleted = 0) AND deleted = 0")
    String selectAircraftStateByUuid(String uuid);

    @Select("SELECT name FROM nest WHERE uuid = #{uuid} and deleted = 0")
    String selectNameByUuid(String uuid);

    @Select("select * from nest where deleted = 0")
    List<NestEntity> selectAllNests();

    /**
     * 根据orgCode获取机巢对象
     * @return List<NestEntity>
     */
    List<NestDto> listNestByOrgCode(@Param("orgCode") String orgCode);

    List<NestAppNameHttpUrlDTO> getNestAppNameHttpUrlList();

}
