package com.imapcloud.nest.utils;

import com.imapcloud.sdk.utils.StringUtil;

/**
 * 缓存可以的常量类
 *
 * @author wmin
 */
public class CacheKeyConstant {
    public final static String ONLINE_NEST_UUID_LIST = "onlineNestUuidList";
    public final static String ONLINE_USERNAME_LIST = "onlineUsernameList";
    public final static String WEBSOCKET_PUSH_BASE_CACHE_KEY = "nestId:%s:class:%s:dtoName:%s";
    public final static String CURRENT_REQUEST_USER_ID = "currentRequestUserId";
    public final static String CURRENT_REQUEST_USERNAME = "currentRequestUsername";
    public final static String LOGIN_STATE = "login:state:phone:";
    public final static String NEST_AIR_LINE_LIST = "nest:air:line:list";
}
