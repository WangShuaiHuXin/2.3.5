package com.imapcloud.nest.v2.manager.sql;

import com.imapcloud.nest.v2.manager.dataobj.in.DataEquipmentPintRelQueryInDO;
import com.imapcloud.nest.v2.manager.dataobj.in.DataEquipmentPointInDO;
import com.imapcloud.nest.v2.manager.dataobj.in.DataEquipmentPointQueryInDO;
import com.imapcloud.nest.v2.manager.dataobj.out.DataEquipmentPintRelQueryOutDO;
import com.imapcloud.nest.v2.manager.dataobj.out.DataEquipmentPointQueryOutDO;

import java.util.List;

public interface DataEquipmentPointManager {
    boolean save(DataEquipmentPointInDO inDO);

    boolean update(DataEquipmentPointInDO inDO);

    boolean deleteBatch(List<String> deletes);

    DataEquipmentPointQueryOutDO queryByCondition(DataEquipmentPointQueryInDO build);

    List<DataEquipmentPintRelQueryOutDO> queryRelByCondition(DataEquipmentPintRelQueryInDO build);
}
