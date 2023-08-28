package com.imapcloud.nest.service.impl;

import com.imapcloud.nest.common.constant.MissionConstant;
import com.imapcloud.nest.model.DefectInfoEntity;
import com.imapcloud.nest.service.DefectTypeTrafficService;
import com.imapcloud.nest.service.MissionPhotoService;
import com.imapcloud.nest.service.StationInfraredRecordService;
import com.imapcloud.nest.service.SysTagService;
import com.imapcloud.nest.v2.common.properties.GeoaiUosProperties;
import com.imapcloud.nest.v2.manager.rest.FileManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Service
public class RecordServiceImpl{
    @Autowired
    private SysTagService sysTagService;
    @Autowired
    private DefectTypeTrafficService defectTypeTrafficService;
    @Resource
    private MissionPhotoService missionPhotoService;
    @Autowired
    private StationInfraredRecordService stationInfraredRecordService;

    @Resource
    private GeoaiUosProperties geoaiUosProperties;

    @Resource
    private FileManager fileManager;

    /**
     * 是否使用华为云（Huawei Cloud Machine）主机
     */
    private boolean isUsedHCM(){
        return geoaiUosProperties.getAi().isUseHuawei();
    }

//    /**
//     * 生成其他类型的标记图片（除缺陷、表记）
//     * @deprecated 2.2.3，该接口仅违建点中使用，已确认废弃，将在后续版本删除
//     */
//    @Deprecated
//    public String createOtherPhoto(String info, Long photoId) {
//        // 获取图片路径
//        MissionPhotoEntity missionPhotoEntity = missionPhotoService.getOne(new QueryWrapper<MissionPhotoEntity>().eq("deleted", false).eq("id", photoId));
//        // 保存缺陷图片
//        String photoUrl = missionPhotoEntity.getPhotoUrl();
//        String fileNginxUrl = geoaiUosProperties.getAi().getOtherFilePathNginx() + "/";
//        String newPhotoName = "otherPhoto-" + missionPhotoEntity.getId() + "-" + System.currentTimeMillis() + ".jpg";
//        createMarkPhoto(photoUrl, newPhotoName, info, fileNginxUrl);
//        return fileNginxUrl + newPhotoName;
//    }
//    /**
//     * 生成标记后的图片
//     * @deprecated 2.2.3，该接口仅违建点中使用，已确认废弃，将在后续版本删除
//     * @param photoUrl
//     * @param newPhotoName
//     * @param info
//     * @param fileNginxUrl
//     */
//    @Deprecated
//    @SneakyThrows(Exception.class)
//    public void createMarkPhoto(String photoUrl, String newPhotoName, String info, String fileNginxUrl) {
//        // 读取原图片信息
//        InputStream inputSteam = fileManager.getInputSteam(photoUrl);
//        BufferedImage image;
//        image = ImageIO.read(inputSteam);
//        Graphics g = image.getGraphics();
//        Graphics2D g2d = (Graphics2D) g;
//        g2d.setStroke(new BasicStroke(3.0f));
//        g2d.setColor(Color.RED);
//        Font font = new Font("宋体", Font.PLAIN, 30);
//        g2d.setFont(font);
//        //0.08909,0.10188000000000001,0.33141,0.3177,临河施工
//        String[] records = info.split(",");
//        for(int i=0;i<records.length;) {
//            int pixelX = (int) (Double.parseDouble(records[i]) * image.getWidth());
//            int pixelY = (int) (Double.parseDouble(records[i+1]) * image.getHeight());
//            int pixelX1 = (int) (Double.parseDouble(records[i+2]) * image.getWidth());
//            int pixelY1 = (int) (Double.parseDouble(records[i+3]) * image.getHeight());
//            int width = pixelX1 - pixelX;
//            int height = pixelY1 - pixelY;
//            //FontRenderContext frc = g2d.getFontRenderContext();
//            //TextLayout tl = new TextLayout(text, font, frc);
//            FontMetrics metrics = FontDesignMetrics.getMetrics(font);
//            int height1 = metrics.getHeight();
//            // 画缺陷框、写缺陷类型
//            g2d.drawRect(pixelX, pixelY, width, height);
//            g.drawString(records[i+4], pixelX, pixelY - height1 / 4);
//            i=i+5;
//        }
//        String destPath = fileNginxUrl + newPhotoName;
//        // 输出图片
//        FileUtils.forceMkdir(new File(destPath).getParentFile());
//        FileOutputStream outImgStream = new FileOutputStream(destPath);
//        if(photoUrl.endsWith("png")) {
//            ImageIO.write(image, "png", outImgStream);
//        }else {
//            ImageIO.write(image, "jpg", outImgStream);
//        }
//        outImgStream.flush();
//        outImgStream.close();
//        log.info("画图部分全部结束");
//    }



    /**
     * 处理标记的x，x1，y，y1，生成图片
     *
     * @param photoId
     * @param defectInfoEntityList
     * @param recordId
     * @param source               图片来源（0:缺陷,1:表计,2:红外,3:交通,4:河道,5:违建,6:定点取证））
     * @return
     */
    public String delaMarkInfo(Long photoId, List<DefectInfoEntity> defectInfoEntityList, Integer recordId, Integer source) {
        String info = "";
        for (int i = 0; i < defectInfoEntityList.size(); i++) {
            DefectInfoEntity e = defectInfoEntityList.get(i);
            e.setRecordId(recordId);
            e.setPhotoId(photoId);
            e.setSource(source);

            // 获取框框的坐标宽高
            double x = Double.parseDouble(e.getX().substring(0, e.getX().length() - 1)) / 100;
            double y = Double.parseDouble(e.getY().substring(0, e.getY().length() - 1)) / 100;
            double x1 = Double.parseDouble(e.getX1().substring(0, e.getX1().length() - 1)) / 100;
            double y1 = Double.parseDouble(e.getY1().substring(0, e.getY1().length() - 1)) / 100;
            // 缺陷标记图片的坐标和标记名称
            String title = e.getType();
            if(MissionConstant.PhotoType.METER.equals(source)){
                title = e.getNote();
            }
            if (i != defectInfoEntityList.size() - 1) {
                info = new StringBuilder().append(info)
                        .append(x).append(",").append(y).append(",")
                        .append(x1).append(",").append(y1).append(",")
                        .append(title).append(",").toString();
            } else {
                info = new StringBuilder().append(info)
                        .append(x).append(",").append(y).append(",")
                        .append(x1).append(",").append(y1).append(",")
                        .append(title).toString();
            }
        }
        return info;
    }
}
