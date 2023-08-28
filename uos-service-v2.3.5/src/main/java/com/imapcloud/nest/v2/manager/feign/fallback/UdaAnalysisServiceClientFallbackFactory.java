package com.imapcloud.nest.v2.manager.feign.fallback;

import com.geoai.common.core.bean.TrustedAccessTracerHolder;
import com.geoai.common.web.rest.Result;
import com.imapcloud.nest.v2.manager.dataobj.in.*;
import com.imapcloud.nest.v2.manager.dataobj.out.UdaAlTaskResponseOutDO;
import com.imapcloud.nest.v2.manager.dataobj.out.UdaAlgorithmRepoOutDO;
import com.imapcloud.nest.v2.manager.dataobj.out.UdaAnalysisOutDO;
import com.imapcloud.nest.v2.manager.feign.UdaAnalysisServiceClient;
import com.imapcloud.nest.v2.service.dto.out.TopicUDAProblemTypeConfigOutDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * UDA分析服务降级处理接口
 * @author Vastfy
 * @date 2022/11/02 15:03
 * @since 2.1.4
 */
@Component
public class UdaAnalysisServiceClientFallbackFactory extends AbstractFeignFallbackFactory<UdaAnalysisServiceClient> {

    @Override
    protected UdaAnalysisServiceClient doFallbackHandle(String errMessage) {
        return new UdaAnalysisServiceClientFallback(errMessage);
    }

    @Slf4j
    static class UdaAnalysisServiceClientFallback implements UdaAnalysisServiceClient {

        private final String errorMsg;

        UdaAnalysisServiceClientFallback(String errorMsg) {
            this.errorMsg = errorMsg;
        }


        @Override
        public Result<List<UdaAlgorithmRepoOutDO>> getUdaGrantedFunctionsByOrg(OrgAlgorithmQueryInDO params) {
            log.error("获取单位[{}]已授权识别功能信息失败，原因：{}", params, errorMsg);
            return Result.error(errorMsg);
        }

        @Override
        public Result<List<UdaAlgorithmRepoOutDO>> getUdaGrantedFunctions(Integer recDataType) {
            log.error("获取账号[{}]已授权识别功能信息失败，原因：{}", TrustedAccessTracerHolder.get().getUsername(), errorMsg);
            return Result.error(errorMsg);
        }

        @Override
        public Result<UdaAlTaskResponseOutDO> createAndExecTask(UdaAnalysisTaskInDO body) {
            log.error("创建AI识别任务失败，失败数据：{}，原因：{}", body, errorMsg);
            return Result.error(errorMsg);
        }

        @Override
        public Result<Void> pauseTask(String taskId) {
            log.error("暂停AI识别任务失败，任务ID：{}，原因：{}", taskId, errorMsg);
            return Result.error(errorMsg);
        }

        @Override
        public Result<Void> resumeTask(String taskId) {
            log.error("继续AI识别任务失败，任务ID：{}，原因：{}", taskId, errorMsg);
            return Result.error(errorMsg);
        }

        @Override
        public Result<List<TopicUDAProblemTypeConfigOutDTO>> queryUDAProblemTypeByFunction(String functionId) {
            log.error("获取账号[{}]UDA问题类型信息失败，原因：{}", TrustedAccessTracerHolder.get().getUsername(), errorMsg);
            return Result.error(errorMsg);
        }

        @Override
        public Result<UdaAlTaskResponseOutDO> openAiStreaming(AIStreamingOpenInDO body) {
            log.error("开启视频流AI识别失败，原因：{}", errorMsg);
            return Result.error(errorMsg);
        }

        @Override
        public Result<Void> exitAiStreaming(AIStreamingExitInDO body) {
            log.error("退出视频流AI识别失败，原因：{}", errorMsg);
            return Result.error(errorMsg);
        }

        @Override
        public Result<List<UdaAlTaskResponseOutDO.AuthError>> getOrgVideoAuthExpiredList(OrgVideoRecFunQueryInDO params) {
            log.error("查询单位视频AI识别功能过期信息失败，参数：{}，原因：{}", params, errorMsg);
            return Result.error(errorMsg);
        }

        @Override
        public Result<String> addPictureTask(UdaAnalysisInDO.AddPictureTaskInDO addPictureTask) {
            log.error("#UdaAnalysisServiceClientFallback.addPictureTask# param:{}", addPictureTask);
            return Result.error(errorMsg);
        }

        @Override
        public Result<UdaAnalysisOutDO.CheckPictureTaskOutDO> checkPictureTask(UdaAnalysisInDO.CheckPictureTaskInDO checkPictureTaskInDO) {
            log.error("#UdaAnalysisServiceClientFallback.checkPictureTask# param:{}", checkPictureTaskInDO);
            return Result.error(errorMsg);
        }
    }

}
