package com.imapcloud.nest.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.geoai.common.core.bean.TrustedAccessTracerHolder;
import com.imapcloud.nest.mapper.ImageDataMapper;
import com.imapcloud.nest.model.ImageDataEntity;
import com.imapcloud.nest.pojo.dto.AddTagsDTO;
import com.imapcloud.nest.pojo.dto.DeleteTagDTO;
import com.imapcloud.nest.pojo.dto.reqDto.ImageDataReqDto;
import com.imapcloud.nest.service.ImageDataService;
import com.imapcloud.nest.utils.*;
import com.imapcloud.nest.v2.manager.dataobj.out.OrgSimpleOutDO;
import com.imapcloud.nest.v2.manager.rest.UosOrgManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author daolin
 * @since 2020-11-16
 */
@Service
@Slf4j
public class ImageDataServiceImpl extends ServiceImpl<ImageDataMapper, ImageDataEntity> implements ImageDataService {

    @Resource
    private UosOrgManager uosOrgManager;

    /**
     * 获取缩略图分页列表
     *
     * @param imageDataReqDto
     * @return
     */
    @Override
    public PageUtils getThumbnailPage(ImageDataReqDto imageDataReqDto) {
        String orgCode = TrustedAccessTracerHolder.get().getOrgCode();
        Map<String, Object> params = new HashMap<>(2);
        params.put("page", imageDataReqDto.getCurrentPageNo());
        params.put("limit", imageDataReqDto.getCurrentPageSize());
        IPage<ImageDataReqDto> totalPage = baseMapper.getThumbnailPage(new Query<ImageDataReqDto>().getPage(params), imageDataReqDto.getName(),
                orgCode, imageDataReqDto.getStartTime(), imageDataReqDto.getEndTime(), imageDataReqDto.getRegion());
        if(!CollectionUtils.isEmpty(totalPage.getRecords())){
            List<String> orgCodes = totalPage.getRecords().stream().map(ImageDataReqDto::getOrgCode).distinct().collect(Collectors.toList());
            List<OrgSimpleOutDO> orgInfos = uosOrgManager.listOrgInfos(orgCodes);
            if(!CollectionUtils.isEmpty(orgInfos)){
                Map<String, String> map = orgInfos.stream().collect(Collectors.toMap(OrgSimpleOutDO::getOrgCode, OrgSimpleOutDO::getOrgName));
                totalPage.getRecords().forEach(r -> r.setUnitName(map.get(r.getOrgCode())));
            }
        }
        return new PageUtils(totalPage);
    }

    @Override
    public ImageDataEntity getInfoById(Integer id) {
        ImageDataEntity entity = baseMapper.getInfoById(id);
        if(Objects.nonNull(entity)){
            Optional<OrgSimpleOutDO> optional = uosOrgManager.getOrgInfo(entity.getOrgCode());
            optional.ifPresent(r -> entity.setUnitName(r.getOrgName()));
        }
        return entity;
    }

    @Override
    public RestRes updateImageData(ImageDataEntity imageDataEntity) {
        imageDataEntity.setModifyTime(LocalDateTime.now());
        this.updateById(imageDataEntity);
        return RestRes.ok();
    }

    @Override
    public RestRes deleteImageData(List idList) {
        baseMapper.deleteByIdList(idList);
        return RestRes.ok();
    }

}
