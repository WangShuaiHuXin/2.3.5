package com.imapcloud.nest.controller;

import com.imapcloud.nest.pojo.dto.AudioDto;
import com.imapcloud.nest.utils.redis.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by wmin on 2020/9/23 18:23
 * 管理redis的控制器
 *
 * @author wmin
 */
@RestController
@RequestMapping("/redis")
public class RedisController {

    @Autowired
    private RedisService redisService;

    @GetMapping("/set")
    public boolean redisSet() {
        AudioDto audioDto = new AudioDto();
        audioDto.setFileName("redis1");
        audioDto.setIndex(2);

        return redisService.set("redisAudioKey1", audioDto);
    }

    @GetMapping("/get")
    public AudioDto redisGet() {
        Object redisAudioKey = redisService.get("redisAudioKey");
        return (AudioDto) redisService.get("redisAudioKey1");
    }
}
