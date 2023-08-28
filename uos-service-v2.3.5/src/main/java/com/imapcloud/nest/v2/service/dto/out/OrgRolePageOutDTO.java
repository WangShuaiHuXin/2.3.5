package com.imapcloud.nest.v2.service.dto.out;

import lombok.Data;

import java.util.List;

/**
 * 角色页面资源
 *
 * @author boluo
 * @date 2022-05-23
 */
@Data
public class OrgRolePageOutDTO {

    private List<Info> infoList;

    @Data
    public static class Info {
        /**
         * 资源ID
         */
        private Long pageResourceId;

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
        private Long parentPageResourceId;

        private boolean active;
    }
}
