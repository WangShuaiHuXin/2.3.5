package com.imapcloud.nest.common.exception;

import com.geoai.common.web.rest.Result;
import com.google.common.collect.Lists;
import com.imapcloud.nest.utils.RestRes;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Objects;

/**
 * @author wmin
 * 异常类处理
 * @deprecated at 2022/06/20，使用新架构
 */
@Deprecated
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
@Slf4j
public class OldGlobalExceptionHandler {
    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 处理自定义异常
     *
     * @param e
     * @return
     */
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(NestException.class)
    public RestRes handleNestException(NestException e) {
        log.error("handleNestException：", e);
        return RestRes.err(e.getCode(), e.getMsg());
    }

    private final List<String> objectNameList = Lists.newArrayList("dataScenePhotoReqVO",
            "editIndustryReq", "industryListReq", "editIndustryProblemReq", "industryProblemListReq",
            "industryInfoReqList", "industryProblemInfoReqList", "collectSumReq", "problemTrendReq",
            "problemReq", "AIRecognitionTaskReqVO");

    /**
     * 参数校验通不过的时候的异常
     *
     * @param e
     * @return
     */
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public Object handleMethodArgumentNotValidException(Exception e) {

        ObjectError objectError = null;
        if (e instanceof BindException) {
            objectError = ((BindException) e).getBindingResult().getAllErrors().get(0);
        }
        if (e instanceof MethodArgumentNotValidException) {
            objectError = ((MethodArgumentNotValidException) e).getBindingResult().getAllErrors().get(0);
        }
        if (Objects.nonNull(objectError)) {
            if (objectNameList.contains(objectError.getObjectName())) {
                return Result.error(objectError.getDefaultMessage());
            }
            return RestRes.err(objectError.getDefaultMessage());
        }
        return RestRes.err(e.getMessage());
    }

//    /**
//     * 兜底处理异常
//     */
//    @ResponseStatus(HttpStatus.OK)
//    @ExceptionHandler(Exception.class)
//    public RestRes handleException(Exception e) {
//        log.error("系统异常", e);
//        return RestRes.err();
//    }

    /**
     * 业务参数异常
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BizParameterException.class)
    public RestRes handleBizParameterException(BizParameterException e) {
        return RestRes.errorParam(e.getMessage());
    }

}
