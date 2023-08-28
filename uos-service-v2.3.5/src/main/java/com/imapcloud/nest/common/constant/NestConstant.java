package com.imapcloud.nest.common.constant;

/**
 * 全局常量
 *
 * @author: zhengxd
 * @create: 2020/8/14
 **/
public interface NestConstant {
    /**
     * 类型,0-固定机巢，1-mini机巢
     */
    interface NestType {
        Integer FIXED = 0;
        Integer MINI = 1;
    }

    /**
     * 类型,0-没删除，1-已标记删除
     */
    interface DeleteType {
        Integer NOT_DELETE = 0;
        Integer DELETED = 1;
    }

    /**
     * 类型,ZREO-0，ONE-1
     */
    interface CommonNum {
        Integer ZREO = 0;
        Integer ONE = 1;
    }

    /**
     * 类型,"0"-"0"，"1"-"1"
     */
    interface CommonStr {
        String ZREO = "0";
        String ONE = "1";
    }

    /**
     * 类型,RIGHT-true，NOT_RIGHT-false
     */
    interface CommonBol {
        boolean RIGHT = true;
        boolean NOT_RIGHT = false;
    }

    /**
     * 类型,-1 超管
     */
    interface CommonOrg {
        Integer SUPER_ORG = -1;
    }

}
