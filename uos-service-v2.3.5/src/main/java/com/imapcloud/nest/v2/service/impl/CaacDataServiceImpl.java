package com.imapcloud.nest.v2.service.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.imapcloud.nest.v2.common.enums.CaacCpnTypeEnum;
import com.imapcloud.nest.v2.common.properties.CaacConfig;
import com.imapcloud.nest.v2.common.properties.GeoaiUosProperties;
import com.imapcloud.nest.v2.common.utils.NumberFormatUtils;
import com.imapcloud.nest.v2.service.CaacDataService;
import com.imapcloud.nest.v2.service.dto.out.CaacCloudUavOutDTO;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 民航数据业务接口实现
 * @author Vastfy
 * @date 2023/3/8 15:54
 * @since 2.2.5
 */
@Slf4j
@RefreshScope
@Service
public class CaacDataServiceImpl implements CaacDataService, InitializingBean, DisposableBean {

    @Resource
    private RestTemplate restTemplate;

    @Resource
    private GeoaiUosProperties geoaiUosProperties;

    /**
     * 民航云端无人机信息缓存
     */
    private static final CaacCloudUavCache CAAC_CLOUD_UAV_CACHE = new CaacCloudUavCache();

    private final ScheduledExecutorService scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1, r -> new Thread(r, "Caac-uav-thread"));

    @Override
    public List<CaacCloudUavOutDTO> listCloudUav() {
        List<CaacCloudUavOutDTO> uavList = CAAC_CLOUD_UAV_CACHE.getUavList();
        if(!CollectionUtils.isEmpty(uavList)){
            return uavList;
        }
        return Collections.emptyList();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        CaacConfig caacConfig = geoaiUosProperties.getCaac();
        if(caacConfig.isUavFetchEnabled()){
            log.info("Initialized task to fetch caac uav data with frequency({}), scheduledThreadPoolExecutor is {}", caacConfig.getUavFetchFrequency(), scheduledThreadPoolExecutor);
            scheduledThreadPoolExecutor.scheduleWithFixedDelay(this::updateCaacCloudUavCache, 2, caacConfig.getUavFetchFrequency().toMillis(), TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public void destroy() throws Exception {
        log.info("Shutdown scheduledThreadPoolExecutor ==> {}", scheduledThreadPoolExecutor);
        if(!scheduledThreadPoolExecutor.isShutdown()){
            scheduledThreadPoolExecutor.shutdown();
        }
        // 清理缓存
        List<CaacCloudUavOutDTO> uavList = CAAC_CLOUD_UAV_CACHE.getUavList();
        if(!CollectionUtils.isEmpty(uavList)){
            uavList.clear();
        }
    }

    private void updateCaacCloudUavCache(){
        String uavFetchUrl = geoaiUosProperties.getCaac().getUavFetchUrl();
        boolean debugEnabled = log.isDebugEnabled();
        if(debugEnabled){
            log.debug("Fetch caac cloud uav data starting, url is {}", uavFetchUrl);
        }
        List<CaacCloudUavOutDTO> uavList = null;
        try {
            if(StringUtils.hasText(uavFetchUrl)){
                String encode = geoaiUosProperties.getCaac().getUavFetchHeader();
                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromUriString(uavFetchUrl)
                        .queryParam("header", "{header}");
                CaacOriginResponse caacResponse = restTemplate.getForObject(builder.encode().toUriString(), CaacOriginResponse.class, Collections.singletonMap("header", encode));
                if(Objects.nonNull(caacResponse)){
                    List<CaacOriginUavInfo> data = caacResponse.getData();
                    if(!CollectionUtils.isEmpty(data)){
                        uavList = data.stream()
                                // 过滤中科云图本身的无人机数据
                                .filter(r -> !Objects.equals(r.getCpn(), CaacCpnTypeEnum.CPN_011.getCpn()))
//                                .filter(r -> Objects.nonNull(r.getTime()))
                                // 无人机编号相同时，取时间靠后无人机信息
                                .collect(Collectors.toMap(CaacOriginUavInfo::getUavId, Function.identity(), (o1, o2) -> /*o1.getTime().isAfter(o2.getTime()) ? o1 : */o2))
                                .values()
                                .stream()
                                .map(r -> {
                                    CaacCloudUavOutDTO ccu = new CaacCloudUavOutDTO();
                                    CaacCpnTypeEnum caacCpn = CaacCpnTypeEnum.findMatch(r.getCpn());
                                    ccu.setCpn(r.getCpn());
                                    if(!Objects.equals(caacCpn, CaacCpnTypeEnum.UNKNOWN)){
                                        ccu.setCpnName(caacCpn.getCpnName());
                                    }
                                    ccu.setLng(NumberFormatUtils.format(r.getLng(), 7));
                                    ccu.setLat(NumberFormatUtils.format(r.getLat(), 7));
                                    ccu.setHeight(NumberFormatUtils.format(r.getHeight(), 2));
                                    ccu.setSpeed(NumberFormatUtils.format(r.getSpeed(), 2));
                                    ccu.setAngle(r.getAngle());
                                    ccu.setUavId(r.getUavId());
                                    return ccu;
                                })
                                .collect(Collectors.toList());
                    }
                }
            }
        }catch (Throwable t){
            log.warn("Fetched caac cloud uav data error", t);
        }
        if(debugEnabled){
            log.debug("Fetch caac cloud uav data finished ==> {}", uavList);
        }
        if(!CollectionUtils.isEmpty(uavList)){
            if(debugEnabled){
                log.debug("Updated caac uav data cache");
            }
            CAAC_CLOUD_UAV_CACHE.setUavList(uavList);
        }
    }

    @Data
    static class CaacCloudUavCache {
        private volatile List<CaacCloudUavOutDTO> uavList;
    }

    @Data
    static class CaacOriginResponse {
        private String code;
        private String message;
        private Integer count;
        private List<CaacOriginUavInfo> data;
    }

    @Data
    static class CaacOriginUavInfo{

        private String id;
        private String cpn;
        private Long lng;
        private Long lat;
        private Long height;
        private Integer speed;
//        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
//        private LocalDateTime time;
        private Integer angle;
        @JsonProperty("uavid")
        private String uavId;
    }

}
