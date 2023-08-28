package com.imapcloud.nest.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.imapcloud.nest.model.NestUnitEntity;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author hc
 * @since 2021-05-20
 */
public interface NestUnitMapper extends BaseMapper<NestUnitEntity> {

    @Select("select distinct org_code from nest_unit where nest_id = #{nestId}")
    List<String> listUnitIds(Integer nestId);

    @Select("select distinct nest_id from nest_unit where org_code = #{unitId}")
    List<String> listNestIds(String unitId);

    @Select("select nu.*,(select `name` from nest n where nu.nest_id = n.id and deleted = 0) as nest_name from nest_unit nu")
    List<NestUnitEntity> getListAndName();

}
