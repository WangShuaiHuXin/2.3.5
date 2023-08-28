package com.imapcloud.nest.v2.service.impl;

import com.imapcloud.nest.v2.service.AbstractAirLineService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Component
public class DjiAirLineServiceImpl extends AbstractAirLineService {

    @Override
    public String transformKmzToJsonMainImpl(MultipartFile multipartFile) {
        return super.transformKmzToJsonMain(multipartFile);
    }

    @Override
    public String transformJsonToKmzMainImpl(String airLineJson , Object... param) {
        return super.transformJsonToKmzMain(airLineJson , param);
    }
}
