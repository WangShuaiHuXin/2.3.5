package com.imapcloud.nest.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.geoai.common.web.util.MessageUtils;
import com.imapcloud.nest.mapper.StationInfraredRecordMapper;
import com.imapcloud.nest.model.MissionPhotoEntity;
import com.imapcloud.nest.model.MissionPhotoTagRelEntity;
import com.imapcloud.nest.model.StationInfraredRecordEntity;
import com.imapcloud.nest.model.StationInfraredRecordRectangleEntity;
import com.imapcloud.nest.pojo.dto.AIRedRecognitionDto;
import com.imapcloud.nest.pojo.dto.StationInfraredRecordDto;
import com.imapcloud.nest.pojo.dto.reqDto.RecordDto;
import com.imapcloud.nest.pojo.vo.StationInfraredRecordRectangleVO;
import com.imapcloud.nest.service.*;
import com.imapcloud.nest.utils.*;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.common.properties.GeoaiUosProperties;
import com.imapcloud.nest.v2.manager.dataobj.in.CommonFileInDO;
import com.imapcloud.nest.v2.manager.rest.FileManager;
import com.imapcloud.nest.v2.manager.rest.UploadManager;
import com.imapcloud.sdk.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import sun.font.FontDesignMetrics;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Comparator.comparingLong;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toCollection;

/**
 * <p>
 * 变电站的设备出现的红外测温记录 服务实现类
 * </p>
 *
 * @author wmin
 * @since 2020-12-29
 */
@Slf4j
@Service
public class StationInfraredRecordServiceImpl extends ServiceImpl<StationInfraredRecordMapper, StationInfraredRecordEntity> implements StationInfraredRecordService {
    @Resource
    SysTagService sysTagService;
    @Resource
    MissionPhotoService missionPhotoService;
    @Resource
    MissionPhotoTagRelService missionPhotoTagRelService;
    @Resource
    private StationInfraredRecordRectangleService stationInfraredRecordRectangleService;
    @Autowired
    private SSHHelper sshHelper;

    @Resource
    private GeoaiUosProperties geoaiUosProperties;

    @Resource
    private FileManager fileManager;

    @Resource
    private UploadManager uploadManager;

    /**
     * 是否使用华为云（Huawei Cloud Machine）主机
     */
    private boolean isUsedHCM(){
        return geoaiUosProperties.getAi().isUseHuawei();
    }

