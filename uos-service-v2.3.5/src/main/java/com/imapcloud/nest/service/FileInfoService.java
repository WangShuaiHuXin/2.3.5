package com.imapcloud.nest.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.imapcloud.nest.model.DefectInfoEntity;
import com.imapcloud.nest.model.FileInfoEntity;
import com.imapcloud.nest.pojo.dto.FileInfoDto;
import com.imapcloud.nest.utils.RestRes;
import com.imapcloud.nest.v2.service.DataParseService;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 文件服务类
 *
 * @author: zhengxd
 * @create: 2020/12/15
 **/
public interface FileInfoService extends IService<FileInfoEntity> {

    /**
     * @deprecated 2.2.3，使用新接口{@link DataParseService#parseFlightTrackData(com.imapcloud.nest.v2.service.dto.in.VideoSrtInDTO)}替代，将在后续版本删除
     */
    @Deprecated
    Integer uploadSrt(String fileName,String filePath,Integer videoId);

    Long save(FileInfoDto fileInfoDto);

//    /**
//     * 此方法弃用在v2.2.3后弃用，若后续版本无异常则可以删除，若有异常则去掉该注释并写明方法
//     */
//    @Deprecated
//    void unPack(String uploadPath, String fileName, String unPackPath);
}
