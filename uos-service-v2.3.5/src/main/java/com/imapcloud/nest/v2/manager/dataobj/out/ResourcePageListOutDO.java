package com.imapcloud.nest.v2.manager.dataobj.out;

import lombok.Data;
import java.util.List;

/**
 * 页面资源
 *
 * @author boluo
 * @date 2022-05-23
 */
@Data
public class ResourcePageListOutDO {

    private List<RolePageInfo> rolePageInfoList;
    @Data
    public static class RolePageInfo {
        private Long roleId;
        private List<PageInfo> pageInfoList;
    }

    @Data
    public static class PageInfo {
        /**
         * 资源ID
         */
        private String pageResourceId;

        /**
         * 资源名称
         */
        private String pageResourceName;

        /**
         * 资源类型【0：菜；1：按钮】
         */
        private Integer pageResourceType;

        /**
         * 资源key值，application_type和page_resource_key唯一
         */
        private String pageResourceKey;

        private Integer seq;

        /**
         * 父级资源ID
         */
        private String parentPageResourceId;
    }
}
