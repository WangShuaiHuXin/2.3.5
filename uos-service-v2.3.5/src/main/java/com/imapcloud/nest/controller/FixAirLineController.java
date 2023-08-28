package com.imapcloud.nest.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.geoai.common.web.util.MessageUtils;
import com.imapcloud.nest.model.FixAirLineEntity;
import com.imapcloud.nest.model.NestEntity;
import com.imapcloud.nest.pojo.dto.FixAirLineDto;
import com.imapcloud.nest.service.FixAirLineService;
import com.imapcloud.nest.service.NestService;
import com.imapcloud.nest.utils.RestRes;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.service.BaseNestService;
import com.imapcloud.sdk.manager.ComponentManager;
import com.imapcloud.sdk.manager.ComponentManagerFactory;
import com.imapcloud.sdk.pojo.entity.Mission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;


/**
 * <p>
 * 固定航线表 前端控制器
 * </p>
 *
 * @author wmin
 * @since 2020-10-10
 */
@RestController
@RequestMapping("/fixAirLine")
public class FixAirLineController {

    @Autowired
    private FixAirLineService fixAirLineService;

    @Autowired
    private NestService nestService;

    @Autowired
    private BaseNestService baseNestService;

    /**
     * 云端航线导入
     *
     * @param file
     * @param nestId
     * @param airLineType
     * @return
     */
    @Deprecated
    @PostMapping("/import/cloud/air/line")
    public RestRes importCloudAirLine(MultipartFile file, Integer nestId, Integer airLineType) {
        if (file != null && nestId != null && airLineType != null) {
            return fixAirLineService.importCloudAirLine(file, nestId, airLineType);
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_INCORRECT_PARAMETERS.getContent()));
    }

    /**
     * 导出云端航线
     *
     * @param cloudAirLineId
     */
    @GetMapping("/export/cloud/air/line")
    public void exportCloudAirLine(Integer cloudAirLineId, HttpServletResponse response) throws IOException {
        if (cloudAirLineId != null) {
            ServletOutputStream outputStream = null;
            try {
                FixAirLineEntity fixAirLineEntity = fixAirLineService.getById(cloudAirLineId);
                String waypoints = fixAirLineEntity.getWaypoints();
                String name = fixAirLineEntity.getName() + ".json";
                String fileName = URLEncoder.encode(name, "UTF-8");
                response.reset();
                response.setCharacterEncoding("UTF-8");
                response.setContentType("application/octet-stream");
                response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
                response.setHeader("content-type", "application/octet-stream");
                outputStream = response.getOutputStream();
                outputStream.write(waypoints.getBytes());
                outputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (outputStream != null) {
                    outputStream.close();
                }
            }
        }
    }


    /**
     * 查询云端航线
     *
     * @param nestId
     * @return
     */
