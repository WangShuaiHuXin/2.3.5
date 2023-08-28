package com.imapcloud.nest.utils.fineUtil;

import cn.hutool.core.util.ZipUtil;
import com.imapcloud.nest.utils.DataUtil;
import com.imapcloud.nest.utils.spring.SpringContextUtils;
import com.imapcloud.nest.utils.utm2gps.CoordinateConversion;
import com.imapcloud.nest.v2.manager.rest.FileManager;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

/**
 * @author wmin
 */
public class FineInspectionZipParseUtil {

//    @Deprecated
//    public static FineParseRes uploadAdnParseZip(String zipPath, String originalFilename) {
//        //上传文件
//        String unzipPath = zipPath.substring(0, zipPath.lastIndexOf(".")) + "/";
//        List<Map<String, Object>> maps = parseKml(unzipPath);
//        if (CollectionUtil.isEmpty(maps)) {
//            return null;
//        }
//
//        FineInspZipEntity fineInspZipEntity = new FineInspZipEntity();
//        fineInspZipEntity.setZipName(originalFilename);
//        fineInspZipEntity.setOriginZipUrl(unzipPath);
//
//        List<FineInspTowerEntity> fiteList = new ArrayList<>(maps.size());
//        for (Map<String, Object> map : maps) {
//            String dataFileName = unzipPath + map.get("kmlName") + "_" + map.get("name") + ".data";
//            String txtFileName = unzipPath + map.get("kmlName") + "_" + map.get("name") + ".txt";
//            String coordinates = (String) map.get("coordinates");
//            String[] split = coordinates.split(",");
//            FineInspTowerEntity fineInspTowerEntity = new FineInspTowerEntity();
//            fineInspTowerEntity.setTowerName((String) map.get("name"));
//            fineInspTowerEntity.setPointCloudFileUrl(txtFileName);
//            fineInspTowerEntity.setRouteFileUrl(dataFileName);
//            if (split.length == 3) {
//                fineInspTowerEntity.setTowerLng(Double.parseDouble(split[0]));
//                fineInspTowerEntity.setTowerLat(Double.parseDouble(split[1]));
//                fineInspTowerEntity.setTowerAlt(Double.parseDouble(split[2]));
//            }
//            fiteList.add(fineInspTowerEntity);
//        }
//        FineParseRes fineParseRes = new FineParseRes();
//        fineParseRes.setFineInspZipEntity(fineInspZipEntity);
//        fineParseRes.setFineInspTowerEntityList(fiteList);
//        return fineParseRes;
//    }

    @Deprecated
    public static Map<String, String> uploadZipAndUnzip(String fileName, String filePath) {
        //根据路径解压文件
        ZipUtil.unzip(filePath, Charset.forName("GBK"));
        Map<String, String> resMap = new HashMap<>(2);
        resMap.put("zipPath", filePath);
        resMap.put("originalFilename", fileName);
        return resMap;
    }

//    private static List<Map<String, Object>> parseKml(String unzipPath) {
//        List<Map<String, Object>> resList = new ArrayList<>();
//        SAXReader reader = new SAXReader();
//        Optional<File> first = Arrays.stream(FileUtil.ls(unzipPath)).filter(file -> file.getName().contains(".kml")).findFirst();
//        if (!first.isPresent()) {
//            return Collections.emptyList();
//        }
//        File file = first.get();
//        String kmlName = file.getName().split("\\.")[0];
//        try (FileInputStream fileInputStream = new FileInputStream(file);) {
//            Document doc = reader.read(fileInputStream);
//            Element rootElement = doc.getRootElement();
//            Element document = rootElement.element("Document");
//            List<Element> placemarkList = document.elements("Placemark");
//            for (Element e : placemarkList) {
//                Map<String, Object> eleMap = new HashMap<>(4);
//                Element nameEle = e.element("name");
//                Element pointEle = e.element("Point");
//                Element coordinatesEle = pointEle.element("coordinates");
//                String name = nameEle.getStringValue();
//                String coordinates = coordinatesEle.getStringValue();
//                eleMap.put("kmlName", kmlName);
//                eleMap.put("name", name);
//                eleMap.put("coordinates", coordinates);
//                resList.add(eleMap);
//            }
//            return resList;
//        } catch (DocumentException | IOException e) {
//            return Collections.emptyList();
//        }
//    }

    public static List<List<Double>> parseTxt(String fileUrl, Double lat, Double lng) {
        List<List<Double>> list = new ArrayList<>();
        CoordinateConversion coordinateConversion = new CoordinateConversion();
        try (InputStream is = getFileManager().getInputSteam(fileUrl);
             BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            for (String line = br.readLine(); line != null; line = br.readLine()) {
                List<Double> chiList = new ArrayList<>(3);
                String[] strList = line.split(" ");
                // UTM 转成 GPS坐标
                String utm = lng + " " + lat + " " + strList[0] + " " + strList[1];
                double[] latLon = coordinateConversion.utm2LatLon(utm);
                chiList.add(latLon[1]);
                chiList.add(latLon[0]);
                chiList.add(Double.parseDouble(strList[2]));
                list.add(chiList);
            }
            return list;
        } catch (IOException e) {
            return Collections.emptyList();
        }
    }

    private static FileManager getFileManager(){
        return SpringContextUtils.getBean(FileManager.class);
    }

    public static DataUtil.Data parseData(String fileUrl) {
        try(InputStream inputSteam = getFileManager().getInputSteam(fileUrl);
            ByteArrayOutputStream bos = new ByteArrayOutputStream()){
            IOUtils.copy(inputSteam, bos);
            return DataUtil.readDataMultiPartFile(bos.toByteArray());
        } catch (IOException e) {
            return null;
        }
    }
}
