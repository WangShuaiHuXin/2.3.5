package com.imapcloud.nest.v2.manager.dataobj;

import com.geoai.common.core.util.DateUtils;
import lombok.Data;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * minio相关实体
 *
 * @author boluo
 * @date 2022-10-27
 */
@ToString
public class MinioDO {

    private MinioDO() {

    }

    public static final String DEFAULT_ORG_CODE = "orgCode";
    public static final String DEFAULT_NEST_ID = "nestId";
    public static final String DEFAULT_TIME = "2010-01-01 00:00:00";

    /**
     * minio无效的对象 tagVersion=-10000
     */
    public static final int DEFAULT_INVALID_OBJECT = -10000;

    public static String getOrgCode(String orgCode) {

        return StringUtils.isBlank(orgCode) ? DEFAULT_ORG_CODE : orgCode;
    }

    public static String getNestId(String nestId) {
        return StringUtils.isBlank(nestId) ? DEFAULT_NEST_ID : nestId;
    }

    public static String getNestName(String nestId, Map<String, String> map) {

        if (DEFAULT_NEST_ID.equals(nestId)) {
            return "其他";
        }
        String s = map.get(nestId);
        return s == null ? "-" : s;
    }

    public static String getObjectTime(LocalDateTime time) {
        try {
            return time == null ? DEFAULT_TIME : time.format(DateUtils.DATE_TIME_FORMATTER_OF_CN);
        } catch (Exception e) {
            return DEFAULT_TIME;
        }
    }

    @Data
    public static class TagDO {

        private String orgCode;

        private String nestId;

        private String objectTime;

        private String object;

        private String app = "uos";

        /**
         * 对象类型 MinioObjectTypeEnum的code
         */
        private String objectType;

        private String tagVersion;
    }
}
