package com.imapcloud.nest.common.aspect;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONArray;
import com.geoai.common.core.bean.ITrustedAccessTracer;
import com.geoai.common.core.bean.TrustedAccessTracerHolder;
import com.imapcloud.nest.common.annotation.SysLog;
import com.imapcloud.nest.common.annotation.SysLogIgnoreParam;
import com.imapcloud.nest.common.annotation.SysLogIgnoreResult;
import com.imapcloud.nest.model.SysLogEntity;
import com.imapcloud.nest.utils.TraceUuidUtil;
import com.imapcloud.nest.utils.TrimArgsUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author wmin
 */
@Slf4j
@Aspect
@Component
public class SysLogAspect {

    @Pointcut("@annotation(com.imapcloud.nest.common.annotation.SysLog)")
    public void logPointCut() {

    }

    @Pointcut("execution(public * com.imapcloud.nest.controller.*.*(..))")
    public void controllerMethodPointCut() {

    }

    @Pointcut("execution(public * com.imapcloud.nest.v2.web.*.*(..)) && !execution(public * com.imapcloud.nest.v2.web.UosNestController.resetCameraStream(..))")
    public void v2ControllerMethodPointCut() {

    }

    @Pointcut("@annotation(com.imapcloud.nest.common.annotation.Trace)")
    public void mqttLogPointCut() {

    }

    @Before("mqttLogPointCut()")
    public void before() {
        TraceUuidUtil.createTraceUuid();
    }


    @Around("controllerMethodPointCut() || v2ControllerMethodPointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        long beginTime = System.currentTimeMillis();
        Object result = null;
        String exception = "";
        SysLogEntity sysLogEntity = getSysLogEntity(point);
        try {
            log.info("{}, accountId={}, param={}", sysLogEntity.getMethodName(), sysLogEntity.getRequestAccount(), sysLogEntity.getMethodParam());
            result = point.proceed(TrimArgsUtil.trimArgs(point));
        } catch (Throwable throwable) {
            Throwable mostSpecificCause = NestedExceptionUtils.getMostSpecificCause(throwable);
            log.error("切面异常:", mostSpecificCause);
            throw throwable;
        } finally {
            long time = System.currentTimeMillis() - beginTime;
            try {
                if (ignoreRes(point)) {
                    log.info("{}, accountId={}, param={}, result={}, runtime={}ms", sysLogEntity.getMethodName(), sysLogEntity.getRequestAccount(), sysLogEntity.getMethodParam(), null, time);
                }else {
                    log.info("{}, accountId={}, param={}, result={}, runtime={}ms", sysLogEntity.getMethodName(), sysLogEntity.getRequestAccount(), sysLogEntity.getMethodParam(), JSONUtil.toJsonStr(result), time);
                }
            } catch (Exception e) {
                log.info("{}, accountId={}, param={}, result={}, runtime={}ms", sysLogEntity.getMethodName(), sysLogEntity.getRequestAccount(), sysLogEntity.getMethodParam(), result, time);
            }
        }
        return result;
    }

    private SysLogEntity getSysLogEntity(ProceedingJoinPoint point) {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        SysLogEntity sysLogEntity = new SysLogEntity();
        SysLog sysLog = method.getAnnotation(SysLog.class);
        if (sysLog != null) {
            //注解上的描述
            sysLogEntity.setRemarks(sysLog.value());
        } else {
            sysLogEntity.setRemarks("");
        }

        String className = point.getTarget().getClass().getName();
        String methodName = signature.getName();
        sysLogEntity.setMethodName(className + "." + methodName + "()");
        Object[] args = point.getArgs();
        String param;
        try {
            //参数如果是文件的话就不获取参数，否则在Json转换的时候的时候会报java.lang.OutOfMemoryError: Java heap space
            SysLogIgnoreParam sysLogIgnoreParam = method.getAnnotation(SysLogIgnoreParam.class);
            if (sysLogIgnoreParam != null) {
                param = sysLogIgnoreParam.value();
            } else {
                List<Object> collect = Arrays.stream(args).filter(a -> !(a instanceof ServletResponse) && !(a instanceof ServletRequest) && !(a instanceof BindingResult)).collect(Collectors.toList());
                param = JSONArray.toJSONString(collect);
            }
        } catch (Exception e) {
            param = "";
        }

        sysLogEntity.setMethodParam(param);
        ITrustedAccessTracer trustedAccessTracer = TrustedAccessTracerHolder.get();
        if (Objects.nonNull(trustedAccessTracer)) {
            String account = trustedAccessTracer.getUsername();
            sysLogEntity.setRequestAccount(account);
            sysLogEntity.setOrgCode(trustedAccessTracer.getOrgCode());
        }
        sysLogEntity.setExecTime(LocalDateTime.now());
        return sysLogEntity;
    }

    //保存日志
    private void saveSysLog(ProceedingJoinPoint point, long time, String exception, Object result) {
        ITrustedAccessTracer trustedAccessTracer = TrustedAccessTracerHolder.get();
        if (Objects.isNull(trustedAccessTracer)) {
            return;
        }
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        SysLogEntity sysLogEntity = new SysLogEntity();
        SysLogIgnoreResult sysLogIgnoreResult = method.getAnnotation(SysLogIgnoreResult.class);
        if (sysLogIgnoreResult != null) {
            sysLogEntity.setResult(result);
        }
        sysLogEntity.setTimeLength(time);
        sysLogEntity.setException(exception);
        SysLog sysLog = method.getAnnotation(SysLog.class);
        if (sysLog != null) {
            //注解上的描述
            sysLogEntity.setRemarks(sysLog.value());
        } else {
            sysLogEntity.setRemarks("");
        }

        String className = point.getTarget().getClass().getName();
        String methodName = signature.getName();
        sysLogEntity.setMethodName(className + "." + methodName + "()");
        Object[] args = point.getArgs();
        String param;
        try {
            //参数如果是文件的话就不获取参数，否则在Json转换的时候的时候会报java.lang.OutOfMemoryError: Java heap space
            SysLogIgnoreParam sysLogIgnoreParam = method.getAnnotation(SysLogIgnoreParam.class);
            if (sysLogIgnoreParam != null) {
                param = sysLogIgnoreParam.value();
            } else {
                List<Object> collect = Arrays.stream(args).filter(a -> !(a instanceof ServletResponse) && !(a instanceof ServletRequest) && !(a instanceof BindingResult)).collect(Collectors.toList());
                param = JSONArray.toJSONString(collect);
            }
        } catch (Exception e) {
            param = "";
        }

        sysLogEntity.setMethodParam(param);

        String account = trustedAccessTracer.getUsername();

        // 查询用户所在单位
        String orgCode = trustedAccessTracer.getOrgCode();

        sysLogEntity.setRequestAccount(account);
        sysLogEntity.setOrgCode(orgCode);
        sysLogEntity.setExecTime(LocalDateTime.now());
        sysLogEntity.setExecDateMilli(LocalDate.now().atStartOfDay().toInstant(ZoneOffset.of("+8")).toEpochMilli());
//        log.info(JSON.toJSONString(sysLogEntity));
    }

    private boolean ignoreRes(ProceedingJoinPoint point) {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        SysLogIgnoreResult sysLogIgnoreResult = method.getAnnotation(SysLogIgnoreResult.class);
        return Objects.nonNull(sysLogIgnoreResult);
    }
}

