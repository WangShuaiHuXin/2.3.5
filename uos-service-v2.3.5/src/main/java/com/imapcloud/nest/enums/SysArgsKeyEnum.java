package com.imapcloud.nest.enums;

/**
 * @author wmin
 */

public enum SysArgsKeyEnum {
    CURRENT_SYSTEM_VERSION(1, "currentSystemVersion"),
    SUPER_ADMIN_SYSTEM_TITLE(3,"superAdminSystemTitle"),
    SUPER_ADMIN_SYSTEM_THEME(3,"superAdminSystemTheme")
    ;

    /**
     * id对应数据库里面的id
     */
    private Integer id;
    /**
     * 名称对应数据库里面
     */
    private String name;

    SysArgsKeyEnum(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

}
