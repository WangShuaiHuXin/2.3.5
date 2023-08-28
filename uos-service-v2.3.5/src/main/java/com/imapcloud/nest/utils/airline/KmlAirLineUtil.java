package com.imapcloud.nest.utils.airline;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONArray;
import com.imapcloud.nest.pojo.dto.ParseKmlResDTO;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.util.*;

/**
 * Created by wmin on 2020/12/12 23:24
 *
 * @author wmin
 */
public class KmlAirLineUtil {

    public static ParseKmlResDTO parseKml(InputStream inputStream) {
        ParseKmlResDTO parseKmlResDTO = new ParseKmlResDTO();
        JSONArray jsonArray = new JSONArray();
        parseKmlResDTO.setJsonArray(jsonArray);
        SAXReader reader = new SAXReader();
        try {
            Document doc = reader.read(inputStream);
            Element rootElement = doc.getRootElement();
            Element document = rootElement.element("Document");
            Element folder = document.element("Folder");
            parseKmlResDTO.setAbsolute(getAbsolute(document.element("Placemark")));
            List<Element> placemarkList = folder.elements("Placemark");
            for (Element e : placemarkList) {
                Map<String, Object> pointMap = new HashMap<>();
                Element point = e.element("Point");
                Element coordinates = point.element("coordinates");
                String stringValue = coordinates.getStringValue();
                if (stringValue != null) {
                    String[] split = stringValue.split(",");
                    pointMap.put("aircraftLocationLongitude", Double.valueOf(split[0]));
                    pointMap.put("aircraftLocationLatitude", Double.valueOf(split[1]));
                    pointMap.put("aircraftLocationAltitude", Double.valueOf(split[2]));
                }
                Element extendedData = e.element("ExtendedData");
                Element gimbalPitch = extendedData.element("gimbalPitch");
                pointMap.put("gimbalPitch", gimbalPitch == null ? 0 : Double.parseDouble(gimbalPitch.getStringValue()));
                Element heading = extendedData.element("heading");
                Double headingDouble = Double.parseDouble(heading.getStringValue());
                pointMap.put("aircraftYaw", headingDouble.intValue());
                Element speed = extendedData.element("speed");
                pointMap.put("speed", speed == null ? 0 : Double.parseDouble(speed.getStringValue()));
                List<Element> actions = extendedData.elements("actions");
                if (CollectionUtil.isNotEmpty(actions)) {
                    kmlAction2PointCountAction(pointMap, actions);
                }

                if (null == pointMap.get("waypointType")) {
                    if ((gimbalPitch != null && gimbalPitch.getStringValue() != null) || heading.getStringValue() != null) {
                        pointMap.put("waypointType", 1);
                    }else {
                        pointMap.put("waypointType", 2);
                    }
                }
                jsonArray.add(pointMap);
            }

            return parseKmlResDTO;
        } catch (DocumentException e) {
            return null;
        }
    }

    private static void kmlAction2PointCountAction(Map<String, Object> jsonObject, List<Element> actionElementList) {
        for (Element e : actionElementList) {
            String kmlActionName = e.getStringValue();
            switch (kmlActionName) {
                case "ShootPhoto":
                    jsonObject.put("waypointType", 0);
                    break;
                case "StartRecording":
                    break;
                case "StopRecording":
                    break;
                case "GimbalPitch":
                    double gPitch = Double.parseDouble(e.attributeValue("param"));
                    jsonObject.put("gimbalPitch", gPitch >= -90 && gPitch <= 30 ? gPitch : gPitch / 10);
                    jsonObject.put("waypointType", 1);
                    break;
                case "Hovering":
                    jsonObject.put("waypointType", 1);
                    break;
                case "AircraftYaw":
                    jsonObject.put("aircraftYaw", Integer.valueOf(e.attributeValue("param")));
                    jsonObject.put("waypointType", 1);
                    break;
                default:
                    break;
            }

        }
    }

    /**
     * 如果KML航线没有改配置的，默认当作绝对海拔来飞行
     *
     * @param placemark
     * @return
     */
    private static boolean getAbsolute(Element placemark) {
        Element extendedData = placemark.element("ExtendedData");
        Element droneInfo = extendedData.element("droneInfo");
        if (droneInfo == null) {
            return true;
        }
        Element droneHeight = droneInfo.element("droneHeight");
        if (droneHeight == null) {
            return true;
        }
        Element useAbsolute = droneHeight.element("useAbsolute");
        if (useAbsolute == null) {
            return true;
        }
        return Boolean.parseBoolean(useAbsolute.getStringValue());
    }
}
