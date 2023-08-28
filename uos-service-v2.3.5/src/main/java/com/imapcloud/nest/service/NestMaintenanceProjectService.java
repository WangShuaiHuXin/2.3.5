package com.imapcloud.nest.service;

import com.imapcloud.nest.model.NestMaintenanceProjectEntity;
import com.baomidou.mybatisplus.extension.service.IService;
import com.imapcloud.nest.pojo.dto.SaveNestMaintenanceProjectDto;
import com.imapcloud.nest.utils.RestRes;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 维护项目 服务类
 * </p>
 *
 * @author hc
 * @since 2021-05-14
 */
public interface NestMaintenanceProjectService extends IService<NestMaintenanceProjectEntity> {

    /**
     *
     * @param type
     * @return
     */
    List<NestMaintenanceProjectEntity> listByType(Integer type);

    @Transactional
    RestRes saveProject(SaveNestMaintenanceProjectDto dto);
}
