package com.imapcloud.nest.v2.service;

import com.geoai.common.core.bean.PageResultInfo;
import com.imapcloud.nest.v2.service.dto.in.AirspaceManageInDTO;
import com.imapcloud.nest.v2.service.dto.in.AirspacePageInDTO;
import com.imapcloud.nest.v2.service.dto.in.AirspaceUploadFileInDTO;
import com.imapcloud.nest.v2.service.dto.out.AirspaceManageAirCoorOutDTO;
import com.imapcloud.nest.v2.service.dto.out.AirspaceManageOutDTO;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @Classname AirspaceManageService
 * @Description 空域管理服务
 * @Date 2023/3/8 17:59
 * @Author Carnival
 */
public interface AirspaceManageService {


    /**
     * 申请空域
     */
    String addAirspace(AirspaceManageInDTO inDTO);


    /**
     * 批量删除空域域
     */
    Boolean deleteBatchAirspace(List<String> airspaceIds);


    /**
     * 分页查询空域列表
     */
    PageResultInfo<AirspaceManageOutDTO> pageAirspaceList(AirspacePageInDTO inDTO);

//    /**
//     * 上传批复文件
//     * @deprecated 2.2.3，使用接口{@link AirspaceManageService#saveApprovalFile(com.imapcloud.nest.v2.service.dto.in.AirspaceUploadFileInDTO)}替代
//     */
//    @Deprecated
//    String uploadApprovalFile(AirspaceUploadFileInDTO inDTO);

    /**
     * 保存批复文件
     */
    String saveApprovalFile(AirspaceUploadFileInDTO inDTO);

    /**
     * 上传边界范围
     */
    AirspaceManageAirCoorOutDTO uploadAirCoor(MultipartFile file);

//    /**
//     * 上传范围截图
//     * @deprecated 2.2.3，将在后续版本删除
//     */
//    @Deprecated
//    String uploadPhoto(MultipartFile file);

    /**
     * 下载申请单
     */
    void exportAirspace(HttpServletResponse response, String airspaceId);

    /**
     * 下载批复文件
     */
    void exportApprovalFile(HttpServletResponse response, String airspaceId);

    /**
     * 数据鉴权
     * @param airspaceId
     * @param orgCode
     * @return
     */
    int selectNum(String airspaceId, String orgCode);
}
