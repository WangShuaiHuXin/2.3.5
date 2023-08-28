package com.imapcloud.nest.v2.manager.feign;

import com.geoai.common.web.rest.Result;
import com.imapcloud.nest.v2.manager.dataobj.in.*;
import com.imapcloud.nest.v2.manager.dataobj.out.UdaAlTaskResponseOutDO;
import com.imapcloud.nest.v2.manager.dataobj.out.UdaAlgorithmRepoOutDO;
import com.imapcloud.nest.v2.manager.dataobj.out.UdaAnalysisOutDO;
import com.imapcloud.nest.v2.manager.feign.config.TokenRelayConfiguration;
import com.imapcloud.nest.v2.manager.feign.fallback.UdaAnalysisServiceClientFallbackFactory;
import com.imapcloud.nest.v2.service.dto.out.TopicUDAProblemTypeConfigOutDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(contextId = "uda-analysis-service-client", name = "uda-analysis-service", path = "analysis-api",
        configuration = TokenRelayConfiguration.class, fallbackFactory = UdaAnalysisServiceClientFallbackFactory.class)
public interface UdaAnalysisServiceClient {

    /**
     * 获取识别功能列表（根据单位）
     */
    @GetMapping("uosDiscernTask/functionTreeByOrgCode")
    Result<List<UdaAlgorithmRepoOutDO>> getUdaGrantedFunctionsByOrg(@SpringQueryMap OrgAlgorithmQueryInDO params);

    /**
     * 获取识别功能列表（登录用户）
     */
    @GetMapping("uosDiscernTask/functionTree")
    Result<List<UdaAlgorithmRepoOutDO>> getUdaGrantedFunctions(@RequestParam Integer recDataType);

    /**
     * 创建并执行分析任务
     */
    @PostMapping("uosDiscernTask/execTask")
    Result<UdaAlTaskResponseOutDO> createAndExecTask(@RequestBody UdaAnalysisTaskInDO body);

    /**
     * 暂停执行分析任务
     */
    @PostMapping("uosDiscernTask/stopTask")
    Result<Void> pauseTask(@RequestParam("taskId") String taskId);

    /**
     * 继续执行分析任务
     */
    @PostMapping("uosDiscernTask/runTaskAgain")
    Result<Void> resumeTask(@RequestParam("taskId") String taskId);

    /**
     * 获取UDA问题类型列表（登录用户）
     */
    @PostMapping("uosDiscernTask/queryUDAProblemTypeByFunction")
    Result<List<TopicUDAProblemTypeConfigOutDTO>> queryUDAProblemTypeByFunction(@RequestParam("functionId") String functionId);

    /**
     * 开启视频流AI识别任务
     * @param body  请求调整
     * @return  结果
     */
    @PostMapping("uosDiscernTask/video/open")
    Result<UdaAlTaskResponseOutDO> openAiStreaming(@RequestBody AIStreamingOpenInDO body);

    /**
     * 退出视频流AI识别任务
     * @param body  请求调整
     * @return  结果
     */
    @PostMapping("uosDiscernTask/video/exit")
    Result<Void> exitAiStreaming(@RequestBody AIStreamingExitInDO body);

    /**
     * 获取单位授权过期列表
     */
    @GetMapping("uosDiscernTask/video/authErrorList")
    Result<List<UdaAlTaskResponseOutDO.AuthError>> getOrgVideoAuthExpiredList(@SpringQueryMap OrgVideoRecFunQueryInDO params);

    /**
     * 添加图片任务
     *
     * @param addPictureTask 添加图片任务
     * @return {@link Result}<{@link String}>
     */
    @PostMapping(value = "uosDiscernTask/addPictureTask")
    Result<String> addPictureTask(@RequestBody UdaAnalysisInDO.AddPictureTaskInDO addPictureTask);

    /**
     * 添加图片前 算法授权检测
     *
     * @param checkPictureTaskInDO 检查图片任务
     * @return {@link Result}<{@link UdaAnalysisOutDO.CheckPictureTaskOutDO}>
     */
    @PostMapping(value = "uosDiscernTask/checkPictureTask")
    Result<UdaAnalysisOutDO.CheckPictureTaskOutDO> checkPictureTask(@RequestBody UdaAnalysisInDO.CheckPictureTaskInDO checkPictureTaskInDO);
}


