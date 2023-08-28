package com.imapcloud.nest.service;

import com.imapcloud.nest.model.MapOperationTipEntity;
import com.baomidou.mybatisplus.extension.service.IService;
import com.imapcloud.nest.utils.RestRes;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * <p>
 * 航线编辑地图操作提示 服务类
 * </p>
 *
 * @author wmin
 * @since 2022-05-24
 */
public interface MapOperationTipService extends IService<MapOperationTipEntity> {

    /**
     * 开关提示
     *
     * @param enable
     * @return
     */
    RestRes switchMapOperationTip(Integer enable);

    /**
     * 获取提示状态
     * @return
     */
    RestRes getMapOperationTip();


    /**
     * 网格化开关提示
     *
     */
    RestRes switchGridOperationTip(Integer enable);

    /**
     * 获取网格化提示状态
     */
    RestRes getGridOperationTip();
}
