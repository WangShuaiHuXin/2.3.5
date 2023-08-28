package com.imapcloud.nest.v2.service.factory;

import com.imapcloud.nest.utils.spring.SpringContextUtils;
import com.imapcloud.nest.v2.service.CommonDataSynService;
import com.imapcloud.nest.v2.service.G503DataSynService;
import com.imapcloud.nest.v2.service.PubDataSynService;
import com.imapcloud.nest.v2.service.S110DataSynService;
import com.imapcloud.sdk.pojo.constant.NestTypeEnum;

public class DataSynFactory {

    public static PubDataSynService getDataSynService(Integer nestType){
        PubDataSynService pubDataSynService = null;
        if( NestTypeEnum.G503.getValue() == nestType ){
            pubDataSynService = SpringContextUtils.getBean(G503DataSynService.class);
        }else if( NestTypeEnum.S110_MAVIC3.getValue() == nestType ){
            pubDataSynService = SpringContextUtils.getBean(S110DataSynService.class);
        }else {
            pubDataSynService = SpringContextUtils.getBean(CommonDataSynService.class);
        }

        return pubDataSynService;
    }
}
