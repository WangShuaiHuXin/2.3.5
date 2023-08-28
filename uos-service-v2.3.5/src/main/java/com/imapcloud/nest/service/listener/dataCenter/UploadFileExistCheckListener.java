package com.imapcloud.nest.service.listener.dataCenter;

import com.geoai.common.core.constant.SymbolConstants;
import com.geoai.common.web.util.MessageUtils;
import com.imapcloud.nest.common.exception.NestException;
import com.imapcloud.nest.common.listener.AbstractEventListener;
import com.imapcloud.nest.pojo.dto.FileInfoDto;
import com.imapcloud.nest.service.UploadService;
import com.imapcloud.nest.service.event.dataCenter.UploadFileExistCheckEvent;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.manager.rest.FileManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.Optional;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName FileCheckListener.java
 * @Description FileCheckListener
 * @createTime 2022年03月31日 15:12:00
 */
@Slf4j
@Service
public class UploadFileExistCheckListener extends AbstractEventListener<UploadFileExistCheckEvent> {

    @Resource
    private UploadService uploadService;

    @Resource
    private FileManager fileManager;
    /**
     * 消息监听-处理
     *
     * @param uploadFileExistCheckEvent 消息事件
     */
    @Override
    @EventListener
    public void eventListener(UploadFileExistCheckEvent uploadFileExistCheckEvent) {
        log.info("文件检查监听:{}", uploadFileExistCheckEvent.getSource().toString());
        FileInfoDto fileInfoDto = uploadFileExistCheckEvent.getSource();
        Optional.ofNullable(fileInfoDto)
                .filter(r -> {
                    // 兼容旧的存储方式
                    String filepath = r.getFilePath();
                    if(filepath.startsWith(SymbolConstants.SLASH_LEFT)){
                        filepath = filepath.substring(1);
                    }
                    return filepath.startsWith("data/origin") || filepath.startsWith("origin");
                })
                .ifPresent(x -> {
                    if (!fileManager.checkFileExists(x.getFilePath())) {
                        // 删除upload记录
                        uploadService.deleteByFilePath(x.getFilePath());
                        throw new NestException(String.format("%s," + MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FILE_NOT_EXIST.getContent()), x.getFileName()));
                    }
        });
        log.info("文件检查监听:{}", "end");
    }

}
