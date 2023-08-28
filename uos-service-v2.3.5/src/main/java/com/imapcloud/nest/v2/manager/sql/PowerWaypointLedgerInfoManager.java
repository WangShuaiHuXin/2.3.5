package com.imapcloud.nest.v2.manager.sql;

import com.imapcloud.nest.v2.manager.dataobj.in.PowerEquipmentInDO;
import com.imapcloud.nest.v2.manager.dataobj.in.PowerWaypointLedgerInfoInDO;
import com.imapcloud.nest.v2.manager.dataobj.out.PowerWaypointLedgerInfoOutDO;
import com.imapcloud.nest.v2.manager.dataobj.out.PowerWaypointListInfoOutDO;

import java.util.List;
import java.util.Map;

public interface PowerWaypointLedgerInfoManager {
    void uploadWayPoint(List<PowerWaypointLedgerInfoInDO> dos, String orgCode, String map, String sub, String substa, String wholeUnit, String zipPath, Map<String, String> geoPos);

    PowerWaypointListInfoOutDO queryWaypointListByCondition(String orgCode, String deviceLayer, String unitLayer, String subRegion, String equipmentArea, String equipmentStatu, String componentStatu, Integer pageNo, Integer pageSize);

    void updateWaypointPmsId(String key, String value);

    public PowerWaypointListInfoOutDO queryWaypointListByOrg(String orgCode);

    void updateWaypointComponentId(String key, String value);

    /**
     * 通过行带你查询航点台账信息
     *
     * @param waypointIdList 路标id列表
     * @param orgCode 单位code
     * @return {@link List}<{@link PowerWaypointLedgerInfoOutDO}>
     */
    List<PowerWaypointLedgerInfoOutDO> selectByWaypointIdList(List<String> waypointIdList, String orgCode);

    void checkAndUpdatePmsId(PowerEquipmentInDO inDO);

    PowerWaypointLedgerInfoOutDO selectByWaypointId(String waypointId);

    public PowerWaypointLedgerInfoOutDO selectByWaypointStationId(String wayPointStationId);

    /**
     * 选择通过部件库id列表
     *
     * @param componentIdList 部件库id列表
     * @return {@link List}<{@link PowerWaypointLedgerInfoOutDO}>
     */
    List<PowerWaypointLedgerInfoOutDO> selectByComponentIdList(List<String> componentIdList);
}
