package com.imapcloud.nest.runner;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.imapcloud.nest.model.StationDeviceEntity;
import com.imapcloud.nest.service.StationDeviceService;
import lombok.extern.slf4j.Slf4j;
import net.lingala.zip4j.crypto.PBKDF2.MacBasedPRF;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/*
@Component
//现在先不需要，后面可能需要
public class StartupRunner implements CommandLineRunner {
    public static Map<String, String> STATION_DEVICE_MAP = new HashMap();
    @Autowired
    private StationDeviceService stationDeviceService;

    @Override
    public void run(String... args) throws Exception {
        List<StationDeviceEntity> stationDeviceEntities = stationDeviceService.list(new QueryWrapper<StationDeviceEntity>().eq("deleted", false));
        if (CollectionUtil.isNotEmpty(stationDeviceEntities)) {
            Map<String, String> stationDeviceMap = stationDeviceEntities.stream().collect(Collectors.toMap(StationDeviceEntity::getUuid, StationDeviceEntity::getName, (key1, key2) -> key1));
            STATION_DEVICE_MAP = stationDeviceMap;
        }
    }

    public static Map getStationDeviceMap() {
        return STATION_DEVICE_MAP;
    }
}*/
