package com.imapcloud.nest.v2.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * @author wmin
 */
@Slf4j
public class DjiHmsUtils {
    private static JSONObject jsonObject;

    public static String getHmsZhTip(HmsEvent event) {
        if (Objects.isNull(event)) {
            return "";
        }
        Tip tip = getTip(keySplicing(event));
        if (Objects.isNull(tip) || Objects.isNull(tip.getZh())) {
            return "";
        }
        return tipSplicing(event, tip.getZh());
    }

    private static Tip getTip(String key) {
        if (Objects.isNull(jsonObject)) {
            if (!loadHmsJson()) {
                return null;
            }
        }
        return jsonObject.getObject(key, Tip.class);
    }

    //1、加载hms.json文件到内存中
    private static boolean loadHmsJson() {
        StringBuilder hmsStr = new StringBuilder();
        try {
            ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            org.springframework.core.io.Resource[] resources = resolver.getResources("classpath:hms.json");
            org.springframework.core.io.Resource resource = resources[0];
            try (BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
                String readLine = null;
                while ((readLine = br.readLine()) != null) {
                    hmsStr.append(readLine);
                }
            } catch (Exception e) {
                log.info("解析HMS报错:",e);
                return false;
            }
            jsonObject = JSON.parseObject(hmsStr.toString());

        } catch (FileNotFoundException e) {
            log.info("读取HMS找不到:",e);
            return false;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 如果是机场设备的告警，需要将dock_tip_拼接在{code}前，机场拼接逻辑：dock_tip_{code}
     * 以错误码为0x16100083为例，文案Key为：dock_tip_0x16100083
     * <p>
     * 如果是飞行器的告警，需要用户自行拼接文案Key。飞行器拼接逻辑：fpv_tip_{code}_{in_the_sky}
     * 以错误码为0x16100083为例：
     * 若协议中in_the_sky为0，说明设备在飞行时与在地面时，统一使用fpv_tip_0x16100083的文案Key。
     * 若协议中in_the_sky为1，设备在飞行时，文案Key为：fpv_tip_0x16100083_in_the_sky。设备在地面时，文案Key为：fpv_tip_0x16100083。
     * 提示语key拼接
     *
     * @return
     */
    private static String keySplicing(HmsEvent event) {
        String key = "";
        if (EquipmentEnum.DRONE_NEST.name().equalsIgnoreCase(event.getDomainType())) {
            key = "dock_tip_" + event.getCode();
        } else {
            key = "fpv_tip_";
            if (Objects.equals(InTheSkyEnum.IN_THE_SKY.getCode(), event.getInTheSky())) {
                key = key + event.getCode() + "_in_the_sky";
            } else {
                key = key + event.getCode();
            }
        }
        return key;
    }

    private static String tipSplicing(HmsEvent event, String tip) {
        String fullTip = tip;
        //1、飞行器警告
        if (EquipmentEnum.DRONE.name().equalsIgnoreCase(event.getDomainType())) {
            if (tip.contains("%alarmid")) {
                if (Objects.nonNull(event.getArgs()) && Objects.nonNull(event.getArgs().getAlarmid())) {
                    String hex = "0x" + Integer.toHexString(event.getArgs().getAlarmid());
                    fullTip = tip.replace("%alarmid", hex);
                }
            }
            if (tip.contains("%index")) {
                if (Objects.nonNull(event.getArgs()) && Objects.nonNull(event.getArgs().getSensorIndex())) {
                    Integer index = event.getArgs().getSensorIndex() + 1;
                    fullTip = tip.replace("%index", String.valueOf(index));
                }
            }

            if (tip.contains("%component_index")) {
                if (Objects.nonNull(event.getArgs()) && Objects.nonNull(event.getArgs().getComponentIndex())) {
                    Integer index = event.getArgs().getComponentIndex() + 1;
                    fullTip = tip.replace("%component_index", String.valueOf(index));
                }
            }

            if (tip.contains("%battery_index")) {
                if (Objects.nonNull(event.getArgs()) && Objects.nonNull(event.getArgs().getSensorIndex())) {
                    String batteryIndex = event.getArgs().getSensorIndex() == 0 ? "左" : "右";
                    fullTip = tip.replace("%battery_index", batteryIndex);
                }
            }

        }
        //2、机场警告
        else {
            if (tip.contains("%dock_cover_index")) {
                if (Objects.nonNull(event.getArgs()) && Objects.nonNull(event.getArgs().getSensorIndex())) {
                    String dockCoverIndex = event.getArgs().getSensorIndex() == 0 ? "左" : "右";
                    fullTip = tip.replace("%dock_cover_index", dockCoverIndex);
                }
            }

            if (tip.contains("%charging_rod_index")) {
                if (Objects.nonNull(event.getArgs()) && Objects.nonNull(event.getArgs().getSensorIndex())) {
                    String chargingRodIndex = "";
                    Integer sensorIndex = event.getArgs().getSensorIndex();
                    if (Objects.equals(sensorIndex, 0)) {
                        chargingRodIndex = "前";
                    }
                    if (Objects.equals(sensorIndex, 1)) {
                        chargingRodIndex = "后";
                    }
                    if (Objects.equals(sensorIndex, 2)) {
                        chargingRodIndex = "左";
                    }
                    if (Objects.equals(sensorIndex, 3)) {
                        chargingRodIndex = "右";
                    }
                    fullTip = tip.replace("%charging_rod_index", chargingRodIndex);
                }
            }
        }

        return fullTip;
    }

    @Data
    public static class Tip {
        private String de;
        private String en;
        private String es;
        private String fr;
        private String ja;
        private String ko;
        private String ru;
        private String tr;
        private String zh;
    }

    @Data
    public static class HmsEvent {

        private Integer module;
        /**
         * 是否飞行
         */
        private Integer inTheSky;

        /**
         * 告警码
         */
        private String code;

        /**
         * 参数
         */
        private HmsEventArgs args;

        private String domainType;
    }

    @Data
    public static class HmsEventArgs {
        private Integer componentIndex;
        private Integer sensorIndex;
        private Integer alarmid;
    }

    public enum HmsModuleEnum {
        FLIGHT_MISSIONS(0, "飞行任务"),
        EQUIPMENT_MANAGEMENT(1, "设备管理"),
        MEDIA(2, "媒体"),
        HMS(3, "hms");
        private Integer code;
        private String str;


        HmsModuleEnum(Integer code, String str) {
            this.code = code;
            this.str = str;
        }
    }

    public enum InTheSkyEnum {
        ON_THE_GROUND(0, "在地上"),
        IN_THE_SKY(1, "在天上");
        private Integer code;
        private String str;

        InTheSkyEnum(Integer code, String str) {
            this.code = code;
            this.str = str;
        }

        public Integer getCode() {
            return code;
        }

        public String getStr() {
            return str;
        }
    }

    public enum EquipmentEnum {
        DRONE, DRONE_NEST
    }

}
