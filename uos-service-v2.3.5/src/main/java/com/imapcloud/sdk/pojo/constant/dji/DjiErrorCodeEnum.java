package com.imapcloud.sdk.pojo.constant.dji;

import java.util.Objects;

public enum DjiErrorCodeEnum {
    UNKNOWN(-1, "大疆未知错误"),
    SUCCESS(0, "正常"),
    FAIL(1,"失败"),
    TIMEOUT(2,"超时"),

    ROUTE_MANAGER_219004(219004, "飞行任务已过期，无法执行"),
    ROUTE_MANAGER_219016(219016, "飞行任务已被挂起，无法执行"),
    ROUTE_MANAGER_219018(219018, "机场执行飞行任务过程中超时未响应，1h未上报任何状态"),
    ROUTE_MANAGER_219101(219101, "服务器异常，请稍后重试"),
    ROUTE_MANAGER_219102(219102, "机场已离线，无法执行飞行任务，请检查机场是否已断电，机场网络连接是否正常"),
    ROUTE_MANAGER_219103(219103, "项目中不存在此机场，无法执行飞行任务"),
    ROUTE_MANAGER_219104(219104, "服务器内部错误，无法执行飞行任务"),
    ROUTE_MANAGER_219105(219105, "云端与机场通信异常，无法执行飞行任务"),
    ROUTE_MANAGER_219106(219106, "机场执行飞行任务过程中超时未响应"),

    ROUTE_MANAGER_312010(312010, "下发的升级请求命令与协议不符"),
    ROUTE_MANAGER_312012(312012, "下发的 sn 不在目标设备列表内，下发了错误的 sn"),
    ROUTE_MANAGER_312013(312013, "目标设备可能未向远程升级进行过0x82升级传输注册"),
    ROUTE_MANAGER_312014(312014, "设备已在升级中"),
    ROUTE_MANAGER_312015(312015, "请求 drone_nest 进入升级模式失败，可能是工作状态不允许失败"),
    ROUTE_MANAGER_312016(312016, "进度50时正翻转或75逆翻转时，sdr链路翻转失败"),
    ROUTE_MANAGER_312022(312022, "触发飞行器开机后或 drone_nest 返回飞行器在舱后120s内未收到sdr链接"),
    ROUTE_MANAGER_312023(312023, "drone_nest 查询飞行器在舱状态返回0"),
    ROUTE_MANAGER_312024(312024, "sdr 连接飞行器后60s内未收到飞行器的0x82升级传输注册"),
    ROUTE_MANAGER_312025(312025, "删除旧的固件包失败"),
    ROUTE_MANAGER_312026(312026, "离线升级包解压失败"),
    ROUTE_MANAGER_312027(312027, "仅下发了飞行器升级命令，但飞行器不在位"),
    ROUTE_MANAGER_312028(312028, "设备下载或传输过程中异常重启"),
    ROUTE_MANAGER_312029(312029, "机场重启中，不支持设备升级"),
    ROUTE_MANAGER_312306(312306, "检查网络连接"),
    ROUTE_MANAGER_312534(312534, "飞行器升级失败飞行器在机场重启失败"),
    ROUTE_MANAGER_312704(312704, "飞行器电量低于20%，建议充电"),

    ROUTE_MANAGER_314000(314000, "服务器异常，请稍后重试或重启机场后重试"),
    ROUTE_MANAGER_314001(314001, "下发的航线任务url为空"),
    ROUTE_MANAGER_314002(314002, "下发的航线任务md5为空"),
    ROUTE_MANAGER_314003(314003, "MissionID不合法"),
    ROUTE_MANAGER_314004(314004, "获取不到云端航线，请检测航线路径"),
    ROUTE_MANAGER_314005(314005, "航线 md5 检验失败"),
    ROUTE_MANAGER_314006(314006, "等待飞机可上传航线超时（等待gs_state）"),
    ROUTE_MANAGER_314007(314007, "上传航线至飞机失败"),
    ROUTE_MANAGER_314008(314008, "等待飞机进入航线可执行状态超时"),
    ROUTE_MANAGER_314009(314009, "开启航线任务失败"),
    ROUTE_MANAGER_314010(314010, "航线执行失败"),
    ROUTE_MANAGER_314011(314011, "航线执行结果查询超时"),
    ROUTE_MANAGER_314012(314012, "飞行器起飞前准备失败，无法执行飞行任务，请重启机场后重试"),
    ROUTE_MANAGER_314013(314013, "获取不到航线文件"),
    ROUTE_MANAGER_314014(314014, "上一次任务未执行完"),

