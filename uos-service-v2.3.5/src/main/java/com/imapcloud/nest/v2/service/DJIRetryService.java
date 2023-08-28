package com.imapcloud.nest.v2.service;

import com.imapcloud.nest.v2.manager.dataobj.out.VideoPlayInfoOutDO;

public interface DJIRetryService {

    VideoPlayInfoOutDO retry(String streamId);

}
