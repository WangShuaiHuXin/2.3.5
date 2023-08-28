package com.imapcloud.nest.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.geoai.common.web.util.MessageUtils;
import com.imapcloud.nest.common.constant.MessageConstant;
import com.imapcloud.nest.common.constant.NestConstant;
import com.imapcloud.nest.common.exception.NestException;
import com.imapcloud.nest.convert.PubMessageUpdToEntityConvert;
import com.imapcloud.nest.convert.PubMessageVOToEntityConvert;
import com.imapcloud.nest.enums.message.PubMessageClassEnum;
import com.imapcloud.nest.enums.message.PubMessageStateEnum;
import com.imapcloud.nest.enums.message.PubMessageTypeEnum;
import com.imapcloud.nest.mapper.PubMessageMapper;
import com.imapcloud.nest.model.PubMessageBodyEntity;
import com.imapcloud.nest.model.PubMessageEntity;
import com.imapcloud.nest.model.PubUserMessageEntity;
import com.imapcloud.nest.pojo.dto.message.PubMessageSaveDTO;
import com.imapcloud.nest.pojo.dto.message.PubMessageUpdDTO;
import com.imapcloud.nest.pojo.quartz.MessagePushJob;
import com.imapcloud.nest.pojo.vo.PubMessageVO;
import com.imapcloud.nest.service.PubMessageBService;
import com.imapcloud.nest.service.PubMessageService;
import com.imapcloud.nest.service.PubUserMessageService;
import com.imapcloud.nest.service.event.message.DeleteMessageAfterEvent;
import com.imapcloud.nest.service.event.message.PushMessageEvent;
import com.imapcloud.nest.service.event.message.SaveMessageAfterEvent;
import com.imapcloud.nest.service.event.message.SaveMessageBeforeEvent;
import com.imapcloud.nest.service.quarzt.QuartzService;
import com.imapcloud.nest.utils.CronUtils;
import com.imapcloud.nest.utils.PageUtils;
import com.imapcloud.nest.utils.Query;
import com.imapcloud.nest.utils.RestRes;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.common.properties.GeoaiUosProperties;
import com.imapcloud.nest.v2.manager.dataobj.out.OrgAccountOutDO;
import com.imapcloud.nest.v2.service.NestAccountService;
import com.imapcloud.nest.v2.service.OrgAccountService;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;
import lombok.extern.slf4j.Slf4j;
import org.quartz.CronScheduleBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author double
 * @since 2022-03-22
 */
@Slf4j
@Service
public class PubMessageServiceImpl extends ServiceImpl<PubMessageMapper, PubMessageEntity> implements PubMessageService {

    @Resource
    private PubMessageBService pubMessageBService;

    @Resource
    private ApplicationContext applicationContext;

    @Resource
    private PubUserMessageService pubUserMessageService;

    @Resource
    private QuartzService quartzService;

    @Resource
    private OrgAccountService orgAccountService;

    @Resource
    private NestAccountService nestAccountService;

    @Resource
    private GeoaiUosProperties geoaiUosProperties;

    /**
     * 消息主键对任务
     */
    public static Map<Integer, TimerTask> messageTask = new HashMap<>();

    public final Integer limitMinute = 5;

