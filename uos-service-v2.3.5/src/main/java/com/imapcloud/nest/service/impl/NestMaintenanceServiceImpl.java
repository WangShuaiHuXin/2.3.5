package com.imapcloud.nest.service.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.geoai.common.web.util.MessageUtils;
import com.imapcloud.nest.mapper.NestMaintenanceMapper;
import com.imapcloud.nest.model.NestMaintenanceEntity;
import com.imapcloud.nest.model.NestMaintenanceProjectEntity;
import com.imapcloud.nest.pojo.dto.NestMaintenanceDTO;
import com.imapcloud.nest.pojo.vo.NestMaintenanceExportVO;
import com.imapcloud.nest.pojo.vo.NestMaintenancePageVO;
import com.imapcloud.nest.pojo.vo.NestMaintenanceVO;
import com.imapcloud.nest.service.NestMaintenanceProjectService;
import com.imapcloud.nest.service.NestMaintenanceService;
import com.imapcloud.nest.utils.PageUtils;
import com.imapcloud.nest.utils.Query;
import com.imapcloud.nest.utils.RestRes;
import com.imapcloud.nest.utils.excel.CustomCellWidthStyleStrategy;
import com.imapcloud.nest.utils.excel.EasyExcelStyleUtils;
import com.imapcloud.nest.utils.excel.EasyExcelUtils;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.common.properties.GeoaiUosProperties;
import com.imapcloud.nest.v2.service.NestOrgRefService;
import com.imapcloud.nest.v2.service.dto.out.NestOrgRefOutDTO;
import com.imapcloud.sdk.pojo.constant.NestTypeEnum;
import com.imapcloud.sdk.utils.StringUtil;
import io.jsonwebtoken.lang.Strings;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * <p>
 * 机巢维保记录表 服务实现类
 * </p>
 *
 * @author hc
 * @since 2021-05-14
 */
@Service
public class NestMaintenanceServiceImpl extends ServiceImpl<NestMaintenanceMapper, NestMaintenanceEntity> implements NestMaintenanceService {

    @Resource
    private NestOrgRefService nestOrgRefService;

    @Resource
    private NestMaintenanceProjectService nestMaintenanceProjectService;

    @Resource
    private GeoaiUosProperties geoaiUosProperties;

    private boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^-?\\d+(\\.\\d+)?$");
        return pattern.matcher(str).matches();
    }

    private String getProjectStr(String project) {
        if (StringUtils.isBlank(project)) {
            return MessageUtils.getMessage(MessageEnum.GEOAI_UOS_NO_MAINTENANCE_ITEMS.getContent());
        }
        String[] strAry = project.split(",");
        List<Integer> ids = new ArrayList<>();
        Stack<String> stack = new Stack<>();
        List<NestMaintenanceProjectEntity> list = null;
        StringBuilder sb = new StringBuilder(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_NO_MAINTENANCE_ITEMS.getContent()));
        for (String str : strAry) {//区分是否为数字
            if (!isInteger(str)) {
                stack.push(str);
            } else {
                ids.add(Integer.valueOf(str));
            }
        }
//        ids = Arrays.stream(project.split(",")).map(Integer::parseInt).collect(Collectors.toList());
        if (ids.size() > 0) {
            list = nestMaintenanceProjectService.listByIds(ids);
        }
        if (list != null && list.size() > 0) {
            sb = new StringBuilder();
            List<String> projectNameList = list.stream().map(NestMaintenanceProjectEntity::getName).collect(Collectors.toList());
            for (int j = 0; j < projectNameList.size(); j++) {
                String projectName = projectNameList.get(j);
                sb.append(projectName);
                if (j != projectNameList.size() - 1) {
                    sb.append(",");
                }
            }
            while (!stack.empty()) {
                sb.append(",");
                sb.append(stack.pop());
            }
            return sb.toString();
        } else {
            if (stack.empty()) {
                return sb.toString();
            }
            sb = new StringBuilder();
            while (!stack.empty()) {
                sb.append(stack.pop());
                if (!stack.empty()) {
                    sb.append(",");
                }
            }
            return sb.toString();
        }
    }

    @Override
    public RestRes queryPage(Map<String, Object> params) {
        IPage<NestMaintenancePageVO> page = getBaseMapper().queryPage(new Query<NestMaintenanceEntity>().getPage(params), params);
        Map<String, Object> map = new HashMap<>(2);
        List<NestMaintenancePageVO> voList = page.getRecords();
        for (NestMaintenancePageVO vo : voList) {//获取项目列表
            vo.setMaintainName(getProjectStr(vo.getMaintainName()));
        }
        map.put("records", page);
        return RestRes.ok(map);
    }

