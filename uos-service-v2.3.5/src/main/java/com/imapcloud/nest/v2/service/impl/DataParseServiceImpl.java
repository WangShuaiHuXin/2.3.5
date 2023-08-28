package com.imapcloud.nest.v2.service.impl;

import cn.hutool.core.util.ZipUtil;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.geoai.common.core.constant.SymbolConstants;
import com.geoai.common.core.exception.BizException;
import com.geoai.common.core.util.BizIdUtils;
import com.geoai.common.core.util.JsonUtils;
import com.imapcloud.nest.service.AirLineService;
import com.imapcloud.nest.service.MissionVideoService;
import com.imapcloud.nest.v2.manager.dataobj.in.CommonFileInDO;
import com.imapcloud.nest.v2.manager.dataobj.out.FileStorageOutDO;
import com.imapcloud.nest.v2.manager.rest.UploadManager;
import com.imapcloud.nest.v2.service.DataParseService;
import com.imapcloud.nest.v2.service.dto.in.VideoSrtInDTO;
import com.imapcloud.nest.v2.service.dto.out.FpiAirlinePackageParseOutDTO;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 数据解析业务接口实现
 *
 * @author Vastfy
 * @date 2023/2/22 9:34
 * @since 2.2.3
 */
@Slf4j
@Service
public class DataParseServiceImpl implements DataParseService {

    private final static String FPI_UNZIP_TEMP_PATH_PREFIX = "fpi_";

    @Resource
    private XmlMapper xmlMapper;

    @Resource
    private UploadManager uploadManager;

    @Resource
    private AirLineService airLineService;

    @Resource
    private MissionVideoService missionVideoService;

    @Override
    public FpiAirlinePackageParseOutDTO parseFinePatrolInspectionData(String filename, InputStream inputStream) {
        File tempPath;
        try {
            tempPath = Files.createTempDirectory(FPI_UNZIP_TEMP_PATH_PREFIX).toFile();
        } catch (IOException e) {
            log.error("创建临时文件失败", e);
            throw new BizException("临时文件夹创建失败");
        }
        ZipUtil.unzip(inputStream, tempPath, Charset.forName("GBK"));
        File[] files = tempPath.listFiles();
        if(Objects.isNull(files) || files.length < 1){
            throw new BizException("压缩文件为空");
        }
        // 解析.kml文件，获取业务数据信息
        Map<String, File> fileMap = Arrays.stream(files).collect(Collectors.toMap(File::getName, r -> r));
        Optional<File> optional = Arrays.stream(files).filter(r -> r.getName().toLowerCase().endsWith(".kml")).findFirst();
        if(!optional.isPresent()){
            throw new BizException("解压文件目录未找到kml文件");
        }
        File kmlFile = optional.get();
        FpiKml fpiKml;
        try {
            fpiKml = xmlMapper.readValue(kmlFile, FpiKml.class);
        } catch (IOException e) {
            log.error("压缩包解析失败", e);
            throw new BizException("压缩包解析失败");
        }
        List<Placemark> placemarks = fpiKml.getPlacemarks();
        List<FpiAirlinePackageParseOutDTO.TowerInfo> towerInfos = new ArrayList<>(placemarks.size());
        if(!CollectionUtils.isEmpty(placemarks)){
            String kmlFilename = kmlFile.getName().substring(0, kmlFile.getName().lastIndexOf(SymbolConstants.POINT));
            for (Placemark placemark : placemarks) {
                FpiAirlinePackageParseOutDTO.TowerInfo towerInfo = new FpiAirlinePackageParseOutDTO.TowerInfo();
                String[] coordinates = placemark.getCoordinates().get(0).split(SymbolConstants.COMMA);
                towerInfo.setPosition(Arrays.asList(coordinates));
                towerInfo.setTowerName(placemark.getName());

                // 上传.data文件
                String dataFilename = kmlFilename + SymbolConstants.UNDER_LINE + placemark.getName() + ".data";
                File dataFile = fileMap.get(dataFilename);
                if(Objects.isNull(dataFile) || !dataFile.exists()){
                    throw new BizException(String.format("解压文件目录未找到文件[%s]", dataFilename));
                }
                String dataUrl = uploadFile(dataFile);
                towerInfo.setDataUrl(dataUrl);

                // 上传.txt文件
                String txtFilename = kmlFilename + SymbolConstants.UNDER_LINE + placemark.getName() + ".txt";
                File txtFile = fileMap.get(txtFilename);
                if(Objects.isNull(txtFile) || !txtFile.exists()){
                    throw new BizException(String.format("解压文件目录未找到文件[%s]", dataFilename));
                }
                String txtUrl = uploadFile(txtFile);
                towerInfo.setTxtUrl(txtUrl);
                towerInfos.add(towerInfo);
            }
        }
        // 保存精细巡检杆塔数据文件信息
        FpiAirlinePackageParseOutDTO result = new FpiAirlinePackageParseOutDTO();
        result.setZipName(filename);
        result.setTowerList(towerInfos);
        String zipId = airLineService.saveFpiPoleTowerInfo(result);
        // 前端需要该字段
        result.setZipId(zipId);
        return result;
    }

