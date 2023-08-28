package com.imapcloud.nest.v2.service;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public abstract class AbstractAirLineService extends PubAirLineService{

    public abstract String transformKmzToJsonMainImpl(MultipartFile multipartFile);


    public abstract String transformJsonToKmzMainImpl(String airLineJson , Object... param);

}
