package com.imapcloud.nest.common.aspect;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.geoai.common.core.bean.ITrustedAccessTracer;
import com.geoai.common.core.bean.TrustedAccessTracerHolder;
import com.geoai.common.web.rest.Result;
import com.imapcloud.nest.common.annotation.NestCodeRecord;
import com.imapcloud.nest.common.annotation.NestId;
import com.imapcloud.nest.common.annotation.NestUUID;
import com.imapcloud.nest.common.netty.ws.ChannelService;
import com.imapcloud.nest.enums.WebSocketTopicEnum;
import com.imapcloud.nest.utils.WebSocketRes;
import com.imapcloud.nest.v2.manager.dataobj.in.ListDictItemInfoInDO;
import com.imapcloud.nest.v2.manager.dataobj.out.AccountOutDO;
import com.imapcloud.nest.v2.manager.dataobj.out.DictItemInfoOutDO;
import com.imapcloud.nest.v2.manager.feign.AccountServiceClient;
import com.imapcloud.nest.v2.manager.feign.BaseServiceClient;
import com.imapcloud.nest.v2.service.BaseNestService;
import com.imapcloud.nest.v2.service.NestCodeOperationRecordsService;
import com.imapcloud.nest.v2.service.dto.in.NestCodeOperationInDTO;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Aspect
@Component
public class NestCodeRecordsAspect {

    @Resource
    private NestCodeOperationRecordsService nestCodeOperationRecordsService;

    @Resource
    private BaseNestService baseNestService;

    @Resource
    private AccountServiceClient accountServiceClient;

    @Resource
    private BaseServiceClient baseServiceClient;

    @Pointcut("@annotation(com.imapcloud.nest.common.annotation.NestCodeRecord)")
    public void nestCodeRecord() {

    }

    @Before("nestCodeRecord()")
    public void before(JoinPoint joinPoint) {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        String language = request.getHeader("Accept-Language");
        String methodType = request.getMethod();
        ITrustedAccessTracer trustedAccessTracer = TrustedAccessTracerHolder.get();
        String accountId = trustedAccessTracer.getAccountId();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        NestCodeRecord nestCodeRecord = method.getAnnotation(NestCodeRecord.class);
        String[] nestCodes = nestCodeRecord.value();
        Object[] args = joinPoint.getArgs();
        Param param = buildNestIdOrNestUuid(method, args, methodType);
        pushByWs(param, nestCodes, accountId, language);
        saveOperationRecords(param, nestCodes, accountId);
    }