//    @Deprecated
//    @GetMapping("/list/cloud/air/line/{nestId}")
//    public RestRes listCloudAirLine(@PathVariable Integer nestId) {
//        if (nestId != null) {
//            return fixAirLineService.listCloudAirLine(nestId);
//        }
//        return RestRes.err("参数不正确");
//    }


    /**
     * 批量删除云端航线
     *
     * @param cloudAirLineIdList
     * @return
     */
    @Deprecated
    @DeleteMapping("/batch/delete/cloud/air/line")
    public RestRes batchDeleteCloudAirLine(@RequestBody List<Integer> cloudAirLineIdList) {
        if (CollectionUtil.isNotEmpty(cloudAirLineIdList)) {
            Integer res = fixAirLineService.batchSoftDelete(cloudAirLineIdList);
            if (res > 0) {
                return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SUCCESS_BATCH_DELETE.getContent()));
            }
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_BATCH_DELETE_FAILED.getContent()));
    }

    /**
     * 复制航线
     *
     * @param cloudAirLineId
     * @return
     */
    @Deprecated
    @PostMapping("/copy/cloud/air/line/{cloudAirLineId}")
    public RestRes copyCloudAirLine(@PathVariable Integer cloudAirLineId) {
        if (cloudAirLineId != null) {
            return fixAirLineService.copyCloudAirLine(cloudAirLineId);
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAIL_CLOUD_ROUTE_REPLICATION.getContent()));
    }

    /**
     * 保存编辑
     *
     * @return
     */
    @Deprecated
    @PostMapping("/save/update/air/line")
    public RestRes saveUpdateAirLine(@RequestBody FixAirLineDto dto) {
        if (ObjectUtil.isNotNull(dto)) {
            return fixAirLineService.updateCloudAirLine(dto);
        }
        return RestRes.err("保存编辑航线出错");
    }


    /**
     * 选择航线
     *
     * @param param
     * @return
     */
    @PostMapping("/choice/air/line")
    public RestRes choiceAirLine(@RequestBody Map<String, Object> param) {
        if (param != null) {
            String nestId = (String) param.get("nestId");
            Integer missionId = (Integer) param.get("missionId");
            return fixAirLineService.choiceAirLine(missionId, nestId);
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_INCORRECT_PARAMETERS.getContent()));
    }


    /**
     * 获取基站上面的航线列表
     *
     * @param nestId
     * @return
     */
    @GetMapping("/list/mission/from/nest/{nestId}")
    public RestRes listMissionFromNest(@PathVariable String nestId) {
        if (nestId != null) {
            return fixAirLineService.listMissionFromNestByNestId(nestId);
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_INCORRECT_PARAMETERS.getContent()));
    }

    /**
     * 获取基站航线的详细信息
     *
     * @param param
     * @return
     */
    @PostMapping("/find/mission/detail/from/nest")
    public RestRes findMissionDetailFromNest(@RequestBody Map<String, Object> param) {
        if (CollectionUtil.isNotEmpty(param)) {
            String nestId = (String) param.get("nestId");
            String missionId = (String) param.get("missionId");
            return fixAirLineService.findMissionDetailFromNestByMissionId(nestId, missionId);
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_INCORRECT_PARAMETERS.getContent()));
    }


    /**
     * 上传航线到基站
     *
     * @return
     */
    @Deprecated
    @PostMapping("/upload/air/line/to/nest")
    public RestRes uploadAirLineToNest(Integer nestId, Integer airLineType, MultipartFile file) {
        if (nestId != null && file != null) {
            return fixAirLineService.uploadAirLineToNest(nestId, airLineType, file);
        }
        return RestRes.err();
    }

    /**
     * 服务航线上传到云端航线
     *
     * @param param key->missionId,val->missionName
     * @return
     */
    @Deprecated
    @PostMapping("/upload/air/line/nest/to/cloud")
    public RestRes uploadAirLineNestToCloudAir(@RequestBody Map<String, Object> param) {
        if (CollectionUtil.isNotEmpty(param)) {
            Map<String, String> missionMap = (Map<String, String>) param.get("missionMap");
            Integer nestId = (Integer) param.get("nestId");
            return fixAirLineService.uploadNestAirLineToCloudAirLine(missionMap, nestId);
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SERVICE_ROUTE_UPLOAD_TO_CLOUD_ROUTE_FAILED.getContent()));
    }

    /**
     * 删除基站航线
     *
     * @param param
     * @return
     */
    @DeleteMapping("/batch/delete/nest/air/line")
    public RestRes batchDeleteNestAirLine(@RequestBody Map<String, Object> param) {
        String nestId = (String) param.get("nestId");
        List<String> missionUuidList = (List<String>) param.get("missionUuidList");
        if (nestId != null && CollectionUtil.isNotEmpty(missionUuidList)) {
//            ComponentManager cm = getComponentManager(nestId);
            ComponentManager cm = baseNestService.getComponentManagerByNestId(nestId);
            if (cm != null) {
                for (String missionUuid : missionUuidList) {
                    cm.getMissionManager().deleteMission(missionUuid, (result, isSuccess, errMsg) -> {

                    });
                }
            }
            return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_BASE_STATION_ROUTE_DELETION_SUCCESS.getContent()));
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_BASE_STATION_ROUTE_DELETION_FAILED.getContent()));
    }

//    private ComponentManager getComponentManager(int nestId) {
//        NestEntity nest = nestService.getNestByIdIsCache(nestId);
//        if (nest != null) {
//            return ComponentManagerFactory.getInstance(nest.getUuid());
//        }
//        return null;
//    }

}