    ROUTE_MANAGER_316001(316001, "备降点设置失败"),
    ROUTE_MANAGER_316002(316002, "备降安全转移高度设备失败"),
    ROUTE_MANAGER_316003(316003, "设置起飞高度失败"),
    ROUTE_MANAGER_316004(316004, "失控行为设置失败"),
    ROUTE_MANAGER_316005(316005, "飞行器RTK收敛失败"),
    ROUTE_MANAGER_316006(316006, "飞行器降落前，机场准备失败"),
    ROUTE_MANAGER_316007(316007, "等待飞机就绪可设置参数超时"),
    ROUTE_MANAGER_316008(316008, "机场获取飞行器控制权失败，无法执行飞行任务，请确认遥控器未锁定控制权"),
    ROUTE_MANAGER_316009(316009, "飞行器电量低，无法执行飞行任务，请充电至30%以上后重试"),
    ROUTE_MANAGER_316010(316010, "机场未检测到飞行器，无法执行飞行任务，请检查舱内是否有飞行器，机场与飞行器是否已对频，或重启机场后重试"),
    ROUTE_MANAGER_316011(316011, "飞行器降落位置偏移过大，请检查飞行器是否需要现场摆正"),
    ROUTE_MANAGER_316012(316012, "文件夹染色失败"),
    ROUTE_MANAGER_316013(316013, "电池电量查询失败"),
    ROUTE_MANAGER_316014(316014, "飞行控制权推送接收超时"),
    ROUTE_MANAGER_316015(316015, "飞行器 RTK 收敛位置距离机场过远，无法执行飞行任务，请重启机场后重试"),
    ROUTE_MANAGER_316016(316016, "飞行器降落至机场超时，可能是机场与飞行器断连导致，请通过直播查看飞行器是否降落至舱内"),
    ROUTE_MANAGER_316017(316017, "获取飞行器媒体数量超时，可能是机场与飞行器断连导致，请通过直播查看飞行器是否降落至舱内"),
    ROUTE_MANAGER_316018(316018, "飞行任务执行超时，可能是机场与飞行器断连导致，请通过直播查看飞行器是否降落至舱内"),
    ROUTE_MANAGER_316019(316019, "相机染色超时"),
    ROUTE_MANAGER_316020(316020, "飞行器使用的 RTK 信号源错误，请稍后重试"),
    ROUTE_MANAGER_316021(316021, "飞行器 RTK 信号源检查超时，请稍后重试"),
    ROUTE_MANAGER_316022(316022, "飞行器无法执行返航指令，请检查飞行器是否已开机，机场与飞行器是否已断连，请确认无以上问题后重试"),
    ROUTE_MANAGER_316023(316023, "飞行器无法执行返航指令，飞行器已被 B 控接管，请在 B 控操控飞行器，或关闭 B 控后重试"),
    ROUTE_MANAGER_316024(316024, "飞行器执行返航指令失败，请检查飞行器是否已起飞，确认飞行器已起飞后请重试"),
    ROUTE_MANAGER_316025(316025, "设置返航高度失败"),
    ROUTE_MANAGER_316026(316026, "机场急停按钮被按下，无法执行飞行任务，请释放急停按钮后重试"),
    ROUTE_MANAGER_316027(316027, "飞行器参数配置超时，请稍后重试或重启机场后重试"),

    ROUTE_MANAGER_317001(317001, "获取媒体文件数量失败"),
    ROUTE_MANAGER_317002(317002, "远程相机格式化，相机未连接"),
    ROUTE_MANAGER_317003(317003, "获取媒体文件数量失败"),

