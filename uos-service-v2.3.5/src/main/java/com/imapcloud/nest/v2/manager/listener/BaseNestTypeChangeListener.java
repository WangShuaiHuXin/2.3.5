package com.imapcloud.nest.v2.manager.listener;

import cn.hutool.core.collection.CollectionUtil;
import com.imapcloud.nest.common.listener.AbstractEventListener;
import com.imapcloud.nest.v2.common.utils.AuditUtils;
import com.imapcloud.nest.v2.manager.dataobj.in.BaseNestInDO;
import com.imapcloud.nest.v2.manager.dataobj.out.BaseUavNestRefOutDO;
import com.imapcloud.nest.v2.manager.dataobj.out.BaseUavOutDO;
import com.imapcloud.nest.v2.manager.event.BaseNestTypeChangeEvent;
import com.imapcloud.nest.v2.manager.sql.BaseUavManager;
import com.imapcloud.nest.v2.manager.sql.BaseUavNestRefManager;
import com.imapcloud.nest.v2.service.BaseNestService;
import com.imapcloud.sdk.pojo.constant.NestTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName BaseNestTypeChangeListener.java
 * @Description BaseNestTypeChangeListener
 * @createTime 2022年11月14日 09:12:00
 */
@Slf4j
@Service
public class BaseNestTypeChangeListener extends AbstractEventListener<BaseNestTypeChangeEvent> {

    @Resource
    private BaseNestService baseNestService;

    @Resource
    private BaseUavManager baseUavManager;

    @Resource
    private BaseUavNestRefManager baseUavNestRefManager;

//    @Resource
//    private MediaStreamManager mediaStreamManager;

    /**
     * 消息监听-处理
     *
     * @param baseNestTypeChangeEvent 消息事件
     */
    @Override
    @EventListener
    public void eventListener(BaseNestTypeChangeEvent baseNestTypeChangeEvent) {
        log.info("【BaseNestTypeChangeEvent】-【BaseNestTypeChangeListener】事件:{}", baseNestTypeChangeEvent.toString());
        BaseNestInDO.BaseNestEntityInDO baseNestEntityInDO = baseNestTypeChangeEvent.getSource();
        //如果是多机机巢G503转为单机机巢，或者单机机巢转为多机机巢G503，清空无人机信息以及图传信息
        if(Objects.isNull(baseNestEntityInDO) || Objects.isNull(baseNestEntityInDO.getType())
            || Objects.isNull(baseNestEntityInDO.getNestId())){
            return;
        }
        NestTypeEnum nestTypeEnum = this.baseNestService.getNestType(baseNestEntityInDO.getNestId());
        NestTypeEnum originNestType = NestTypeEnum.getInstance(baseNestEntityInDO.getType());
        log.info("【BaseNestTypeChangeListener】originNestType -> {} , nestType -> {} ", originNestType ,nestTypeEnum );
        if(check(nestTypeEnum,originNestType)){
            //删除数据
            log.info("【BaseNestTypeChangeListener】check true");
            this.deleteDB(baseNestEntityInDO);
        }

    }

    /**
     * 这里为了符合产品需求，实际做法不应该写这种特殊逻辑。应该有字段区分单机型、多机型
     * 修改前是G503或者修改后是G503 则应清空
     * @param nestTypeEnum
     * @param originNestTypeEnum
     * @return
     */
    public boolean check(NestTypeEnum nestTypeEnum,NestTypeEnum originNestTypeEnum){
        //修改前后都为相同类型则不处理
        if(!NestTypeEnum.UNKNOWN.equals(originNestTypeEnum) && nestTypeEnum.equals(originNestTypeEnum)) {
            return false;
        }
        //修改前如果是G503，修改后不等于G503，都清空
        if(NestTypeEnum.G503.equals(originNestTypeEnum)){
            return true;
        }
        //修改前有数据，且修改后是G503，则清空
        if(!NestTypeEnum.UNKNOWN.equals(originNestTypeEnum) && NestTypeEnum.G503.equals(nestTypeEnum)){
            return true;
        }
        return false;
    }

    /**
     *
     * @param baseNestEntityInDO
     */
    private void deleteDB(BaseNestInDO.BaseNestEntityInDO baseNestEntityInDO){
        List<BaseUavNestRefOutDO.EntityOutDO> entityOutDOList = this.baseUavNestRefManager.selectListByNestId(baseNestEntityInDO.getNestId());
        List<String> uavList = entityOutDOList.stream().map(BaseUavNestRefOutDO.EntityOutDO::getUavId).collect(Collectors.toList());
        if(CollectionUtil.isEmpty(uavList)){
            return;
        }
        List<BaseUavOutDO.BaseUavEntityOutDO> baseUavEntityOutDOList = this.baseUavManager.selectListByUavIdList(uavList);
        List<String> streamIdList = baseUavEntityOutDOList.stream().map(BaseUavOutDO.BaseUavEntityOutDO::getStreamId).collect(Collectors.toList());
        if(log.isDebugEnabled()){
            log.debug("【BaseNestTypeChangeListener】-nestId -> {} , uavList -> {} , streamIdList -> {}"
                    ,baseNestEntityInDO.getNestId()
                    ,uavList
                    ,streamIdList);
        }
        //删除关联关系
        this.baseUavNestRefManager.deleteByNestId(baseNestEntityInDO.getNestId(), AuditUtils.getAudit());
        //删除无人机数据
        this.baseUavManager.deleteByUavIdList(uavList,AuditUtils.getAudit());
//        //删除无人机图传Stream
//        this.mediaStreamManager.deleteByStreamIdList(streamIdList,AuditUtils.getAudit());

    }

}
