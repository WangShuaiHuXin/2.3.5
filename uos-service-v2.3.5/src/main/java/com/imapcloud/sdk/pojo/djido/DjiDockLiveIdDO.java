package com.imapcloud.sdk.pojo.djido;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class DjiDockLiveIdDO {

    private String dockVideoId;

    private String aircraftSelfVideoId;

    private String aircraftLiveVideoId;

}