    ROUTE_MANAGER_319001(319001, "航线任务中心当前不在空闲状态"),
    ROUTE_MANAGER_319002(319002, "dronenest通讯超时"),
    ROUTE_MANAGER_319003(319003, "任务 id 在机场中不存在"),
    ROUTE_MANAGER_319004(319004, "任务达到最大延误值"),
    ROUTE_MANAGER_319005(319005, "go 指令等待超时"),
    ROUTE_MANAGER_319006(319006, "当前状态不允许用户取消任务"),
    ROUTE_MANAGER_319007(319007, "当前状态不允许用户修改任务"),
    ROUTE_MANAGER_319008(319008, "云端与机场通信异常，无法执行飞行任务"),
    ROUTE_MANAGER_319009(319009, "任务信息存储失败（任务下发失败后会不断重试，直到到达任务执行时间）"),
    ROUTE_MANAGER_319015(319015, "机场正在初始化中，无法执行飞行任务，请等待机场初始化完成后重试"),
    ROUTE_MANAGER_319016(319016, "机场正在执行其他飞行任务，无法执行本次飞行任务"),
    ROUTE_MANAGER_319017(319017, "机场正在处理上次飞行任务媒体文件，无法执行本次飞行任务，请稍后重试"),
    ROUTE_MANAGER_319018(319018, "机场正在自动导出日志中（设备异常反馈），无法执行飞行任务，请稍后重试"),
    ROUTE_MANAGER_319019(319019, "机场正在拉取日志中（设备异常反馈），无法执行飞行任务，请稍后重试"),
    ROUTE_MANAGER_319999(319999, "未知错误，例如崩溃后重启"),

    ROUTE_MANAGER_321000(321000, "航线执行失败，未知错误"),
    ROUTE_MANAGER_321004(321004, "航线文件有问题"),
    ROUTE_MANAGER_321257(321257, "航线已经开始，不能再次开始"),
    ROUTE_MANAGER_321258(321258, "该状态下无法中断航线"),
    ROUTE_MANAGER_321259(321259, "航线未开始，不能结束航线"),
    ROUTE_MANAGER_321513(321513, "到达限高"),
    ROUTE_MANAGER_321514(321514, "到达限远"),
    ROUTE_MANAGER_321515(321515, "穿过限飞区"),
    ROUTE_MANAGER_321516(321516, "限低"),
    ROUTE_MANAGER_321517(321517, "避障"),
    ROUTE_MANAGER_321520(321520, "飞行器起飞检测到断桨"),
    ROUTE_MANAGER_321523(321523, "飞行器起桨检查未通过，可能是桨叶损坏导致，请稍后重试，如果仍报错请联系大疆售后更换桨叶"),

    ROUTE_MANAGER_321769(321769, "GPS信号弱"),
    ROUTE_MANAGER_321770(321770, "当前档位状态不能执行，B控夺控制权，切换了档位"),
    ROUTE_MANAGER_321771(321771, "返航点未刷新"),
    ROUTE_MANAGER_321772(321772, "当前电量过低无法开始航线任务"),
    ROUTE_MANAGER_321773(321773, "低电量返航"),
    ROUTE_MANAGER_321775(321775, "RC信号丢失"),
    ROUTE_MANAGER_321776(321776, "RTK未ready"),
    ROUTE_MANAGER_321778(321778, "飞机在地面怠速，不允许开始航线，认为用户没准备好。"),

    ROUTE_MANAGER_322776(322282, "用户中断（B控接管）"),
    ROUTE_MANAGER_322548(322548, "航点数量异常"),

    ROUTE_MANAGER_325001(325001, "缺少参数，命令解析失败"),
    ROUTE_MANAGER_324012(324012, "日志导出，设备超时"),
    ROUTE_MANAGER_324013(324013, "日志列表拉取失败"),
    ROUTE_MANAGER_324014(324014, "日志列表导出异常，设备列表为空"),
    ROUTE_MANAGER_324015(324015, "日志列表导出异常，飞行器未连接"),
    ROUTE_MANAGER_324016(324016, "设备日志导出异常，无足够空间"),
    ROUTE_MANAGER_324017(324017, "设备日志导出异常，无此飞行器架次日志"),
    ROUTE_MANAGER_324018(324018, "设备日志导出异常，导出过程失败"),
    ROUTE_MANAGER_324019(324019, "设备日志导出异常，上传服务器过程失败"),
    //日志跟航线错误码重复
//    ROUTE_MANAGER_314000(314000, "机场状态不能执行当前操作，另一个任务正在上传中，或者是机场作业中"),


