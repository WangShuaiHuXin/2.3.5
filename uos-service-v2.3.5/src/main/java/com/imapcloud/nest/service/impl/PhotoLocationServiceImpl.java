package com.imapcloud.nest.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.config.Configure;
import com.deepoove.poi.plugin.table.LoopRowTableRenderPolicy;
import com.geoai.common.web.util.MessageUtils;
import com.imapcloud.nest.common.exception.NestException;
import com.imapcloud.nest.mapper.PhotoLocationMapper;
import com.imapcloud.nest.model.*;
import com.imapcloud.nest.service.*;
import com.imapcloud.nest.utils.*;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author zhengxd
 * @since 2021-06-02
 */
@Slf4j
@Service
public class PhotoLocationServiceImpl extends ServiceImpl<PhotoLocationMapper, PhotoLocationEntity> implements PhotoLocationService {

    @Resource
    private DefectInfoService defectInfoService;

    @Resource
    private MissionPhotoService missionPhotoService;

    @Resource
    private DataProblemService dataProblemService;

    @Resource
    private MissionRecordsService missionRecordsService;

    @Resource
    private MissionService missionService;

    @Resource
    private TaskService taskService;

    @Override
    public RestRes getPageList(Integer page, Integer limit, List photoIdList) {
        if (photoIdList != null && photoIdList.size() > 0) {
            Map<String, Object> params = new HashMap<>(3);
            params.put("page", page);
            params.put("limit", limit);
            IPage<PhotoLocationEntity> totalPage = baseMapper.getPhotoLocationList(new Query<PhotoLocationEntity>().getPage(params), photoIdList);
            List<PhotoLocationEntity> photoLocationEntityList = totalPage.getRecords();

            photoLocationEntityList.forEach(e -> {
                List<DefectInfoEntity> defectInfoEntities = defectInfoService.lambdaQuery().eq(DefectInfoEntity::getPhotoId, e.getPhotoId()).eq(DefectInfoEntity::getDeleted, false).list();
                List<String> defectTypeNames = defectInfoEntities.stream().map(DefectInfoEntity::getType).distinct().collect(Collectors.toList());
                e.setDefectName(CollectionUtils.isNotEmpty(defectTypeNames) ? StringUtils.join(defectTypeNames.toArray(), ",") : "");
                MissionRecordsEntity missionRecordsEntity = getMissionRecordsEntity(e.getPhotoId().intValue());
                e.setTaskName(missionRecordsEntity.getTaskName());
                if(e.getClearId()!=null) {
                    MissionPhotoEntity byId = missionPhotoService.getById(e.getClearId());
                    e.setClearPhoto(byId);
                }else {
                    List<MissionRecordsEntity> missionRecordsIds = getMissionRecordsIds(e.getPhotoId().intValue(),1);
                    if(missionRecordsIds.size()>0) {
                        List<MissionPhotoEntity> photo = getPhotos(missionRecordsIds.get(0).getId(),e.getPhotoId().intValue());
                        if (photo != null && photo.size() > 0) {
                            e.setClearPhoto(photo.get(0));
                        }
                    }
                }
            });
            totalPage.setRecords(photoLocationEntityList);
            Map<String, Object> map = new HashMap<>(2);
            map.put("page", new PageUtils(totalPage));
            return RestRes.ok(map);
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_NO_PHOTO_SELECTED.getContent()));
    }

    @Override
    public List<MissionRecordsEntity> getMissionRecordsIds(Integer id,Integer flag){
        MissionRecordsEntity missionRecordsEntity = getMissionRecordsEntity(id);
        //flag为1时默认显示当前问题照片所属架次的同任务分组的上一个架次的照片
        List<MissionRecordsEntity> missionRecordsEntities = baseMapper.getMission(missionRecordsEntity.getId(), missionRecordsEntity.getTaskId(),flag);
        List<MissionRecordsEntity> recordsEntities = new ArrayList<>();
        if(missionRecordsEntities.size()>0) {
            for (MissionRecordsEntity recordsEntity : missionRecordsEntities) {
                List<MissionPhotoEntity> list = baseMapper.getMissionPhoto(recordsEntity.getId());
                if (list.size() > 0) {
                    recordsEntities.add(recordsEntity);
                }
            }
        }
        return recordsEntities;
    }

    public MissionRecordsEntity getMissionRecordsEntity(Integer photoId){
        MissionPhotoEntity missionPhotoEntity = missionPhotoService.getById(photoId);
        Integer missionRecordsId = missionPhotoEntity.getMissionRecordsId();
        MissionRecordsEntity missionRecordsEntity = missionRecordsService.getById(missionRecordsId);
        MissionEntity missionEntity = missionService.getById(missionRecordsEntity.getMissionId());
        TaskEntity taskEntity = taskService.getById(missionEntity.getTaskId());
        missionRecordsEntity.setTaskId(taskEntity.getId());
        missionRecordsEntity.setTaskName(taskEntity.getName());
        return missionRecordsEntity;
    }

    @Override
    public IPage<MissionPhotoEntity> getPhoto(Integer current, Integer size,Integer missionRecordsId,Integer id){
        if(missionRecordsId!=null){
            List<MissionPhotoEntity> list = getPhotos(missionRecordsId,id);
            IPage<MissionPhotoEntity> page = new Page<>(current,size);
            int count = list.size();
            List<MissionPhotoEntity> pageList = new ArrayList<>();
//计算当前页第一条数据的下标
            int currId = current>1 ? (current-1)*size:0;
            for (int i=0; i<size && i<count - currId;i++){
                pageList.add(list.get(currId+i));
            }
            page.setSize(size);
            page.setCurrent(current);
            page.setTotal(count);
//计算分页总页数
            page.setPages(count %10 == 0 ? count/10 :count/10+1);
            page.setRecords(pageList);
            return page;
        }else {
            return null;
        }
    }

    public List<MissionPhotoEntity> getPhotos(Integer missionRecordsId,Integer id){
        MissionPhotoEntity missionPhotoEntity = missionPhotoService.getById(id);
        Double latitude = missionPhotoEntity.getLatitude();
        Double longitude = missionPhotoEntity.getLongitude();

        List<MissionPhotoEntity> list = baseMapper.getMissionPhoto(missionRecordsId);
        // 按照当前设置距离问题照片半径范围内查找，照片按照距离由近到远排序
        List<MissionPhotoEntity> collect = new ArrayList<>();
        if(latitude!=null&&longitude!=null) {
            collect = list.stream().sorted(new Comparator<MissionPhotoEntity>() {
                @Override
                public int compare(MissionPhotoEntity o1, MissionPhotoEntity o2) {
                    if (o1.getLongitude() == null || o1.getLatitude() == null) {
                        return -1;
                    } else if (o2.getLongitude() == null || o2.getLatitude() == null) {
                        return 1;
                    } else {
                        Double d1 = DistanceUtil.getDistance(longitude, latitude, o1.getLongitude(), o1.getLatitude());
                        Double d2 = DistanceUtil.getDistance(longitude, latitude, o2.getLongitude(), o2.getLatitude());
                        return d1.compareTo(d2);
                    }
                }
            }).collect(Collectors.toList());
        }else {
            collect = list;
        }
        return collect;
    }

    @Override
    public RestRes insertOrUpdate(PhotoLocationEntity photoLocationEntity,String fileName,String filePath) {
        if (fileName != null&&filePath!=null) {
            photoLocationEntity.setLocationName(fileName);
            photoLocationEntity.setLocationUrl(filePath);
        }

        // 用户没填写位置信息，则根据经纬度，获取位置的具体信息
        if (photoLocationEntity.getLocationInfo() == null) {
            if (photoLocationEntity.getLat() != null && photoLocationEntity.getLng() != null) {
                String address = getBDAddress(photoLocationEntity.getLat(), photoLocationEntity.getLng());
                photoLocationEntity.setLocationInfo(address);
            }
        }
        this.saveOrUpdate(photoLocationEntity);
        return RestRes.ok();
    }

    @Override
    public void exportPhotoWater(String photoIdList, HttpServletResponse response){
        // 下载文件
        String fileName = null;
        try {
            fileName = URLEncoder.encode("智慧丹灶小镇水务巡查报告.docx", "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/force-download");
        response.addHeader("Content-Disposition", "attachment;fileName=" + fileName);
        if (StringUtils.isBlank(photoIdList)) {
            return ;
        }
        List<String> ids = Arrays.asList(photoIdList.split(","));
        Integer type[] = {3,4,5,6};
        List<Integer> arrayList = Arrays.asList(type);
        List<DataProblemEntity> dataProblemEntities = dataProblemService.lambdaQuery().in(DataProblemEntity::getDataId, ids).in(DataProblemEntity::getProblemSource,arrayList).eq(DataProblemEntity::getDeleted, 0).list();
        if (dataProblemEntities.size()<=0) {
            return ;
        }
        int i = 1;
        for(DataProblemEntity dataProblemEntity:dataProblemEntities){
            dataProblemEntity.setOrderNo(i);
            List<PhotoLocationEntity> list = this.lambdaQuery().eq(PhotoLocationEntity::getPhotoId, dataProblemEntity.getDataId()).list();
            if(list.size()>0){
                dataProblemEntity.setLocationInfo(list.get(0).getLocationInfo());
            }
            dataProblemEntity.setRecordsTime(dataProblemEntity.getMissionRecordTime().format(DateTimeFormatter.ofPattern("yyyy年MM月dd日"))+"巡查");
            MissionPhotoEntity missionPhotoEntity = missionPhotoService.getById(dataProblemEntity.getDataId());
            Integer clearId = missionPhotoEntity.getClearId();
            if(clearId!=null) {
                MissionPhotoEntity clearMissionPhoto = missionPhotoService.getById(clearId);
                dataProblemEntity.setClearPhotoUrl(clearMissionPhoto.getPhotoUrl());
                if(clearMissionPhoto.getMissionRecordsId()!=null) {
                    MissionRecordsEntity missionRecord = missionRecordsService.getById(clearMissionPhoto.getMissionRecordsId());
                    dataProblemEntity.setClearRecordsTime(missionRecord.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy年MM月dd日"))+"巡查");
                }
            }else {
                List<MissionRecordsEntity> missionRecordsIds = getMissionRecordsIds(dataProblemEntity.getDataId().intValue(),1);
                if(missionRecordsIds.size()>0) {
                    dataProblemEntity.setClearRecordsTime(missionRecordsIds.get(0).getCreateTime().format(DateTimeFormatter.ofPattern("yyyy年MM月dd日"))+"巡查");
                    List<MissionPhotoEntity> photo = getPhotos(missionRecordsIds.get(0).getId(),dataProblemEntity.getDataId().intValue());
                    if (photo != null && photo.size() > 0) {
                        dataProblemEntity.setClearPhotoUrl(photo.get(0).getPhotoUrl());
                    }
                }
            }
            i++;
        }
        Map<String, List<String>> collect = dataProblemEntities.stream().collect(Collectors.groupingBy(e -> e.getMissionRecordTime().format(DateTimeFormatter.ofPattern("yyyy年MM月dd日")), Collectors.mapping(DataProblemEntity::getTaskName, Collectors.toList())));
        //String inspectTimeAndPlace = "于2021年6月18日巡查官山涌、南沙涌，于202年7月15日巡查XXX涌，";
        StringBuffer inspectTimeAndPlace = new StringBuffer();
        Iterator<Map.Entry<String, List<String>>> iterator = collect.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<String, List<String>> next = iterator.next();
            String key = next.getKey();
            List<String> value = next.getValue();
            value = value.stream().distinct().collect(Collectors.toList());
            String localtion = value.stream().collect(Collectors.joining("、"));
            inspectTimeAndPlace.append("于")
                    .append(key)
                    .append("巡查")
                    .append(localtion)
                    .append("，");
        }
        String str = inspectTimeAndPlace.toString();
        String inspectTimeAndPlaceStr = StringUtils.isNotEmpty(str)?str.substring(0,str.lastIndexOf("，")) + "。":"";
        Map<String, List<DataProblemEntity>> collect1 = dataProblemEntities.stream().collect(Collectors.groupingBy(DataProblemEntity::getTaskName));
        //String problem="官山涌整体水面情况良好，较多问题为新增建筑物。发现垃圾3处，新增建筑5处，疑似污水排放1处，疑似水面漂浮物1处、砂场1处。";
        List<Map> list = new ArrayList<>();
        Iterator<Map.Entry<String, List<DataProblemEntity>>> iterator1 = collect1.entrySet().iterator();
        while (iterator1.hasNext()){
            Map.Entry<String, List<DataProblemEntity>> next = iterator1.next();
            String key = next.getKey();
            List<DataProblemEntity> value = next.getValue();
            String maxProblem = "";
            String allProblem = "";
            Map<String, Long> stringLongMap = value.stream().map(DataProblemEntity::getProblemName).collect(Collectors.groupingBy(p -> p, Collectors.counting()));
            Iterator<Map.Entry<String, Long>> iterator2 = stringLongMap.entrySet().iterator();
            while (iterator2.hasNext()){
                Map.Entry<String, Long> next1 = iterator2.next();
                String key1 = next1.getKey();
                Long value1 = next1.getValue();
                allProblem = allProblem + key1 + value1 + "处，";
            }
            allProblem = StringUtils.isNotEmpty(allProblem)?allProblem.substring(0,allProblem.lastIndexOf("，")) + "。":"";
            Optional<Map.Entry<String, Long>> max0 = stringLongMap.entrySet()
                    .stream()
                    .max(Map.Entry.comparingByValue());
            StringBuffer result = new StringBuffer();
            result.append(key)
                    .append("整体水面情况良好，较多问题为"+max0.get().getKey())
                    .append(maxProblem)
                    .append("。发现")
                    .append(allProblem);
            Map problemMap = new HashMap();
            problemMap.put("problem",result.toString());
            list.add(problemMap);
        }
        Map<String, Object> datas = new HashMap<String, Object>() {
            {
                put("exportTime",new SimpleDateFormat("yyyy年MM月dd日").format(new Date()));
                put("inspectTimeAndPlace", inspectTimeAndPlaceStr);
                put("result", list);
                put("photos",dataProblemEntities);
                put("photos1",dataProblemEntities);
            }
        };
        ClassPathResource oldDoc = new ClassPathResource("word/TW.docx");
        LoopRowTableRenderPolicy policy = new LoopRowTableRenderPolicy();
        Configure config = Configure.builder()
                .bind("photos", policy).build();
        try {
            InputStream inputStream = oldDoc.getInputStream();
            XWPFTemplate template = XWPFTemplate.compile(inputStream,config)
                    .render(datas);
            OutputStream os = response.getOutputStream();
            template.writeAndClose(os);
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updatePhoto(Integer id, Integer clearId) {
        MissionPhotoEntity missionPhotoEntity = missionPhotoService.getById(id);
        missionPhotoEntity.setClearId(clearId);
        missionPhotoService.updateById(missionPhotoEntity);
    }

    /**
     * 百度地图-根据经纬度获取详细地址
     */
    public static String getBDAddress(Double lat, Double lng) {
        // 百度地图开发者密钥
        String BD_SERVICE_API = "ho2OQlrvsaRUByr46Xs99K0nihX2w9fm";

        StringBuilder path = new StringBuilder()
                .append("http://api.map.baidu.com/reverse_geocoding/v3/?ak=")
                .append(BD_SERVICE_API).append("&output=json&coordtype=wgs84ll")
                .append("&location=").append(lat).append(",").append(lng);

        HttpURLConnection con = null;
        try {
            URL url = new URL(path.toString());
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty(
                    "User-Agent",
                    "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/38.0.2125.104 Safari/537.36");
            con.setRequestProperty("Referer", "http://api.map.baidu.com/geocoder/v2/");
            String b = "0", c = "{", d = "status";
            String responseBody = new Scanner(con.getInputStream(), "utf-8").useDelimiter("\\A").next();
            System.out.println("getGDAddress: res=" + responseBody);

            JSONObject jsonObject = JSONObject.parseObject(responseBody);
            if (!b.equals(jsonObject.getString(d))) {
                throw new NestException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAILED_TO_GET_DETAILED_ADDRESS_ACCORDING_TO_LATITUDE_AND_LONGITUDE.getContent()));
            }
            if (!responseBody.startsWith(c)) {
                throw new NestException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAILED_TO_GET_DETAILED_ADDRESS_ACCORDING_TO_LATITUDE_AND_LONGITUDE.getContent()));
            }
            String address = jsonObject.getJSONObject("result").getString("formatted_address");
            return address;
//            return JSONObject.parseObject(jsonObject.getJSONObject("result").getString("addressComponent"), AddressDTO.class);
        } catch (Exception e) {
            throw new NestException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAILED_TO_GET_DETAILED_ADDRESS_ACCORDING_TO_LATITUDE_AND_LONGITUDE.getContent()));
        } finally {
            if (null != con) {
                con.disconnect();
            }
        }
    }
}
