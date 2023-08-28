package com.imapcloud.nest.service.listener.dataCenter;

import com.imapcloud.nest.common.exception.NestException;
import com.imapcloud.nest.common.listener.AbstractEventListener;
import com.imapcloud.nest.enums.UploadTypeEnum;
import com.imapcloud.nest.model.UploadEntity;
import com.imapcloud.nest.service.event.dataCenter.UploadFileBeforeEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.regex.Pattern;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName UploadFileCheckListener.java
 * @Description UploadFileCheckListener
 * @createTime 2022年03月31日 17:12:00
 */
@Slf4j
@Service
public class UploadFileBeforeCheckListener extends AbstractEventListener<UploadFileBeforeEvent> {

    public final String PHOTO = "000";

    public final String VIDEO = "001";

    public final String ORTHOR = "002";

    public final String POINTCLOUD = "003";

    public final String TILT = "004";

    public final String VECTOR = "005";

    public final String PANORAMA = "006";

    public final String POLLUTION_GRID = "006";

    /**
     * 消息监听-处理
     *
     * @param uploadFileBeforeEvent 消息事件
     */
    @Override
    @EventListener
    public void eventListener(UploadFileBeforeEvent uploadFileBeforeEvent) {
        log.info("文件检查监听:{}","start");
        UploadEntity uploadEntity = uploadFileBeforeEvent.getSource();

        Optional.ofNullable(uploadEntity).ifPresent(uploadEntity1->{
            switch(UploadTypeEnum.matchCode(uploadEntity1.getCode())){
                case PHOTO : {
                    log.info("文件类型检查:{}-{}",UploadTypeEnum.PHOTO.getDesc(),PHOTO);
                    String pattern  = ".*(\\.jpg|\\.jpeg|\\.png|\\.tiff|\\.tif|\\.gif)$";
                    if (uploadEntity1.getFileName() == null || !Pattern.matches(pattern,uploadEntity1.getFileName().toLowerCase())) {
                        throw new NestException(String.format("非照片类型文件(%s)无法导入！请检查", uploadEntity1.getFileName()));
                    }
                    break;
                }
                case VIDEO : {
                    //预留VIDEO口子，暂不做校验
                    log.info("文件类型检查:{}-{}",UploadTypeEnum.VIDEO.getDesc(),VIDEO);
                    break;
                }
                case ORTHOR : {
                    //预留ORTHOR口子，暂不做校验
                    log.info("文件类型检查:{}",UploadTypeEnum.ORTHOR.getDesc(),ORTHOR);
                    break;
                }
                case POINTCLOUD : {
                    //预留POINTCLOUD口子，暂不做校验
                    log.info("文件类型检查:{}",UploadTypeEnum.POINTCLOUD.getDesc(),POINTCLOUD);
                    break;
                }
                default:break;
            }
        });
    }

}