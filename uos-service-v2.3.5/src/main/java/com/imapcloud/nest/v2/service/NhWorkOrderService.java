package com.imapcloud.nest.v2.service;

import com.imapcloud.nest.v2.service.dto.in.*;
import com.imapcloud.nest.v2.service.dto.out.*;
import com.imapcloud.nest.v2.web.vo.req.NhOrderReportReqVO;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface NhWorkOrderService {
    NhQueryOrderOutDTO queryOrderList(NhQueryOrderInDTO inDTO);

    String addOrderInfo(NhOrderInfoInDTO infoInDTO);

    void editOrderInfo(NhOrderInfoInDTO infoInDTO);

    NhQueryDetailOutDTO queryDetail(String orderId);

    List<NhQueryVectorOutDTO> queryVertor(String orderId);

    void deleteVertor(String vertorId);

    void executeOrder(NhExecuteOrderInDTO inDTO);

    List<NhQueryDetailOutDTO.OutDtoProcess> executeProcessOrder(String orderId);

    void addPlanRel(String orderId, Integer planId);

    void deletedPlan(NhOrderPlanInDTO planDeleteReqVO);

    NhOrderPlanOutDTO listOrderPlan(String orderId, Long pageNum, Long pageSize);

    List<NhOrderPlanOptionOutDTO> listOrderPlanOption(String orderId);

    List<NhOrderMissionOptionOutDTO> listRecordsMissionOption(Integer planId);

    NhOrderPhotoOutDTO getAllPhotoByCondition(NhOrderPhotoInDTO inDTO);

    NhOrderVideoOutDTO getAllVideoByCondition(Integer recordId);

//    /**
//     * @deprecated 2.2.3，使用新接口{@link NhWorkOrderService#addPatrolReport(com.imapcloud.nest.v2.web.vo.req.NhOrderReportReqVO)}替代，将在后续版本删除
//     */
//    @Deprecated
//    void addPatrolReport(NhOrderReportReqVO reportReqVO, MultipartFile file);

    void addPatrolReport(NhOrderReportReqVO reportReqVO);

    void delPatrolReport(String reportId);

    List<NhOrderReportOutDTO> getPatrolReportList(String orderId);

    void exportPatrolReport(String reportId, HttpServletResponse response);
}
