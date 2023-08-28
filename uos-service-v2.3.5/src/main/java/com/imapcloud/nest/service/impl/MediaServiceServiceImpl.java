package com.imapcloud.nest.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.imapcloud.nest.mapper.MediaServiceMapper;
import com.imapcloud.nest.model.media.MediaServiceEntity;
import com.imapcloud.nest.service.MediaServiceService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 流媒体服务表 服务实现类
 * </p>
 * @deprecated 2.3.2，将在后续版本删除
 * @author daolin
 * @since 2022-04-25
 */
@Service
public class MediaServiceServiceImpl extends ServiceImpl<MediaServiceMapper, MediaServiceEntity> implements MediaServiceService {


//    @Autowired
//    private RedisService redisService;

//    @Override
//    public List<String> getThirdPartyDomains() {
//        String redisKey = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.RELAY_DOMAIN_SET);
//        Set<Object> domains = redisService.sGet(redisKey);
//        if (CollectionUtil.isEmpty(domains)) {
//            QueryWrapper<MediaServiceEntity> queryWrapper = new QueryWrapper<>();
//            queryWrapper.eq("service_type",3);
//            List<MediaServiceEntity> list = this.list(queryWrapper);
//            if (CollectionUtil.isNotEmpty(list)) {
//                List<String> domainDistic = list.stream().map(MediaServiceEntity::getDomain).collect(Collectors.toList());
//                redisService.sSet(redisKey, domainDistic.toArray());
//                return domainDistic;
//            }
//            return Collections.emptyList();
//        }
//        return domains.stream().map(o -> (String) o).collect(Collectors.toList());
//    }

//    @Override
//    public List<MediaServiceEntity> getDomainsByType(Integer type) {
//        QueryWrapper<MediaServiceEntity> queryWrapper = new QueryWrapper<>();
//        queryWrapper.eq("service_type", type);
//        List<MediaServiceEntity> list = this.list(queryWrapper);
//        return list;
//    }

}