    LIVE_MANAGER_513001(513001,"飞行器不存在"),
    LIVE_MANAGER_513002(513002,"相机不存在"),
    LIVE_MANAGER_513003(513003,"该相机已经开始直播"),
    LIVE_MANAGER_513004(513004,"功能不支持"),
    LIVE_MANAGER_513005(513005,"策略不支持"),
    LIVE_MANAGER_513006(513006,"当前 App 不在 liveview 界面，无法响应某些直播设置"),
    LIVE_MANAGER_513007(513007,"无飞行控制权，无法响应控制指令"),
    LIVE_MANAGER_513008(513008,"当前 App 没有码流数据"),
    LIVE_MANAGER_513009(513009,"App 端正在响应直播请求，请勿太过频繁操作"),
    LIVE_MANAGER_513010(513010,"开启直播失败，请检查直播服务（声网）是否正常 也可能是设备没有外网"),
    LIVE_MANAGER_513011(513011,"当前没有直播，无需关闭"),
    LIVE_MANAGER_513012(513012,"当前已经有另一个图传在直播，不支持直接切码流直播"),
    LIVE_MANAGER_513013(513013,"URL_TYPE不支持"),
    LIVE_MANAGER_513014(513014,"直播URL参数异常或者不完整"),
    LIVE_MANAGER_513099(513099,"JSON数据格式有问题"),

    ROUTE_MANAGER_514100(514100, "命令不支持"),
    ROUTE_MANAGER_514101(514101, "合拢推杆失败"),
    ROUTE_MANAGER_514102(514102, "释放推杆失败"),
    ROUTE_MANAGER_514103(514103, "飞机电量低"),
    ROUTE_MANAGER_514104(514104, "开始充电失败"),
    ROUTE_MANAGER_514105(514105, "停止充电失败"),
    ROUTE_MANAGER_514106(514106, "重启飞机失败"),
    ROUTE_MANAGER_514107(514107, "打开舱盖失败"),
    ROUTE_MANAGER_514108(514108, "关闭舱盖失败"),
    ROUTE_MANAGER_514109(514109, "打开飞机失败"),
    ROUTE_MANAGER_514110(514110, "关闭飞机失败"),
    ROUTE_MANAGER_514111(514111, "飞机在舱内开启慢转桨失败"),
    ROUTE_MANAGER_514112(514112, "飞机在舱内停止慢转桨失败"),
    ROUTE_MANAGER_514113(514113, "与飞机有线连接建立失败"),
    ROUTE_MANAGER_514114(514114, "获取飞机电源状态，命令超时，或返回码不为0"),
    ROUTE_MANAGER_514116(514116, "无法执行当前操作，机场正在执行其他控制指令，请稍后重试"),
    ROUTE_MANAGER_514117(514117, "检查舱盖状态失败"),
    ROUTE_MANAGER_514118(514118, "检查推杆状态失败"),
    ROUTE_MANAGER_514120(514120, "机场和飞机SDR连接失败"),
    ROUTE_MANAGER_514121(514121, "急停状态"),
    ROUTE_MANAGER_514122(514122, "获取飞机充电状态失败（获取充电状态失败，飞行航线任务可执行，影响充电和远程排障）"),
    ROUTE_MANAGER_514123(514123, "低电量无法开机"),
    ROUTE_MANAGER_514124(514124, "获取电池信息失败"),
    ROUTE_MANAGER_514125(514125, "电池满电量无法充电"),
    ROUTE_MANAGER_514134(514134, "雨量过大，机场无法执行飞行任务，请稍后重试"),
    ROUTE_MANAGER_514135(514135, "风速过大（≥12m/s），机场无法执行飞行任务，请稍后重试"),
    ROUTE_MANAGER_514136(514136, "机场供电断开，机场无法执行飞行任务，请恢复机场供电后重试"),
    ROUTE_MANAGER_514137(514137, "环境温度过低（<-20℃），机场无法执行飞行任务，请稍后重试"),
    ROUTE_MANAGER_514138(514138, "飞行器电池正在保养中，机场无法执行飞行任务，请等待保养结束后重试"),
    ROUTE_MANAGER_514139(514139, "飞行器电池无法执行保养指令，飞行器电池无需保养"),
    ROUTE_MANAGER_514140(514140, "电池存储模式设置失败"),
    ROUTE_MANAGER_514141(514141, "机场系统运行异常，请重启机场后重试"),
    ROUTE_MANAGER_514142(514142, "飞行器起飞前，机场与飞行器无法建立有线连接，请检查飞行器是否在舱内，推杆闭合时是否被卡住，充电连接器是否脏污或损坏"),
    ROUTE_MANAGER_514145(514145, "处于现场调试无法作业"),
    ROUTE_MANAGER_514146(514146, "处于远程调试无法作业"),
    ROUTE_MANAGER_514147(514147, "处于升级状态无法作业"),
    ROUTE_MANAGER_514148(514148, "处于作业状态无法执行新的航线任务"),
    ROUTE_MANAGER_514149(514149, "机场系统运行异常，无法执行飞行任务，请重启机场后重试"),
    ROUTE_MANAGER_514150(514150, "机场正在自动重启"),
    ROUTE_MANAGER_514151(514151, "设备升级中，无法执行设备重启指令，请等待升级完成后重试"),
    ROUTE_MANAGER_514153(514153, "机场已退出远程调试模式，无法执行当前操作"),