    @Override
    public String parseFlightTrackData(VideoSrtInDTO data) {
        // 解析srt文件坐标信息
        String gpsJson = parseGpsJsonFromSrt(data.getInputStream());
        // 上传飞行轨迹解析坐标信息文件
        String gpsStorageUrl = uploadSrtGpsJsonFile(gpsJson, data.getSrtFilename().replace(".srt", ".json"));
        // 写库
        return missionVideoService.saveVideoGpsInfo(data.getVideoId(), data.getSrtFilename(), gpsStorageUrl);
    }

    private String uploadSrtGpsJsonFile(String gpsJson, String gpsJsonFilename) {
        try(InputStream inputStream = new ByteArrayInputStream(gpsJson.getBytes())) {
            CommonFileInDO fileInDO = new CommonFileInDO();
            fileInDO.setInputStream(inputStream);
            fileInDO.setFileMd5(BizIdUtils.simpleUuid());
            fileInDO.setFileName(gpsJsonFilename);
            Optional<FileStorageOutDO> outDO = uploadManager.uploadFile(fileInDO);
            if(outDO.isPresent()){
                FileStorageOutDO storage = outDO.get();
                return storage.getStoragePath() + SymbolConstants.SLASH_LEFT + storage.getFilename();
            }
        } catch (IOException e) {
            log.error("上传飞行轨迹解析JSON文件");
            throw new BizException("上传飞行轨迹解析JSON文件错误");
        }
        return null;
    }

    private String parseGpsJsonFromSrt(InputStream srtFileInputStream) {
        Map<String, String> map = new LinkedHashMap<>();
        try(InputStream inputStream = srtFileInputStream;
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))){
            String line;
            int id = 0;
            while ((line = bufferedReader.readLine()) != null) {
                if(line.contains("GPS")){
                    map.put(String.valueOf(id), getGps(line));
                    id++;
                }
            }
        } catch (IOException e) {
            log.error("上传飞行轨迹解析JSON文件");
            throw new BizException("解析飞行轨迹文件错误");
        }
        // 生成json文件
        return JsonUtils.writeJson(map);
    }

    private String getGps(String line) {
        StringBuilder gps = new StringBuilder();
        String separator = SymbolConstants.COMMA + SymbolConstants.SPACE;
        String[] strAry = line.split(separator);
        gps.append(strAry[4].substring(5));
        gps.append(separator);
        gps.append(strAry[5]);
        gps.append(separator);
        gps.append(strAry[6], 0, 2);
        return gps.toString();
    }

    private String uploadFile(File dataFile) {
        try {
            CommonFileInDO fileInDO = new CommonFileInDO();
            fileInDO.setInputStream(Files.newInputStream(dataFile.toPath()));
            fileInDO.setFileMd5(BizIdUtils.simpleUuid());
            fileInDO.setFileName(dataFile.getName());
            Optional<FileStorageOutDO> outDO = uploadManager.uploadFile(fileInDO);
            if(outDO.isPresent()){
                FileStorageOutDO storage = outDO.get();
                return storage.getStoragePath() + SymbolConstants.SLASH_LEFT + storage.getFilename();
            }
        } catch (IOException e) {
            log.error("上传精细巡检.data文件");
        }
        return "";
    }

    @Data
    @JacksonXmlRootElement
    static class FpiKml{
        @JacksonXmlElementWrapper(localName = "Document")
        @JacksonXmlProperty(localName = "Placemark")
        private List<Placemark> placemarks;
    }

    @Data
    static class Placemark {

        private String name;

        @JacksonXmlElementWrapper(localName = "Point")
        @JacksonXmlProperty(localName = "coordinates")
        private List<String> coordinates;
    }

}
