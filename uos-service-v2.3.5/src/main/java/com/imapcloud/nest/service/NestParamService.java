package com.imapcloud.nest.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.imapcloud.nest.model.NestParamEntity;
import org.apache.ibatis.annotations.Select;

/**
 * 机巢信息 服务类
 *
 * @author: zhengxd
 * @create: 2020/9/25
 **/
public interface NestParamService extends IService<NestParamEntity> {

    void deleteByNestId(Integer nestId);

    /**
     * 查询机巢的起降高度
     *
     * @return
     */
    Double selectStartStopPointAltitudeByNestId(Integer nestId);
}
