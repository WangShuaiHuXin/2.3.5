package com.imapcloud.nest.pojo.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by wmin on 2020/9/16 14:39
 * 录音传输类
 */
@Data
public class AudioDto implements Serializable {
    private String fileName;
    private Integer index;
}
