package com.imapcloud.nest.pojo.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author wmin
 */
@Data
@Accessors(chain = true)
public class TaskLabelDTO {

    private Integer id;
    @NotNull
    private Integer taskId;
    @NotNull
    private Double lat;
    @NotNull
    private Double lng;
    @NotNull
    private Double alt;
    @NotBlank
    private String remarks;
    @NotNull
    private Double top;
    @NotNull
    private Double left;
}
