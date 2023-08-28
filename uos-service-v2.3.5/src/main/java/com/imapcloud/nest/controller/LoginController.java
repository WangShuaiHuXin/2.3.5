package com.imapcloud.nest.controller;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.geoai.common.core.bean.TrustedAccessTracerHolder;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.google.common.collect.Maps;
import com.imapcloud.nest.model.MapParamEntity;
import com.imapcloud.nest.service.MapParamService;
import com.imapcloud.nest.utils.RestRes;
import com.imapcloud.nest.utils.ToolUtil;
import com.imapcloud.nest.v2.common.properties.DomainConfig;
import com.imapcloud.nest.v2.common.properties.GeoaiUosProperties;
import com.imapcloud.nest.v2.manager.dataobj.out.OrgSimpleOutDO;
import com.imapcloud.nest.v2.manager.dataobj.out.OrgThemeInfoOutDO;
import com.imapcloud.nest.v2.manager.dataobj.out.PowerHomeBaseSettingInfoOutDO;
import com.imapcloud.nest.v2.manager.rest.UosOrgManager;
import com.imapcloud.nest.v2.manager.sql.PowerHomeManager;
import com.imapcloud.nest.v2.service.NestAccountService;
import com.imapcloud.nest.v2.service.UosAccountService;
import com.imapcloud.nest.v2.service.dto.out.AccountInfoOutDTO;
import com.imapcloud.nest.v2.service.dto.out.NestAccountInfoOutDTO;
import com.imapcloud.nest.v2.web.vo.resp.SysOrgParamRespVO;
import io.swagger.annotations.Api;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author wmin
 */
@ApiSupport(author = "wumiao@geoai.com", order = 1)
@Api(value = "认证API", tags = "认证API")
@RestController
@RequestMapping("/sys")
public class LoginController {

    @Resource
    private MapParamService mapParamService;

    @Resource
    private NestAccountService nestAccountService;

    @Resource
    private UosAccountService uosAccountService;

    @Resource
    private UosOrgManager uosOrgManager;

    @Resource
    private GeoaiUosProperties geoaiUosProperties;

    @Resource
    private PowerHomeManager powerHomeManager;

    @GetMapping("/getUserDetails")
    public RestRes getUserDetails() {

        // 查询用户所在单位
        String accountId = TrustedAccessTracerHolder.get().getAccountId();
        // 查询用户信息
        AccountInfoOutDTO accountInfoOutDTO = uosAccountService.accountInfo(accountId);

        String orgCode = TrustedAccessTracerHolder.get().getOrgCode();

        if (!StringUtils.hasText(orgCode)) {
            return RestRes.errorParam();
        }
        MapParamEntity mapParamEntity = mapParamService.getOne(new QueryWrapper<MapParamEntity>().eq("deleted", 0).eq("org_code", orgCode));
        Integer spaceError = ToolUtil.isEmpty(mapParamEntity) || ToolUtil.isEmpty(mapParamEntity.getSystemSpaceError()) ? 2 : Integer.parseInt(mapParamEntity.getSystemSpaceError());

        int theme = 0;
        String title = "";
        Optional<OrgThemeInfoOutDO> optional = uosOrgManager.getUosForegroundThemeInfos(orgCode);
        SysOrgParamRespVO paramRespVO = new SysOrgParamRespVO();
        Optional<OrgSimpleOutDO> orgInfo = uosOrgManager.getOrgInfo(orgCode);
        OrgSimpleOutDO orgSimpleOutDO = orgInfo.get();
        if (optional.isPresent()) {
            theme = optional.get().getThemeType();
            title = optional.get().getSystemTitle();
            paramRespVO.setPageType(optional.get().getPageType());
        }
        // 查询用户对基站是否可控
        boolean isOperation = false;
        NestAccountInfoOutDTO nestAccountInfoOutDTO = nestAccountService.nestAccountInfo(accountId);
        if (CollUtil.isNotEmpty(nestAccountInfoOutDTO.getInfoList())) {
            isOperation = nestAccountInfoOutDTO.getInfoList().get(0).isNestControlStatus();
        }

        Map<String, Object> userInfo = Maps.newHashMap();
        userInfo.put("id", accountId);
        userInfo.put("account", TrustedAccessTracerHolder.get().getUsername());
        userInfo.put("isOperation", isOperation);
        userInfo.put("unitId", orgCode);
        userInfo.put("mobile", accountInfoOutDTO.getMobile());
        userInfo.put("name", accountInfoOutDTO.getName());

        HashMap<String, Object> hashMap = Maps.newHashMap();
        hashMap.put("userInfo", userInfo);
        DomainConfig domain = geoaiUosProperties.getDomain();
        hashMap.put("photoPrefix", domain.getNginx() == null ? "" : domain.getGateway());
        hashMap.put("mediaDomain", domain.getMedia() == null ? "" : domain.getMedia());
        hashMap.put("downloadDomain", domain.getDownload() == null ? "" : domain.getDownload());
        hashMap.put("uploadDomain", domain.getUpload());
        hashMap.put("theme", theme);
        hashMap.put("title", title);
        hashMap.put("spaceError", spaceError);
        hashMap.put("longitude", orgSimpleOutDO.getLongitude());
        hashMap.put("latitude", orgSimpleOutDO.getLatitude());
        //巡视点位 覆盖面积
        PowerHomeBaseSettingInfoOutDO powerHomeBaseSettingInfoOutDO = powerHomeManager.queryByOrg(orgCode);
        paramRespVO.setCoverageArea(powerHomeBaseSettingInfoOutDO.getCoverageArea().toString());
        paramRespVO.setInspectPoint(powerHomeBaseSettingInfoOutDO.getInspectionPoints().toString());
        hashMap.put("orgPowerParams", paramRespVO);
        return RestRes.ok(hashMap);
    }

}