//    @Override
//    public RestRes downloadAttachment(HttpServletResponse response, String fileName) {
//        OutputStream os = null;
//        InputStream is;
//        try {
//            response.setCharacterEncoding("utf-8");
//            response.setContentType("application/octet-stream");
//            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
//            os = response.getOutputStream();
//            is = MinIoUnit.download(geoaiUosProperties.getMinio().getBucketName(), fileName);
//            int len;
//            byte[] buf = new byte[10240];
//            while ((len = is.read(buf)) != -1) {
//                os.write(buf, 0, len);
//            }
//        } catch (IOException e) {
//            log.error("传输文件时发生错误", e);
//        } finally {
//            try {
//                if (os != null) {
//                    os.close();
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        return RestRes.ok();
//    }

    @Override
    public RestRes createNestMaintenanceRecord(NestMaintenanceDTO nestMaintenanceDTO) {
//        BeanUtils.copyProperties(nestMaintenanceDTO, nestMaintenanceEntity);
        Optional<NestMaintenanceEntity> opt = Optional.ofNullable(nestMaintenanceDTO).map(dto -> {
            NestMaintenanceEntity ent = new NestMaintenanceEntity();
            ent.setId(dto.getId());
            ent.setBaseNestId(dto.getNestId());
            ent.setType(dto.getType());
            ent.setProject(dto.getProject());
            ent.setStaff(dto.getStaff());
            ent.setRemarks(dto.getRemarks());
            ent.setBatteryIndex(dto.getBatteryIndex());
            ent.setMaintainName(dto.getMaintainName());
            ent.setAttachmentPath(dto.getAttachmentPath());
            return ent;
        });

        if (opt.isPresent()) {
            NestMaintenanceEntity nestMaintenanceEntity = opt.get();
            nestMaintenanceEntity.setStartTime(LocalDate.parse(nestMaintenanceDTO.getStartTime()));
            nestMaintenanceEntity.setEndTime(LocalDate.parse(nestMaintenanceDTO.getEndTime()));
            boolean res1 = this.saveOrUpdate(nestMaintenanceEntity);
            if (res1) {
                return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SUCCESS_OPERATION.getContent()));
            }
        }

        return RestRes.err("操作失败");
    }

    @Override
    public RestRes listNestMaintenanceRecord(Integer nestId) {
        List<NestMaintenanceEntity> maintenanceList = this.lambdaQuery()
                .eq(NestMaintenanceEntity::getNestId, nestId)
                .eq(NestMaintenanceEntity::getDeleted, false)
                .select(NestMaintenanceEntity::getId,
                        NestMaintenanceEntity::getStartTime,
                        NestMaintenanceEntity::getEndTime,
                        NestMaintenanceEntity::getType,
                        NestMaintenanceEntity::getProject,
                        NestMaintenanceEntity::getStaff,
                        NestMaintenanceEntity::getRemarks,
                        NestMaintenanceEntity::getBatteryIndex
                ).list().stream().peek(e -> e.setMaintainName(getProjectStr(e.getProject()))).collect(Collectors.toList());

        Map<String, Object> map = new HashMap<>(2);
        map.put("records", maintenanceList);
        return RestRes.ok(map);
    }

    /**
     * 查询维保记录-分页方式
     *
     * @param nestId
     * @param page
     * @param limit
     * @param startTime
     * @param endTime
     * @return
     */
    @Override
    public RestRes listNestMaintenanceRecordByPage(String nestId, Integer page, Integer limit, String startTime, String endTime) {
        Map<String, Object> params = new HashMap<>(2);
        params.put(Query.PAGE, page);
        params.put(Query.LIMIT, limit);
        Date startTimeDate = null, endTimeDate = null;
        if (StringUtil.isNotEmpty(startTime)) {
            startTimeDate = DateUtil.parse(startTime, "yyyy-MM-dd");
        }
        if (StringUtil.isNotEmpty(endTime)) {
            endTimeDate = DateUtil.parse(endTime, "yyyy-MM-dd");
        }
        IPage<NestMaintenanceVO> totalPage = baseMapper.getNestMaintenanceRecordPage(new Query<NestMaintenanceVO>().getPage(params)
                , nestId
                , startTimeDate
                , endTimeDate);
        PageUtils pageData = new PageUtils(totalPage);
        Map<String, Object> returnMap = new HashMap<>(2);
        List<NestMaintenanceVO> maintenanceList = totalPage.getRecords();
        maintenanceList.stream().forEach(e -> e.setMaintainName(getProjectStr(e.getProject())));
        //获取总体记录
        pageData.setList(maintenanceList);
        returnMap.put("page", pageData);
        return RestRes.ok(returnMap);
    }

    /**
     * 重构导出Excel 按照时间跟基站导出
     *
     * @param nestIds
     * @param startTime
     * @param endTime
     * @param response
     */
    @Override
    public void exportNestMaintenanceRecord(String nestIds, String startTime, String endTime, HttpServletResponse response) {
        List<NestMaintenanceExportVO> nestMaintenanceExportVOS = new ArrayList<>();
        String format = "yyyy-MM-dd";
        if (nestIds != null) {
            //获取单位名
            List<String> nestIdsList = Arrays.stream(Strings.commaDelimitedListToStringArray(nestIds)).filter(org.springframework.util.StringUtils::hasText).collect(Collectors.toList());
            List<NestOrgRefOutDTO> noRefs = nestOrgRefService.listNestOrgRefs(nestIdsList, true);
            //nestId 对应 组织名称。 多个组织名称按逗号隔开
            Map<String, String> nestIdToOrgNameMap = new HashMap<>(8);
            if (!CollectionUtils.isEmpty(noRefs)) {
                nestIdToOrgNameMap = noRefs.stream()
                        .collect(Collectors.toMap(NestOrgRefOutDTO::getNestId, NestOrgRefOutDTO::getOrgName, (oldValue, newValue) -> String.format("%s,\n%s", oldValue, newValue)));
            }
            //根据条件获取导出的VO，如果时间为空，默认指定一个月数据
            nestMaintenanceExportVOS = this.getBaseMapper().getNestMaintenanceRecordExport(nestIdsList
                    , DateUtil.beginOfDay(StringUtil.isEmpty(startTime) ? DateUtil.lastMonth() : DateUtil.parse(startTime, format))
                    , DateUtil.endOfDay(StringUtil.isEmpty(endTime) ? DateUtil.date() : DateUtil.parse(endTime, format)));
            int index = 1;
            //获取的VO进行数据处理
            for (NestMaintenanceExportVO vo : nestMaintenanceExportVOS) {
                //序号
                vo.setNum(index++);
                //单位
                vo.setOrgName(Optional.ofNullable(nestIdToOrgNameMap.get(vo.getNestId())).map(String::toString).orElseGet(() -> ""));
                //设备类型
                vo.setDevType(NestTypeEnum.getInstance(Integer.parseInt(vo.getType())).name());
                //维保类型
                vo.setType(NestMaintenanceEntity.getTypeName(Integer.parseInt(vo.getType())));
                //维保项目
                vo.setProjectName(getProjectStr(vo.getProjectName()));
            }
        }
        //自定义指定宽度跟样式。
        List handlers = new ArrayList<>();
        handlers.add(new CustomCellWidthStyleStrategy());
        handlers.add(EasyExcelStyleUtils.getStyleStrategy());
        EasyExcelUtils.writeAndResponseForHandler(String.format("维保记录文件_%s", DateUtil.format(LocalDateTime.now(), "yyyy-MM-dd"))
                , NestMaintenanceExportVO.class
                , nestMaintenanceExportVOS
                , response
                , handlers);
    }

}
