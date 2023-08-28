package com.imapcloud.nest.v2.service;


import com.imapcloud.nest.pojo.dto.UosNestStreamRefDTO;

import java.util.List;

public interface UosNestStreamRefService {

    void updateStreamIdByNestId(String nestId, String streamId, Integer streamUse);

    /**
     * 根据基站查询流ID
     * @param nestId    基站ID
     * @param streamUse 推流用途
     * @return  推流ID
     */
    String getStreamIdByNestId(String nestId, Integer streamUse);

    UosNestStreamRefDTO findByNestId(String nestId, Integer streamUse);

    List<UosNestStreamRefDTO> listByNestId(String nestId);

    void deleteNestStreamRef(String nestId);

}
