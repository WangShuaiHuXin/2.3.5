package com.imapcloud.nest.v2.service;

import com.imapcloud.nest.utils.spring.SpringContextUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Component
public class AirLineProxy {

    private AbstractAirLineService airLineService;

    public AirLineProxy(){}

    public AirLineProxy(Class clazz){
        this.airLineService = (AbstractAirLineService) SpringContextUtils.getBean(clazz);
    }

    public String proxyTransformJsonToKmzMainImpl(String airLine , Object... param){
        return this.airLineService.transformJsonToKmzMainImpl(airLine , param);
    }

    public String proxyTransformKmzToJsonMainImpl(MultipartFile file){
        return this.airLineService.transformKmzToJsonMainImpl(file);
    }

}