    /**
     * 保存接口
     *
     * @param pubMessageDTO 保存参数
     * @return 返回RestRes
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestRes savePubMessage(PubMessageSaveDTO pubMessageDTO) {
        log.info("【PubMessageService】:savePubMessage,DTO->{}",pubMessageDTO.toString());
        PubMessageEntity pubMessageEntity = new PubMessageEntity();
        //保存前事件
        this.applicationContext.publishEvent(new SaveMessageBeforeEvent<>(pubMessageDTO , pubMessageEntity));
        List<String> companies = pubMessageDTO.getCompanyIds();
        List<PubMessageBodyEntity> pubMessageBodyEntityList = new ArrayList<>();
        //保存表头信息
        this.save(pubMessageEntity);
        companies.stream().filter(Objects::nonNull).forEach(companyId->{
            pubMessageBodyEntityList.add(PubMessageBodyEntity.builder().pkMessage(pubMessageEntity.getId()).orgCode(companyId).build());
        });
        //保存表体信息
        this.pubMessageBService.saveBatch(pubMessageBodyEntityList);
        pubMessageEntity.setPubMessageBodyEntityList(pubMessageBodyEntityList);
        //保存后事件
        PubMessageVO pubMessageVO = new PubMessageVO();
        this.applicationContext.publishEvent(new SaveMessageAfterEvent<>(pubMessageEntity,pubMessageVO));
        Map<String,Object> returnMap = new HashMap<>(8);
        returnMap.put("messageVO",pubMessageVO);
        return RestRes.ok(returnMap, MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SUCCESS_SAVE.getContent()));
    }

    /**
     * 修改接口
     *
     * @param pubMessageUpdDTO pubMessageUpdDTO
     * @return RestRes
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestRes updatePubMessage(PubMessageUpdDTO pubMessageUpdDTO) {
        log.info("【PubMessageService】:updatePubMessage,DTO->{}",pubMessageUpdDTO.toString());
        Integer id = pubMessageUpdDTO.getId();
        PubMessageEntity pubMessageEntity = this.lambdaQuery().eq(PubMessageEntity::getId,id).one();
        Integer messageDbState = pubMessageEntity.getMessageState(),messagesState = pubMessageUpdDTO.getMessageState();
        PubMessageUpdToEntityConvert.INSTANCES.updatePubMessageEntity(pubMessageUpdDTO,pubMessageEntity);
        log.info("PubMessageService:updatePubMessage,entity->{}",pubMessageEntity.toString());
        List<String> companies = pubMessageUpdDTO.getCompanyIds();
        if(CollectionUtils.isEmpty(companies)){
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_AUTH_PLEASE_SELECT_THE_UNIT_FIRST.getContent()));
        }
        //如果是由草稿到待推送，则需要发起一个定时任务。
        if(PubMessageStateEnum.MESSAGE_STATE_0.getCode().equals(messageDbState) && PubMessageStateEnum.MESSAGE_STATE_1.getCode().equals(messagesState) && pubMessageUpdDTO.getBeginTime()!=null){
            //定时推送--延迟队列触发。
            log.info("由草稿到带推送-发往延迟队列：{}",pubMessageEntity.getId());
            this.pushMain(pubMessageEntity);
        }
        //保存表头信息
        this.updateById(pubMessageEntity);
        List<PubMessageBodyEntity> pubMessageBodyEntityList = new ArrayList<>();
        companies.stream().filter(Objects::nonNull).forEach(companyId->{
            pubMessageBodyEntityList.add(PubMessageBodyEntity.builder().pkMessage(pubMessageEntity.getId()).orgCode(companyId).build());
        });
        if(CollectionUtil.isNotEmpty(pubMessageBodyEntityList)){
            //清空原有表体记录
            this.pubMessageBService.remove(Wrappers.<PubMessageBodyEntity>query().lambda().eq(PubMessageBodyEntity::getPkMessage,id));
            //保存表体信息
            this.pubMessageBService.saveBatch(pubMessageBodyEntityList);
        }
        PubMessageEntity pubMessage = this.queryAggEntityById(pubMessageEntity.getId());
        PubMessageVO pubMessageVO = PubMessageVOToEntityConvert.INSTANCES.doToDto(pubMessage);
        Map<String,Object> returnMap = new HashMap<>(8);
        returnMap.put("messageVO",pubMessageVO);
        return RestRes.ok(returnMap,MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MODIFY_SUCCESSFULLY.getContent()));
    }

    /**
     * 批量删除接口
     *
     * @param ids 消息主体主键
     * @return RestRes
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestRes deleteBatchPubMessage(String ids) {
        log.info("【PubMessageService】:deleteBatchPubMessage,ids->{}",ids);
        String[] arr = ids.split(",");
        List<Integer> idList = Arrays.stream(arr).map(Integer::parseInt).collect(Collectors.toList());
        for(Integer id : idList){
            this.deletePubMessage(id);
        }
        this.applicationContext.publishEvent(new DeleteMessageAfterEvent(idList));
        return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SUCCESS_DELETED.getContent()));
    }

    /**
     * 删除接口
     *
     * @param id 消息主体主键
     * @return RestRes
     */
    @Override
    public RestRes deletePubMessage(Integer id) {
        this.removeById(id);
        this.pubMessageBService.remove(Wrappers.<PubMessageBodyEntity>query().lambda().eq(PubMessageBodyEntity::getPkMessage,id));
        return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SUCCESS_DELETED.getContent()));
    }

    /**
     * 查询接口
     *
     * @param id 消息主体主键
     * @return RestRes
     */
    @Override
    public RestRes queryPubMessage(Integer id) {
        PubMessageEntity pubMessageEntity = this.lambdaQuery().eq(PubMessageEntity::getId,id).one();
        return RestRes.ok("data",pubMessageEntity);
    }

    /**
     *
     * @param page 页码
     * @param limit 每页数量
     * @param messageId 消息主键
     * @param messageState 消息状态
     * @param messageType 消息类型
     * @param messageClass 消息种类
     * @param userId 用户ID
     * @return RestRes
     */
    @Override
    public RestRes queryPubMessagePage(Integer page, Integer limit,Integer messageId,Integer messageState,Integer messageType,Integer messageClass,Long userId,String messageTitle) {
        Map<String,Object> params = new HashMap<>(2);
        params.put("page",page);
        params.put("limit",limit);
        params.put(Query.ORDER_FIELD,"modify_time");
        params.put(Query.ORDER,"desc");
        IPage<PubMessageVO> totalPage;
        if(PubMessageStateEnum.MESSAGE_STATE_0.getCode().equals(messageState) || PubMessageStateEnum.MESSAGE_STATE_1.getCode().equals(messageState)){
            //草稿和待推送
            totalPage = baseMapper.getDraftMessagePage(new Query<PubMessageVO>().getPage(params),messageId,messageState,messageType,messageClass,userId,messageTitle);
        }else{
            //已推送
            totalPage = baseMapper.getMessagePage(new Query<PubMessageVO>().getPage(params),messageId,messageState,messageType,messageClass,userId,messageTitle);
        }
        PageUtils pageData = new PageUtils(totalPage);
        Map<String, Object> returnMap = new HashMap<>(2);
        List<PubMessageVO> pubMessageVOList = totalPage.getRecords();
        if(CollectionUtil.isNotEmpty(pubMessageVOList)) {
            List<PubMessageBodyEntity> messageBodyEntityList = this.pubMessageBService.lambdaQuery().in(PubMessageBodyEntity::getPkMessage
                            , pubMessageVOList.stream().map(PubMessageVO::getId).distinct().collect(Collectors.toList()))
                    .select(PubMessageBodyEntity::getPkMessage, PubMessageBodyEntity::getOrgCode)
                    .list();
            Map<Integer, List<PubMessageBodyEntity>> messageToBodyMap = messageBodyEntityList.stream().collect(Collectors.groupingBy(PubMessageBodyEntity::getPkMessage));
            for (PubMessageVO pubMessageVO : pubMessageVOList) {
                pubMessageVO.setPubMessageBodyEntityList(messageToBodyMap.get(pubMessageVO.getId()));
            }
        }
        pageData.setList(pubMessageVOList);
        returnMap.put("page", pageData);
        return RestRes.ok(returnMap,MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SEARCH_SUCCESS.getContent()));
    }

    /**
     *
     * @param page 页码
     * @param limit 每页数量
     * @param messageId 消息主键
     * @param messageState 消息状态
     * @param messageType 消息类型
     * @param messageClass 消息种类
     * @param userId 用户ID
     * @return RestRes
     */
    @Override
    public RestRes queryPubMessageLogPage(Integer page, Integer limit,Integer messageId,Integer messageState,Integer messageType,Integer messageClass,Long userId) {
        Map<String,Object> params = new HashMap<>(2);
        params.put("page",page);
        params.put("limit",limit);
        params.put(Query.ORDER_FIELD,"modify_time");
        params.put(Query.ORDER,"desc");
        IPage<PubMessageVO> totalPage;
        if(PubMessageStateEnum.MESSAGE_STATE_0.getCode().equals(messageState) || PubMessageStateEnum.MESSAGE_STATE_1.getCode().equals(messageState)){
            //草稿和待推送
            totalPage = baseMapper.getDraftMessageLogPage(new Query<PubMessageVO>().getPage(params),messageId,messageState,messageType,messageClass,userId);
        }else{
            //已推送
            totalPage = baseMapper.getMessageLogPage(new Query<PubMessageVO>().getPage(params),messageId,messageState,messageType,messageClass,userId);
        }
        PageUtils pageData = new PageUtils(totalPage);
        Map<String, Object> returnMap = new HashMap<>(2);
        List<PubMessageVO> pubMessageVOList = totalPage.getRecords();
        pageData.setList(pubMessageVOList);
        returnMap.put("page", pageData);
        return RestRes.ok(returnMap,MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SEARCH_SUCCESS.getContent()));
    }

    /**
     *
     * @param messageId 消息主键
     * @param userId 用户id
     * @param allRead 全部已读
     * @param messageClass 消息种类
     * @return RestRes
     */
    @Override
    public RestRes updatePubMessageState(Integer messageId,Long userId,Integer allRead,Integer messageClass) {
        //全部已读
        if(allRead == 1){
            LambdaUpdateChainWrapper<PubUserMessageEntity> lambdaUpdateChainWrapper = this.pubUserMessageService.lambdaUpdate()
                    .setSql(" read_state = 1 ")
                    .eq(PubUserMessageEntity::getAccountId,userId)
                    .eq(PubUserMessageEntity::getDeleted, NestConstant.DeleteType.NOT_DELETE)
                    .eq(PubUserMessageEntity::getReadState,MessageConstant.ReadStatus.NOT_READ);
            //如果是处理任务已读
            if(PubMessageClassEnum.MESSAGE_CLASS_1.getCode().equals(messageClass)){
                lambdaUpdateChainWrapper.exists(" select 1 from pub_message p where p.id = message_id and message_class = 1 ");
            }
            lambdaUpdateChainWrapper.update();
        }else{
            if(messageId == null || userId == null){
                return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MESSAGE_ID_AND_USER_ID_CANNOT_BE_EMPTY.getContent()));
            }
            this.pubUserMessageService.lambdaUpdate()
                    .setSql(" read_state = 1 ")
                    .eq(PubUserMessageEntity::getAccountId,userId)
                    .eq(PubUserMessageEntity::getMessageId,messageId)
                    .eq(PubUserMessageEntity::getDeleted,NestConstant.DeleteType.NOT_DELETE)
                    .eq(PubUserMessageEntity::getReadState,MessageConstant.ReadStatus.NOT_READ)
                    .update();
        }
        return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SUCCESS_CHECK.getContent()));
    }

    /**
     * 推送接口
     *
     * @param id
     * @return
     */
    @Override
    public RestRes pushPubMessage(Integer id , String nestId) {
        PubMessageEntity pubMessageEntity = this.queryAggEntityById(id);
        Optional.ofNullable(nestId).ifPresent(nest->pubMessageEntity.setNestId(nest));
        this.applicationContext.publishEvent(new PushMessageEvent(new ArrayList(Arrays.asList(pubMessageEntity))));
        return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SUCCESS_PUSH.getContent()));
    }

    /**
     * 保存并推送接口
     *
     * @param pubMessageSaveDTO pubMessageSaveDTO
     * @return RestRes
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestRes saveAndPushPubMessage(PubMessageSaveDTO pubMessageSaveDTO) {
        RestRes restRes = this.savePubMessage(pubMessageSaveDTO);
        PubMessageVO pubMessage = (PubMessageVO) restRes.getParam().get("messageVO");
        Integer id = pubMessage.getId();
        return this.pushPubMessage(id,null);
    }

    /**
     * 保存并推送接口-针对任务消息
     * 推送范围：当前执行人、 组织下拥有基站Id权限的用户以及所有超管（涉及PubMessageSaveDTO对象字段createUserId \companyIds\nestId\ ）
     * @param pubMessageSaveDTO pubMessageSaveDTO
     * @return RestRes
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestRes saveAndPushPubMessageForTask(PubMessageSaveDTO pubMessageSaveDTO) {
        log.info("【PubMessageService】:saveAndPushPubMessageForTask->{}",pubMessageSaveDTO.toString());
        Optional.ofNullable(pubMessageSaveDTO)
                .orElseThrow(() -> new NestException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_CALLING_SAVEANDPUSHPUBMESSAGEFORTASK_METHOD_THE_INCOMING_PARAMETER_CANT_BE_EMPTY.getContent())));
        Optional.ofNullable(pubMessageSaveDTO)
                .map(x->x.getNestId())
                .orElseThrow(() -> new NestException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_CALL_SAVEANDPUSHPUBMESSAGEFORTASK_METHOD_NESTID_CANT_BE_EMPTY.getContent())));
        Optional.ofNullable(pubMessageSaveDTO)
                .map(x->x.getCompanyIds())
                .orElseThrow(() -> new NestException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_CALLING_SAVEANDPUSHPUBMESSAGEFORTASK_METHOD_COMPANYIDS_CANT_BE_EMPTY.getContent())));
        this.assignValueForTask(pubMessageSaveDTO);
        RestRes restRes = this.savePubMessage(pubMessageSaveDTO);
        PubMessageVO pubMessage = (PubMessageVO) restRes.getParam().get("messageVO");
        Integer id = pubMessage.getId();
        return this.pushPubMessage(id, pubMessageSaveDTO.getNestId());
    }

    /**
     * 任务计划消息赋值默认属性
     * @param pubMessageSaveDTO
     */
    public void assignValueForTask(PubMessageSaveDTO pubMessageSaveDTO){
        pubMessageSaveDTO.setMessageClass(PubMessageClassEnum.MESSAGE_CLASS_1.getCode());
        pubMessageSaveDTO.setMessageType(PubMessageTypeEnum.MESSAGE_TYPE_1.getCode());
        pubMessageSaveDTO.setMessageState(PubMessageStateEnum.MESSAGE_STATE_0.getCode());
        //默认直接推送
        pubMessageSaveDTO.setBeginTime(null);
        //所有超管也要收到消息，这里直接给组织列表加-1;-1代表超管单位
//        boolean bol = pubMessageSaveDTO.getCompanyIds().stream().anyMatch(companyId -> companyId == RoleServiceImpl.ZKYT_UNIT_ID);
//        if(!bol){
//            pubMessageSaveDTO.getCompanyIds().add((int) RoleServiceImpl.ZKYT_UNIT_ID);
//        }
    }

    /**
     * 获取聚合AggVO
     * @param id 消息主键
     * @return PubMessageEntity
     */
    public PubMessageEntity queryAggEntityById(Integer id) {
        PubMessageEntity pubMessageEntity = this.lambdaQuery()
                .eq(PubMessageEntity::getId, id)
                .eq(PubMessageEntity::getDeleted,NestConstant.DeleteType.NOT_DELETE)
                .one();
        pubMessageEntity = Optional.ofNullable(pubMessageEntity).map(messageEntity -> {
            List<PubMessageBodyEntity> pubMessageBodyEntityList = this.pubMessageBService.lambdaQuery()
                    .eq(PubMessageBodyEntity::getPkMessage, id)
                    .eq(PubMessageBodyEntity::getDeleted,NestConstant.DeleteType.NOT_DELETE)
                    .list();
            messageEntity.setPubMessageBodyEntityList(pubMessageBodyEntityList);
            return messageEntity;
        }).orElseGet(PubMessageEntity::new);
        return pubMessageEntity;
    }

    /**
     * 推送主动作
     * @param pubMessage 消息主体
     */
    @Override
    public void pushMain(PubMessageEntity pubMessage){
        log.info("【PubMessageService】:pushMain方法开始：{}",pubMessage.toString());
        if(pubMessage == null || CollectionUtil.isEmpty(pubMessage.getPubMessageBodyEntityList())){
            log.warn("pushMain:传参为空，跳过处理，{}",pubMessage);
            return;
        }

        Map<String,Integer> companyToMessage = new HashMap<>(32);
        List<PubMessageBodyEntity> pubMessageEntityList = pubMessage.getPubMessageBodyEntityList();

        List<PubMessageBodyEntity> pubMessageBodyEntityList = pubMessage.getPubMessageBodyEntityList();
        pubMessageBodyEntityList.forEach(pubMessageBodyEntity -> {
            companyToMessage.put(pubMessageBodyEntity.getOrgCode(), pubMessageBodyEntity.getPkMessage());
        });
        List<PubUserMessageEntity> pubUserMessageEntityList = new ArrayList<>();

        //根据组织获取对应的用户信息
        List<String> companyIds =pubMessageEntityList.stream()
                .map(PubMessageBodyEntity::getOrgCode)
                .filter(org.springframework.util.StringUtils::hasText)
                .collect(Collectors.toList());
        List<OrgAccountOutDO> orgAccountRefs = Collections.emptyList();
        if(CollectionUtil.isNotEmpty(companyIds)){
            // 查询单位中的用户
            orgAccountRefs = orgAccountService.listOrgAccountRefs(companyIds);
        }
        if (PubMessageClassEnum.MESSAGE_CLASS_1.getCode().equals(pubMessage.getMessageClass())) {
            // 任务需要过滤非基站用户
            List<String> accountIdList = nestAccountService.getAccountIdByNest(pubMessage.getNestId());
            orgAccountRefs = orgAccountRefs.stream()
                    .filter(r -> accountIdList.contains(r.getAccountId())).collect(Collectors.toList());
        }

        List<PubUserMessageEntity> finalPubUserMessageEntityList = pubUserMessageEntityList;
        //拼装数据生成用户消息关联表Entity
        orgAccountRefs.forEach(userEntity->{
            PubUserMessageEntity pubUserMessageEntity = PubUserMessageEntity.builder()
                    .messageId(companyToMessage.get(userEntity.getOrgCode()))
                    .accountId(Long.valueOf(userEntity.getAccountId()))
                    .readState(false)
                    .build();
            log.info("pushMain-拼装用户消息entity:{}",pubUserMessageEntity.toString());
            finalPubUserMessageEntityList.add(pubUserMessageEntity);
        });
        pubUserMessageEntityList = this.addSenderMessage(pubMessage,finalPubUserMessageEntityList);
        //判断触发定时还是即时推送
        if(pubMessage.getBeginTime()!=null){
            //定时推送--延迟队列触发。
            log.info("pushMain-发往延迟队列：{}",pubMessage.getId());
            LocalDateTime now = LocalDateTime.now(),beginTime = pubMessage.getBeginTime();
            Duration duration = Duration.between(now,beginTime);
            long seconds = duration.getSeconds();
            if(duration.toMinutes() < limitMinute){
                throw new NestException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_THE_TIME_INTERVAL_DIFFERENCE_IS_LESS_THAN_5_MINUTES.getContent()));
            }
            log.info("messageId:{},启动定时任务，定时时间为:{},当前时间：{}，相差{}秒"
                    ,pubMessage.getId()
                    ,DateUtil.formatLocalDateTime(beginTime)
                    ,DateUtil.formatLocalDateTime(now),seconds);
            //优化定时任务
            this.addJob(pubMessage,pubUserMessageEntityList);
        }else{
            //即时推送
            log.info("pushMain-立即执行：{}","");
            this.pubUserMessageService.saveBatch(pubUserMessageEntityList);
            this.lambdaUpdate().setSql(" message_state = 2 ")
                    .eq(PubMessageEntity::getId,pubMessage.getId())
                    .update();
        }
        log.info("pushMain方法结束：{}", pubMessage);
    }

    /**
     *  把发件人也加入进来
     * @param pubMessage 消息主体
     * @param pubUserMessageEntityList 消息主体列表
     */
    @Override
    public List<PubUserMessageEntity> addSenderMessage(PubMessageEntity pubMessage, List<PubUserMessageEntity> pubUserMessageEntityList){
        Integer count = this.pubUserMessageService.lambdaQuery()
                .eq(PubUserMessageEntity::getAccountId,pubMessage.getCreatorId())
                .eq(PubUserMessageEntity::getMessageId,pubMessage.getId())
                .eq(PubUserMessageEntity::getDeleted,NestConstant.DeleteType.NOT_DELETE)
                .count();
        if(count > NestConstant.CommonNum.ZREO){
            return pubUserMessageEntityList;
        }
        PubUserMessageEntity pubUserMessageEntity = PubUserMessageEntity.builder()
                .messageId(pubMessage.getId())
                .accountId(pubMessage.getCreatorId())
                .readState(true)
                .build();
        pubUserMessageEntityList.add(pubUserMessageEntity);
        //重写了equals 根据 userId 以及 MessageId 判断唯一
        pubUserMessageEntityList = pubUserMessageEntityList.stream()
                .filter(msg -> msg.getMessageId()!=null && msg.getAccountId()!=null )
                .distinct().collect(Collectors.toList());
        //找到发送人，并将发送人的记录标为已读
        pubUserMessageEntityList.stream()
                .filter(x->pubMessage.getCreatorId().equals(x.getAccountId()) && pubMessage.getId().equals(x.getMessageId()))
                .findFirst()
                .ifPresent(x->x.setReadState(NestConstant.CommonBol.RIGHT));
        return pubUserMessageEntityList;
    }

    /**
     * 获取数量
     *
     * @param userId 用户Id
     * @param readState 已读状态
     * @return Integer
     */
    @Override
    public Integer getReadStateCount(Long userId, Integer readState,Integer messageClass) {
        //获取任务未读数量
        if(PubMessageClassEnum.MESSAGE_CLASS_1.getCode().equals(messageClass)){
            return this.pubUserMessageService.lambdaQuery()
                    .eq(PubUserMessageEntity::getAccountId,userId)
                    .eq(PubUserMessageEntity::getReadState,readState)
                    .eq(PubUserMessageEntity::getDeleted,NestConstant.DeleteType.NOT_DELETE)
                    .exists(" select 1 from pub_message p where p.id = message_id and message_class = 1 ")
                    .count();
        }else{
            //获取公告未读数量
            return this.pubUserMessageService.lambdaQuery()
                    .eq(PubUserMessageEntity::getAccountId,userId)
                    .eq(PubUserMessageEntity::getReadState,readState)
                    .eq(PubUserMessageEntity::getDeleted,NestConstant.DeleteType.NOT_DELETE)
                    .exists(" select 1 from pub_message p where p.id = message_id and message_class = 0 ")
                    .count();
        }

    }