    public Param buildNestIdOrNestUuid(Method method, Object[] args, String methodType) {
        Param param = new Param();
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        for (int i = 0; i < parameterAnnotations.length; i++) {
            Object arg = args[i];
            Class<?> clazz = arg.getClass();
            Annotation[] parameterAnnotation = parameterAnnotations[i];
            long pathVarAnnoCount = Arrays.stream(parameterAnnotation).filter(a -> Objects.equals(a.annotationType(), PathVariable.class)).count();
            long nestIdAnnoCount = Arrays.stream(parameterAnnotation).filter(a -> Objects.equals(a.annotationType(), NestId.class)).count();
            long nestUuidAnnoCount = Arrays.stream(parameterAnnotation).filter(a -> Objects.equals(a.annotationType(), NestUUID.class)).count();
            long reqBodyAnnoCount = Arrays.stream(parameterAnnotation).filter(a -> Objects.equals(a.annotationType(), RequestBody.class)).count();
            Optional<Annotation> nestIdAnnoOpt = Arrays.stream(parameterAnnotation).filter(a -> Objects.equals(a.annotationType(), NestId.class)).findFirst();
            Optional<Annotation> nestUuidAnnoOpt = Arrays.stream(parameterAnnotation).filter(a -> Objects.equals(a.annotationType(), NestUUID.class)).findFirst();
            if ("GET".equals(methodType)) {
                if (nestIdAnnoCount > 0) {
                    if (String.class.equals(clazz)) {
                        param.addNestId(String.valueOf(arg));
                    }
                }
                if (nestUuidAnnoCount > 0) {
                    if (String.class.equals(clazz)) {
                        param.addUuid(String.valueOf(arg));
                    }
                }

            }

            if (pathVarAnnoCount > 0) {
                if (nestIdAnnoCount > 0) {
                    if (String.class.equals(clazz)) {
                        param.addNestId(String.valueOf(arg));
                    }
                }
                if (nestUuidAnnoCount > 0) {
                    if (String.class.equals(clazz)) {
                        param.addUuid(String.valueOf(arg));
                    }
                }

            }
            if (reqBodyAnnoCount > 0) {
                String packageName = clazz.getPackage().getName();
                if (packageName.contains("com.imapcloud") && !clazz.isArray()) {
                    try {
                        Field[] declaredFields = clazz.getDeclaredFields();
                        for (Field field : declaredFields) {
                            field.setAccessible(true);
                            NestId nestIdAnno = field.getAnnotation(NestId.class);
                            NestUUID nestUuidAnno = field.getAnnotation(NestUUID.class);
                            if (Objects.nonNull(nestIdAnno)) {
                                if (nestIdAnno.more()) {
                                    Object o1 = field.get(arg);
                                    List newArgs = (List) o1;
                                    for (int j = 0; j < newArgs.size(); j++) {
                                        Object o = newArgs.get(j);
                                        if (String.class.equals(o.getClass())) {
                                            param.addNestId(String.valueOf(o));
                                        }
                                    }
                                } else {
                                    param.addNestId((String) field.get(arg));
                                }
                            }
                            if (Objects.nonNull(nestUuidAnno)) {
                                if (nestUuidAnno.more()) {
                                    Object o1 = field.get(arg);
                                    List newArgs = (List) o1;
                                    for (int j = 0; j < newArgs.size(); j++) {
                                        Object o = newArgs.get(j);
                                        if (String.class.equals(o.getClass())) {
                                            param.addUuid(String.valueOf(o));
                                        }
                                    }
                                } else {
                                    field.setAccessible(true);
                                    param.addUuid((String) field.get(arg));
                                }

                            }
                        }
                    } catch (IllegalAccessException e) {
                        System.out.println("没有nestId");
                    }
                }

                try {
                    boolean isList = List.class.isAssignableFrom(arg.getClass());
                    if (isList) {
                        List list = (List) arg;
                        boolean annoMore = (nestIdAnnoOpt.isPresent() && ((NestId) nestIdAnnoOpt.get()).more())
                                || (nestUuidAnnoOpt.isPresent() && ((NestUUID) nestUuidAnnoOpt.get()).more());

                        if (annoMore) {
                            for (int j = 0; j < list.size(); j++) {
                                Object o = list.get(j);
                                Class<?> oClazz = o.getClass();
                                if (String.class.equals(oClazz)) {
                                    if (nestIdAnnoOpt.isPresent()) {
                                        param.addNestId(String.valueOf(o));
                                    }
                                    if (nestUuidAnnoOpt.isPresent()) {
                                        param.addUuid(String.valueOf(o));
                                    }

                                }
                            }
                        }
                        for (int j = 0; j < list.size(); j++) {
                            Object o = list.get(j);
                            Class<?> oClazz = o.getClass();
                            String oPackageName = oClazz.getPackage().getName();
                            if (oPackageName.contains("com.imapcloud") && !oClazz.isPrimitive() && !oClazz.isArray() && !oClazz.isEnum()) {
                                Field[] declaredFields = oClazz.getDeclaredFields();
                                for (Field field : declaredFields) {
                                    field.setAccessible(true);
                                    NestId nestIdAnnoChi = field.getAnnotation(NestId.class);
                                    NestUUID nestUuidAnnoChi = field.getAnnotation(NestUUID.class);
                                    if (Objects.nonNull(nestIdAnnoChi)) {
                                        param.addNestId(String.valueOf(field.get(o)));
                                    }
                                    if (Objects.nonNull(nestUuidAnnoChi)) {
                                        param.addUuid(String.valueOf(field.get(o)));
                                    }
                                }
                            }
                        }
                        args[i] = list;
                    }
                } catch (IllegalAccessException e) {
                    System.out.println("没有nestId");
                }
            }
        }
        return param;
    }

