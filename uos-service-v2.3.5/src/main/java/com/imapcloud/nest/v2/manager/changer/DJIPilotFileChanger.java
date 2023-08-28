package com.imapcloud.nest.v2.manager.changer;

import com.imapcloud.nest.v2.manager.dataobj.in.DjiPilotFileUploadCallBackDO;
import com.imapcloud.sdk.pojo.djido.FileUploadCallBackDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


@Mapper(componentModel = "spring")
public interface DJIPilotFileChanger {

    DJIPilotFileChanger INSTANCES = Mappers.getMapper(DJIPilotFileChanger.class);

    /**
     * 转换入口
     * @param in
     * @return
     */
    FileUploadCallBackDO.FileUpload change(DjiPilotFileUploadCallBackDO in);

}
