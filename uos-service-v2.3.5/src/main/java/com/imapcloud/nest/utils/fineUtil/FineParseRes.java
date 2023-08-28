package com.imapcloud.nest.utils.fineUtil;

import com.imapcloud.nest.model.FineInspTowerEntity;
import com.imapcloud.nest.model.FineInspZipEntity;
import lombok.Data;

import java.util.List;

/**
 * @author wmin
 */
@Data
public class FineParseRes {
    private List<FineInspTowerEntity> fineInspTowerEntityList;
    private FineInspZipEntity fineInspZipEntity;
}
