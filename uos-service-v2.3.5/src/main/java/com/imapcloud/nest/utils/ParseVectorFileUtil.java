package com.imapcloud.nest.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.geoai.common.core.exception.BizException;
import com.geoai.common.web.util.MessageUtils;
import com.google.common.collect.Lists;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.web.vo.resp.GridRespVO;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @Classname parseVectorFileUtil
 * @Description 解析矢量区域文件
 * @Date 2023/3/9 10:32
 * @Author Carnival
 */
@Slf4j
@Component
public class ParseVectorFileUtil {

    /**
     * 解析单个kml
     */
    public static String parseKmlFile(MultipartFile file) {
        String result = null;
        String fileName = file.getOriginalFilename();
        if (StringUtils.hasText(fileName)) {
            String suffixName = fileName.substring(fileName.lastIndexOf("."));
            if (".kml".equals(suffixName)) {
                try (InputStream inputStream = file.getInputStream()) {
                    SAXReader reader = new SAXReader();
                    Document doc = reader.read(inputStream);
                    Element rootElement = doc.getRootElement();
                    result = getCoodinatesFromKML(rootElement);
                } catch (Exception e) {
                    throw new BizException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_AIRSPACEMANAGESERVICEIMPL_03.getContent()));
                }
            }
        }
        if (StringUtils.hasText(result)) {
            result = result.trim();
        }
        return result;
    }

    /**
     * 解析单个Json
     */
    public static String parseJsonFile(MultipartFile file) {
        String result = null;
        String fileName = file.getOriginalFilename();
        if (StringUtils.hasText(fileName)) {
            String suffixName = fileName.substring(fileName.lastIndexOf("."));
            if (".json".equals(suffixName) || ".geojson".equals(suffixName)) {
                String jsonParse = multipartFileToStr(file);
                if (!JsonUtil.isJsonObject(jsonParse)) {
                    return null;
                }
                JSONObject jsonObject = JSON.parseObject(jsonParse);
                result = getCoodinatesFromJSON(jsonObject);
            }
        }
        if (StringUtils.hasText(result)) {
            result = result.trim();
        }
        return result;
    }

    /**
     * 解析多个Json
     */
    public static List<GridRespVO.GridOrgParseVO> parseMultiJsonFile(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        List<GridRespVO.GridOrgParseVO> res = null;
        if (StringUtils.hasText(fileName)) {
            String suffixName = fileName.substring(fileName.lastIndexOf("."));
            if (".json".equals(suffixName) || ".geojson".equals(suffixName)) {
                String jsonParse = multipartFileToStr(file);
                if (!JsonUtil.isJsonObject(jsonParse)) {
                    return null;
                }
                JSONObject jsonObject = JSON.parseObject(jsonParse);
                res = getFeaturesFromJSON(jsonObject);
            }
        }
        return res;
    }


    /**
     * kml获取坐标信息
     */
    private static String getCoodinatesFromKML(Element element) {
        String res = "";
        for (Iterator<Element> ieson = element.elementIterator(); ieson.hasNext(); ) {
            Element elementSon = ieson.next();
            if ("coordinates".equals(elementSon.getName())) {
                res = dealStr(elementSon.getText());
            }
            if (!StringUtils.hasText(res)) {
                res = getCoodinatesFromKML(elementSon);
            }
        }
        return res;
    }

    /**
     * 处理kml文件空格和高度问题
     */
    private static String dealStr(String str) {
        String res = "";
        if (StringUtils.hasText(str)) {
            str = str.trim();
            str = str.replaceAll("\r|\n", "");
            String[] coorArr = str.split(" ");
            StringBuilder sb = new StringBuilder();
            for (String coor : coorArr) {
                if (StringUtils.hasText(coor)) {
                    String[] split = coor.split(",");
                    sb.append(split[0]);
                    sb.append(",");
                    sb.append(split[1]);
                    sb.append(" ");
                }
            }
            res = sb.toString();
            res = res.substring(0, res.length() - 1);
        }
        return res;
}

    /**
     * 文件解析
     */
    private static String multipartFileToStr(MultipartFile file) {
        try (InputStream is = file.getInputStream();) {
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            StringBuilder fileSb = new StringBuilder();
            String str;
            while ((str = br.readLine()) != null) {
                fileSb.append(str);
            }
            return fileSb.toString();
        } catch (IOException e) {
            throw new BizException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_AIRSPACEMANAGESERVICEIMPL_03.getContent()));
        }
    }

    /**
     * Json获取坐标信息
     */
    private static String getCoodinatesFromJSON(Object object) {
        String res = "";
        boolean isPoint = StringUtils.hasText(judgeIsPointForJson(object));
        // 兼容type是点的格式
        if (object instanceof JSONArray) {
            res = getCoodinatesFromJSON(((JSONArray) object).get(0));
        } else if (object instanceof JSONObject) {
            for (Map.Entry<String, Object> next : ((JSONObject) object).entrySet()) {
                if ("coordinates".equals(next.getKey())) {
                    StringBuilder sb = new StringBuilder();
                    JSONArray coordinates = (JSONArray) next.getValue();
                    // 如果Json是点，特殊处理
                    if (isPoint) {
                        for (Object tmp : coordinates) {
                            sb.append(tmp.toString());
                            sb.append(",");
                        }
                        sb.deleteCharAt(sb.length() - 1);
                        res = sb.toString();
                    } else {
                        JSONArray coorArray = findCoorArray(coordinates);
                        if (ObjectUtils.isEmpty(coorArray)) {
                            throw new BizException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_AIRSPACEMANAGESERVICEIMPL_03.getContent()));
                        }

                        for (Object tmp : coorArray) {
                            JSONArray a = (JSONArray) tmp;
                            sb.append(a.get(0).toString());
                            sb.append(",");
                            sb.append(a.get(1).toString());
                            sb.append(" ");
                        }
                        res = sb.toString();
                    }
                }
                if (!StringUtils.hasText(res)) {
                    res = getCoodinatesFromJSON(next.getValue());
                }
            }
        }
        return res;
    }

    /**
     * 判断是否是点类型
     */
    private static String judgeIsPointForJson(Object object) {
        String res = null;
        if (object instanceof JSONArray) {
            res = judgeIsPointForJson(((JSONArray) object).get(0));
        } else if (object instanceof JSONObject) {
            for (Map.Entry<String, Object> next : ((JSONObject) object).entrySet()) {
                if ("type".equals(next.getKey())) {
                    if ("Point".equals(next.getValue())) {
                        res = "Point";
                    }
                }
                if (!StringUtils.hasText(res)) {
                    res = judgeIsPointForJson(next.getValue());
                }
            }
        }
        return res;
    }

    private static JSONArray findCoorArray(Object object) {
        if (ObjectUtils.isEmpty(object) || !(object instanceof JSONArray)) return null;
        Object o1 = ((JSONArray) object).get(0);
        if (isCoor(o1))
            return (JSONArray) object;
        else {
            return findCoorArray(o1);
        }
    }

    private static boolean isCoor(Object object) {
        if (!ObjectUtils.isEmpty(object)) {
            if (object instanceof JSONArray) {
                Object obj = ((JSONArray) object).get(0);
                return obj instanceof BigDecimal;
            } else
                return false;
        }
        return false;
    }


    /**
     * Json获取坐标信息
     */
    private static List<GridRespVO.GridOrgParseVO> getFeaturesFromJSON(Object object) {
        List<GridRespVO.GridOrgParseVO> res = Lists.newArrayList();
        if (object instanceof JSONObject) {
            for (Map.Entry<String, Object> next : ((JSONObject) object).entrySet()) {
                if ("features".equals(next.getKey())) {
                    JSONArray featuresArr = (JSONArray)next.getValue();
                    for (Object obj : featuresArr) {
                        JSONObject jsonObject = (JSONObject) obj;
                        JSONObject properties = (JSONObject)jsonObject.get("properties");
                        String name = (String) properties.get("name");
                        String gridOrgVector = getCoodinatesFromJSON(jsonObject);
                        GridRespVO.GridOrgParseVO vo = new GridRespVO.GridOrgParseVO();
                        vo.setOrgName(name);
                        vo.setGridOrgVector(gridOrgVector);
                        res.add(vo);
                    }
                }
            }
        }
        return res;
    }
}