//    /**
//     * 上传接口-消息公告
//     */
//    @Override
//    public RestRes uploadForMessage(String mediaFileString, MultipartFile fileData) {
//        log.info("【PubMessageService】:uploadForMessage->{}",mediaFileString);
//        JSONObject jsonObj = JSONObject.parseObject(mediaFileString);
//        String fileName = jsonObj.getString("fileName"),
//                fileMd5 = jsonObj.getString("fileMd5"),
//                fileCode = jsonObj.getString("fileCode"),
//                fileType = jsonObj.getString("fileType"),
//                photoPath = "";
//        Integer pkMessage = jsonObj.getInteger("pkMessage");
//        if(StringUtils.isEmpty(fileCode)){
//            throw new NestException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_THE_FILE_TYPE_CODE_CANNOT_BE_EMPTY.getContent()));
//        }
//        if(fileData == null ){
//            throw new NestException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FILE_CANNOT_BE_EMPTY.getContent()));
//        }
//        String pathPrefix = UploadTypeEnum.enmuMap.get(fileCode);
//        String pattern  = ".*(\\.jpg|\\.jpeg|\\.png|\\.tiff|\\.tif|\\.gif)$";
//        if (fileName == null || !Pattern.matches(pattern,fileName.toLowerCase())) {
//            throw new NestException(String.format(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_NON_PHOTO_TYPE_FILES_CANNOT_BE_IMPORTED_PLEASE_CHECK.getContent())
//                    , fileName));
//        }
//        // 上传图片到MinIO服务器
//        photoPath = MinIoUnit.upload(geoaiUosProperties.getMinio().getBucketName(), fileData, fileName, pathPrefix );
//        photoPath = String.format("%s%s", geoaiUosProperties.getStore().getOriginPath(), photoPath);
//        return RestRes.ok("photoPath",photoPath);
//    }

    /**
     * 获取新的定时任务
     * @param pubMessage 消息主体
     * @param pubUserMessageService 消息用户关联服务类
     * @param pubMessageService 消息主体服务类
     * @param pubUserMessageEntityList 消息用户关联实体列表
     * @return TimeTask
     */
    public TimerTask getNewTimer(PubMessageEntity pubMessage,PubUserMessageService pubUserMessageService,PubMessageService pubMessageService, List<PubUserMessageEntity> pubUserMessageEntityList){
        return new TimerTask(){
            @Override
            public void run(Timeout timeout) throws Exception {
                log.info("定时任务执行messageId:{},执行时间:{}",pubMessage.getId(), DateUtil.formatLocalDateTime(LocalDateTime.now()));
                int count = pubMessageService.lambdaQuery()
                        .eq(PubMessageEntity::getId,pubMessage.getId())
                        .eq(PubMessageEntity::getDeleted,NestConstant.DeleteType.NOT_DELETE).count();
                if(count>=1){
//                    pubMessageService.addSenderMessage(pubMessage,pubUserMessageEntityList);
                    pubUserMessageService.saveBatch(pubUserMessageEntityList);
                    pubMessageService.lambdaUpdate().setSql(" message_state = 2 ")
                            .eq(PubMessageEntity::getId,pubMessage.getId())
                            .update();
                }
            }
        };
    }

    /**
     * 定时推送
     * @param pubMessage
     * @param pubUserMessageEntityList
     */
    public Map addJob(PubMessageEntity pubMessage , List<PubUserMessageEntity> pubUserMessageEntityList){
        //新增定时推送
        String jobName = String.format("%d-%s",pubMessage.getId(),pubMessage.getBeginTime());
        Map map = new HashMap(8);
        map.put(MessageConstant.Job.VALUES , pubUserMessageEntityList);
        map.put(MessageConstant.Job.JOB_NAME_STR,jobName);
        map.put(MessageConstant.Job.JOB_GROUP_NAME_STR,MessageConstant.Job.PUB_MESSAGE_GROUP);
        log.info("【addJob】-{}",map.toString());
        this.quartzService.addJob(MessagePushJob.class , jobName , MessageConstant.Job.PUB_MESSAGE_GROUP , map
                , CronScheduleBuilder.cronSchedule(CronUtils.getCronByLocalTime(pubMessage.getBeginTime())));
        return map;
    }
}