    LIVE_MANAGER_613001(613001,"直播错误，飞机不存在，比如遥控器连的是M300，需要响应给对端的却是M200，就需要报此错误"),
    LIVE_MANAGER_613002(613002,"直播错误，相机不存在，飞机没有挂Z30，却要求Z30的图传，就需要报此错误"),
    LIVE_MANAGER_613003(613003,"直播错误，该相机已经开始直播"),
    LIVE_MANAGER_613004(613004,"直播错误，功能不支持，比如不支持单独改帧率功能"),
    LIVE_MANAGER_613005(613005,"直播错误，策略不支持，比如App只支持标清，高清，超清，自适应四种直播策略，不支持其他类型的策略"),
    LIVE_MANAGER_613006(613006,"直播错误，当前App不在liveview界面，无法响应某些直播设置"),
    LIVE_MANAGER_613007(613007,"直播错误，无飞行控制权，无法响应控制指令"),
    LIVE_MANAGER_613008(613008,"直播错误，当前App没有码流数据"),
    LIVE_MANAGER_613009(613009,"直播错误，App端正在响应直播请求，请勿太过频繁操作"),
    LIVE_MANAGER_613010(613010,"直播错误，开启直播失败，请检查直播服务（声网）是否正常"),
    LIVE_MANAGER_613011(613011,"直播错误，当前没有正在直播，针对停止直播，切换码流，切换直播参数"),
    LIVE_MANAGER_613012(613012,"直播错误，当前已经有另一个图传在直播，不支持直接切码流直播"),
    LIVE_MANAGER_613013(613013,"直播错误，URL_TYPE不支持"),
    LIVE_MANAGER_613014(613014,"直播错误，直播URL参数异常或者不完整"),
    LIVE_MANAGER_613099(613099,"直播错误，开启直播失败，原因未知"),

    ;

    public int getCode() {
        return code;
    }

    private int code;

    public String getMsg() {
        return msg;
    }

    private String msg;

    DjiErrorCodeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static boolean isSuccess(Integer code) {
        if (Objects.nonNull(code)) {
            return code == SUCCESS.code;
        }
        return false;
    }


    public static String getMsg(Integer code) {
        if (Objects.nonNull(code)) {
            for (DjiErrorCodeEnum e : DjiErrorCodeEnum.values()) {
                if (e.code == code) {
                    return e.msg;
                }
            }
        }
        return UNKNOWN.msg;
    }
}
