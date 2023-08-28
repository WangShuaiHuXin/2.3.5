package com.imapcloud.nest.pojo.dto;

import com.imapcloud.nest.model.NestMaintenanceProjectEntity;
import lombok.Data;

import java.util.List;

@Data
public class SaveNestMaintenanceProjectDto {

    private List<NestMaintenanceProjectEntity> entities;

    private Integer type;

}
