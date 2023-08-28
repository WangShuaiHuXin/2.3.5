package com.imapcloud.nest.v2.service.impl;

import cn.hutool.core.util.ZipUtil;
import com.alibaba.fastjson.JSONException;
import com.geoai.common.core.util.BizIdUtils;
import com.geoai.common.web.util.MessageUtils;
import com.google.common.collect.Lists;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.exception.ExcelAnalysisException;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.geoai.common.web.rest.Result;
import com.imapcloud.nest.common.constant.DataConstant;
import com.imapcloud.nest.enums.ContentTypeEnum;
import com.imapcloud.nest.utils.MinIoUnit;
import com.imapcloud.nest.utils.MinioSavingUtil;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.common.exception.BusinessException;
import com.imapcloud.nest.v2.common.properties.GeoaiUosProperties;
import com.imapcloud.nest.v2.common.utils.FileUtils;
import com.imapcloud.nest.v2.common.utils.ZipFileUtils;
import com.imapcloud.nest.v2.dao.entity.PowerEquipmentLegerInfoEntity;
import com.imapcloud.nest.v2.dao.entity.PowerWaypointLedgerInfoEntity;
import com.imapcloud.nest.v2.manager.dataobj.in.PowerEquipmentInDO;
import com.imapcloud.nest.v2.manager.dataobj.in.PowerEquipmentQueryDO;
import com.imapcloud.nest.v2.manager.dataobj.in.PowerWaypointLedgerInfoInDO;
import com.imapcloud.nest.v2.manager.dataobj.out.AccountOutDO;
import com.imapcloud.nest.v2.manager.dataobj.out.PowerComponentInfoOutDO;
import com.imapcloud.nest.v2.manager.dataobj.out.PowerEquipmentInfoOutDO;
import com.imapcloud.nest.v2.manager.dataobj.out.PowerWaypointListInfoOutDO;
import com.imapcloud.nest.v2.manager.feign.AccountServiceClient;
import com.imapcloud.nest.v2.manager.listener.PowerEquipmentUploadListener;
import com.imapcloud.nest.v2.manager.sql.PowerComponentInfoManager;
import com.imapcloud.nest.v2.manager.sql.PowerEquipmentLegerInfoManager;
import com.imapcloud.nest.v2.manager.sql.PowerWaypointLedgerInfoManager;
import com.imapcloud.nest.v2.service.PowerEquipmentService;
import com.imapcloud.nest.v2.service.dto.in.PowerEquipmentInDTO;
import com.imapcloud.nest.v2.service.dto.in.PowerEquipmentJsonRootInDTO;
import com.imapcloud.nest.v2.service.dto.in.PowerEquipmentSubstationRouteListInDTO;
import com.imapcloud.nest.v2.service.dto.out.*;
import com.imapcloud.nest.v2.web.vo.req.PowerArtificialReqVO;
import com.imapcloud.nest.v2.web.vo.req.PowerEquipmentMatchReqVO;
import com.imapcloud.sdk.pojo.djido.FlightTaskPrepareDO;
import com.imapcloud.sdk.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import net.lingala.zip4j.unzip.UnzipUtil;
import org.agrona.collections.ArrayListUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.spring.web.json.Json;

import javax.annotation.Resource;
import javax.validation.constraints.Min;
import java.io.*;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

@Slf4j
@Service
public class PowerEquipmentServiceImpl implements PowerEquipmentService {

    private final Integer MAX_SIZE = 1024 * 1024 * 2;
    private String MapJson = "maps.json";
    private String SubstationRoute = "substationRouteList.json";
    private String SubstationTree = "substationTree.json";
    private String WholeUnit = "WholeUnit.json";
    private final String BUCKET = "power";
    @Resource
    private GeoaiUosProperties geoaiUosProperties;
    @Resource
    private PowerComponentInfoManager powerComponentInfoManager;

    @Resource
    private PowerEquipmentLegerInfoManager powerEquipmentLegerInfoManager;

    @Resource
    private PowerWaypointLedgerInfoManager powerWaypointLedgerInfoManager;

