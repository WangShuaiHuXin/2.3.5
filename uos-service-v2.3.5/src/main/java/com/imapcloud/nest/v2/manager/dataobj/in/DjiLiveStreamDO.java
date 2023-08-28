package com.imapcloud.nest.v2.manager.dataobj.in;

import com.imapcloud.sdk.pojo.djido.DjiDockLiveCapacityStateDO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class DjiLiveStreamDO {

    private DjiDockLiveCapacityStateDO.LiveCapacity liveCapacity;

    private DjiDockLiveCapacityStateDO.LiveCapacity oldLiveCapacity;

    private String nestUuid;
}
