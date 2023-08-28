package com.imapcloud.nest.v2.web.vo.resp;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 菜单信息签证官
 *
 * @author boluo
 * @date 2022-05-23
 */
public class ResourcePageInfoVo implements Serializable {

    private List<Info> infoList;
    @Data
    public static class Info {
        /**
         * 资源ID
         */
        private String id;

        /**
         * 资源名称
         */
        private String name;

        /**
         * 资源类型【0：菜；1：按钮】
         */
        private Integer pageResourceType;

        private String pageResourceKey;

        /**
         * 资源key值，application_type和page_resource_key唯一
         */
        private String url;

        /**
         * 分析应用 idenvalue值
         */
        private Integer idenValue;

        private Integer seq;

        private List<Info> childrenList;
    }
}
