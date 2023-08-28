package com.imapcloud.nest.v2.service.dto.out;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName DataAnalysisDetailDrawOutDTO.java
 * @Description DataAnalysisDetailDrawOutDTO
 * @createTime 2022年07月08日 15:41:00
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class DataAnalysisDetailDrawOutDTO implements Serializable {

    private Long centerDetailId;

    private String thumImageMarkPath;

    private String imageMarkPath;


}
