package com.imapcloud.nest.utils;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;

/**
 * @author wmin
 * 获取图片属性
 */
public class ImageAttributeUtil {
    public static Map<Long, Map<String, Object>> listImageMap(List<Map<String, Object>> photoIdAndUrlList) {
        if (CollectionUtil.isNotEmpty(photoIdAndUrlList)) {
            Map<Long, Map<String, Object>> resMap = new HashMap<>(photoIdAndUrlList.size());
            for (Map<String, Object> map : photoIdAndUrlList) {
                Map<String, Object> imageMap = getImageMap((String) map.get("imageUrl"));
                if (ObjectUtil.isNotEmpty(imageMap.get("originLat")) && ObjectUtil.isNotEmpty(imageMap.get("originLng")) && ObjectUtil.isNotEmpty(imageMap.get("dataTime"))) {
                    String s = (String) imageMap.get("originLat") + imageMap.get("originLng") + imageMap.get("dataTime");
                    imageMap.put("photoMark", s.hashCode());
                } else {
                    imageMap.put("photoMark", -1);
                }

                resMap.put((Long) map.get("photoId"), imageMap);
            }
            return resMap;
        }
        return Collections.emptyMap();
    }

    public static Map<String, Object> getImageMap(String imageUrl) {
        if (imageUrl != null) {
            HttpURLConnection urlConnection = null;
            try {
                int i1 = imageUrl.lastIndexOf("/");
                int i2 = imageUrl.lastIndexOf(".");
                String substring = imageUrl.substring(i1 + 1, i2);
                String encode = URLEncoder.encode(substring, "UTF-8");
                String s = imageUrl.substring(0, i1 + 1) + encode + ".jpg";
                URL urlOne = new URL(s);
                urlConnection = (HttpURLConnection) urlOne.openConnection();
                urlConnection.connect();
                Metadata metadata1 = JpegMetadataReader.readMetadata(urlConnection.getInputStream());
                Map<String, Object> imageMap = new HashMap<>(4);
                for (Directory directory : metadata1.getDirectories()) {
                    for (Tag tag : directory.getTags()) {
                        if ("GPS Latitude".equals(tag.getTagName())) {
                            double lat = getLngOrLat(tag.getDescription());
                            imageMap.put("lat", lat);
                            imageMap.put("originLat", tag.getDescription());
                        }

                        if ("GPS Longitude".equals(tag.getTagName())) {
                            double lng = getLngOrLat(tag.getDescription());
                            imageMap.put("lng", lng);
                            imageMap.put("originLng", tag.getDescription());
                        }

                        if ("Image Height".equals(tag.getTagName())) {
                            long hPixels = getPixels(tag.getDescription());
                            imageMap.put("hPixels", hPixels);

                        }
                        if ("Image Width".equals(tag.getTagName())) {
                            long wPixels = getPixels(tag.getDescription());
                            imageMap.put("wPixels", wPixels);
                        }
                        if ("Date/Time".equals(tag.getTagName())) {
                            imageMap.put("dateTime", tag.getDescription());
                        }

                    }
                }
                return imageMap;
            } catch (IOException | JpegProcessingException e) {
                return Collections.emptyMap();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
        }
        return Collections.emptyMap();
    }

    private static double getLngOrLat(String description) {
        if (description != null) {
            String[] s = description.split(" ");
            if (s.length >= 3) {
                String s0 = s[0];
                String s1 = s[1];
                String s2 = s[2];
                if (s0 != null && s1 != null && s2 != null) {
                    return Double.parseDouble(s0.replace("°", "")) + Double.parseDouble(s1.replace("'", "")) / 60 + Double.parseDouble(s2.replace("\"", "")) / 3600;
                }
            }
        }
        return 0.0;
    }

    private static long getPixels(String description) {
        if (description != null) {
            String[] s = description.split(" ");
            if (s.length >= 2) {
                if (s[0] != null) {
                    return Long.parseLong(s[0]);
                }
            }
        }
        return 0L;
    }
}
