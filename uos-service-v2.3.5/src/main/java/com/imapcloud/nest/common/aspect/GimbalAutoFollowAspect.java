package com.imapcloud.nest.common.aspect;

import cn.hutool.core.collection.CollectionUtil;
import com.imapcloud.nest.common.annotation.NestId;
import com.imapcloud.nest.common.annotation.NestUUID;
import com.imapcloud.nest.pojo.dto.GimbalAutoFollowStateDTO;
import com.imapcloud.nest.sdk.CommonNestStateService;
import com.imapcloud.nest.utils.redis.RedisKeyConstantList;
import com.imapcloud.nest.utils.redis.RedisService;
import com.imapcloud.nest.v2.service.BaseNestService;
import com.imapcloud.nest.v2.service.UosNestService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

@Slf4j
@Aspect
@Component
public class GimbalAutoFollowAspect {

    @Resource
    private RedisService redisService;

    @Resource
    private BaseNestService baseNestService;

    @Resource
    private CommonNestStateService commonNestStateService;

    @Resource
    private UosNestService uosNestService;

    @Pointcut("@annotation(com.imapcloud.nest.common.annotation.GimbalAutoFollow)")
    public void gimbalAutoFollow() {

    }

    /**
     * 自动追踪切面
     * 1、追踪模式累计次数直接变成0
     * 2、发送取消自动追踪指令
     *
     * @param joinPoint
     */
    @Before("gimbalAutoFollow()")
    public void before(JoinPoint joinPoint) {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        String methodType = request.getMethod();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Param param = buildNestIdOrNestUuid(method, joinPoint.getArgs(), methodType);
        //1、追踪模式累计次数直接变成0
        perfectParam(param);
        for (String nestUuid : param.getNestUuidList()) {
            String redisKey = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.AUTO_FOLLOW_MODE, nestUuid);
            redisService.set(redisKey, 0);
        }
        //2、判断如果云台是自动追踪模式，则发送取消自动跟随指令
        for (int i = 0; i < param.getNestUuidList().size(); i++) {
            String nestUuid = param.getNestUuidList().get(i);
            GimbalAutoFollowStateDTO gimbalAutoFollowStateDTO = commonNestStateService.getGimbalAutoFollowStateDTO(nestUuid);
            if (gimbalAutoFollowStateDTO.getEnable()) {
                String nestId = param.getNestIdList().get(i);
                uosNestService.exitGimbalAutoFollow(nestId);
            }

        }


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

    private void perfectParam(Param param) {
        if (CollectionUtil.isEmpty(param.getNestUuidList())) {
            List<String> nestIdList = param.getNestIdList();
            for (String nestId : nestIdList) {
                param.addUuid(baseNestService.getNestUuidByNestIdInCache(nestId));
            }
        }
        List<String> nestIdList = param.getNestIdList();
        if (CollectionUtil.isEmpty(nestIdList)) {
            for (String nestUuid : param.getNestUuidList()) {
                param.addNestId(baseNestService.getNestIdByNestUuid(nestUuid));
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