    /**
     * 根据标签id获取分组图片列表
     * @param tagId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public RestRes queryRecordByTagId(Integer tagId) {
        List<Map> list = baseMapper.queryRecordByTagId(tagId);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("devicePhotos",list);
        return RestRes.ok(map);
    }




    /**
     * 根据标签id获取所有图片列表
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public RestRes queryPhotosByPhotoName(Map<String, Object> params) {
    	if(!params.containsKey("page") || !params.containsKey("limit")) {
    		params.put("page", 1);
    		params.put("limit", Integer.MAX_VALUE);
    	}
        IPage<StationInfraredRecordDto> list = baseMapper.queryPhotosByPhotoName(new Query<StationInfraredRecordDto>().getPage(params),params);
        List<StationInfraredRecordDto> photoInfoList = list.getRecords().stream().map(row -> {
            StationInfraredRecordDto resRow = new StationInfraredRecordDto();
            BeanUtils.copyProperties(row, resRow);
            return resRow;
        }).collect(Collectors.toList());

        photoInfoList.forEach(e -> {
            if (e.getSid() != null) {
                List<StationInfraredRecordRectangleEntity> list1 = stationInfraredRecordRectangleService.lambdaQuery().eq(StationInfraredRecordRectangleEntity::getDeleted, false).eq(StationInfraredRecordRectangleEntity::getPhotoId, e.getId()).list();
                e.setStationInfraredRecordRectangleEntities(list1);
            }
            //路径特殊符号处理，包含#号的缩略图，需要编码一下
            if(e !=null && StringUtil.isNotEmpty(e.getThumbnailUrl()) ){
                try {
                    e.setThumbnailUrl(URLEncoder.encode(e.getThumbnailUrl(), "utf-8"));
                } catch (UnsupportedEncodingException ex) {
                    ex.printStackTrace();
                }
            }
        });
        Map<String,Object> map = new HashMap<>();
        list.setRecords(photoInfoList);
        map.put("page",new PageUtils(list));
        return RestRes.ok(map);
    }

    /**
     * 曲线图
     * @param tagId
     * @param startTime
     * @param endTime
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public RestRes queryPicByMonth(Integer tagId,String photoName,String startTime,String endTime) {
        List<Map> maxList = baseMapper.queryMaxPicByMonth(tagId,photoName,startTime,endTime);
        List<Map> minList = baseMapper.queryMinPicByMonth(tagId,photoName,startTime,endTime);
        List<Map> avgList = baseMapper.queryAvgPicByMonth(tagId,photoName,startTime,endTime);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("maxMonth",maxList);
        map.put("minMonth",minList);
        map.put("avgMonth",avgList);
        return RestRes.ok(map);
    }

    /**
     * 获取ai识别的图片
     * @param aiRedRecognitionDto
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public RestRes getAiRecognitionPic(AIRedRecognitionDto aiRedRecognitionDto) {
        List<Map> tagPhotoNames = aiRedRecognitionDto.getTagPhotoNames();
        List<StationInfraredRecordDto> missionPhotoEntities = new ArrayList<>();
        if(tagPhotoNames!=null&&tagPhotoNames.size()>0){
            for (Map tagDeviceMap:tagPhotoNames) {
                String tagId = tagDeviceMap.get("tagId").toString();
                List<String> photoNames = (List<String>)tagDeviceMap.get("photoNames");
                if(photoNames!=null&&photoNames.size()>0) {
                    List<StationInfraredRecordDto> stationInfraredRecordDtos = baseMapper.queryPhotos(tagId);
                    stationInfraredRecordDtos = stationInfraredRecordDtos.stream().filter(e->photoNames.contains(e.getName())).collect(Collectors.toList());
                    missionPhotoEntities.addAll(stationInfraredRecordDtos);
                }
            }
        }
        List<StationInfraredRecordDto> unique = missionPhotoEntities.stream().collect(collectingAndThen(toCollection(() -> new TreeSet<>(comparingLong(StationInfraredRecordDto::getId))), ArrayList::new));
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("missionPhoto",unique);
        return RestRes.ok(map);
    }

    /**
     * 获取所有列表
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public RestRes getAllList() {
        return null;
    }

    /**
     * 设置阈值
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public RestRes setThreshold(String value) {
        baseMapper.setThreshold(value);
        return RestRes.ok();
    }

    /**
     * 获取阈值
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map getThreshold() {
        Map<String, Double> map = new HashMap<>();
        map.put("value",baseMapper.getThreshold());
        return map;
    }

    @Override
    public RestRes getTem(StationInfraredRecordRectangleEntity stationInfraredRecordRectangleEntity) {
        MissionPhotoEntity missionPhotoEntity = missionPhotoService.getById(stationInfraredRecordRectangleEntity.getPhotoId());
        StationInfraredRecordEntity stationInfraredRecordEntity = this.lambdaQuery()
                .eq(StationInfraredRecordEntity::getPhotoId,missionPhotoEntity.getId()).eq(StationInfraredRecordEntity::getDeleted,false).one();
        String cmd = "";
        String originPath = geoaiUosProperties.getStore().getOriginPath();
        if(stationInfraredRecordEntity == null || stationInfraredRecordEntity.getTiffUrl()==null) {
            String replace = missionPhotoEntity.getPhotoUrl().replace(originPath, geoaiUosProperties.getAi().getOriginPath());
            cmd = "cd /data/dji-measure" + " && " + "/data/dji-measure/djimeasure " + replace
                    + " " + stationInfraredRecordRectangleEntity.getX()
                    + " " + stationInfraredRecordRectangleEntity.getY()
                    + " " + stationInfraredRecordRectangleEntity.getX1()
                    + " " + stationInfraredRecordRectangleEntity.getY1();
        }else {
            if (2 == stationInfraredRecordRectangleEntity.getType()) {
                String tiffUrl = stationInfraredRecordEntity.getTiffUrl();
                String replace = tiffUrl.replace(originPath, geoaiUosProperties.getAi().getOriginPath());
                cmd = "cd /data/dji-measure" + " && " + "python /data/dji-measure/djimeasure.py " + replace
                        + " " + stationInfraredRecordRectangleEntity.getX()
                        + " " + stationInfraredRecordRectangleEntity.getY()
                        + " " + stationInfraredRecordRectangleEntity.getX1()
                        + " " + stationInfraredRecordRectangleEntity.getY1();
            }
        }
        try {
            log.info("#StationInfraredRecordServiceImpl.getTem# cmd={}", cmd);
            String result = sshHelper.exec(cmd);
            System.out.println("result:" + result);
            if (StringUtils.isNotBlank(result) && result.contains("ERROR")) {
                return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_PROBLEM_WITH_THE_IMAGE_FORMAT.getContent()));
            } else if (StringUtils.isBlank(result)) {
                return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_PROBLEM_UPLOADING_THE_COORDINATES.getContent()));
            } else {
                Map<String, Object> map = new HashMap<String, Object>();
                String[] split = result.split(" ");
                stationInfraredRecordRectangleEntity.setCreateTime(LocalDateTime.now());
                stationInfraredRecordRectangleEntity.setDeleted(false);
                if (1 == stationInfraredRecordRectangleEntity.getType() && split.length == 7) {
                    stationInfraredRecordRectangleEntity.setMaxTemperatureX(split[0]);
                    stationInfraredRecordRectangleEntity.setMaxTemperatureY(split[1]);
                    stationInfraredRecordRectangleEntity.setMinTemperatureX(split[2]);
                    stationInfraredRecordRectangleEntity.setMinTemperatureY(split[3]);
                    stationInfraredRecordRectangleEntity.setMaxTemperature(Double.parseDouble(split[4]));
                    stationInfraredRecordRectangleEntity.setMinTemperature(Double.parseDouble(split[5]));
                    stationInfraredRecordRectangleEntity.setAvgTemperature(Double.parseDouble(split[6]));
                    stationInfraredRecordRectangleEntity.setType(1);
                } else if (2 == stationInfraredRecordRectangleEntity.getType() && split.length == 1) {
                    stationInfraredRecordRectangleEntity.setAvgTemperature(Double.parseDouble(split[0]));
                    stationInfraredRecordRectangleEntity.setMaxTemperature(Double.parseDouble(split[0]));
                    stationInfraredRecordRectangleEntity.setMinTemperature(Double.parseDouble(split[0]));
                    stationInfraredRecordRectangleEntity.setType(2);
                }
                map.put("result", stationInfraredRecordRectangleEntity);
                return RestRes.ok(map);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_NO_KNOWN_EXCEPTIONS.getContent()));
        }
    }


    @Override
    public RestRes saveInfrared(StationInfraredRecordRectangleVO stationInfraredRecordRectangleVO) {
        List<StationInfraredRecordRectangleEntity> list = stationInfraredRecordRectangleVO.getList();
        Integer photoId = stationInfraredRecordRectangleVO.getPhotoId();
        if (photoId==null){
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
        }
        //删除该红外照片所有手动标记信息
        stationInfraredRecordRectangleService.lambdaUpdate()
                .eq(StationInfraredRecordRectangleEntity::getPhotoId,photoId)
                .eq(StationInfraredRecordRectangleEntity::getDeleted,false)
                .set(StationInfraredRecordRectangleEntity::getDeleted,true).update();
        if (CollectionUtils.isEmpty(list)){
            return RestRes.ok();
        }
        //新增手动标记信息
        MissionPhotoEntity missionPhotoEntity = missionPhotoService.getById(photoId);
        if (missionPhotoEntity == null) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_NOT_EXIST_IMAGE.getContent()));
        }
        stationInfraredRecordRectangleService.saveBatch(list);
        StationInfraredRecordEntity stationInfraredRecordEntity = this.lambdaQuery().eq(StationInfraredRecordEntity::getPhotoId, photoId)
                .eq(StationInfraredRecordEntity::getDeleted, false).one();
        if (stationInfraredRecordEntity==null){
            MissionPhotoEntity missionPhotoEntity1 = missionPhotoService.lambdaQuery()
                    .eq(MissionPhotoEntity::getMissionRecordsId, missionPhotoEntity.getMissionRecordsId())
                    .eq(MissionPhotoEntity::getPhotoType, missionPhotoEntity.getPhotoType() == 1 ? 0 : 4)
                    .eq(MissionPhotoEntity::getWaypointIndex,missionPhotoEntity.getWaypointIndex())
                    .eq(MissionPhotoEntity::getDeleted, false).one();
            String thumbnailUrl = missionPhotoEntity.getThumbnailUrl();
            if (missionPhotoEntity1 != null) {
                thumbnailUrl = missionPhotoEntity1.getThumbnailUrl();
            }
            stationInfraredRecordEntity = new StationInfraredRecordEntity();
            stationInfraredRecordEntity.setPhotoId(photoId);
            stationInfraredRecordEntity.setSunUrl(thumbnailUrl);
            stationInfraredRecordEntity.setModifyTime(LocalDateTime.now());
            stationInfraredRecordEntity.setCreateTime(LocalDateTime.now());
            stationInfraredRecordEntity.setDeleted(false);
            this.save(stationInfraredRecordEntity);
        }
        try {
            Drawing(list,missionPhotoEntity,stationInfraredRecordEntity);
        } catch (Exception e) {
            e.printStackTrace();
            return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAIL_PROBLEM_IMAGE_DRAWING.getContent()));
        }
        return RestRes.ok();
    }

    @Override
    public RestRes updateInfraredState(RecordDto recordDto) {
        boolean update = this.lambdaUpdate()
                .set(StationInfraredRecordEntity::getFlag, recordDto.getDefectStatus())
                .in(StationInfraredRecordEntity::getPhotoId, recordDto.getPhotoIds())
                .eq(StationInfraredRecordEntity::getDeleted, false)
                .update();
        return update?RestRes.ok():RestRes.err();
    }

    private void Drawing(List<StationInfraredRecordRectangleEntity> list, MissionPhotoEntity byId, StationInfraredRecordEntity stationInfraredRecordEntity) throws Exception{
        //String replace = byId.getPhotoUrl().replace(originPath, AIAfterPath);
        //System.out.println("红外图片地址:"+replace);
        // 读取原图片信息
        try(InputStream inputSteam = fileManager.getInputSteam(byId.getPhotoUrl())) {
        BufferedImage image = ImageIO.read(inputSteam);
        Graphics g = image.getGraphics();
        Graphics2D g2d = (Graphics2D) g;
        g2d.setStroke(new BasicStroke(1.0f));
        Font font = new Font("宋体", Font.PLAIN, 10);
        g2d.setFont(font);
        //获取阈值，用于判断是否存在缺陷
        Double info = baseMapper.getThreshold();

        int a = 0;
        for (StationInfraredRecordRectangleEntity stationInfraredRecordRectangleEntity : list) {
            if (stationInfraredRecordRectangleEntity.getType()==1){
                g2d.setColor(Color.white);
                int pixelX = (int) ((Double.parseDouble(stationInfraredRecordRectangleEntity.getX().replace("%",""))/100) * image.getWidth());
                int pixelY = (int) ((Double.parseDouble(stationInfraredRecordRectangleEntity.getY().replace("%",""))/100) * image.getHeight());
                int pixelX1 = (int) ((Double.parseDouble(stationInfraredRecordRectangleEntity.getX1().replace("%",""))/100) * image.getWidth());
                int pixelY1 = (int) ((Double.parseDouble(stationInfraredRecordRectangleEntity.getY1().replace("%",""))/100) * image.getHeight());
                int maxX = (int) ((Double.parseDouble(stationInfraredRecordRectangleEntity.getMaxTemperatureX().replace("%",""))/100) * image.getWidth());
                int maxY = (int) ((Double.parseDouble(stationInfraredRecordRectangleEntity.getMaxTemperatureY().replace("%",""))/100) * image.getHeight());
                int minX = (int) ((Double.parseDouble(stationInfraredRecordRectangleEntity.getMinTemperatureX().replace("%",""))/100) * image.getWidth());
                int minY = (int) ((Double.parseDouble(stationInfraredRecordRectangleEntity.getMinTemperatureY().replace("%",""))/100) * image.getHeight());
                int width = pixelX1 - pixelX;
                int height = pixelY1 - pixelY;
                FontMetrics metrics = FontDesignMetrics.getMetrics(font);
                //int height1 = metrics.getHeight();
                // 画缺陷框、写缺陷类型  1.画缺陷框  2.画实心圆标记   3.画描述文字框  4.填写文字内容
                //1.画缺陷框
                g2d.drawRect(pixelX, pixelY, width, height);

                //2.画实心圆标记
                // g2d.drawString(stationInfraredRecordRectangleEntity.get, pixelX, pixelY - height1 / 4);
                g2d.setColor(Color.blue);
                g2d.fillOval(minX-5, minY-5,10,10);
                g2d.setColor(Color.red);
                g2d.fillOval(maxX-5, maxY-5,10,10);

                //3.画描述文字框，拼接描述文字内容
                //3.1draw  最低温+最高温+平均温度
                String draw="max:"+stationInfraredRecordRectangleEntity.getMaxTemperature()+"℃,avg:"+stationInfraredRecordRectangleEntity.getAvgTemperature()+
                        "℃,min:"+stationInfraredRecordRectangleEntity.getMinTemperature()+"℃";
                g2d.setColor(Color.WHITE);
                g2d.fillRect(pixelX,pixelY-font.getSize(),draw.length()*10,font.getSize());

                //3.2最低温度框，拼接最低文字描述
                String minT=stationInfraredRecordRectangleEntity.getMinTemperature()+"℃";
                g2d.setColor(Color.RED);
                g2d.fillRect(minX+5, minY+5, 50, 20);

                //3.3最高温度框，拼接最高文字描述
                String maxT=stationInfraredRecordRectangleEntity.getMaxTemperature()+"℃";
                g2d.setColor(Color.RED);
                g2d.fillRect(maxX+5, maxY+5, 50, 20);

                //4.绘制文字
                //写最低温+最高温+平均温度
                g2d.setColor(Color.BLACK);
                g2d.drawString(draw,pixelX,pixelY);
                //写最低温
                g2d.setColor(Color.WHITE);
                g2d.drawString(minT,minX+5,minY+20);
                //写最高温
                g2d.setColor(Color.WHITE);
                g2d.drawString(maxT,minX+5,minY+20);
            }
            if (stationInfraredRecordRectangleEntity.getType()==2){
                g2d.setColor(Color.white);
                int pixelX = (int) ((Double.parseDouble(stationInfraredRecordRectangleEntity.getX().replace("%",""))/100) * image.getWidth());
                int pixelY = (int) ((Double.parseDouble(stationInfraredRecordRectangleEntity.getY().replace("%",""))/100) * image.getHeight());
                g2d.drawOval(pixelX-5, pixelY-5,10,10);
            }
            if(stationInfraredRecordRectangleEntity.getMaxTemperature()>info){
                a++;
            }
        }
        String newUr = "otherPhoto-" + byId.getId() + "-" + System.currentTimeMillis() + ".jpg";
        String newUrl = geoaiUosProperties.getAi().getOtherFilePathNginx() + newUr;
        Double maxTemperature = stationInfraredRecordEntity.getMaxTemperature();
        stationInfraredRecordEntity.setFlag(a>0?2:maxTemperature!=null&&maxTemperature>info?2:1);
        stationInfraredRecordEntity.setRecordUrl(newUrl);
        this.updateById(stationInfraredRecordEntity);
        // 输出图片
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(image, "jpeg", os);
        InputStream input = new ByteArrayInputStream(os.toByteArray());
        CommonFileInDO commonFileInDO = new CommonFileInDO();
        commonFileInDO.setFileName(newUrl);
        commonFileInDO.setInputStream(input);
        uploadManager.uploadFile(commonFileInDO);
        } catch (Exception e) {
            log.error("#StationInfraredRecordServiceImpl.Drawing#", e);
        }
    }
}
