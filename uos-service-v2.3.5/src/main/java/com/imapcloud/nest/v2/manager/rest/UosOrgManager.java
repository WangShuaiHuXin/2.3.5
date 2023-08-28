package com.imapcloud.nest.v2.manager.rest;

import com.geoai.common.core.bean.PageResultInfo;
import com.geoai.common.web.rest.Result;
import com.imapcloud.nest.v2.manager.dataobj.in.OrgPageQueryDO;
import com.imapcloud.nest.v2.manager.dataobj.out.OrgInfoOutDO;
import com.imapcloud.nest.v2.manager.dataobj.out.OrgNodeOutDO;
import com.imapcloud.nest.v2.manager.dataobj.out.OrgSimpleOutDO;
import com.imapcloud.nest.v2.manager.dataobj.out.OrgThemeInfoOutDO;
import com.imapcloud.nest.v2.manager.feign.OrgServiceClient;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * UOS单位数据管理器
 * @author Vastfy
 * @date 2022/8/23 17:21
 * @since 2.0.0
 */
@Component
public class UosOrgManager {

    public static final String UOS_SYSTEM_TYPE = "geoai-uos-foreground";

    @Resource
    private OrgServiceClient orgServiceClient;

    public Optional<OrgNodeOutDO> getOrgTree(String orgCode){
        Result<OrgNodeOutDO> result = orgServiceClient.getOrgTree(orgCode);
        if(result.isOk()){
            return Optional.ofNullable(result.getData());
        }
        return Optional.empty();
    }

    public List<OrgSimpleOutDO> listAllOrgInfos(){
        Result<List<OrgSimpleOutDO>> result = orgServiceClient.listAllOrgSimpleInfos();
        if(result.isOk()){
            return result.getData();
        }
        return Collections.emptyList();
    }

    public List<OrgSimpleOutDO> listAllOrgSimpleInfoWithoutAccount(){
        Result<List<OrgSimpleOutDO>> result = orgServiceClient.listAllOrgSimpleInfoWithoutAccount();
        if(result.isOk()){
            return result.getData();
        }
        return Collections.emptyList();
    }

    public List<OrgSimpleOutDO> listOrgInfos(List<String> orgCodes){
        if(!CollectionUtils.isEmpty(orgCodes)){
            Result<List<OrgSimpleOutDO>> result = orgServiceClient.listOrgDetails(orgCodes);
            if(result.isOk()){
                return result.getData();
            }
        }
        return Collections.emptyList();
    }

    public Optional<OrgSimpleOutDO> getOrgInfo(String orgCode){
        if(StringUtils.hasText(orgCode)){
            Result<OrgSimpleOutDO> result = orgServiceClient.getOrgDetails(orgCode);
            if(result.isOk()){
                return Optional.ofNullable(result.getData());
            }
        }
        return Optional.empty();
    }

    public PageResultInfo<OrgInfoOutDO> queryOrgInfo(OrgPageQueryDO condition){
        Result<PageResultInfo<OrgInfoOutDO>> result = orgServiceClient.pageOrgInfo(condition);
        if(result.isOk()){
            return result.getData();
        }
        return PageResultInfo.empty();
    }

    public List<OrgThemeInfoOutDO> getOrgThemeInfos(String orgCode, String appType){
        Result<List<OrgThemeInfoOutDO>> result = orgServiceClient.getOrgThemeInfos(orgCode, appType);
        if(result.isOk()){
            return result.getData();
        }
        return Collections.emptyList();
    }

    public Optional<OrgThemeInfoOutDO> getUosForegroundThemeInfos(String orgCode){
        List<OrgThemeInfoOutDO> orgThemeInfos = getOrgThemeInfos(orgCode, UOS_SYSTEM_TYPE);
        if(!CollectionUtils.isEmpty(orgThemeInfos)){
            return Optional.ofNullable(orgThemeInfos.get(0));
        }
        return Optional.empty();
    }

}
