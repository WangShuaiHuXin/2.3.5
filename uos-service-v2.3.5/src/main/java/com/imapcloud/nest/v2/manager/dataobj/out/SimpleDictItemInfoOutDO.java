package com.imapcloud.nest.v2.manager.dataobj.out;

import lombok.Data;

import java.io.Serializable;

/**
 * 字典项简要信息
 * @author Vastfy
 * @date 2022/7/25 15:04
 * @since 1.0.0
 */
@Data
public class SimpleDictItemInfoOutDO implements Serializable {

    private String name;

    private String value;

}
