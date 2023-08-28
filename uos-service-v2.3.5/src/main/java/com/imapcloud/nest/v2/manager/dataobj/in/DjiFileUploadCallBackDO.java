package com.imapcloud.nest.v2.manager.dataobj.in;

import com.imapcloud.sdk.pojo.constant.NestTypeEnum;
import com.imapcloud.sdk.pojo.djido.DjiCommonDO;
import com.imapcloud.sdk.pojo.djido.FileUploadCallBackDO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class DjiFileUploadCallBackDO {

    private DjiCommonDO<FileUploadCallBackDO> doDjiCommonDO;
    private Boolean success;
    private String errMsg;
    private NestTypeEnum nestTypeEnum;
    private DjiPilotFileUploadCallBackDO djiPilotFileUploadCallBackDO;

}