    @Override
    public PowerEquipmentDTO.PowerEquipmentUploadDto equipmentExcelUpload(MultipartFile file, String orgCode) {
        //文件类型
        String contentType = file.getContentType();
        if (!contentType.equalsIgnoreCase(ContentTypeEnum.XLS.getType()) && !contentType.equalsIgnoreCase(ContentTypeEnum.XLSX.getType())) {
            return PowerEquipmentDTO.PowerEquipmentUploadDto.builder()
                    .flag(false).resultString(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_POWEREQUIPMENTSERVICEIMPL_001.getContent())).build();
        }
        try {
            PowerEquipmentUploadListener powerEquipmentUploadListener = new PowerEquipmentUploadListener();
            EasyExcel.read(file.getInputStream(), PowerEquipmentDTO.PowerEquipmentFileDTO.class, powerEquipmentUploadListener).autoTrim(true)
                    .sheet().doRead();
            List<PowerEquipmentDTO.PowerEquipmentFileDTO> objList = powerEquipmentUploadListener.getObjList();
            List<PowerEquipmentDTO.PowerEquipmentFileDTO> saveList = objList.stream().map(e -> {
                e.setOrgCode(orgCode);
                return e;
            }).collect(Collectors.toList());
            PowerEquipmentQueryDO queryDO = new PowerEquipmentQueryDO();
            queryDO.setOrgCode(orgCode);
            List<String> powerEquipmentPmsIds = powerEquipmentLegerInfoManager.queryAllPmsIdByCondition(queryDO);
            List<PowerEquipmentInDO> resultList = saveList.stream().map(e -> {
                PowerEquipmentInDO inDO = new PowerEquipmentInDO();
                BeanUtils.copyProperties(e, inDO);
                return inDO;
            }).collect(Collectors.toList());
            //核查新增的数据
            List<PowerEquipmentInDO> updateList = resultList.stream().filter(e -> powerEquipmentPmsIds.contains(e.getPmsId())).collect(Collectors.toList());
            //核查更新的数据
            resultList.removeAll(updateList);
            //不存在的数据进行新增
            if (CollectionUtils.isNotEmpty(resultList)) {
                powerEquipmentLegerInfoManager.saveList(resultList);
            }
            //已存在的数据进行更新
            if (CollectionUtils.isNotEmpty(updateList)) {
                powerEquipmentLegerInfoManager.updateList(updateList);
            }
            return PowerEquipmentDTO.PowerEquipmentUploadDto.builder().resultString("已导入" + (resultList.size() + updateList.size()) + "条数据").flag(true).build();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public PowerEquipmentListOutDTO equipmentListQuery(Integer pageNo, Integer pageSize, String orgCode, String equipmentName, String spacingUnitName, String voltageLevel, String beginTime, String endTime
            , String equipmentType) {
        Page<PowerEquipmentLegerInfoEntity> legerInfoEntityPage = powerEquipmentLegerInfoManager.equipmentListQuery(pageNo, pageSize, orgCode, equipmentName, spacingUnitName, voltageLevel, beginTime, endTime, equipmentType);
        List<PowerEquipmentLegerInfoEntity> records = legerInfoEntityPage.getRecords();
        PowerEquipmentListOutDTO powerEquipmentListOutDTOS = new PowerEquipmentListOutDTO();
        if (CollectionUtils.isEmpty(records)) {
            return powerEquipmentListOutDTOS;
        }
        List<String> accountList = records.stream().map(e -> {
            return e.getModifierId();
        }).collect(Collectors.toList());
        List<AccountOutDO> accountOutDOS = powerEquipmentLegerInfoManager.queryAccountInfoByOrg(accountList);

        Map<String, AccountOutDO> accountMap = accountOutDOS.stream().collect(Collectors.toMap(AccountOutDO::getAccountId, q -> q));

        List<PowerEquipmentListOutDTO.PowerEquipmentObj> powerEquipmentObjs = records.stream().map(e -> {
            PowerEquipmentListOutDTO.PowerEquipmentObj build = PowerEquipmentListOutDTO.PowerEquipmentObj.builder().createdTime(e.getCreatedTime())
                    .equipmentId(e.getEquipmentId())
                    .equipmentName(e.getEquipmentName())
                    .equipmentPmsId(e.getPmsId())
                    .equipmentType(e.getEquipmentType())
                    .operationTime(e.getModifiedTime())
                    .spacingUnit(e.getSpacingUnitName())
                    .stationName(e.getSubstationName())
                    .voltageLevel(e.getVoltageLevel())
                    .lastOperator("")
                    .build();
            AccountOutDO accountOutDO = accountMap.get(e.getModifierId());
            if (!ObjectUtils.isEmpty(accountOutDO)) {
                build.setLastOperator(accountOutDO.getName());
            }
            return build;
        }).collect(Collectors.toList());
        powerEquipmentListOutDTOS.setTotal(legerInfoEntityPage.getTotal());
        powerEquipmentListOutDTOS.setPowerEquipmentLists(powerEquipmentObjs);
        return powerEquipmentListOutDTOS;
    }

    @Override
    @Transactional
    public void equipmentSaveOrUpdate(PowerEquipmentInDTO.PowerEquipmentSaveOrUpdateDTO saveOrUpdateDTO) {
        //保存及编辑前,判断当前pms_id是否已存在\
        PowerEquipmentQueryDO queryDO = new PowerEquipmentQueryDO();
        queryDO.setPmsId(saveOrUpdateDTO.getEquipmentPmsId());
        queryDO.setOrgCode(saveOrUpdateDTO.getOrgCode());
        //新增的pms-id是否重复
        PowerEquipmentInDO inDO = new PowerEquipmentInDO();
        BeanUtils.copyProperties(saveOrUpdateDTO, inDO);
        inDO.setSpacingUnitName(saveOrUpdateDTO.getSpacingUnit());
        inDO.setSubstationName(saveOrUpdateDTO.getStationName());
        inDO.setPmsId(saveOrUpdateDTO.getEquipmentPmsId());
        inDO.setOrgCode(saveOrUpdateDTO.getOrgCode());
        if (!StringUtils.hasText(saveOrUpdateDTO.getEquipmentId())) {
            List<String> pmsIds = powerEquipmentLegerInfoManager.queryAllPmsIdByCondition(queryDO);
            if (CollectionUtils.isNotEmpty(pmsIds)) {
                throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_POWEREQUIPMENTSERVICEIMPL_002.getContent()));
            }
            //保存操作
            ArrayList<PowerEquipmentInDO> powerEquipmentInDOS = new ArrayList<>();
            powerEquipmentInDOS.add(inDO);
            powerEquipmentLegerInfoManager.saveList(powerEquipmentInDOS);
        } else {
            //编辑是否和已有的冲突了
            queryDO.setEquipmentId(saveOrUpdateDTO.getEquipmentId());
            powerEquipmentLegerInfoManager.checkPmsIdIsExist(queryDO);
            //更新对应航点台账的pmsId数据
            powerWaypointLedgerInfoManager.checkAndUpdatePmsId(inDO);
            //编辑操作
            powerEquipmentLegerInfoManager.updateEquipment(inDO);
        }
    }

    @Override
    public PowerEquipmentListOutDTO.PowerEquipmentObj queryEquipmentById(String equipmentId) {
        List<PowerEquipmentLegerInfoEntity> powerEquipmentLegerInfoEntities = powerEquipmentLegerInfoManager.queryEquipmentById(equipmentId);
        if (CollectionUtils.isEmpty(powerEquipmentLegerInfoEntities)) {
            throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_POWEREQUIPMENTSERVICEIMPL_003.getContent()));
        }
        PowerEquipmentLegerInfoEntity powerEntity = powerEquipmentLegerInfoEntities.get(0);
        return PowerEquipmentListOutDTO.PowerEquipmentObj.builder().equipmentType(powerEntity.getEquipmentType())
                .voltageLevel(powerEntity.getVoltageLevel())
                .stationName(powerEntity.getSubstationName())
                .spacingUnit(powerEntity.getSpacingUnitName())
                .equipmentPmsId(powerEntity.getPmsId())
                .equipmentName(powerEntity.getEquipmentName())
                .equipmentId(powerEntity.getEquipmentId()).build();
    }

    @Override
    public boolean deleteEquipment(String equipmentId) {
        List<PowerEquipmentLegerInfoEntity> powerEquipmentLegerInfoEntities = powerEquipmentLegerInfoManager.queryEquipmentById(equipmentId);
        if (CollectionUtils.isEmpty(powerEquipmentLegerInfoEntities)) {
            throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_POWEREQUIPMENTSERVICEIMPL_004.getContent()));
        }
        List<String> equipmentList = new ArrayList<>();
        equipmentList.add(equipmentId);
        boolean flag = powerEquipmentLegerInfoManager.deleteEquipmentList(equipmentList);
        return flag;
    }

    @Override
    public boolean deleteEquipments(List<String> equipmentIds) {
        return powerEquipmentLegerInfoManager.deleteEquipmentList(equipmentIds);
    }

    @Override
    public boolean waypointEquipmentJsonUpload(String orgCode, MultipartFile file) {
        InputStream inputStream = null;
        try {
            inputStream = file.getInputStream();
            int available = inputStream.available();
            //获取文件大小
            if (available < 0 || available > MAX_SIZE) {
                throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_POWEREQUIPMENTSERVICEIMPL_005.getContent()));
            }
            ZipInputStream zipInputStream = new ZipInputStream(inputStream, Charset.forName("GBK"));
            List<String> stringList = new ArrayList<>();
            stringList.add(MapJson);
            stringList.add(SubstationRoute);
            stringList.add(WholeUnit);
            stringList.add(SubstationTree);
            List<String> repeatList = new ArrayList<>();
            repeatList.addAll(stringList);
            ZipEntry zipEntry = null;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                //判断名称
                String name = zipEntry.getName();
                //文件名称不能包含  /,所以直接判断是否包含 /判断是否存在文件夹
                boolean directory = name.contains("/");
                if (directory) {
                    throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_POWEREQUIPMENTSERVICEIMPL_013.getContent()));
                }
                boolean remove = stringList.remove(name);
                if (!remove) {
                    boolean contains = repeatList.contains(name);
                    //移除失败，校验是否重复
                    if (contains) {
                        zipInputStream.close();
                        throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_POWEREQUIPMENTSERVICEIMPL_006.getContent()));
                    }
                }
            }
            if (stringList.size() > 0) {
                zipInputStream.close();
                throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_POWEREQUIPMENTSERVICEIMPL_007.getContent()));
            }
            String path = BUCKET + "/" + orgCode + "/" + BizIdUtils.simpleUuid() + "/";
            Map<String, String> jsonString = explainJson(file, path);
            //解析文件
            try {
                //解析 substationTree
                PowerEquipmentJsonRootInDTO powerEquipmentJsonRootInDO = JSON.parseObject(jsonString.get(SubstationTree), PowerEquipmentJsonRootInDTO.class);
                if (ObjectUtils.isEmpty(powerEquipmentJsonRootInDO)) {
                    throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_POWEREQUIPMENTSERVICEIMPL_009.getContent()));
                }
                //解析实体类并保存dto-entity
                List<PowerWaypointLedgerInfoInDO> powerWaypointLedgerInfoInDOS = explainSubstationTreeJson(powerEquipmentJsonRootInDO, orgCode);
                // powerWaypointLedgerInfoInDOS = powerWaypointLedgerInfoInDOS.stream().filter(e -> StringUtils.hasText(e.getWaypointId())).collect(Collectors.toList());
                //解析 substationRouteList.json
                PowerEquipmentSubstationRouteListInDTO powerEquipmentSubstationRouteListInDTO = JSON.parseObject(jsonString.get(SubstationRoute), PowerEquipmentSubstationRouteListInDTO.class);
                List<PowerEquipmentSubstationRouteListInDTO.RouteList> routelist = new ArrayList<>();
                if (!ObjectUtils.isEmpty(powerEquipmentSubstationRouteListInDTO)) {
                    routelist = powerEquipmentSubstationRouteListInDTO.getROUTELIST();
                }
                //去对应 航点ID 和geopos 成map
                Map<String, String> geoPos = routelist.stream().collect(Collectors.toMap(PowerEquipmentSubstationRouteListInDTO.RouteList::getGuid_id, PowerEquipmentSubstationRouteListInDTO.RouteList::getGeopos));
                if (CollectionUtil.isNotEmpty(powerWaypointLedgerInfoInDOS)) {
                    //设置单位原来的数据为已删除,并执行数据插入操作
                    powerWaypointLedgerInfoManager.uploadWayPoint(powerWaypointLedgerInfoInDOS, orgCode, MapJson, SubstationTree, SubstationRoute, WholeUnit, path, geoPos);
                    return true;
                } else {
                    throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_POWEREQUIPMENTSERVICEIMPL_009.getContent()));
                }
            } catch (JSONException e) {
                throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_POWEREQUIPMENTSERVICEIMPL_008.getContent()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Map<String, String> explainJson(MultipartFile file, String path) throws IOException {
        Map<String, String> resultMap = new HashMap<>();
        String jsonStr = "";
        try(ZipInputStream zipInputStream = new ZipInputStream(file.getInputStream());
            BufferedInputStream bufferedInputStream = new BufferedInputStream(zipInputStream)){
            ZipEntry nextEntry = null;
            while ((nextEntry = zipInputStream.getNextEntry()) != null) {
                long size = nextEntry.getSize();
                byte[] bytes = new byte[(int) size];
                bufferedInputStream.read(bytes, 0, (int) size);
                if (nextEntry.getName().equals(SubstationTree)) {
                    jsonStr = new String(bytes);
                    resultMap.put(SubstationTree, jsonStr);
                }
                if (nextEntry.getName().equals(SubstationRoute)) {
                    jsonStr = new String(bytes);
                    resultMap.put(SubstationRoute, jsonStr);
                }
                // 不影响业务
//            ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
//            String uploadPath = MinIoUnit.upload(inputStream, geoaiUosProperties.getMinio().getBucketName(), nextEntry.getName(), path, "application/json");
            }
        }

        return resultMap;
    }


    public List<PowerWaypointLedgerInfoInDO> explainSubstationTreeJson(PowerEquipmentJsonRootInDTO powerEquipmentJsonRootInDTO, String orgCode) {
        List<PowerWaypointLedgerInfoInDO> powerWaypointLedgerInfoInDOS = new ArrayList<>();
        //设备层
        powerEquipmentJsonRootInDTO.getLeafInDTOList().stream().forEach(e -> {
            //单元层
            if (CollectionUtil.isNotEmpty(e.getList())) {
                e.getList().stream().forEach(e1 -> {
                    PowerWaypointLedgerInfoInDO powerWaypointLedgerInfoInDO = new PowerWaypointLedgerInfoInDO();
                    powerWaypointLedgerInfoInDO.setSubstationName(powerEquipmentJsonRootInDTO.getName());
                    powerWaypointLedgerInfoInDO.setEquipmentAreaName(e.getName());
                    powerWaypointLedgerInfoInDO.setEquipmentAreaId(e.getNumber());
                    powerWaypointLedgerInfoInDO.setSubRegionId(e1.getNumber());
                    powerWaypointLedgerInfoInDO.setSubRegionName(e1.getName());
                    powerWaypointLedgerInfoInDO.setOrgCode(orgCode);
                    if (CollectionUtil.isNotEmpty(e1.getList())) {
                        //子区域层
                        e1.getList().stream().forEach(e2 -> {
                            powerWaypointLedgerInfoInDO.setUnitLayerId(e2.getNumber());
                            powerWaypointLedgerInfoInDO.setUnitLayerName(e2.getName());
                            if (CollectionUtil.isNotEmpty(e2.getList())) {
                                e2.getList().stream().forEach(e3 -> {
                                    powerWaypointLedgerInfoInDO.setDeviceLayerId(e3.getNumber());
                                    powerWaypointLedgerInfoInDO.setDeviceLayerName(e3.getName());
                                    PowerWaypointLedgerInfoInDO E3Power = new PowerWaypointLedgerInfoInDO();
                                    BeanUtils.copyProperties(powerWaypointLedgerInfoInDO, E3Power);
                                    Map<String, String> photolist = e3.getPhotolist();
                                    if (photolist != null) {
                                        Set<String> objects = photolist.keySet();
                                        Iterator<String> iterator = objects.iterator();
                                        while (iterator.hasNext()) {
                                            String next = iterator.next();
                                            E3Power.setWaypointId(photolist.get(next));
                                            E3Power.setWaypointName(next);
                                            PowerWaypointLedgerInfoInDO E4Power = new PowerWaypointLedgerInfoInDO();
                                            BeanUtils.copyProperties(E3Power, E4Power);
                                            powerWaypointLedgerInfoInDOS.add(E4Power);
                                        }
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });
        return powerWaypointLedgerInfoInDOS;
    }

    @Override
    public PowerWaypointListInfoOutDTO waypointEquipmentList(String orgCode, String deviceLayer, String unitLayer, String subRegion, String equipmentArea, String equipmentStatu, String componentStatu, Integer pageNo, Integer pageSize) {
        PowerWaypointListInfoOutDO powerWaypointListInfoOutDO = powerWaypointLedgerInfoManager.queryWaypointListByCondition(orgCode, deviceLayer, unitLayer, subRegion, equipmentArea, equipmentStatu, componentStatu, pageNo, pageSize);
        PowerWaypointListInfoOutDTO powerWaypointListInfoOutDTO = new PowerWaypointListInfoOutDTO();
        if (powerWaypointListInfoOutDO.getTotal() != 0) {
            powerWaypointListInfoOutDTO.setTotal(powerWaypointListInfoOutDO.getTotal());
            List<PowerWaypointListInfoOutDTO.PowerWaypointInfoDTO> collect;
            if (CollectionUtil.isNotEmpty(powerWaypointListInfoOutDO.getInfoDTOList())) {
                collect = powerWaypointListInfoOutDO.getInfoDTOList().stream().map(e -> {
                    PowerWaypointListInfoOutDTO.PowerWaypointInfoDTO dto = new PowerWaypointListInfoOutDTO.PowerWaypointInfoDTO();
                    BeanUtils.copyProperties(e, dto);
                    return dto;
                }).collect(Collectors.toList());
                powerWaypointListInfoOutDTO.setInfoDTOList(collect);
            }
        }
        return powerWaypointListInfoOutDTO;
    }

    @Override
    public List<EquipmentOptionListOutDTO> equipmentOptionList(String orgCode, String keyWord) {
        List<PowerEquipmentInfoOutDO> dos = powerEquipmentLegerInfoManager.queryListByOrgAKyeWord(orgCode, keyWord);
        if (CollectionUtil.isNotEmpty(dos)) {
            return dos.stream().map(e -> {
                EquipmentOptionListOutDTO dto = new EquipmentOptionListOutDTO();
                dto.setEquipmentId(e.getEquipmentId());
                dto.setEquipmentName(e.getEquipmentName());
                return dto;
            }).collect(Collectors.toList());
        }
        return null;
    }

    @Override
    public void equipmatchArtificial(PowerArtificialReqVO vo) {
        powerEquipmentLegerInfoManager.updateEquipmentId(vo);
    }

    @Override
    public void componentmatchArtificial(PowerArtificialReqVO vo) {
        powerEquipmentLegerInfoManager.updateComponentId(vo);
    }

    @Override
    public PowerEquipmentMatchOutDTO componentmatchAuto(PowerEquipmentMatchReqVO vo) {
        //返回vo
        PowerEquipmentMatchOutDTO dto = new PowerEquipmentMatchOutDTO();
        //最终需要更新的 航点台账：设备台账pmsid
        Map<String, String> resultMap = new HashMap<>();
        //成功与失败的记录数
        Integer success = 0;
        Integer fail = 0;
        //查询单位下的部件信息
        List<PowerComponentInfoOutDO> powerComponentInfoOutDOS = powerComponentInfoManager.queryListByOrg(vo.getOrgCode());
        if (CollectionUtil.isEmpty(powerComponentInfoOutDOS)) {
            throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_POWEREQUIPMENTSERVICEIMPL_010.getContent()));
        }
        //查询单位下的航点台账信息
        PowerWaypointListInfoOutDO powerWaypointListInfoOutDO = powerWaypointLedgerInfoManager.queryWaypointListByOrg(vo.getOrgCode());
        List<PowerWaypointListInfoOutDO.PowerWaypointInfoDO> infoDTOList = powerWaypointListInfoOutDO.getInfoDTOList();
        if (CollectionUtil.isEmpty(infoDTOList)) {
            throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_POWEREQUIPMENTSERVICEIMPL_011.getContent()));
        }
        //根据设备类型进行类型
        for (PowerWaypointListInfoOutDO.PowerWaypointInfoDO infoDO : infoDTOList) {
            //没设备类型字段的，即是没匹配设备的，不参与根据设备类型进行匹配
            if (StringUtils.hasText(infoDO.getEquipmentType())) {
                String result = componentTypeMatch(infoDO.getEquipmentType(), powerComponentInfoOutDOS);
                if (StringUtils.hasText(result)) {
                    resultMap.put(infoDO.getWayPointStationId(), result);
                    success++;
                } else {
                    fail++;
                }
            } else {
                fail++;
            }
        }
        if (success > 0) {
            resultMap.forEach((key, value) -> {
                powerWaypointLedgerInfoManager.updateWaypointComponentId(key, value);
            });
        }
        dto.setFailureCount(fail);
        dto.setSuccessCount(success);
        return dto;
    }

    @Override
    public PowerEquipmentMatchOutDTO equipmatchAuto(PowerEquipmentMatchReqVO vo) {
        //返回vo
        PowerEquipmentMatchOutDTO dto = new PowerEquipmentMatchOutDTO();
        //最终需要更新的 航点台账：设备台账pmsid
        Map<String, String> resultMap = new HashMap<>();
        //成功与失败的记录数
        Integer success = 0;
        Integer fail = 0;
        //查询出当前单位下的所有设备台账
        List<PowerEquipmentInfoOutDO> equipmentInfoOutDOS = powerEquipmentLegerInfoManager.queryListByOrg(vo.getOrgCode());
        if (CollectionUtil.isEmpty(equipmentInfoOutDOS)) {
            throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_POWEREQUIPMENTSERVICEIMPL_012.getContent()));
        }
        //查询当前单位下所有的航点台账
        PowerWaypointListInfoOutDO powerWaypointListInfoOutDO = powerWaypointLedgerInfoManager.queryWaypointListByOrg(vo.getOrgCode());
        List<PowerWaypointListInfoOutDO.PowerWaypointInfoDO> infoDTOList = powerWaypointListInfoOutDO.getInfoDTOList();
        if (CollectionUtil.isEmpty(infoDTOList)) {
            throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_POWEREQUIPMENTSERVICEIMPL_011.getContent()));
        }
        //根据名称进行匹配
        for (PowerWaypointListInfoOutDO.PowerWaypointInfoDO powerWaypointInfoDO : infoDTOList) {
            // -1 设备层   0 单元层 方便后续不同层级不同匹配规则拆分
            if (vo.getCol().equals("-1")) {
                String result = matchByEquipmentLayer(powerWaypointInfoDO.getDeviceLayerName(), equipmentInfoOutDOS);
                if (StringUtils.hasText(result)) {
                    resultMap.put(powerWaypointInfoDO.getWayPointStationId(), result);
                    success++;
                } else {
                    fail++;
                }
            }
            //单元层
            if (vo.getCol().equals("0")) {
                String result = matchByEquipmentLayer(powerWaypointInfoDO.getUnitLayerName(), equipmentInfoOutDOS);
                if (StringUtils.hasText(result)) {
                    resultMap.put(powerWaypointInfoDO.getWayPointStationId(), result);
                    success++;
                } else {
                    fail++;
                }
            }
        }
        dto.setFailureCount(fail);
        dto.setSuccessCount(success);
        //对数据进行更新操作
        if (success > 0) {
            resultMap.forEach((key, value) -> {
                powerWaypointLedgerInfoManager.updateWaypointPmsId(key, value);
            });
        }
        return dto;
    }

    public String matchByEquipmentLayer(String wayPointName, List<PowerEquipmentInfoOutDO> equipmentInfoOutDOS) {
        //wayPointName 长度<=4 则直接进行匹配即可
        List<String> result = new ArrayList<>();
        equipmentInfoOutDOS.stream().forEach(e -> {
            if (wayPointName.length() <= 4) {
                if (e.getEquipmentName().equals(wayPointName)) {
                    result.add(e.getEquipmentId());
                }
            } else {
                //长度>4,则按照长度从5开始 逐渐加1 进行匹配,匹配失败则一直+1 直到length=wayPointName.length
                boolean b = matchLengthString(wayPointName, e.getEquipmentName(), 4);
                if (b) {
                    result.add(e.getEquipmentId());
                }
            }
        });
        //只有命中一个才算命中，其他都是匹配失败
        if (result.size() == 1) {
            return result.get(0);
        }
        return null;
    }

    /**
     * @param str1   航点匹配的字段
     * @param str2   设备名称
     * @param length 长度
     * @return
     */
    public boolean matchLengthString(String str1, String str2, int length) {
        //length =初始值为5    final endLength=str1.length
        int endLength = str1.length();
        while (length <= endLength) {
            if (str2.equals(str1.substring(0, length))) {
                return true;
            } else {
                length++;
            }
        }
        return false;
    }

    public String componentTypeMatch(String str, List<PowerComponentInfoOutDO> powerComponentInfoOutDOS) {
        List<String> result = new ArrayList<>();
        powerComponentInfoOutDOS.stream().forEach(e -> {
            if (e.getEquipmentType().equals(str)) {
                result.add(e.getComponentId());
            }
        });
        if (result.size() == 1) {
            return result.get(0);
        }
        return null;
    }

}