    public void saveOperationRecords(Param param, String[] nestCodes, String creatorId) {
        if (Objects.nonNull(param) && Objects.nonNull(nestCodes) && Objects.nonNull(creatorId)) {
            List<NestCodeOperationInDTO> dtoList = new ArrayList<>();
            List<String> nestIdList = param.getNestIdList();
            if (CollectionUtil.isEmpty(nestIdList)) {
                for (String nestUuid : param.getNestUuidList()) {
                    param.addNestId(baseNestService.getNestIdByNestUuid(nestUuid));
                }
            }
            for (String nestId : nestIdList) {
                if (Objects.isNull(nestId)) {
                    return;
                }
                for (int i = 0; i < nestCodes.length; i++) {
                    String nestCode = nestCodes[i];
                    NestCodeOperationInDTO dto = NestCodeOperationInDTO
                            .builder()
                            .nestId(nestId)
                            .nestCode(nestCode)
                            .creatorId(creatorId)
                            .build();
                    dtoList.add(dto);
                }

            }

            nestCodeOperationRecordsService.batchSaveRecords(dtoList);
        }
    }

    public void pushByWs(Param param, String[] nestCodes, String creatorId, String language) {
        if (Objects.nonNull(param) && Objects.nonNull(nestCodes) && Objects.nonNull(creatorId)) {
            if (CollectionUtil.isEmpty(param.getNestUuidList())) {
                List<String> nestIdList = param.getNestIdList();
                for (String nestId : nestIdList) {
                    param.addUuid(baseNestService.getNestUuidByNestIdInCache(nestId));
                }

            }
            for (String nestUuid : param.getNestUuidList()) {
                if (Objects.isNull(nestUuid)) {
                    return;
                }

                DateTimeFormatter dfDateTime = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

                HashMap<String, String> data = new HashMap<>(4);
                //添加缓存
                Result<AccountOutDO> accountInfo = accountServiceClient.getAccountInfo(creatorId);
                String username = StrUtil.isNotEmpty(accountInfo.getData().getName()) ? accountInfo.getData().getName() : accountInfo.getData().getAccount();
                data.put("username", username);
                String dictCode = "NEST_CODE_TYPE";
                if (StringUtils.hasText(language) && "en-US".equals(language)) dictCode = "NEST_CODE_TYPE_EN";
                //添加缓存
                ListDictItemInfoInDO listDictItemInfoInDO = ListDictItemInfoInDO.builder().itemValues(Arrays.asList(nestCodes)).dictCode(dictCode).build();
                Result<List<DictItemInfoOutDO>> listResult = baseServiceClient.listDictItemInfo(listDictItemInfoInDO);
                if (Objects.nonNull(listResult) && CollectionUtil.isNotEmpty(listResult.getData())) {
                    String itemName = listResult.getData().stream().map(DictItemInfoOutDO::getItemName).collect(Collectors.joining(","));
                    data.put("codeName", itemName);
                } else {
                    data.put("codeName", "操作基站");
                }
                data.put("operationTime", dfDateTime.format(LocalDateTime.now()));
                String message = WebSocketRes.ok().topic(WebSocketTopicEnum.NEST_CODE_OPER_RECORD).data(data).toJSONString();
                ChannelService.sendMessageByType10Channel(nestUuid, message);
            }

        }
    }

    @Data
    public static class Param {
        private List<String> nestIdList = new ArrayList<>();
        private List<String> nestUuidList = new ArrayList<>();

        public void addNestId(String nestId) {
            this.nestIdList.add(nestId);
        }

        public void addUuid(String uuid) {
            this.nestUuidList.add(uuid);
        }
    }
}
