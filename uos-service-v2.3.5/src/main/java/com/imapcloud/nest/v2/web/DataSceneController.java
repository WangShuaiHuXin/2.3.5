package com.imapcloud.nest.v2.web;

import com.geoai.common.core.bean.TrustedAccessTracerHolder;
import com.geoai.common.web.rest.Result;
import com.geoai.common.web.util.MessageUtils;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.service.DataScenePhotoService;
import com.imapcloud.nest.v2.service.dto.in.DataScenePhotoDTO;
import com.imapcloud.nest.v2.web.vo.req.DataScenePhotoReqVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * @author zhongtb
 * @version 1.0.0
 * @date  2022年07月08日 16:47:00
 */
@Slf4j
@ApiSupport(author = "zhongtaigbao@geoai.com", order = 2)
@Api(value = "分析统计-综合", tags = "分析统计-综合")
@RequestMapping("v2/dataScene")
@RestController
public class DataSceneController {

    @Resource
    private DataScenePhotoService dataScenePhotoService;

//    /**
//     * @deprecated 2.2.3，使用新接口{@link DataSceneController#pushSceneData(com.imapcloud.nest.v2.web.vo.req.DataScenePhotoReqVO)} 替代，将在后续版本删除
//     */
//    @Deprecated
//    @ApiOperationSupport(author = "zhongtaibaoo@geoai.com", order = 1)
//    @ApiOperation(value = "现场取证-上传截图", notes = "上传原图、记录路径到现场取证表")
//    @PostMapping("/uploadPic")
//    public Result<String> uploadPic(MultipartFile fileData) {
//        if (fileData == null) {
//            return Result.error(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_PLEASE_SELECT_THE_FILE.getContent()));
//        }
//        try (InputStream is = fileData.getInputStream()) {
//            String fileType = MinIoUnit.getFileType(is);
//            if (!DataConstant.IMAGE_TYPE_LIST.contains(fileType)) {
//                return Result.error(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FILE_TYPE_IS_NOT_AN_IMAGE.getContent()));
//            }
//        } catch (Exception e) {
//            return Result.error(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAIL_GET_THE_FILE_STREAM.getContent()));
//        }
//
//        String fileName = fileData.getOriginalFilename();
//        String accountId = TrustedAccessTracerHolder.get().getAccountId();
//        Long scenePhotoId = dataScenePhotoService.create(fileData, fileName, accountId);
//        return Result.ok(String.valueOf(scenePhotoId));
//    }

    @ApiOperationSupport(author = "zhongtaibaoo@geoai.com", order = 2)
    @ApiOperation(value = "现场取证-推送分析", notes = "更新数据到现场取证表，推送数据到数据分析中心")
    @PostMapping("/pushSceneData")
    public Result<String> pushSceneData(@Valid @RequestBody DataScenePhotoReqVO dataScenePhotoReqVO) {

        DataScenePhotoDTO.UpdateInfo updateInfo = new DataScenePhotoDTO.UpdateInfo();
        updateInfo.setAccountId(TrustedAccessTracerHolder.get().getAccountId());
        updateInfo.setAddr(dataScenePhotoReqVO.getAddr());
        updateInfo.setExecMissionId(dataScenePhotoReqVO.getExecMissionId());
        updateInfo.setLongitude(dataScenePhotoReqVO.getLongitude());
        updateInfo.setLatitude(dataScenePhotoReqVO.getLatitude());
        updateInfo.setTopicLevelId(Long.valueOf(dataScenePhotoReqVO.getTopicLevelId()));
        updateInfo.setIndustryType(dataScenePhotoReqVO.getIndustryType());
        updateInfo.setTopicProblemId(Long.valueOf(dataScenePhotoReqVO.getTopicProblemId()));

        updateInfo.setRecX(dataScenePhotoReqVO.getRecX());
        updateInfo.setRecY(dataScenePhotoReqVO.getRecY());
        updateInfo.setRecWidth(dataScenePhotoReqVO.getRecWidth());
        updateInfo.setRecHeight(dataScenePhotoReqVO.getRecHeight());
        updateInfo.setRelX(dataScenePhotoReqVO.getRelX());
        updateInfo.setRelY(dataScenePhotoReqVO.getRelY());
        updateInfo.setCutWidth(dataScenePhotoReqVO.getCutWidth());
        updateInfo.setCutHeight(dataScenePhotoReqVO.getCutHeight());
        updateInfo.setPicScale(dataScenePhotoReqVO.getPicScale());
        updateInfo.setImagePath(dataScenePhotoReqVO.getImagePath());
        boolean update = dataScenePhotoService.update(updateInfo);
        return update ? Result.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SUCCESS_UPDATE.getContent())
        ) : Result.error(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAIL_UPDATE.getContent()));
    }

}
