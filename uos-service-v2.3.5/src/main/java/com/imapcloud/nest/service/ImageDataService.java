package com.imapcloud.nest.service;

import com.imapcloud.nest.model.ImageDataEntity;
import com.baomidou.mybatisplus.extension.service.IService;
import com.imapcloud.nest.pojo.dto.DeleteTagDTO;
import com.imapcloud.nest.pojo.dto.AddTagsDTO;
import com.imapcloud.nest.pojo.dto.reqDto.ImageDataReqDto;
import com.imapcloud.nest.utils.PageUtils;
import com.imapcloud.nest.utils.RestRes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author daolin
 * @since 2020-11-16
 */
public interface ImageDataService extends IService<ImageDataEntity> {

    /**
     * 获取缩略图url分页列表
     * @param imageDataReqDto
     * @return
     */
    PageUtils getThumbnailPage(ImageDataReqDto imageDataReqDto);

    /**
     * 获取详情
     * @param id
     * @return
     */
    ImageDataEntity getInfoById(Integer id);

    /**
     * 修改图像数据信息
     * @param imageDataEntity
     * @return
     */
    RestRes updateImageData(ImageDataEntity imageDataEntity);

    /**
     * 批量删除图像数据
     * @param idList
     * @return
     */
    RestRes deleteImageData(List idList);

}
