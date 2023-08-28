package com.imapcloud.nest.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.imapcloud.sdk.utils.StringUtil;
import org.mozilla.universalchardet.UniversalDetector;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

/**
 * @author wmin
 */
public class DataUtil {
    private final static String DEFAULT_ENCODE = "UTF-8";
    private final static String SPLIT_SIGN = "@%&";
    private final static String DES_KEY = "pIbJLMNbrRcMyULMYTfBVjcf";
    private final static String DATA_KEY = "Mirupdqo";


    public static Data readDataMultiPartFile(MultipartFile file) {
        try {
            byte[] bytes = file.getBytes();
            return readDataMultiPartFile(bytes);
        } catch (IOException e) {

        }
        return null;
    }

    /**
     * 读取网络传输的文件
     *
     * @param bytes
     * @return
     */
    public static Data readDataMultiPartFile(byte[] bytes) {
        int headParamsLength = 0;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(bytes)));
            StringBuilder fileSB = new StringBuilder();
            String str;
            while ((str = br.readLine()) != null) {
                fileSB.append(str);
            }
            String fileStr = fileSB.toString();
            String[] split = fileStr.split(SPLIT_SIGN);
            if (split.length == 3) {

                headParamsLength = fileStr.length() - split[split.length - 1].length();

                if ("<HEAD>".equals(split[0])) {
                    String params = split[1];
                    String data = split[2];

                    if (StringUtil.isEmpty(params) || StringUtil.isEmpty(data)) {
                        return null;
                    }

                    String paramsDec = DesUtil.decrypt(DES_KEY, params);

                    if (StringUtil.isEmpty(paramsDec)) {
                        return null;
                    }

                    String[] paramsSplit = paramsDec.split("&");
                    HeapParam heapParam = getHeapParam(paramsSplit);

                    if (paramsSplit.length >= 3) {
                        if (!"YF-PRO".equals(heapParam.getAppid())) {
                            return null;
                        }
                        Data d = new Data();
                        if (heapParam.getMultiMission()) {
                            d.setMultiMission(true);
                        } else {
                            d.setMultiMission(false);
                        }


                        byte[] dataFromFile = bytes;

                        byte[] dataBytes = BytesUtil.readBytes(dataFromFile, headParamsLength, dataFromFile.length - headParamsLength);
                        byte[] decryptBytes = xorDecrypt(dataBytes, DATA_KEY.getBytes());

                        String charSet = heapParam.getCoding() != null ? heapParam.getCoding() : getBytesEncode(decryptBytes);
                        String xor = new String(decryptBytes, charSet);
                        JSONObject xorJsonObject = JSON.parseObject(xor);
                        JSONObject dataObject = xorJsonObject.getJSONObject("data");
                        JSONArray jsonArray = dataObject.getJSONArray(d.isMultiMission ? "missions" : "waypointList");
                        d.setJsonData(jsonArray);
                        return d;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }


    /**
     * 异或解密
     *
     * @param bytes
     * @param key
     * @return
     */
    private static byte[] xorDecrypt(byte[] bytes, byte[] key) {
        if (bytes == null) {
            return null;
        }
        int len = bytes.length;
        for (int i = 0; i < len; i++) {
            bytes[i] ^= key[i % key.length];
        }
        return bytes;
    }

    private static class HeapParam {
        private String version;
        private Long timestamp;
        private String appid;
        private String coding;
        private boolean multiMission = false;

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public Long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(Long timestamp) {
            this.timestamp = timestamp;
        }

        public String getAppid() {
            return appid;
        }

        public void setAppid(String appid) {
            this.appid = appid;
        }

        public String getCoding() {
            return coding;
        }

        public void setCoding(String coding) {
            this.coding = coding;
        }

        public boolean getMultiMission() {
            return multiMission;
        }

        public void setMultiMission(boolean multiMission) {
            this.multiMission = multiMission;
        }
    }

    private static HeapParam getHeapParam(String[] params) {
        HeapParam heapParam = new HeapParam();
        for (int i = 0; i < params.length; i++) {
            String param = params[i];
            String[] splitChi = param.split("=");
            if ("version".equals(splitChi[0])) {
                heapParam.setVersion(splitChi[1]);
            }
            if ("timestamp".equals(splitChi[0])) {
                heapParam.setTimestamp(Long.parseLong(splitChi[1]));
            }
            if ("appid".equals(splitChi[0])) {
                heapParam.setAppid(splitChi[1]);
            }
            if ("coding".equals(splitChi[0])) {
                heapParam.setCoding(splitChi[1]);
            }
            if ("isMultiMission".equals(splitChi[0])) {
                heapParam.setMultiMission(Boolean.parseBoolean(splitChi[1]));
            }
        }
        return heapParam;
    }

    public static class Data {
        private boolean isMultiMission;
        private String data;
        private Integer points;
        private JSONArray jsonData;
        private JSONArray propList;

        public boolean isMultiMission() {
            return isMultiMission;
        }

        public void setMultiMission(boolean multiMission) {
            isMultiMission = multiMission;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }

        public JSONArray getJsonData() {
            return jsonData;
        }

        public void setJsonData(JSONArray jsonData) {
            this.jsonData = jsonData;
        }

        public Integer getPoints() {
            return points;
        }

        public void setPoints(Integer points) {
            this.points = points;
        }

        public JSONArray getPropList() {
            return propList;
        }

        public void setPropList(JSONArray propList) {
            this.propList = propList;
        }
    }

    private static String getBytesEncode(byte[] bytes) {
        UniversalDetector detector = new UniversalDetector(null);
        detector.handleData(bytes, 0, bytes.length);
        detector.dataEnd();
        String detectedCharset = detector.getDetectedCharset();
        detector.reset();
        if (detectedCharset == null || DEFAULT_ENCODE.equalsIgnoreCase(detectedCharset)) {
            return DEFAULT_ENCODE;
        } else {
            return "GBK";
        }
    }


}
