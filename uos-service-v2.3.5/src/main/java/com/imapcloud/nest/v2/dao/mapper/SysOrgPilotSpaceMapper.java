package com.imapcloud.nest.v2.dao.mapper;

import com.imapcloud.nest.v2.dao.entity.SysOrgPilotSpaceEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName SysOrgPilotSpaceMapper.java
 * @Description SysOrgPilotSpaceMapper
 * @createTime 2022年07月13日 15:02:00
 */
@Mapper
public interface SysOrgPilotSpaceMapper extends BaseBatchMapper<SysOrgPilotSpaceEntity> {

    /**
     * 新增
     * @author double
     * @date 2023/03/28
     **/
    int insert(SysOrgPilotSpaceEntity sysOrgPilotSpace);

    /**
     * 刪除
     * @author double
     * @date 2023/03/28
     **/
    int delete(int id);

    /**
     * 更新
     * @author double
     * @date 2023/03/28
     **/
    int update(SysOrgPilotSpaceEntity sysOrgPilotSpace);

    /**
     * 查询接口
     * @param orgCode
     * @param workSpaceId
     * @return
     */
    List<SysOrgPilotSpaceEntity> query(String orgCode , String workSpaceId);


}
