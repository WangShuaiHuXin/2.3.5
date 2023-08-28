package com.imapcloud.nest.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.imapcloud.nest.model.media.MediaServiceEntity;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 流媒体服务表 服务类
 * </p>
 * @deprecated 2.3.2，将在后续版本删除
 * @author daolin
 * @since 2022-04-25
 */
@Deprecated
public interface MediaServiceService extends IService<MediaServiceEntity> {

//    /**
//     * @deprecated 2.3.2，将在后续版本删除
//     */
//    @Deprecated
//    List<String> getThirdPartyDomains();

//    /**
//     * @deprecated 2.3.2，将在后续版本删除
//     */
//    @Deprecated
//    List<MediaServiceEntity> getDomainsByType(Integer type);
}
