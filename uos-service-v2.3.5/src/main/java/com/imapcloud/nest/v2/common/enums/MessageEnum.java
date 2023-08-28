package com.imapcloud.nest.v2.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Classname MessageEnum
 * @Description 消息提示枚举类
 * @Date 2022/11/1 14:32
 * @Author Carnival
 */
@Getter
@AllArgsConstructor
public enum MessageEnum {

    /**
     * 通用成功
     */
    GEOAI_UOS_SUCCESS("geoai_uos_success"),
    /**
     * 通用失败
     */
    GEOAI_UOS_FAILURE("geoai_uos_failure"),
    /**
     * 通用打开
     */
    GROAI_UOS_OPEN("geoai_uos_open"),
    /**
     * 通用关闭
     */
    GROAI_UOS_CLOSE("geoai_uos_close"),
    /**
     * Addr不能为空
     */
    GEOAI_UOS_CANNOT_EMPTY_ADDR("geoai_uos_cannot_empty_Addr"),
    /**
     * aiMark不能为空
     */
    GEOAI_UOS_CANNOT_EMPTY_AIMARK("geoai_uos_cannot_empty_aiMark"),
    /**
     * audioId不能为空！清检查
     */
    GEOAI_UOS_CANNOT_EMPTY_AUDIOID("geoai_uos_cannot_empty_audioid"),
    /**
     * baseId不能为空
     */
    GEOAI_UOS_CANNOT_EMPTY_BASEID("geoai_uos_cannot_empty_baseid"),
    /**
     * CPS固件安装包解压失败
     */
    GEOAI_UOS_FAIL_CPS_DECOMPRESSION("geoai_uos_fail_cps_decompression"),
    /**
     * CPS固件安装包仅支持APK文件
     */
    GEOAI_UOS_ONLY_SUPPORT_APK_FILE("geoai_uos_only_support_apk_file"),
    /**
     * CPS日志解析完成
     */
    GEOAI_UOS_CPS_LOG_PARSING_COMPLETED("geoai_uos_cps_log_parsing_completed"),
    /**
     * data航线解析失败
     */
    GEOAI_UOS_FAIL_DATA_ROUTE_PARSING("geoai_uos_fail_data_route_parsing"),
    /**
     * detailId 不能为空
     */
    GEOAI_UOS_CANNOT_EMPTY_DETAILID("geoai_uos_cannot_empty_detailid"),
    /**
     * detailIds 不能为空
     */
    GEOAI_UOS_CANNOT_EMPTY_DETAILIDS("geoai_uos_cannot_empty_detailids"),
    /**
     * endTime不能为空
     */
    GEOAI_UOS_CANNOT_EMPTY_ENDTIME("geoai_uos_cannot_empty_endtime"),
    /**
     * ImagePath不能为空
     */
    GEOAI_UOS_CANNOT_EMPTY_IMAGEPATH("geoai_uos_cannot_empty_imagepath"),
    /**
     * insertInfoInList不能为空
     */
    GEOAI_UOS_CANNOT_EMPTY_INSERTINFOINLIST("geoai_uos_cannot_empty_insertinfoinlist"),
    /**
     * json文件解析失败
     */
    GEOAI_UOS_FAIL_JSON_FILE("geoai_uos_fail_json_file"),
    /**
     * kml航线解析失败
     */
    GEOAI_UOS_FAIL_KML_ROUTE("geoai_uos_fail_kml_route"),
    /**
     * Latitude不能为空
     */
    GEOAI_UOS_CANNOT_EMPTY_LATITUDE("geoai_uos_cannot_empty_latitude"),
    /**
     * Longitude不能为空
     */
    GEOAI_UOS_CANNOT_EMPTY_LONGITUDE("geoai_uos_cannot_empty_longitude"),
    /**
     * markId 不能为空
     */
    GEOAI_UOS_CANNOT_EMPTY_MARKID("geoai_uos_cannot_empty_markId"),
    /**
     * MissionRecordsId不能为空
     */
    GEOAI_UOS_CANNOT_EMPTY_MISSIONRECORDSID("geoai_uos_cannot_empty_missionrecordsid"),
    /**
     * MPS固件安装包解压失败
     */
    GEOAI_UOS_FAIL_MPS_DECOMPRESSION("geoai_uos_fail_mps_decompression"),
    /**
     * MPS固件安装包仅支持ZIP文件
     */
    GEOAI_UOS_MPS_ONLY_SUPPORT_ZIP_FILE("geoai_uos_mps_only_support_zip_file"),
    /**
     * mqtt代理地址不存在
     */
    GEOAI_UOS_NOT_EXIST_MQTT("geoai_uos_not_exist_mqtt"),
    /**
     * mqtt内网代理地址不能超200个字符
     */
    GEOAI_UOS_CANNOT_EXCEED_200_CHAR_IN_MQTT("geoai_uos_cannot_exceed_200_char_in_mqtt"),
    /**
     * mqtt内网代理地址不能为空
     */
    GEOAI_UOS_CANNOT_EMPTY_IN_MQTT("geoai_uos_cannot_empty_in_mqtt"),
    /**
     * mqtt日志上传成功
     */
    GEOAI_UOS_SUCCESS_UPLOAD_MQTT("geoai_uos_success_upload_mqtt"),
    GEOAI_UOS_FAILURE_UPLOAD_MQTT("geoai_uos_failure_upload_mqtt"),
    /**
     * mqtt外网代理地址不能超200个字符
     */
    GEOAI_UOS_CANNOT_EXCEED_200_CHAR_OUT_MQTT("geoai_uos_cannot_exceed_200_char_out_mqtt"),
    /**
     * mqtt外网代理地址不能为空
     */
    GEOAI_UOS_CANNOT_EMPTY_OUT_MQTT("geoai_uos_cannot_empty_out_mqtt"),
    /**
     * nestId 不能为空
     */
    GEOAI_UOS_CANNOT_EMPTY_NESTID("geoai_uos_cannot_empty_nestid"),
    /**
     * PhotoCreateTime不能为空
     */
    GEOAI_UOS_CANNOT_EMPTY_PHOTOCREATETIME("geoai_uos_cannot_empty_photocreatetime"),
    /**
     * photoID不能为空
     */
    GEOAI_UOS_CANNOT_EMPTY_PHOTOID("geoai_uos_cannot_empty_photoid"),
    /**
     * ResultImagePath不能为空
     */
    GEOAI_UOS_CANNOT_EMPTY_RESULTIMAGEPATH("geoai_uos_cannot_empty_resultimagepath"),
    /**
     * SrcDataType不能为空
     */
    GEOAI_UOS_CANNOT_EMPTY_SRCDATATYPE("geoai_uos_cannot_empty_srcdatatype"),
    /**
     * startTime不能为空
     */
    GEOAI_UOS_CANNOT_EMPTY_STARTTIME("geoai_uos_cannot_empty_starttime"),
    /**
     * taskId 不能为空
     */
    GEOAI_UOS_CANNOT_EMPTY_TASKID("geoai_uos_cannot_empty_taskid"),
    /**
     * ThumImagePath不能为空
     */
    GEOAI_UOS_CANNOT_EMPTY_THUMIMAGEPATH("geoai_uos_cannot_empty_thumimagepath"),
    /**
     * TopicIndustryId不能为空
     */
    GEOAI_UOS_CANNOT_EMPTY_TOPICINDUSTRYID("geoai_uos_cannot_empty_topicindustryid"),
    /**
     * topicKey不能为空
     */
    GEOAI_UOS_CANNOT_EMPTY_TOPICKEY("geoai_uos_cannot_empty_topickey"),
    /**
     * topicKey值不能为空
     */
    GEOAI_UOS_CANNOT_EMPTY_TOPICVALUE("geoai_uos_cannot_empty_topicvalue"),
    /**
     * TopicLevelId不能为空
     */
    GEOAI_UOS_CANNOT_EMPTY_TOPICLEVELID("geoai_uos_cannot_empty_topiclevelid"),
    /**
     * TopicProblemId不能为空
     */
    GEOAI_UOS_CANNOT_EMPTY_TOPICPROBLEMID("geoai_uos_cannot_empty_topicproblemid"),
    /**
     * 保存成功
     */
    GEOAI_UOS_SUCCESS_SAVE("geoai_uos_success_save"),
    /**
     * 编辑标签成功
     */
    GEOAI_UOS_SUCCESS_EDIT("geoai_uos_success_edit"),
    /**
     * 播放成功
     */
    GEOAI_UOS_SUCCESS_PLAY("geoai_uos_success_play"),
    /**
     * 不存在该单位
     */
    GEOAI_UOS_NOT_EXIST_UNIT("geoai_uos_not_exist_unit"),
    /**
     * 参数错误
     */
    GEOAI_UOS_ERROR_PARAMETER("geoai_uos_error_parameter"),
    /**
     * 操作成功
     */
    GEOAI_UOS_SUCCESS_OPERATION("geoai_uos_success_operation"),
    /**
     * 查看任务详情失败
     */
    GEOAI_UOS_FAIL_VIEW_MISSION_DETAIL("geoai_uos_fail_view_mission_detail"),
    /**
     * 查询不到对应的杆塔
     */
    GEOAI_UOS_FAIL_FIND_CORRESPONDING_TOWER("geoai_uos_fail_find_corresponding_tower"),
    /**
     * 查询不到对应的航线
     */
    GEOAI_UOS_FAIL_FIND_CORRESPONDING_ROUTE("geoai_uos_fail_find_corresponding_route"),
    /**
     * 查询不到对应的基站
     */
    GEOAI_UOS_FAIL_FIND_CORRESPONDING_NEST("geoai_uos_fail_find_corresponding_nest"),
    /**
     * 查询不到对应的输电路线
     */
    GEOAI_UOS_FAIL_FIND_CORRESPONDING_TRANSMISSION_ROUTE("geoai_uos_fail_find_corresponding_transmission_route"),
    /**
     * 查询单位信息失败，请稍后重试
     */
    GEOAI_UOS_FAIL_FIND_CORRESPONDING_UNIT("geoai_uos_fail_find_corresponding_unit"),
    /**
     * 查询航线失败
     */
    GEOAI_UOS_FAIL_FIND_CORRESPONDING_MISSION("geoai_uos_fail_find_corresponding_mission"),
    /**
     * 查阅成功
     */
    GEOAI_UOS_SUCCESS_CHECK("geoai_uos_success_check"),
    /**
     * 成功设置拍照/录像源为
     */
    GEOAI_UOS_SUCCESS_PHOTO("geoai_uos_success_photo"),
    /**
     * 传入的基础信息为空，请检查
     */
    GEOAI_UOS_CANNOT_EMPTY_BASE_INFORMATION("geoai_uos_cannot_empty_base_information"),
    /**
     * 传入的架次记录Id为空，请检查
     */
    GEOAI_UOS_CANNOT_EMPTY_MISSIONID("geoai_uos_cannot_empty_missionid"),
    /**
     * 创建成功
     */
    GEOAI_UOS_SUCCESS_CREATE("geoai_uos_success_create"),
    /**
     * 打开探照灯成功
     */
    GEOAI_UOS_SUCCESS_TURN_ON_SEARCHLIGHT("geoai_uos_success_turn_on_searchlight"),
    /**
     * 打开夜航灯成功
     */
    GEOAI_UOS_SUCCESS_TURN_ON_NIGHTLIGHT("geoai_uos_success_turn_on_nightlight"),
    /**
     * 代理地址名称不能超80个字符
     */
    GEOAI_UOS_CANNOT_EXCEED_80_CHAR_MQTT("geoai_uos_cannot_exceed_80_char_mqtt"),
    /**
     * 代理地址名称不能为空
     */
    GEOAI_UOS_CANNOT_EMPTY_MQTT("geoai_uos_cannot_empty_mqtt"),
    /**
     * 代理地址名称限2~16个字符字符
     */
    GEOAI_UOS_LIMITED_2_16_MQTT("geoai_uos_limited_2_16_mqtt"),
    /**
     * 单位ID不能为空
     */
    GEOAI_UOS_CANNOT_EMPTY_UNITID("geoai_uos_cannot_empty_unitid"),
    /**
     * 单位ID和架次执行ID不能同时为空
     */
    GEOAI_UOS_CANNOT_EMPTY_UNITID_MISSIONID("geoai_uos_cannot_empty_unitid_missionid"),
    /**
     * 单位编码不能为空
     */
    GEOAI_UOS_CANNOT_EMPTY_UNITCODE("geoai_uos_cannot_empty_unitcode"),
    /**
     * 当前机巢监控查看状态更改成功
     */
    GEOAI_UOS_SUCCESS_VIEW_NEST("geoai_uos_success_view_nest"),
    /**
     * 当前架次记录已经存在推送流程，请稍后再试
     */
    GEOAI_UOS_ALREADY_PUSH("geoai_uos_already_push"),
    /**
     * 当前区域不存在
     */
    GEOAI_UOS_NOT_EXIST_REGION("geoai_uos_not_exist_region"),
    /**
     * 当前移动终端不存在
     */
    GEOAI_UOS_NOT_EXIST_MOBILE_TERMINAL("geoai_uos_not_exist_mobile_terminal"),
    /**
     * 当前移动终端监控更改状态成功
     */
    GEOAI_UOS_SUCCESS_CHANGE_MOBILE_TERMINAL("geoai_uos_success_change_mobile_terminal"),
    /**
     * 当前已有设备id相同移动终端，名称为
     */
    GEOAI_UOS_ALREADY_EXIST_MOBILE_TERMINAL_ID("geoai_uos_already_exist_mobile_terminal_id"),
    /**
     * 当前已有同名mqtt代理地址
     */
    GEOAI_UOS_ALREADY_EXIST_MQTT("geoai_uos_already_exist_mqtt"),
    /**
     * 当前已有同名区域
     */
    GEOAI_UOS_ALREADY_EXIST_REGION("geoai_uos_already_exist_region"),
    /**
     * 当前已有同名移动终端
     */
    GEOAI_UOS_ALREADY_EXIST_MOBILE_TERMINAL("geoai_uos_already_exist_mobile_terminal"),
    /**
     * 当前账号未查询到单位信息，无法进行该操作
     */
    GEOAI_UOS_FAIL_FIND_UNITINFO("geoai_uos_fail_find_unitinfo"),
    /**
     * 当前找不到相关单位内容
     */
    GEOAI_UOS_FAIL_FIND_UNITCONTENT("geoai_uos_fail_find_unitcontent"),
    /**
     * 导出人数过多，请稍后重试
     */
    GEOAI_UOS_TOO_MANY_PEOPLE_EXPORTED("geoai_uos_too_many_people_exported"),
    /**
     * 导入格式不正确
     */
    GEOAI_UOS_INCORRECT_IMPORT_FORMAT("geoai_uos_incorrect_import_format"),
    /**
     * 地址信息不能为空
     */
    GEOAI_UOS_CANNOT_ADDRESS_INFORMATION("geoai_uos_cannot_address_information"),
    /**
     * 分析统计推送了空表体，清检查
     */
    GEOAI_UOS_ANALYSIS_PUSHED_EMPTY_FORM_BODY("geoai_uos_analysis_pushed_empty_form_body"),
    /**
     * 服务航线上传到机巢失败
     */
    GEOAI_UOS_FAIL_FIND_UNITINFO_SERVICE_ROUTE_UPLOAD("geoai_uos_fail_find_unitinfo_service_route_upload"),
    /**
     * 该操作仅支持CPS或MPS固件
     */
    GEOAI_UOS_ONLY_SUPPORTED_FOR_CPS_OR_MPS_FIRMWARE("geoai_uos_only_supported_for_cps_or_mps_firmware"),
    /**
     * 该问题名称已存在，不可重复!
     */
    GEOAI_UOS_ALREADY_EXIST_QUESTION_NAME("geoai_uos_already_exist_question_name"),
    /**
     * 该照片无经纬度信息，无法匹配历史照片
     */
    GEOAI_UOS_NO_LATITUDE_AND_LONGITUDE_INFORMATION("geoai_uos_no_latitude_and_longitude_information"),
    /**
     * 更新成功
     */
    GEOAI_UOS_SUCCESS_UPDATE("geoai_uos_success_update"),
    /**
     * 更新失败
     */
    GEOAI_UOS_FAIL_UPDATE("geoai_uos_fail_update"),
    /**
     * 固件安装包上传失败
     */
    GEOAI_UOS_FAIL_FIRMWARE_UPLOAD("geoai_uos_fail_firmware_upload"),
    /**
     * 关闭RTK成功
     */
    GEOAI_UOS_SUCCESS_CLOSE_RTK("geoai_uos_success_close_rtk"),
    /**
     * 关闭探照灯成功
     */
    GEOAI_UOS_SUCCESS_CLOSE_SEARCHLIGHT("geoai_uos_success_close_searchlight"),
    /**
     * 关闭夜航灯成功
     */
    GEOAI_UOS_SUCCESS_CLOSE_NIGHTLIGHT("geoai_uos_success_close_nightlight"),
    /**
     * 管理密码不能超80个字符
     */
    GEOAI_UOS_CANNOT_EXCEED_80_PASSWORD("geoai_uos_cannot_exceed_80_password"),
    /**
     * 管理密码不能为空
     */
    GEOAI_UOS_CANNOT_EMPTY_USERPASSWORD("geoai_uos_cannot_empty_userpassword"),
    /**
     * 管理账号不能超80个字符
     */
    GEOAI_UOS_CANNOT_EXCEED_80_USERACCOUNT("geoai_uos_cannot_exceed_80_useraccount"),
    /**
     * 管理账号不能为空
     */
    GEOAI_UOS_CANNOT_EMPTY_USERACCOUNT("geoai_uos_cannot_empty_useraccount"),
    /**
     * 过滤重复数据后，没有能推送的数据
     */
    GEOAI_UOS_NO_DATA_THAT_CAN_BE_PUSHED("geoai_uos_no_data_that_can_be_pushed"),
    /**
     * 航线导入成功
     */
    GEOAI_UOS_SUCCESS_ROUTE_IMPORT("geoai_uos_success_route_import"),
    /**
     * 航线导入失败
     */
    GEOAI_UOS_FAIL_ROUTE_IMPORT("geoai_uos_fail_route_import"),
    /**
     * 航线解析失败
     */
    GEOAI_UOS_FAIL_ROUTE_RESOLUTION("geoai_uos_fail_route_resolution"),
    /**
     * 航线上传到机巢成功
     */
    GEOAI_UOS_SUCCESS_ROUTE_UPLOAD("geoai_uos_success_route_upload"),
    /**
     * 航线上传到机巢失败
     */
    GEOAI_UOS_FAIL_NEST_ROUTE_UPLOAD("geoai_uos_fail_nest_route_upload"),
    /**
     * 航线上传失败
     */
    GEOAI_UOS_FAIL_ROUTE_UPLOAD("geoai_uos_fail_route_upload"),
    /**
     * 合并失败
     */
    GEOAI_UOS_FAIL_MERGER("geoai_uos_fail_merger"),
    /**
     * 合并文件失败
     */
    GEOAI_UOS_FAIL_MERGE_FILE("geoai_uos_fail_merge_file"),
    /**
     * 后台异步解析,耗时比较久,请稍后再查询结果
     */
    GEOAI_UOS_ASYNCHRONOUS_PARSING_IN_THE_BACKGROUND("geoai_uos_asynchronous_parsing_in_the_background"),
    /**
     * 获取返航高度失败
     */
    GEOAI_UOS_FAIL_GET_RETURN_ALTITUDE("geoai_uos_fail_get_return_altitude"),
    /**
     * 获取失败
     */
    GEOAI_UOS_FAIL_GET("geoai_uos_fail_get"),
    /**
     * 获取文件流失败
     */
    GEOAI_UOS_FAIL_GET_THE_FILE_STREAM("geoai_uos_fail_get_the_file_stream"),
    /**
     * 获取无人机推流地址失败
     */
    GEOAI_UOS_FAIL_GET_DRONE_PUSH_STREAM_ADDRESS("geoai_uos_fail_get_drone_push_stream_address"),
    /**
     * 获取状态失败
     */
    GEOAI_UOS_FAIL_GET_STATUS("geoai_uos_fail_get_status"),
    /**
     * 机巢离线
     */
    GEOAI_UOS_MACHINE_NEST_OFFLINE("geoai_uos_machine_nest_offline"),
    /**
     * 基站ID不存在，更新失败
     */
    GEOAI_UOS_NOT_EXIST_NESTID("geoai_uos_not_exist_nestid"),
    /**
     * 基站ID为空，请检查参数
     */
    GEOAI_UOS_IS_EXIST_NESTID("geoai_uos_is_exist_nestid"),
    /**
     * 基站ID已存在系统中，不能重复添加
     */
    GEOAI_UOS_ALREADY_EXIST_NESTID("geoai_uos_already_exist_nestid"),
    /**
     * 基站必须拥有一个单位，请正确配置
     */
    GEOAI_UOS_MUST_HAVE_A_UNIT("geoai_uos_must_have_a_unit"),
    /**
     * 基站编号已存在系统中，不能重复添加
     */
    GEOAI_UOS_ALREADY_EXIST_NESTNUM("geoai_uos_already_exist_nestnum"),
    /**
     * 基站不存在，请检查参数
     */
    GEOAI_UOS_NOT_EXIST_NEST("geoai_uos_not_exist_nest"),
    /**
     * 基站不在线
     */
    GEOAI_UOS_NOT_ONLINE_NEST("geoai_uos_not_online_nest"),
    /**
     * 基站查询失败
     */
    GEOAI_UOS_FAIL_QUERY_NEST("geoai_uos_fail_query_nest"),
    /**
     * 基站的无人机信息正在被其他用户修改，请稍后重试
     */
    GEOAI_UOS_FAIL_NEST_IS_BEING_MODIFIED("geoai_uos_fail_nest_is_being_modified"),
    /**
     * 基站固件正在更新中, 请稍后再尝试
     */
    GEOAI_UOS_FAIL_NEST_FIRMWARE_IS_BEING_UPDATED("geoai_uos_fail_nest_firmware_is_being_updated"),
    /**
     * 基站航线上传到服务器成功
     */
    GEOAI_UOS_SUCCESS_NEST_ROUTE_UPLOADED_TO_SERVER("geoai_uos_success_nest_route_uploaded_to_server"),
    /**
     * 基站航线上传到服务器失败
     */
    GEOAI_UOS_FAIL_NEST_ROUTE_UPLOADED_TO_SERVER("geoai_uos_fail_nest_route_uploaded_to_server"),
    /**
     * 基站和杆塔不匹配
     */
    GEOAI_UOS_MISMATCH_NEST_AND_TOWER("geoai_uos_mismatch_nest_and_tower"),
    /**
     * 基站离线，无法上传日志
     */
    GEOAI_UOS_NEST_IS_OFFLINE_AND_CANNOT_UPLOAD_LOGS("geoai_uos_nest_is_offline_and_cannot_upload_logs"),
    /**
     * 基站名称不能为空
     */
    GEOAI_UOS_CANNOT_EMPTY_NEST_NAME("geoai_uos_cannot_empty_nest_name"),
    /**
     * 基站名称长度限制:2~16
     */
    GEOAI_UOS_LIMITED_2_16_NEST_NAME("geoai_uos_limited_2_16_nest_name"),
    /**
     * 基站信息正在更新，请不要频繁更新
     */
    GEOAI_UOS_NEST_INFORMATION_IS_BEING_UPDATED("geoai_uos_nest_information_is_being_updated"),
    /**
     * 基站已离线，无法进行固件更新操作
     */
    GEOAI_UOS_NEST_IS_OFFLINE_AND_THE_FIRMWARE_UPDATE_OPERATION("geoai_uos_nest_is_offline_and_the_firmware_update_operation"),
    /**
     * 基站状态非[待机]状态，不允许进行固件更新操作
     */
    GEOAI_UOS_NEST_STATUS_IS_NOT_STANDBY_STATE("geoai_uos_nest_status_is_not_standby_state"),
    /**
     * 基站状态未知，无法进行固件更新操作
     */
    GEOAI_UOS_NEST_STATUS_IS_UNKNOWN("geoai_uos_nest_status_is_unknown"),
    /**
     * 基站状态只能是【待机】、【MPS固件更新初始化】、【等待重置】、【调试模式】、【状态错误】下，才能进行固件更新操作
     */
    GEOAI_UOS_NEST_STATUS_CAN_ONLY_BE_STANDBY("geoai_uos_nest_status_can_only_be_standby"),
    /**
     * 继续任务成功
     */
    GEOAI_UOS_SUCCESS_CONTINUE_MISSION("geoai_uos_success_continue_mission"),
    /**
     * 架次不存在
     */
    GEOAI_UOS_NOT_EXIST_MISSON("geoai_uos_not_exist_misson"),
    /**
     * 架次记录execID不能为空，请检查当前环境和当前任务是否一致
     */
    GEOAI_UOS_CANNOT_EMPTY_EXECID_OF_THE_MISSION_RECORD("geoai_uos_cannot_empty_execid_of_the_mission_record"),
    /**
     * 架次记录不存在
     */
    GEOAI_UOS_NOT_EXIST_MISSION_RECORD("geoai_uos_not_exist_mission_record"),
    /**
     * 检测单位角色异常，请稍后重试
     */
    GEOAI_UOS_DETECTION_UNIT_ROLE_ABNORMAL("geoai_uos_detection_unit_role_abnormal"),
    /**
     * 结束录音成功
     */
    GEOAI_UOS_SUCCESS_END_RECORDING("geoai_uos_success_end_recording"),
    /**
     * 解析完成
     */
    GEOAI_UOS_COMPLETE_PARSING("geoai_uos_complete_parsing"),
    /**
     * 进入云台自动跟随模式成功
     */
    GEOAI_UOS_SUCCESS_ENTER_THE_AUTOMATIC_FOLLOWING_MODE("geoai_uos_success_enter_the_automatic_following_mode"),
    /**
     * 经度不能为空
     */
    GEOAI_UOS_CANNOT_EMPTY_LONGITUDE_CN("geoai_uos_cannot_empty_longitude_cn"),
    /**
     * 纬度不能为空
     */
    GEOAI_UOS_CANNOT_EMPTY_LATITUDE_CN("geoai_uos_cannot_empty_latitude_cn"),
    /**
     * 旧密码不能为空
     */
    GEOAI_UOS_CANNOT_OLD_PASSWORD("geoai_uos_cannot_old_password"),
    /**
     * 距离不能为空
     */
    GEOAI_UOS_CANNOT_EMPTY_DISTANCE("geoai_uos_cannot_empty_distance"),
    /**
     * 开启drtk电源触发成功
     */
    GEOAI_UOS_SUCCESS_TURN_ON_DRTK_POWER_TRIGGER("geoai_uos_success_turn_on_drtk_power_trigger"),
    /**
     * 开启RTK成功
     */
    GEOAI_UOS_SUCCESS_OPEN_RTK("geoai_uos_success_open_rtk"),
    /**
     * 开启云台自动跟随
     */
    GEOAI_UOS_TURN_ON_PTZ_AUTO_FOLLOW("geoai_uos_turn_on_ptz_auto_follow"),
    /**
     * 开始录音成功
     */
    GEOAI_UOS_SUCCESS_START_RECORDING("geoai_uos_success_start_recording"),
    /**
     * 开始时间不能为空
     */
    GEOAI_UOS_CANNOT_EMPTY_START_TIME("geoai_uos_cannot_empty_start_time"),
    /**
     * 流ID为空，请检查参数
     */
    GEOAI_UOS_CANNOT_EMPTY_STREAM_ID("geoai_uos_cannot_empty_stream_id"),
    /**
     * 没有nestID的数据库
     */
    GEOAI_UOS_NO_DATABASE_WITH_NEST("geoai_uos_no_database_with_nest"),
    /**
     * 没有该机巢,设置推流成功
     */
    GEOAI_UOS_NO_DRONE_NEST_SET_UP_PUSH_FLOW_SUCCESS("geoai_uos_no_drone_nest_set_up_push_flow_success"),
    /**
     * 没有选择照片
     */
    GEOAI_UOS_NO_PHOTO_SELECTED("geoai_uos_no_photo_selected"),
    /**
     * 密码不能为空
     */
    GEOAI_UOS_CANNOT_EMPTY_PASSWORD("geoai_uos_cannot_empty_password"),
    /**
     * 明细ID不能为空
     */
    GEOAI_UOS_CANNOT_DETAIL_ID("geoai_uos_cannot_detail_id"),
    /**
     * 目前的mqtt代理地址正在使用，无法删除
     */
    GEOAI_UOS_CANNOT_BE_DELETED_MQTT_IS_USE("geoai_uos_cannot_be_deleted_mqtt_is_use"),
    /**
     * 目前的区域正在使用，无法删除
     */
    GEOAI_UOS_CANNOT_BE_DELETED_REGION_IS_USE("geoai_uos_cannot_be_deleted_region_is_use"),
    /**
     * 批量删除成功
     */
    GEOAI_UOS_SUCCESS_BATCH_DELETE("geoai_uos_success_batch_delete"),
    /**
     * 匹配半径范围不能为空
     */
    GEOAI_UOS_CANNOT_EMPTY_MATCHING_RADIUS("geoai_uos_cannot_empty_matching_radius"),
    /**
     * 切换视频源成功
     */
    GEOAI_UOS_SUCCESS_SWITCHING_VIDEO_SOURCE("geoai_uos_success_switching_video_source"),
    /**
     * 清空成功
     */
    GEOAI_UOS_SUCCESS_EMPTYING("geoai_uos_success_emptying"),
    /**
     * 清空基站日志成功
     */
    GEOAI_UOS_SUCCESS_CLEAR_THE_NEST_LOG("geoai_uos_success_clear_the_nest_log"),
    /**
     * 请选择文件
     */
    GEOAI_UOS_PLEASE_SELECT_THE_FILE("geoai_uos_please_select_the_file"),
    /**
     * 请选择需要合并的标注
     */
    GEOAI_UOS_PLEASE_SELECT_THE_MARKERS_TO_BE_MERGED("geoai_uos_please_select_the_markers_to_be_merged"),
    /**
     * 请选择需要合并的分组
     */
    GEOAI_UOS_PLEASE_SELECT_THE_GROUPS_TO_BE_MERGED("geoai_uos_please_select_the_groups_to_be_merged"),
    /**
     * 区域描述不能超200个字符
     */
    GEOAI_UOS_CANNOT_EXCEED_200_CHAR_REGION_DESCRIPTION("geoai_uos_cannot_exceed_200_char_region_description"),
    /**
     * 区域描述不能超80个字符
     */
    GEOAI_UOS_CANNOT_EXCEED_80_CHAR_REGION_DESCRIPTION("geoai_uos_cannot_exceed_80_char_region_description"),
    /**
     * 区域名称不能为空
     */
    GEOAI_UOS_CANNOT_EMPTY_REGION_NAME("geoai_uos_cannot_empty_region_name"),
    /**
     * 区域名称不限2~16个字符
     */
    GEOAI_UOS_LIMITED_2_16_REGION_NAME("geoai_uos_limited_2_16_region_name"),
    /**
     * 取消合并失败
     */
    GEOAI_UOS_FAIL_CANCEL_MERGE("geoai_uos_fail_cancel_merge"),
    /**
     * 取消上传成功
     */
    GEOAI_UOS_SUCCESS_CANCEL_UPLOAD("geoai_uos_success_cancel_upload"),
    /**
     * 取消云台自动跟随成功
     */
    GEOAI_UOS_SUCCESS_CANCELLATION_OF_THE_HEAD_AUTO_FOLLOW("geoai_uos_success_cancellation_of_the_head_auto-follow"),
    /**
     * 缺少航线数据
     */
    GEOAI_UOS_MISSING_ROUTE_DATA("geoai_uos_missing_route_data"),
    /**
     * 缺陷识别失败!
     */
    GEOAI_UOS_FAIL_DEFECT_IDENTIFICATION("geoai_uos_fail_defect_identification"),
    /**
     * 任务保存修改失败
     */
    GEOAI_UOS_FAIL_TASK_SAVE_MODIFICATION("geoai_uos_fail_task_save_modification"),
    /**
     * 任务标签保存成功
     */
    GEOAI_UOS_SUCCESS_TASK_TAG_SAVE_MODIFICATION("geoai_uos_success_task_tag_save_modification"),
    /**
     * 任务不存在
     */
    GEOAI_UOS_NOT_EXIST_MISSION("geoai_uos_not_exist_mission"),
    /**
     * 任务复制成功
     */
    GEOAI_UOS_SUCCESS_MISSION_REPLICATION("geoai_uos_success_mission_replication"),
    /**
     * 任务开启成功
     */
    GEOAI_UOS_SUCCESS_MISSION_OPENED("geoai_uos_success_mission_opened"),
    /**
     * 任务开启指令下发成功
     */
    GEOAI_UOS_SUCCESS_MISSION_OPENING_COMMAND("geoai_uos_success_mission_opening_command"),
    /**
     * 日期查询最大366天
     */
    GEOAI_UOS_DATE_SEARCH_UP_TO_366_DAYS("geoai_uos_date_search_up_to_366_days"),
    /**
     * 日期格式有问题，请检查参数
     */
    GEOAI_UOS_PROBLEM_WITH_THE_DATE_FORMAT("geoai_uos_problem_with_the_date_format"),
    /**
     * 日志上传成功
     */
    GEOAI_UOS_SUCCESS_LOGS_UPLOADED("geoai_uos_success_logs_uploaded"),
    /**
     * 日志上传失败
     */
    GEOAI_UOS_FAIL_LOGS_UPLOADED("geoai_uos_fail_logs_uploaded"),
    /**
     * 删除成功
     */
    GEOAI_UOS_SUCCESS_DELETED("geoai_uos_success_deleted"),
    /**
     * 删除任务成功
     */
    GEOAI_UOS_SUCCESS_DELETED_TASK("geoai_uos_success_deleted_task"),
    /**
     * 删除任务失败
     */
    GEOAI_UOS_FAIL_DELETED_TASK("geoai_uos_fail_deleted_task"),
    /**
     * 上传日志指令下发成功
     */
    GEOAI_UOS_SUCCESS_UPLOAD_LOG_COMMAND_SENT("geoai_uos_success_upload_log_command_sent"),
    /**
     * 上传日志指令下发失败
     */
    GEOAI_UOS_FAIL_UPLOAD_LOG_COMMAND_SENT("geoai_uos_fail_upload_log_command_sent"),
    /**
     * 上传失败
     */
    GEOAI_UOS_FAIL_UPLOAD("geoai_uos_fail_upload"),
    /**
     * 上传文件失败
     */
    GEOAI_UOS_FAIL_UPLOAD_FILE("geoai_uos_fail_upload_file"),
    /**
     * 上传坐标有问题
     */
    GEOAI_UOS_PROBLEM_UPLOADING_THE_COORDINATES("geoai_uos_problem_uploading_the_coordinates"),
    /**
     * 设备ID为空，请检查参数
     */
    GEOAI_UOS_CANNOT_EMPTY_DEVICEID("geoai_uos_cannot_empty_deviceid"),
    /**
     * 设备变焦成功
     */
    GEOAI_UOS_SUCCESS_DEVICE_ZOOMS("geoai_uos_success_device_zooms"),
    /**
     * 设置RTK账号信息成功
     */
    GEOAI_UOS_SUCCESS_SET_RTK_ACCOUNT_INFORMATION("geoai_uos_success_set_rtk_account_information"),
    /**
     * 设置RTK状态成功
     */
    GEOAI_UOS_SUCCESS_SET_RTK_STATUS("geoai_uos_success_set_rtk_status"),
    /**
     * 设置成功
     */
    GEOAI_UOS_SUCCESS_SET_UP("geoai_uos_success_set_up"),
    /**
     * 设置低电量智能关机成功
     */
    GEOAI_UOS_SUCCESS_SET_LOW_BATTERY_SMART_POWER_OFF("geoai_uos_success_set_low_battery_smart_power_off"),
    /**
     * 设置返航高度成功
     */
    GEOAI_UOS_SUCCESS_SET_RETURN_ALTITUDE("geoai_uos_success_set_return_altitude"),
    /**
     * 设置返航高度失败
     */
    GEOAI_UOS_FAIL_SET_RETURN_ALTITUDE("geoai_uos_fail_set_return_altitude"),
    /**
     * 设置红外测温信息失败
     */
    GEOAI_UOS_FAIL_SET_INFRARED_TEMPERATURE_MEASUREMENT_INFORMATION("geoai_uos_fail_set_infrared_temperature_measurement_information"),
    /**
     * 设置拍照/录像源失败
     */
    GEOAI_UOS_FAIL_SET_PHOTO_VIDEO_SOURCE("geoai_uos_fail_set_photo/video_source"),
    /**
     * 设置探照灯亮度成功
     */
    GEOAI_UOS_SUCCESS_SET_SEARCHLIGHT_BRIGHTNESS("geoai_uos_success_set_searchlight_brightness"),
    /**
     * 设置推流成功
     */
    GEOAI_UOS_SUCCESS_SET_UP_PUSH_FLOW("geoai_uos_success_set_up_push_flow"),
    /**
     * 设置相机镜头视频源成功
     */
    GEOAI_UOS_SUCCESS_CAMERA_LENS_VIDEO_SOURCE("geoai_uos_success_camera_lens_video_source"),
    /**
     * 设置最大飞行高度成功
     */
    GEOAI_UOS_SUCCESS_MAXIMUM_FLIGHT_ALTITUDE("geoai_uos_success_maximum_flight_altitude"),
    /**
     * 设置最大飞行高度失败
     */
    GEOAI_UOS_FAIL_MAXIMUM_FLIGHT_ALTITUDE("geoai_uos_fail_maximum_flight_altitude"),
    /**
     * 设置最远飞行距离成功
     */
    GEOAI_UOS_SUCCESS_SET_THE_LONGEST_FLIGHT_DISTANCE("geoai_uos_success_set_the_longest_flight_distance"),
    /**
     * 设置最远飞行距离请求发送失败
     */
    GEOAI_UOS_FAIL_TO_SEND_A_REQUEST_TO_SET_THE_MAXIMUM_FLIGHT_DISTANCE("geoai_uos_fail_to_send_a_request_to_set_the_maximum_flight_distance"),
    /**
     * 视频字幕成功
     */
    GEOAI_UOS_SUCCESS_VIDEO_CAPTIONING("geoai_uos_success_video_captioning"),
    /**
     * 手机号不能为空
     */
    GEOAI_UOS_CANNOT_EMPTY_PHONE_NUMBER("geoai_uos_cannot_empty_phone_number"),
    /**
     * 手机号格式有误
     */
    GEOAI_UOS_WRONG_FORMAT_PHONE_NUMBER("geoai_uos_wrong_format_phone_number"),
    /**
     * 所属单位不能为空
     */
    GEOAI_UOS_CANNOT_EMPTY_AFFILIATION_UNIT("geoai_uos_cannot_empty_affiliation_unit"),
    /**
     * 所属区域不能为空
     */
    GEOAI_UOS_CANNOT_EMPTY_REGION("geoai_uos_cannot_empty_region"),
    /**
     * 添加标签成功
     */
    GEOAI_UOS_SUCCESS_ADD_TAG("geoai_uos_success_add_tag"),
    /**
     * 添加失败，请勿添加相同名称的图层
     */
    GEOAI_UOS_FAIL_TO_ADD("geoai_uos_fail_to_add"),
    /**
     * 停止播放成功
     */
    GEOAI_UOS_SUCCESS_STOP_PLAY("geoai_uos_success_stop_play"),
    /**
     * 同步成功
     */
    GEOAI_UOS_SUCCESS_SYNCHRONIZATION("geoai_uos_success_synchronization"),
    /**
     * 同步源数据开始
     */
    GEOAI_UOS_BEGIN_SYNCHRONIZATION("geoai_uos_begin_synchronization"),
    /**
     * 同一类别下,不能有两个相同名称的维保项目
     */
    GEOAI_UOS_CANNOT_BE_TWO_MAINTENANCE_PROJECTS_WITH_THE_SAME_NAME("geoai_uos_cannot_be_two_maintenance_projects_with_the_same_name"),
    /**
     * 图片不存在，请检查参数
     */
    GEOAI_UOS_NOT_EXIST_IMAGE("geoai_uos_not_exist_image"),
    /**
     * 图片格式有问题
     */
    GEOAI_UOS_PROBLEM_WITH_THE_IMAGE_FORMAT("geoai_uos_problem_with_the_image_format"),
    /**
     * 推送成功
     */
    GEOAI_UOS_SUCCESS_PUSH("geoai_uos_success_push"),
    /**
     * 推送分析中心基础信息，missionId不能为空
     */
    GEOAI_UOS_CANNOT_EMPTY_MISSIONID_1("geoai_uos_cannot_empty_missionId"),
    /**
     * 推送分析中心基础信息，missionRecordId不能为空
     */
    GEOAI_UOS_CANNOT_EMPTY_MISSIONRECORDID("geoai_uos_cannot_empty_missionRecordId"),
    /**
     * 推送分析中心基础信息，missionRecordTime不能为空
     */
    GEOAI_UOS_CANNOT_EMPTY_MISSIONRECORDTIME("geoai_uos_cannot_empty_missionRecordTime"),
    /**
     * 推送分析中心基础信息，missionSeqId不能为空
     */
    GEOAI_UOS_CANNOT_EMPTY_MISSIONSEQID("geoai_uos_cannot_empty_missionSeqId"),
    /**
     * 推送分析中心基础信息，orgId不能为空
     */
    GEOAI_UOS_CANNOT_EMPTY_ORGID("geoai_uos_cannot_empty_orgId"),
    /**
     * 推送分析中心基础信息，subType不能为空
     */
    GEOAI_UOS_CANNOT_EMPTY_SUBTYPE("geoai_uos_cannot_empty_subType"),
    /**
     * 推送分析中心基础信息，tagId不能为空
     */
    GEOAI_UOS_CANNOT_EMPTY_TAGID("geoai_uos_cannot_empty_tagId"),
    /**
     * 推送分析中心基础信息，taskType不能为空
     */
    GEOAI_UOS_CANNOT_EMPTY_TASKTYPE("geoai_uos_cannot_empty_taskType"),
    /**
     * 退出云台自动跟随模式成功
     */
    GEOAI_UOS_SUCCESS_EXIT_PTZ_AUTO_FOLLOW_MODE("geoai_uos_success_exit_PTZ_auto-follow_mode"),
    /**
     * 未实现获取基站uuid的方法
     */
    GEOAI_UOS_UNIMPLEMENTED_METHOD_TO_GET_UUID_OF_DRONE_NEST("geoai_uos_unimplemented_method_to_get_uuid_of_drone_nest"),
    /**
     * 未知异常，请联系管理员
     */
    GEOAI_UOS_NO_KNOWN_EXCEPTIONS("geoai_uos_no_known_exceptions"),
    /**
     * 文件不能超过5M
     */
    GEOAI_UOS_FILE_SHOULD_NOT_EXCEED_5M("geoai_uos_file_should_not_exceed_5M"),
    /**
     * 文件类型不是图片
     */
    GEOAI_UOS_FILE_TYPE_IS_NOT_AN_IMAGE("geoai_uos_file_type_is_not_an_image"),
    /**
     * 问题已在使用，不可删除!
     */
    GEOAI_UOS_ALREADY_IN_USE_CANNOT_BE_DELETED("geoai_uos_already_in_use_cannot_be_deleted"),
    /**
     * 问题图片绘制失败
     */
    GEOAI_UOS_FAIL_PROBLEM_IMAGE_DRAWING("geoai_uos_fail_problem_image_drawing"),
    /**
     * 无人机ID为空，请检查参数
     */
    GEOAI_UOS_CANNOT_EMPTY_DRONE_ID("geoai_uos_cannot_empty_drone_id"),
    /**
     * 无人机信息不存在，更新失败
     */
    GEOAI_UOS_NOT_EXIST_DRONE_ID("geoai_uos_not_exist_drone_id"),
    /**
     * 无人机型号不合法，请检查参数
     */
    GEOAI_UOS_NOT_LEGAL_DRONE_MODEL("geoai_uos_not_legal_drone_model"),
    /**
     * 现场取证ID不能为空
     */
    GEOAI_UOS_CANNOT_EMPTY_LIVE_FORENSICID("geoai_uos_cannot_empty_live_forensicid"),
    /**
     * 新密码不能为空
     */
    GEOAI_UOS_CANNOT_EMPTY_NEW_PASSWORD("geoai_uos_cannot_empty_new_password"),
    /**
     * 新增分析中心基础数据失败，请检查
     */
    GEOAI_UOS_FAIL_ADDING_ANALYSIS_CENTER_BASE_DATA("geoai_uos_fail_adding_analysis_center_base_data"),
    /**
     * 行业ID不能为空
     */
    GEOAI_UOS_CANNOT_EMPTY_INDUSTRY_ID("geoai_uos_cannot_empty_industry_ID"),
    /**
     * 姓名不能为空
     */
    GEOAI_UOS_CANNOT_EMPTY_NAME("geoai_uos_cannot_empty_name"),
    /**
     * 修改失败，已存在相同名称的图层
     */
    GEOAI_UOS_FAIL_SAME_NAME_ALREADY_EXISTS("geoai_uos_fail_same_name_already_exists"),
    /**
     * 压缩包文件为空
     */
    GEOAI_UOS_IS_EMPTY_ZIP_FILE("geoai_uos_is_empty_zip_file"),
    /**
     * 压缩包中未检索到MPS安装包
     */
    GEOAI_UOS_NOT_RETRIEVED_FROM_THE_ZIP_ARCHIVE("geoai_uos_not_retrieved_from_the_zip_archive"),
    /**
     * 音量设置成功
     */
    GEOAI_UOS_SUCCESS_VOLUME_SETTING("geoai_uos_success_volume_setting"),
    /**
     * 音频名称不能重复，请更换名称
     */
    GEOAI_UOS_CANNOT_BE_REPEATED_AUDIO_NAME("geoai_uos_cannot_be_repeated_audio_name"),
    /**
     * 应用类型不能为空
     */
    GEOAI_UOS_CANNOT_EMPTY_APPLICATION_TYPE("geoai_uos_cannot_empty_application_type"),
    /**
     * 用户名不能为空
     */
    GEOAI_UOS_CANNOT_EMPTY_USERNAME("geoai_uos_cannot_empty_username"),
    /**
     * 用户名仅支持6~18个含英文或数字字符
     */
    GEOAI_UOS_LIMITED_6_18_USER_NAME("geoai_uos_limited_6_18_user_name"),
    /**
     * 用户姓名仅支持2~16个含中、英文和数字字符
     */
    GEOAI_UOS_LIMITED_2_16_USER_NAME("geoai_uos_limited_2_16_user_name"),
    /**
     * 云端航线复制成功
     */
    GEOAI_UOS_SUCCESS_CLOUD_ROUTE_REPLICATION("geoai_uos_success_cloud_route_replication"),
    /**
     * 云端航线复制失败
     */
    GEOAI_UOS_FAIL_CLOUD_ROUTE_REPLICATION("geoai_uos_fail_cloud_route_replication"),
    /**
     * 暂停任务成功
     */
    GEOAI_UOS_SUCCESS_SUSPEND_MISSION("geoai_uos_success_suspend_mission"),
    /**
     * 账号ID不能为空
     */
    GEOAI_UOS_CANNOT_EMPTY_ACCOUNTID("geoai_uos_cannot_empty_accountid"),
    /**
     * 执行ffmpeg命令成功
     */
    GEOAI_UOS_SUCCESS_EXECUTE_FFMPEG_COMMAND("geoai_uos_success_execute_ffmpeg_command"),
    /**
     * 执行成功
     */
    GEOAI_UOS_SUCCESS_IMPLEMENTATION("geoai_uos_success_implementation"),
    /**
     * 执行获取推流模式动作，NestId不能为空
     */
    GEOAI_UOS_CANNOT_EMPTY_NESTID_EXECUTE_GET_PUSH_MODE_ACTION("geoai_uos_cannot_empty_nestId_execute_get_push_mode_action"),
    /**
     * 执行设置推流模式动作，NestId和mode不能为空
     */
    GEOAI_UOS_CANNOT_EMPTY_NESTID_AND_MODE_EXECUTE_SET_PUSH_MODE_ACTION("geoai_uos_cannot_empty_nestId_and_mode_execute_set_push_mode_action"),
    /**
     * 指令发送成功，开始上传音频到CPS
     */
    GEOAI_UOS_SUCCESS_COMMAND_SENT("geoai_uos_success_command_sent"),
    /**
     * 终止任务成功
     */
    GEOAI_UOS_SUCCESS_TERMINATE_MISSION("geoai_uos_success_terminate_mission"),
    /**
     * 重命名成功
     */
    GEOAI_UOS_SUCCESS_RENAME("geoai_uos_success_rename"),
    /**
     * 重置mqtt客户端成功
     */
    GEOAI_UOS_SUCCESS_RESET_MQTT_CLIENT("geoai_uos_success_reset_mqtt_client"),
    /**
     * 重置变焦成功
     */
    GEOAI_UOS_SUCCESS_RESETTING_THE_ZOOM("geoai_uos_success_resetting_the_zoom"),
    /**
     * 重置成功
     */
    GEOAI_UOS_SUCCESS_RESET("geoai_uos_success_reset"),
    /**
     * 重置镜头成功
     */
    GEOAI_UOS_SUCCESS_RESETTING_THE_LENS("geoai_uos_success_resetting_the_lens"),
    /**
     * 专题key不能为空
     */
    GEOAI_UOS_CANNOT_EMPTY_TOPIC_KEY("geoai_uos_cannot_empty_topic_key"),
    /**
     * 专题等级ID不能为空
     */
    GEOAI_UOS_CANNOT_EMPTY_TOPIC_LEVEL("geoai_uos_cannot_empty_topic_level"),
    /**
     * 专题行业名称不能为空
     */
    GEOAI_UOS_CANNOT_EMPTY_TOPIC_THEMATIC_INDUSTRY_NAME("geoai_uos_cannot_empty_topic_thematic_industry_name"),
    /**
     * 专题行业名称最长6个字
     */
    GEOAI_UOS_THEMATIC_INDUSTRY_NAME_UP_TO_6_WORDS("geoai_uos_thematic_industry_name_up_to_6_words"),
    /**
     * 专题行业问题名称不能为空
     */
    GEOAI_UOS_CANNOT_EMPTY_THEMATIC_INDUSTRY_ISSUE_NAME("geoai_uos_cannot_empty_thematic_industry_issue_name"),
    /**
     * 专题行业问题名称最长10个字
     */
    GEOAI_UOS_TOPICAL_INDUSTRY_ISSUE_NAME_UP_TO_10_WORDS("geoai_uos_topical_industry_issue_name_up_to_10_words"),
    /**
     * 最大导出20条数据
     */
    GEOAI_UOS_EXPORT_UP_TO_20_PIECES_OF_DATA("geoai_uos_export_up_to_20_pieces_of_data"),


    /**
     * 推送分析中心基础信息，taskId 不能为空！
     */
    GEOAI_UOS_ANALYTICS_CENTER_CANNOT_EMPTY_TASKID("geoai_uos__analytics center_cannot_empty_taskid"),

    /**
     * 查询app异常
     */
    GEOAI_UOS_QUERY_APP_EXCEPTION("geoai_uos_query_app_exception"),
    /**
     * 查询无人机异常
     */
    GEOAI_UOS_QUERY_UAV_EXCEPTION("geoai_uos_query_uav_exception"),
    /**
     * 查询流信息异常
     */
    GEOAI_UOS_QUERY_FLOW_EXCEPTION("geoai_uos_query_flow_exception"),
    /**
     * 删除移动终端失败
     */
    GEOAI_UOS_DELETE_MOBILE_TERMINAL_FAIL("geoai_uos_delete_mobile_terminal_fail"),
    /**
     * 保存app消息异常
     */
    GEOAI_UOS_SAVING_APP_MESSAGE_EXCEPTION("geoai_uos_saving_app_message_exception"),
    /**
     * 保存流媒体异常
     */
    GEOAI_UOS_SAVING_STREAMING_MEDIA_EXCEPTION("geoai_uos_saving_streaming_media_exception"),
    /**
     * 保存无人机异常
     */
    GEOAI_UOS_SAVING_UAV_EXCEPTION("geoai_uos_saving_uav_exception"),
    /**
     * 保存app与无人机关系异常
     */
    GEOAI_UOS_SAVING_UAV_APP__RELATIONSHIP_EXCEPTION("geoai_uos_saving_uav_app__relationship_exception"),
    /**
     * 查询streamId异常
     */
    GEOAI_UOS_QUERY_STREAMID_EXCEPTION("geoai_uos_query_streamid_exception"),
    /**
     * 基站已经存在无人机信息，请不要重复添加。
     */
    GEOAI_UOS_UAV_STATION_RELATION_ALREADY_EXISTS("geoai_uos_uav_station_relation_already_exists"),
    /**
     * 基站和无人机的关系不存在，设置失败.
     */
    GEOAI_UOS_UAV_STATION_RELATION_NOT_EXISTS("geoai_uos_uav_station_relation_not_exists"),
    /**
     * 基站无人机信息不存在，设置失败
     */
    GEOAI_UOS_STATION_UAV_RELATION_NOT_EXISTS("geoai_uos_station_uav_relation_not_exists"),
    /**
     * 无人机推流地址设置失败
     */
    GEOAI_UOS_STREAM_ADDRESS_SETTING_FAIL("geoai_uos_stream_address_setting_fail"),
    /**
     * 推送分析中心基础信息，missionRecordId 不能为空！
     */
    GEOAI_UOS_CANNOT_EMPTY_MISSION_RECORD_ID("geoai_uos_cannot_empty_mission_record_id"),
    /**
     * 【分析统计】推送了空表体，清检查
     */
    GEOAI_UOS_PUSH_EMPTY_TABLE_BODY("geoai_uos_push_empty_table_body"),
    /**
     * 在待核实[有问题]照片未选中行业-问题类型
     */
    GEOAI_UOS_PROBLEM_INDUSTRY_UNDEFINE("geoai_uos_problem_industry_undefine"),
    /**
     * 已核实过的数据，不允许操作标注，请优先撤回再操作
     */
    GEOAI_UOS_DATA_CANNOT_BE_MARKED_BEFORE_OPERATION("geoai_uos_data_cannot_be_marked_before_operation"),
    /**
     * 生成图片缩略图失败
     */
    GEOAI_UOS_FAILED_TO_GENERATE_IMAGE_THUMBNAIL("geoai_uos_failed_to_generate_image_thumbnail"),
    /**
     * 架次信息不存在
     */
    GEOAI_UOS_SORTIE_INFORMATION_DOES_NOT_EXIST("geoai_uos_sortie_information_does_not_exist"),
    /**
     * 任务信息不存在
     */
    GEOAI_UOS_TASK_INFORMATION_DOES_NOT_EXIST("geoai_uos_task_information_does_not_exist"),
    /**
     * 基站信息不存在
     */
    GEOAI_UOS_BTS_INFORMATION_DOES_NOT_EXIST("geoai_uos_bts_information_does_not_exist"),
    /**
     * 图片没有所属架次记录，请检查。
     */
    GEOAI_UOS_NO_RECORD_OF_SORTIES_IN_THE_PICTURE("geoai_uos_no_record_of_sorties_in_the_picture"),
    /**
     * 图片没有所属架次，请检查
     */
    GEOAI_UOS_PICTURE_DOES_NOT_BELONG_TO_ANY_SORTIES("geoai_uos_picture_does_not_belong_to_any_sorties"),
    /**
     * 图片没有所属任务，请检查
     */
    GEOAI_UOS_IMAGE_DOES_NOT_BELONG_TO_THE_TASK("geoai_uos_image_does_not_belong_to_the_task"),
    /**
     * 架次执行ID不存在
     */
    GEOAI_UOS_SORTIE_EXECUTION_ID_DOES_NOT_EXIST("geoai_uos_sortie_execution_id_does_not_exist"),
    /**
     * 架次执行ID已执行完成
     */
    GEOAI_UOS_EXECUTION_OF_THE_SORTIE_ID_HAS_BEEN_COMPLETED("geoai_uos_execution_of_the_sortie_id_has_been_completed"),
    /**
     * 架次执行ID对应的架次不存在
     */
    GEOAI_UOS_SORTIE_CORRESPONDING_TO_THE_SORTIE_EXECUTION_ID_DOES_NOT_EXIST("geoai_uos_sortie_corresponding_to_the_sortie_execution_id_does_not_exist"),
    /**
     * 架次执行ID对应的任务不存在
     */
    GEOAI_UOS_TASK_CORRESPONDING_TO_THE_SORTIE_EXECUTION_ID_DOES_NOT_EXIST("geoai_uos_task_corresponding_to_the_sortie_execution_id_does_not_exist"),
    /**
     * 基站的监控信息正在被其他用户修改，请稍后重试
     */
    GEOAI_UOS_MONITORING_INFORMATION_OF_THE_BASE_STATION_IS_BEING_MODIFIED("geoai_uos_monitoring_information_of_the_base_station_is_being_modified"),
    /**
     * 基站信息不存在，请检查参数
     */
    GEOAI_UOS_BASE_STATION_INFORMATION_DOES_NOT_EXIST_PLEASE_CHECK_THE_PARAMETERS("geoai_uos_base_station_information_does_not_exist,_please_check_the_parameters"),
    /**
     * 基站的监控流信息不存在，请联系管理员处理
     */
    GEOAI_UOS_MONITORING_STREAM_INFORMATION_OF_THE_BASE_STATION_DOES_NOT_EXIST("geoai_uos_monitoring_stream_information_of_the_base_station_does_not_exist"),
    /**
     * 基站的监控信息不存在，请联系管理员处理
     */
    GEOAI_UOS_MONITORING_INFORMATION_OF_THE_BASE_STATION_DOES_NOT_EXIST("geoai_uos_monitoring_information_of_the_base_station_does_not_exist"),
    /**
     * 请先设置监控信息，再来设置
     */
    GEOAI_UOS_PLEASE_SET_THE_MONITORING_INFORMATION_FIRST_AND_THEN_SET("geoai_uos_please_set_the_monitoring_information_first,_and_then_set"),
    /**
     * 未找到设备信息，请联系管理员
     */
    GEOAI_UOS_DEVICE_INFORMATION_NOT_FOUND_PLEASE_CONTACT_THE_ADMINISTRATOR("geoai_uos_device_information_not_found,_please_contact_the_administrator"),
    /**
     * 设置cps失败，请联系管理员
     */
    GEOAI_UOS_SET_CPS_FAILED_PLEASE_CONTACT_THE_ADMINISTRATOR("geoai_uos_set_cps_failed,_please_contact_the_administrator"),
    /**
     * 行业名称不允许重复
     */
    GEOAI_UOS_INDUSTRY_NAMES_ARE_NOT_ALLOWED_TO_REPEAT("geoai_uos_industry_names_are_not_allowed_to_repeat"),
    /**
     * 行业问题名称不允许重复
     */
    GEOAI_UOS_INDUSTRY_ISSUE_NAMES_ARE_NOT_ALLOWED_TO_REPEAT("geoai_uos_industry_issue_names_are_not_allowed_to_repeat"),
    /**
     * 执行停用启用电池组动作，NestId不能为空
     */
    GEOAI_UOS_PERFORM_DEACTIVATE_ENABLE_BATTERY_PACK_ACTION_NESTID_CANNOT_BE_EMPTY("geoai_uos_perform_deactivate_enable_battery_pack_action,_nestid_cannot_be_empty"),
    /**
     * 执行获取电池组启用状态动作，NestId不能为空
     */
    GEOAI_UOS_EXECUTE_THE_ACTION_OF_GETTING_THE_BATTERY_PACK_ENABLED_STATUS_NESTID_CANNOT_BE_EMPTY("geoai_uos_execute_the_action_of_getting_the_battery_pack_enabled_status,_nestid_cannot_be_empty"),
    /**
     * 执行获取电池组启用状态动作，
     */
    GEOAI_UOS_EXECUTES_THE_GET_BATTERY_PACK_ENABLE_STATUS_ACTION("geoai_uos_executes_the_get_battery_pack_enable_status_action."),
    /**
     * 执行获取电池组启用状态动作，仅支持G900机巢
     */
    GEOAI_UOS_PERFORM_THE_GET_BATTERY_PACK_ENABLED_ACTION_ONLY_SUPPORTED_FOR_G900_NESTS("geoai_uos_perform_the_get_battery_pack_enabled_action,_only_supported_for_g900_nests"),
    /**
     * 执行空中回巢动作，NestId不能为空
     */
    GEOAI_UOS_EXECUTE_AERIAL_NESTING_ACTION_NESTID_CANNOT_BE_EMPTY("geoai_uos_execute_aerial_nesting_action,_nestid_cannot_be_empty"),
    /**
     * CPS固件安装包解压失败
     */
    GEOAI_UOS_CPS_FIRMWARE_INSTALLATION_PACKAGE_DECOMPRESSION_FAILURE("geoai_uos_cps_firmware_installation_package_decompression_failure"),
    /**
     * 基站已离线，无法执行该操作
     */
    GEOAI_UOS_THE_BASE_STATION_IS_OFFLINE_AND_CANNOT_PERFORM_THIS_OPERATION("geoai_uos_the_base_station_is_offline_and_cannot_perform_this_operation"),
    /**
     * 安装包信息不存在
     */
    GEOAI_UOS_INSTALLATION_PACKAGE_INFORMATION_DOES_NOT_EXIST("geoai_uos_installation_package_information_does_not_exist"),
    /**
     * 执行云台调整动作，pitchAngle和yamAngle不能为空
     */
    GEOAI_UOS_EXECUTE_HEAD_ADJUSTMENT_ACTION_PITCHANGLE_AND_YAMANGLE_CANNOT_BE_EMPTY("geoai_uos_execute_head_adjustment_action,_pitchangle_and_yamangle_cannot_be_empty"),
    /**
     * 当前mqtt代理地址不存在
     */
    GEOAI_UOS_CURRENT_MQTT_PROXY_ADDRESS_DOES_NOT_EXIST("geoai_uos_current_mqtt_proxy_address_does_not_exist"),
    /**
     * 基站的mqtt连接信息不存在，请联系管理员。
     */
    GEOAI_UOS_MQTT_CONNECTION_INFORMATION_OF_THE_BASE_STATION_DOES_NOT_EXIST("geoai_uos_mqtt_connection_information_of_the_base_station_does_not_exist"),
    /**
     * 查询基站指令词典项
     */
    GEOAI_UOS_QUERY_BASE_STATION_COMMAND_DICTIONARY_ITEMS("geoai_uos_query_base_station_command_dictionary_items"),
    /**
     * 重置镜头失败
     */
    GEOAI_UOS_RESET_LENS_FAILED("geoai_uos_reset_lens_failed"),
    /**
     * 继续任务执行动作失败
     */
    GEOAI_UOS_FAILED_TO_CONTINUE_TASK_EXECUTION_ACTION("geoai_uos_failed_to_continue_task_execution_action"),
    /**
     * 立即执行任务动作失败
     */
    GEOAI_UOS_IMMEDIATE_TASK_ACTION_FAILED("geoai_uos_immediate_task_action_failed"),
    /**
     * 一键重置失败
     */
    GEOAI_UOS_ONE_CLICK_RESET_FAILED("geoai_uos_one-click_reset_failed"),
    /**
     * 一键重置成功
     */
    GEOAI_UOS_ONE_CLICK_RESET_SUCCESS("geoai_uos_one_click_reset_success"),
    /**
     * 归中X打开失败
     */
    GEOAI_UOS_RETURN_TO_THE_CENTER_X_OPEN_FAILURE("geoai_uos_return_to_the_center_x_open_failure"),
    /**
     * 一键开启成功
     */
    GEOAI_UOS_ONE_CLICK_TO_OPEN_SUCCESSFULLY("geoai_uos_one_click_to_open_successfully"),
    /**
     * 一键回收成功
     */
    GEOAI_UOS_ONE_CLICK_RECYCLING_SUCCESS("geoai_uos_one_click_recycling_success"),
    /**
     * 一键回收失败
     */
    GEOAI_UOS_ONE_CLICK_RECYCLING_FAILURE("geoai_uos_one-click_recycling_failure"),
    /**
     * 启动引导成功
     */
    GEOAI_UOS_BOOTSTRAP_SUCCESS("geoai_uos_bootstrap_success"),
    /**
     * 启动引导失败
     */
    GEOAI_UOS_BOOT_FAILURE("geoai_uos_boot_failure"),
    /**
     * 电池装载成功
     */
    GEOAI_UOS_BATTERY_LOADING_SUCCESS("geoai_uos_battery_loading_success"),
    /**
     * 电池装载失败
     */
    GEOAI_UOS_BATTERY_LOADING_FAILURE("geoai_uos_battery_loading_failure"),
    /**
     * 舱门打开成功
     */
    GEOAI_UOS_SUCCESSFUL_HATCH_OPENING("geoai_uos_successful_hatch_opening"),
    /**
     * 舱门打开失败
     */
    GEOAI_UOS_HATCH_OPENING_FAILURE("geoai_uos_hatch_opening_failure"),
    /**
     * 舱门重置失败
     */
    GEOAI_UOS_HATCH_RESET_FAILURE("geoai_uos_hatch_reset_failure"),
    /**
     * 舱门重置
     */
    GEOAI_UOS_HATCH_RESET("geoai_uos_hatch_reset"),
    /**
     * 平台升起
     */
    GEOAI_UOS_PLATFORM_RISING("geoai_uos_platform_rising"),
    /**
     * 平台升起失败
     */
    GEOAI_UOS_PLATFORM_LIFT_FAILURE("geoai_uos_platform_lift_failure"),
    /**
     * 机械臂X重置
     */
    GEOAI_UOS_ROBOTIC_ARM_X_RESET("geoai_uos_robotic_arm_x_reset"),
    /**
     * 机械臂原点失败
     */
    GEOAI_UOS_ROBOTIC_ARM_HOME_POINT_FAILURE("geoai_uos_robotic_arm_home_point_failure"),
    /**
     * 机械臂中间点失败
     */
    GEOAI_UOS_ROBOTIC_ARM_MIDPOINT_FAILURE("geoai_uos_robotic_arm_midpoint_failure"),
    /**
     * 机械臂Y中间点
     */
    GEOAI_UOS_ROBOTIC_ARM_Y_MIDPOINT("geoai_uos_robotic_arm_y_midpoint"),
    /**
     * 机械臂Z原点
     */
    GEOAI_UOS_ROBOTIC_ARM_Z_ORIGIN("geoai_uos_robotic_arm_z_origin"),
    /**
     * 机械臂Z原点失败
     */
    GEOAI_UOS_ROBOTIC_ARM_Z_HOME_POINT_FAILURE("geoai_uos_robotic_arm_z_home_point_failure"),
    /**
     * 机械臂Z中间点失败
     */
    GEOAI_UOS_ROBOTIC_ARM_Z_MIDPOINT_FAILURE("geoai_uos_robotic_arm_z_midpoint_failure"),
    /**
     * 机械臂Z中间点
     */
    GEOAI_UOS_ROBOTIC_ARM_Z_MIDPOINT("geoai_uos_robotic_arm_z_midpoint"),
    /**
     * 设置低电量智能关机失败
     */
    GEOAI_UOS_FAILED_TO_SET_LOW_BATTERY_SMART_SHUTDOWN("geoai_uos_failed_to_set_low_battery_smart_shutdown"),
    /**
     * 获取最远飞行距离失败
     */
    GEOAI_UOS_FAILED_TO_GET_THE_LONGEST_FLIGHT_DISTANCE("geoai_uos_failed_to_get_the_longest_flight_distance"),
    /**
     * 切换视频源失败
     */
    GEOAI_UOS_SWITCHING_VIDEO_SOURCE_FAILED("geoai_uos_switching_video_source_failed"),
    /**
     * 红外测温设置失败
     */
    GEOAI_UOS_TEMPERATURE_MEASUREMENT_SETTING_FAILED("geoai_uos_temperature_measurement_setting_failed"),
    /**
     * 设置相机镜头视频源失败
     */
    GEOAI_UOS_FAILED_TO_SET_CAMERA_LENS_VIDEO_SOURCE("geoai_uos_failed_to_set_camera_lens_video_source"),
    /**
     * 获取视频字幕失败
     */
    GEOAI_UOS_FAILED_TO_GET_VIDEO_SUBTITLES("geoai_uos_failed_to_get_video_subtitles"),
    /**
     * 视频字幕
     */
    GEOAI_UOS_VIDEO_SUBTITLES("geoai_uos_video_subtitles"),

    /**
     * 打开探照灯失败
     */
    GEOAI_UOS_FAILURE_TURN_ON_SEARCHLIGHT("geoai_uos_failure_turn_on_searchlight"),

    /**
     * 重复播放成功
     */
    GEOAI_UOS_REPLAY_SUCCESS("geoai_uos_replay_success"),
    /**
     * 重复播放失败
     */
    GEOAI_UOS_REPLAY_FAILURE("geoai_uos_replay_failure"),
    /**
     * 播放失败
     */
    GEOAI_UOS_PLAYBACK_FAILED("geoai_uos_playback_failed"),
    /**
     * 停止播放失败
     */
    GEOAI_UOS_FAILED_TO_STOP_PLAYBACK("geoai_uos_failed_to_stop_playback"),
    /**
     * 重命名失败
     */
    GEOAI_UOS_RENAME_FAILURE("geoai_uos_rename_failure"),
    /**
     * 打开夜航灯失败
     */
    GEOAI_UOS_FAILURE_TO_TURN_ON_THE_NIGHT_LIGHT("geoai_uos_failure_to_turn_on_the_night_light"),
    /**
     * 关闭夜航灯失败
     */
    GEOAI_UOS_FAILURE_TO_TURN_OFF_NIGHT_NAVIGATION_LIGHTS("geoai_uos_failure_to_turn_off_night_navigation_lights"),
    /**
     * 设置探照灯亮度失败
     */
    GEOAI_UOS_FAILED_TO_SET_SEARCHLIGHT_BRIGHTNESS("geoai_uos_failed_to_set_searchlight_brightness"),
    /**
     * 发送数据失败,单次传输超过最大值
     */
    GEOAI_UOS_SEND_DATA__EXCEEDS_MAXIMUM_VALUE("geoai_uos_send_data__exceeds_maximum_value"),
    /**
     * 准备发送音频到机巢.....
     */
    GEOAI_UOS_PREPARE_SEND_AUDIO_TO_THE_MACHINE_NEST("geoai_uos_prepare_send_audio_to_the_machine_nest"),
    /**
     * 成功发送音频到机巢.....
     */
    GEOAI_UOS_SUCCESSFULLY_SENT_AUDIO_TO_THE_MACHINE_NEST("geoai_uos_successfully_sent_audio_to_the_machine_nest"),
    /**
     * 失败发送音频到机巢.....
     */
    GEOAI_UOS_FAILED_TO_SEND_AUDIO_TO_THE_MACHINE_NEST("geoai_uos_failed_to_send_audio_to_the_machine_nest"),
    /**
     * 发送数据失败
     */
    GEOAI_UOS_FAILED_TO_SEND_DATA("geoai_uos_failed_to_send_data"),
    /**
     * 开始录音失败
     */
    GEOAI_UOS_FAILED_TO_START_RECORDING("geoai_uos_failed_to_start_recording"),
    /**
     * 转化音频文件为pcm格式失败
     */
    GEOAI_UOS_FAILED_TO_CONVERT_AUDIO_FILE_TO_PCM_FORMAT("geoai_uos_failed_to_convert_audio_file_to_pcm_format"),
    /**
     * 获取音频文件流失败
     */
    GEOAI_UOS_FAILED_TO_GET_AUDIO_FILE_STREAM("geoai_uos_failed_to_get_audio_file_stream"),

    /**
     * 关闭探照灯失败
     */
    GEOAI_UOS_FAILURE_TURN_OFF_THE_SEARCHLIGHT("geoai_uos_failure_turn_off_the_searchlight"),
    /**
     * 登录类型有误
     */
    GEOAI_UOS_WRONG_LOGIN_TYPE("geoai_uos_wrong_login_type"),
    /**
     * 仅支持账号或固定登录
     */
    GEOAI_UOS_ONLY_SUPPORT_ACCOUNT_OR_FIXED_LOGIN("geoai_uos_only_support_account_or_fixed_login"),
    /**
     * 消息内容不能为空，清检查！
     */
    GEOAI_UOS_PUSH_MESSAGE_CONTENT_CANNOT_BE_EMPTY("geoai_uos_push_message_content_cannot_be_empty"),

    /**
     * 消息标题不能为空，清检查！
     */
    GEOAI_UOS_MESSAGE_TITLE_CANNOT_BE_EMPTY("geoai_uos_message_title_cannot_be_empty"),
    /**
     * 消息状态不能为空，清检查！
     */
    GEOAI_UOS_MESSAGE_STATUS_CANNOT_BE_EMPTY("geoai_uos_message_status_cannot_be_empty"),
    /**
     * 消息类型不能为空，清检查！
     */
    GEOAI_UOS_MESSAGE_TYPE_CANNOT_BE_EMPTY("geoai_uos_message_type_cannot_be_empty"),
    /**
     * 推送的单位不能为空，清检查！
     */
    GEOAI_UOS_PUSH_UNIT_CANNOT_BE_EMPTY("geoai_uos_push_unit_cannot_be_empty"),
    /**
     * 终端名称最大长度255
     */
    GEOAI_UOS_MAXIMUM_LENGTH_TERMINAL_NAME_255("geoai_uos_maximum_length_terminal_name_255"),
    /**
     * 终端名称最大长度128
     */
    GEOAI_UOS_MAXIMUM_LENGTH_TERMINAL_NAME_128("geoai_uos_maximum_length_terminal_name_128"),
    /**
     * 推流地址最大长度255
     */
    GEOAI_UOS_MAXIMUM_LENGTH_PUSH_STREAM_ADDRESS_255("geoai_uos_maximum_length_push_stream_address_255"),
    /**
     * 拉流地址最大长度255
     */
    GEOAI_UOS_PULL_STREAM_ADDRESS_MAXIMUM_LENGTH_255("geoai_uos_pull_stream_address_maximum_length_255"),
    /**
     * 用户Id不能为空
     */
    GEOAI_UOS_USER_ID_CANNOT_BE_EMPTY("geoai_uos_user_id_cannot_be_empty"),
    /**
     * 全部已读标识不能为空
     */
    GEOAI_UOS_ALL_READ_MARKS_CANNOT_BE_EMPTY("geoai_uos_all_read_marks_cannot_be_empty"),
    /**
     * 查询成功
     */
    GEOAI_UOS_SEARCH_SUCCESS("geoai_uos_search_success"),
    /**
     * 基站海拔不能为空
     */
    GEOAI_UOS_ALTITUDE_OF_THE_BASE_STATION_CANNOT_BE_EMPTY("geoai_uos_altitude_of_the_base_station_cannot_be_empty"),
    /**
     * 基站经度不能为空
     */
    GEOAI_UOS_LONGITUDE_OF_BASE_STATION_CANNOT_BE_EMPTY("geoai_uos_longitude_of_base_station_cannot_be_empty"),
    /**
     * 基站纬度不能为空
     */
    GEOAI_UOS_BASE_STATION_LATITUDE_CANNOT_BE_EMPTY("geoai_uos_base_station_latitude_cannot_be_empty"),
    /**
     * 部署时间不能为空
     */
    GEOAI_UOS_DEPLOYMENT_TIME_CANNOT_BE_EMPTY("geoai_uos_deployment_time_cannot_be_empty"),
    /**
     * 详细地址限制200字
     */
    GEOAI_UOS_ADDRESS_DETAILS_ARE_LIMITED_TO_200_CHARACTERS("geoai_uos_address_details_are_limited_to_200_characters"),
    /**
     * 基站业务ID不能为空
     */
    GEOAI_UOS_BASE_STATION_SERVICE_ID_CANNOT_BE_EMPTY("geoai_uos_base_station_service_ID_cannot_be_empty"),
    /**
     * 基站ID不能为空
     */
    GEOAI_UOS_BASE_STATION_ID_CANNOT_BE_EMPTY("geoai_uos_base_station_ID_cannot_be_empty"),
    /**
     * 基站型号不能为空
     */
    GEOAI_UOS_BASE_STATION_MODEL_CANNOT_BE_EMPTY("geoai_uos_base_station_model_cannot_be_empty"),
    /**
     * 基站编号不能为空
     */
    GEOAI_UOS_BASE_STATION_NUMBER_CANNOT_BE_EMPTY("geoai_uos_base_station_number_cannot_be_empty"),
    /**
     * 连接地址不能为空
     */
    GEOAI_UOS_CONNECTION_ADDRESS_CANNOT_BE_EMPTY("geoai_uos_connection_address_cannot_be_empty"),
    /**
     * 无人机型号不能为空
     */
    GEOAI_UOS_DRONE_MODEL_CANNOT_BE_EMPTY("geoai_uos_drone_model_cannot_be_empty"),
    /**
     * 相机型号不能为空
     */
    GEOAI_UOS_CAMERA_MODEL_CANNOT_BE_EMPTY("geoai_uos_camera_model_cannot_be_empty"),
    /**
     * RTK过期时间的状态不能为空
     */
    GEOAI_UOS_RTK_EXPIRATION_TIME_STATUS_CAN_NOT_BE_EMPTY("geoai_uos_rtk_expiration_time_status_can_not_be_empty"),
    /**
     * 图传播放地址不能为空
     */
    GEOAI_UOS_MAP_DISSEMINATION_ADDRESS_CANNOT_BE_EMPTY("geoai_uos_map_dissemination_address_cannot_be_empty"),
    /**
     * 推流地址不能为空
     */
    GEOAI_UOS_PUSH_STREAM_ADDRESS_CANNOT_BE_EMPTY("geoai_uos_push_stream_address_cannot_be_empty"),
    /**
     * 登录用户名不能为空
     */
    GEOAI_UOS_LOGIN_USER_NAME_CANNOT_BE_EMPTY("geoai_uos_login_user_name_cannot_be_empty"),
    /**
     * 登录密码不能为空
     */
    GEOAI_UOS_LOGIN_PASSWORD_CANNOT_BE_EMPTY("geoai_uos_login_password_cannot_be_empty"),
    /**
     * 摄像头IP地址不能为空
     */
    GEOAI_UOS_CAMERA_IP_ADDRESS_CANNOT_BE_EMPTY("geoai_uos_camera_IP_address_cannot_be_empty"),
    /**
     * 推流功能开关不能为空
     */
    GEOAI_UOS_PUSH_FUNCTION_SWITCH_CANNOT_BE_EMPTY("geoai_uos_push_function_switch_cannot_be_empty"),
    /**
     * 巢内巢外类型不能为空
     */
    GEOAI_UOS_TYPE_OF_NEST_INSIDE_AND_OUTSIDE_CANNOT_BE_EMPTY("geoai_uos_type_of_nest_inside_and_outside_cannot_be_empty"),
    /**
     * 手机号码不能为空
     */
    GEOAI_UOS_CELL_PHONE_NUMBER_CANNOT_BE_EMPTY("geoai_uos_cell_phone_number_cannot_be_empty"),
    /**
     * 用户姓名不能为空
     */
    GEOAI_UOS_USER_NAME_CANNOT_BE_EMPTY("geoai_uos_user_name_cannot_be_empty"),
    /**
     * 机巢名称最大长度500
     */
    GEOAI_UOS_MAXIMUM_LENGTH_OF_NEST_NAME_IS_500("geoai_uos_maximum_length_of_nest_name_is_500"),
    /**
     * 管理账号最大长度50
     */
    GEOAI_UOS_MAXIMUM_LENGTH_OF_MANAGEMENT_ACCOUNT_50("geoai_uos_maximum_length_of_management_account_50"),
    /**
     * 管理密码最大长度100
     */
    GEOAI_UOS_THE_MAXIMUM_LENGTH_OF_MANAGEMENT_PASSWORD_IS_100("geoai_uos_the_maximum_length_of_management_password_is_100"),
    /**
     * 连接地址最大长度255
     */
    GEOAI_UOS_MAXIMUM_LENGTH_OF_CONNECTION_ADDRESS_255("geoai_uos_maximum_length_of_connection_address_255"),
    /**
     * 图传地址最大长度255
     */
    GEOAI_UOS_MAX_LENGTH_OF_MAP_ADDRESS_255("geoai_uos_max_length_of_map_address_255"),
    /**
     * 推流地址最大长度500
     */
    GEOAI_UOS_MAXIMUM_LENGTH_OF_PUSH_STREAM_ADDRESS_500("geoai_uos_maximum_length_of_push_stream_address_500"),
    /**
     * 巢外监控最大长度500
     */
    GEOAI_UOS_MAXIMUM_LENGTH_OF_MONITORING_OUTSIDE_THE_NEST_500("geoai_uos_maximum_length_of_monitoring_outside_the_nest_500"),
    /**
     * 巢内监控最大长度500
     */
    GEOAI_UOS_MAXIMUM_LENGTH_OF_INNEST_MONITORING_500("geoai_uos_maximum_length_of_innest_monitoring_500"),
    /**
     * 描述信息最大长度500
     */
    GEOAI_UOS_MAXIMUM_LENGTH_OF_DESCRIPTION_INFORMATION_500("geoai_uos_maximum_length_of_description_information_500"),
    /**
     * 详细地址最大长度255
     */
    GEOAI_UOS_MAXIMUM_LENGTH_OF_DETAILED_ADDRESS_255("geoai_uos_maximum_length_of_detailed_address_255"),
    /**
     * 无人机序列号最大长度80
     */
    GEOAI_UOS_DRONE_SERIAL_NUMBER_MAX_LENGTH_80("geoai_uos_drone_serial_number_max_length_80"),
    /**
     * 相机类型最大长度64
     */
    GEOAI_UOS_MAXIMUM_LENGTH_OF_CAMERA_TYPE_64("geoai_uos_maximum_length_of_camera_type_64"),
    /**
     * 遥控器序列号最大长度80
     */
    GEOAI_UOS_REMOTE_CONTROL_SERIAL_NUMBER_MAX_LENGTH_80("geoai_uos_remote_control_serial_number_max_length_80"),
    /**
     * 基站编号最大长度255
     */
    GEOAI_UOS_BASE_STATION_NUMBER_MAX_LENGTH_255("geoai_uos_base_station_number_max_length_255"),
    /**
     * 巢外摄像头MAC地址最大长度255
     */
    GEOAI_UOS_OUTOFNEST_CAMERA_MAC_ADDRESS_MAX_LENGTH_255("geoai_uos_outofnest_camera_mac_address_max_length_255"),
    /**
     * 巢外摄像头IP地址最大长度255
     */
    GEOAI_UOS_OUTOFNEST_CAMERA_IP_ADDRESS_MAX_LENGTH_255("geoai_uos_outofnest_camera_ip_address_max_length_255"),
    /**
     * 巢外摄像头推流地址最大长度255
     */
    GEOAI_UOS_OUTOFNEST_CAMERA_PUSH_STREAM_ADDRESS_MAX_LENGTH_255("geoai_uos_outofnest_camera_push_stream_address_max_length_255"),
    /**
     * 巢外摄像头登录账号最大长度50
     */
    GEOAI_UOS_OUTOFNEST_CAMERA_LOGIN_ACCOUNT_MAX_LENGTH_50("geoai_uos_outofnest_camera_login_account_max_length_50"),
    /**
     * 巢外摄像头登录密码最大长度50
     */
    GEOAI_UOS_OUTOFNEST_CAMERA_LOGIN_PASSWORD_MAX_LENGTH_50("geoai_uos_outofnest_camera_login_password_max_length_50"),
    /**
     * 巢内摄像头MAC地址最大长度255
     */
    GEOAI_UOS_MAX_LENGTH_OF_NESTING_CAMERA_MAC_ADDRESS_255("geoai_uos_max_length_of_nesting_camera_mac_address_255"),
    /**
     * 巢内摄像头IP地址最大长度255
     */
    GEOAI_UOS_MAXIMUM_LENGTH_OF_IP_ADDRESS_OF_INNEST_CAMERA_255("geoai_uos_maximum_length_of_ip_address_of_innest_camera_255"),
    /**
     * 巢内摄像头推流地址最大长度255
     */
    GEOAI_UOS_MAXIMUM_LENGTH_OF_INNEST_CAMERA_PUSH_ADDRESS_255("geoai_uos_maximum_length_of_innest_camera_push_address_255"),
    /**
     * 巢内摄像头登录账号最大长度50
     */
    GEOAI_UOS_MAXIMUM_LENGTH_OF_INNEST_CAMERA_LOGIN_ACCOUNT_50("geoai_uos_maximum_length_of_innest_camera_login_account_50"),
    /**
     * 巢内摄像头登录密码最大长度50
     */
    GEOAI_UOS_MAXIMUM_LENGTH_OF_NEST_CAMERA_LOGIN_PASSWORD_50("geoai_uos_maximum_length_of_nest_camera_login_password_50"),
    /**
     * 机巢uuid最大长度为80
     */
    GEOAI_UOS_MAXIMUM_LENGTH_OF_MACHINE_NEST_UUID_IS_80("geoai_uos_maximum_length_of_machine_nest_uuid_is_80"),
    /**
     * 任务名称不能为空
     */
    GEOAI_UOS_TASK_NAME_CANNOT_BE_EMPTY("geoai_uos_task_name_cannot_be_empty"),
    /**
     * 任务类型不能为空
     */
    GEOAI_UOS_MISSION_TYPE_CANNOT_BE_EMPTY("geoai_uos_mission_type_cannot_be_empty"),
    /**
     * 机巢Id不能为空
     */
    GEOAI_UOS_NEST_ID_CANNOT_BE_EMPTY("geoai_uos_nest_id_cannot_be_empty"),
    /**
     * 航线Id不能为空
     */
    GEOAI_UOS_ROUTE_ID_CANNOT_BE_EMPTY("geoai_uos_route_id_cannot_be_empty"),
    /**
     * 架次的名次不能为空
     */
    GEOAI_UOS_THE_NAME_OF_THE_SORTIE_CANNOT_BE_EMPTY("geoai_uos_the_name_of_the_sortie_cannot_be_empty"),
    /**
     * 航线信息不能为空
     */
    GEOAI_UOS_ROUTE_INFORMATION_CANNOT_BE_EMPTY("geoai_uos_route_information_cannot_be_empty"),
    /**
     * 航线类型不能为空
     */
    GEOAI_UOS_ROUTE_TYPE_CANNOT_BE_EMPTY("geoai_uos_route_type_cannot_be_empty"),
    /**
     * 航线名称不能为空
     */
    GEOAI_UOS_ROUTE_NAME_CANNOT_BE_EMPTY("geoai_uos_route_name_cannot_be_empty"),
    /**
     * 架次ID不能为空
     */
    GEOAI_UOS_SORTIE_ID_CANNOT_EMPTY("geoai_uos_sortie_id_cannot_empty"),
    /**
     * 任务名称长度为1~64字符
     */
    GEOAI_UOS_NAME_LENGTH_1_64_CHARACTERS("geoai_uos_name_length_1_64_characters"),
    /**
     * 偏航角模式不能为空
     */
    GEOAI_UOS_YAW_ANGLE_MODE_CANNOT_EMPTY("geoai_uos_Yaw_angle_mode_cannot_empty"),
    /**
     * 参数错误，请检查参数
     */
    GEOAI_UOS_PLEASE_CHECK_THE_PARAMETERS("geoai_uos_please_check_the_parameters"),
    /**
     * 参数校验不通过
     */
    GEOAI_UOS_PARAMETER_VERIFICATION_NOT_PASS("geoai_uos_parameter_verification_not_pass"),
    /**
     * 没有对应的数据
     */
    GEOAI_UOS_NO_CORRESPONDING_DATA("geoai_uos_no_corresponding_data"),
    /**
     * 查询不到记录
     */
    GEOAI_UOS_NO_RECORD_SEARCH("geoai_uos_no_record_search"),
    /**
     * 参数不能为空
     */
    GEOAI_UOS_PARAMETERS_CANNOT_EMPTY("geoai_uos_parameters_cannot_empty"),
    /**
     * 参数不正确
     */
    GEOAI_UOS_INCORRECT_PARAMETERS("geoai_uos_Incorrect_parameters"),
    /**
     * 批量删除失败
     */
    GEOAI_UOS_BATCH_DELETE_FAILED("geoai_uos_batch_delete_failed"),
    /**
     * 选择单位的个数最多10个
     */
    GEOAI_UOS_NUMBER_OF_UNITS_UP_TO_10("geoai_uos_number_of_units_up_to_10"),
    /**
     * 请选择单位
     */
    GEOAI_UOS_PLEASE_SELECT_UNITS("geoai_uos_please_select_units"),
    /**
     * 巡检计划名称长度必须在1~50个字符之间
     */
    GEOAI_UOS_INSPECTION_PLAN_NAME_BETWEEN_1_AND_50_CHARACTERS("geoai_uos_inspection_plan_name_between_1_and_50_characters"),
    /**
     * 数据传输方式限定可选值在0~2之间(包含)
     */
    GEOAI_UOS_DATA_TRANSMISSION_METHOD_BETWEEN_0_AND_2("geoai_uos_data_transmission_method_between_0_and_2"),
    /**
     * 必须指定数据传输方式
     */
    GEOAI_UOS_MUST_SPECIFY_THE_DATA_TRANSFER_METHOD("geoai_uos_must_specify_the_data_transfer_method"),
    /**
     * 必须指定是否自动任务
     */
    GEOAI_UOS_MUST_SPECIFY_WHETHER_THE_TASK_IS_AUTOMATIC("geoai_uos_must_specify_whether_the_task_is_automatic"),
    /**
     * 是否自动任务限定可选值在0~1之间(包含)
     */
    GEOAI_UOS_AUTOMATIC_TASK_IS_LIMITED_TO_OPTIONAL_VALUES_BETWEEN_0_AND_1("geoai_uos_automatic_task_is_limited_to_optional_values_between_0_and_1"),
    /**
     * 必须指定是否录屏
     */
    GEOAI_UOS_MUST_SPECIFY_WHETHER_TO_RECORD_THE_SCREEN("geoai_uos_must_specify_whether_to_record_the_screen"),
    /**
     * 是否录频限定可选值在0~1之间(包含)
     */
    GEOAI_UOS_RECORD_THE_FREQUENCY_OPTIONAL_VALUES_BETWEEN_0_AND_1("geoai_uos_record_the_frequency_optional_values_between_0_and_1"),
    /**
     * 必须指定飞行架次
     */
    GEOAI_UOS_MUST_SPECIFY_THE_NUMBER_OF_FLIGHTS("geoai_uos_must_specify_the_number_of_flights"),
    /**
     * 巡检计划仅允许关联1~8个飞行架次任务
     */
    GEOAI_UOS_INSPECTION_PLAN_ONLY_1_8_SORTIES_ARE_ALLOWED("geoai_uos_inspection_plan_Only_1_8_sorties_are_allowed"),
    /**
     * 必须指定巡检计划规则
     */
    GEOAI_UOS_MUST_SPECIFY_THE_INSPECTION_PLAN_RULES("geoai_uos_must_specify_the_inspection_plan_rules"),
    /**
     * 巡检计划名称不能为空
     */
    GEOAI_UOS_NAME_OF_THE_INSPECTION_PLAN_CANNOT_BE_EMPTY("geoai_uos_name_of_the_inspection_plan_cannot_be_empty"),
    /**
     * name参数不能为空
     */
    GEOAI_UOS_NAME_PARAMETER_CANNOT_BE_EMPTY("geoai_uos_name_parameter_cannot_be_empty"),
    /**
     * type参数不能为空
     */
    GEOAI_UOS_TYPE_PARAMETER_CANNOT_BE_EMPTY("geoai_uos_type_parameter_cannot_be_empty"),
    /**
     * pointList参数不能为空
     */
    GEOAI_UOS_POINTLIST_PARAMETER_CANNOT_BE_EMPTY("geoai_uos_pointList_parameter_cannot_be_empty"),
    /**
     * 单次查询数量不能超过100
     */
    GEOAI_UOS__NUMBER_OF_SINGLE_QUERY_CANNOT_EXCEED_100("geoai_uos__number_of_single_query_cannot_exceed_100"),
    /**
     * 基站Id不能为空，清检查！
     */
    GEOAI_UOS_BASE_STATION_ID_CANNOT_BE_EMPTY_CLEAR_THE_CHECK("geoai_uos_base_station_Id_cannot_be_empty_clear_the_check"),
    /**
     * 参数错误,nestIdList不能为空
     */
    GEOAI_UOS_PARAMETER_ERROR_NESTIDLIST_CAN_NOT_BE_EMPTY("geoai_uos_parameter_error_nestIdlist_can_not_be_empty"),
    /**
     * uuid不能为null
     */
    GEOAI_UOS_UUID_CANT_BE_NULL("geoai_uos_uuid_cant_be_null"),
    /**
     * 指令下发成功
     */
    GEOAI_UOS_COMMAND_ISSUED_SUCCESSFULLY("geoai_uos_command_issued_successfully"),
    /**
     * 获取无人机失控行为失败
     */
    GEOAI_UOS_GET_DRONE_OUT_OF_CONTROL_BEHAVIOR_FAILED("geoai_uos_get_drone_out_of_control_behavior_failed"),
    /**
     * 设置无人机失控行为成功
     */
    GEOAI_UOS_SET_DRONE_OUT_OF_CONTROL_BEHAVIOR_SUCCESS("geoai_uos_set_drone_out_of_control_behavior_success"),
    /**
     * 设置无人机失控行为请求发送失败
     */
    GEOAI_UOS_FAILED_TO_SEND_REQUEST_TO_SET_DRONE_OUT_OF_CONTROL_BEHAVIOR("geoai_uos_failed_to_send_request_to_set_drone_out_of_control_behavior"),
    /**
     * 单位不能为空
     */
    GEOAI_UOS_UNIT_CANNOT_BE_NULL("geoai_uos_unit_cannot_be_null"),
    /**
     * 删除失败
     */
    GEOAI_UOS_DELETE_FAILED("geoai_uos_delete_failed"),
    /**
     * 分页不能为空，清检查！
     */
    GEOAI_UOS_PAGE_CANNOT_BE_EMPTY_CLEAR_CHECK("geoai_uos_page_cannot_be_empty_clear_check"),
    /**
     * 页数不能为空，清检查！
     */
    GEOAI_UOS_PAGE_NUMBER_CANNOT_BE_EMPTY_CLEAR_CHECK("geoai_uos_page_number_cannot_be_empty_clear_check"),
    /**
     * 删除数据维保记录成功
     */
    GEOAI_UOS_DELETE_DATA_MAINTENANCE_RECORD_SUCCESSFULLY("geoai_uos_delete_data_maintenance_record_successfully"),
    /**
     * 设置基站为
     */
    GEOAI_UOS_SET_THE_BASE_STATION_TO("geoai_uos_set_the_base_station_to"),
    /**
     * 维保状态
     */
    GEOAI_UOS_MAINTENANCE_STATUS("geoai_uos_maintenance_status"),
    /**
     * 正常状态
     */
    GEOAI_UOS_NORMAL_STATUS("geoai_uos_normal_status"),
    /**
     * 获取超时
     */
    GEOAI_UOS_GET_TIMEOUT("geoai_uos_get_timeout"),
    /**
     * 参数请求错误
     */
    GEOAI_UOS_PARAMETER_REQUEST_ERROR("geoai_uos_parameter_request_error"),
    /**
     * 航线数据包不能为空
     */
    GEOAI_UOS_ROUTE_PACKET_CANT_BE_NULL("geoai_uos_route_packet_cant_be_null"),
    /**
     * 任务类型不能为null
     */
    GEOAI_UOS_TASK_TYPE_CANNOT_BE_NULL("geoai_uos_task_type_cannot_be_null"),
    /**
     * 任务名称长度为1~64字符
     */
    GEOAI_UOS_MISSION_NAME_LENGTH_IS_1_64_CHARACTERS("geoai_uos_mission_name_length_is_1_64_characters"),
    /**
     * 航点数不能为null
     */
    GEOAI_UOS_NUMBER_OF_ROUTE_POINTS_CANNOT_BE_NULL("geoai_uos_number_of_route_points_cannot_be_null"),
    /**
     * 拍照动作数不能为null
     */
    GEOAI_UOS_NUMBER_OF_PHOTO_ACTIONS_CANNOT_BE_NULL("geoai_uos_number_of_photo_actions_cannot_be_null"),
    /**
     * 预计飞行距离不能为null
     */
    GEOAI_UOS_ESTIMATED_FLIGHT_DISTANCE_CANNOT_BE_NULL("geoai_uos_estimated_flight_distance_cannot_be_null"),
    /**
     * 预计飞行时间不能为null
     */
    GEOAI_UOS_ESTIMATED_FLIGHT_TIME_CANNOT_BE_NULL("geoai_uos_estimated_flight_time_cannot_be_null"),
    /**
     * 起降航高不能为null
     */
    GEOAI_UOS_TAKEOFF_AND_LANDING_ALTITUDE_CANNOT_BE_NULL("geoai_uos_takeoff_and_landing_altitude_cannot_be_null"),
    /**
     * 自动飞行最小速度为1m/s
     */
    GEOAI_UOS_MINIMUM_SPEED_OF_AUTOMATIC_FLIGHT_IS_1M("geoai_uos_minimum_speed_of_automatic_flight_is_1m"),
    /**
     * 自动飞行最大速度为15m/s
     */
    GEOAI_UOS_MAXIMUM_SPEED_OF_AUTOMATIC_FLIGHT_IS_15M("geoai_uos_maximum_speed_of_automatic_flight_is_15m"),
    /**
     * 航线数据不能为空
     */
    GEOAI_UOS_ROUTE_DATA_CANT_BE_NULL("geoai_uos_Route_data_cant_be_null"),
    /**
     * 杆塔id不能为空
     */
    GEOAI_UOS_TOWER_ID_CANT_BE_NULL("geoai_uos_Tower_id_cant_be_null"),
    /**
     * 即将推送云冠告警信息
     */
    GEOAI_UOS_ABOUT_TO_PUSH_THE_CLOUD_CROWN_ALARM_INFORMATION("geoai_uos_about_to_push_the_cloud_crown_alarm_information"),
    /**
     * 任务编号不能为空
     */
    GEOAI_UOS_TASK_NUMBER_CANNOT_BE_EMPTY("geoai_uos_task_number_cannot_be_empty"),
    /**
     * 该视频没有轨迹文件
     */
    GEOAI_UOS_VIDEO_DOES_NOT_HAVE_A_TRACK_FILE("geoai_uos_video_does_not_have_a_track_file"),
    /**
     * 连接断开
     */
    GEOAI_UOS_CONNECTION_DISCONNECTED("geoai_uos_connection_disconnected"),
    /**
     * 连接失败
     */
    GEOAI_UOS_CONNECTION_FAILED("geoai_uos_connection_failed"),
    /**
     * 连接成功
     */
    GEOAI_UOS_CONNECTION_SUCCESSFUL("geoai_uos_connection_successful"),
    /**
     * 激活静默模式
     */
    GEOAI_UOS_SILENT_MODE_ACTIVATED("geoai_uos_silent_mode_activated"),
    /**
     * 当前找不到相关机巢内容
     */
    GEOAI_UOS_RELEVANT_NEST_CONTENT_IS_NOT_CURRENTLY_FOUND("geoai_uos_relevant_nest_content_is_not_currently_found"),
    /**
     * 记录报警处理成功
     */
    GEOAI_UOS_RECORDED_ALARM_PROCESSING_SUCCESS("geoai_uos_recorded_alarm_processing_success"),
    /**
     * 查询不到对应的机巢
     */
    GEOAI_UOS_CORRESPONDING_NEST_CANNOT_BE_QUERIED("geoai_uos_corresponding_nest_cannot_be_queried"),
    /**
     * 更新基站编号成功
     */
    GEOAI_UOS_UPDATE_THE_BASE_STATION_NUMBER_SUCCESSFULLY("geoai_uos_update_the_base_station_number_successfully"),
    /**
     * 更新基站编号失败
     */
    GEOAI_UOS_FAILED_TO_UPDATE_THE_BASE_STATION_NUMBER("geoai_uos_failed_to_update_the_base_station_number"),
    /**
     * 获取热红外颜色列表出错
     */
    GEOAI_UOS_ERROR_IN_GETTING_THERMAL_INFRARED_COLOR_LIST("geoai_uos_error_in_getting_thermal_infrared_color_list"),
    /**
     * 获取相机参数有误
     */
    GEOAI_UOS_ERROR_IN_GETTING_CAMERA_PARAMETERS("geoai_uos_error_in_getting_camera_parameters"),
    /**
     * 设置的颜色不存在
     */
    GEOAI_UOS_SET_COLOR_DOES_NOT_EXIST("geoai_uos_set_color_does_not_exist"),
    /**
     * 增加计划失败
     */
    GEOAI_UOS_ADDING_PLAN_FAILED("geoai_uos_adding_plan_failed"),
    /**
     * 修改计划失败
     */
    GEOAI_UOS_MODIFY_PLAN_FAILED("geoai_uos_modify_plan_failed"),
    /**
     * 设备变焦失败
     */
    GEOAI_UOS_DEVICE_ZOOM_FAILED("geoai_uos_device_zoom_failed"),
    /**
     * 设备变焦请求失败
     */
    GEOAI_UOS_DEVICE_ZOOM_REQUEST_FAILED("geoai_uos_device_zoom_request_failed"),
    /**
     * 获取相机变焦失败
     */
    GEOAI_UOS_FAILED_TO_GET_CAMERA_ZOOM("geoai_uos_failed_to_get_camera_zoom"),
    /**
     * 重置变焦失败
     */
    GEOAI_UOS_RESET_ZOOM_FAILED("geoai_uos_reset_zoom_failed"),
    /**
     * 修改云台角度成功
     */
    GEOAI_UOS_MODIFY_HEAD_ANGLE_SUCCESS("geoai_uos_modify_head_angle_success"),
    /**
     * 修改云台角度失败
     */
    GEOAI_UOS_MODIFY_GIMBAL_ANGLE_FAILED("geoai_uos_modify_gimbal_angle_failed"),
    /**
     * 参数不到对应的机巢
     */
    GEOAI_UOS_PARAMETER_DOES_NOT_CORRESPOND_TO_THE_NEST("geoai_uos_parameter_does_not_correspond_to_the_nest"),
    /**
     * 开启自动备降功能成功
     */
    GEOAI_UOS_TURN_ON_THE_AUTO_STANDBY_FUNCTION_SUCCESSFULLY("geoai_uos_turn_on_the_auto_standby_function_successfully"),
    /**
     * 关闭自动备降功能成功
     */
    GEOAI_UOS_TURN_OFF_THE_AUTO_STANDBY_FUNCTION_SUCCESSFULLY("geoai_uos_turn_off_the_auto_standby_function_successfully"),
    /**
     * 设置自动备降功能失败
     */
    GEOAI_UOS_SET_AUTO_STANDBY_FUNCTION_FAILED("geoai_uos_set_auto_standby_function_failed"),
    /**
     * 设置自动备降功能失败，机巢离线
     */
    GEOAI_UOS_SET_AUTO_STANDBY_FUNCTION_FAILED_THE_NEST_IS_OFFLINE("geoai_uos_set_auto_standby_function_failed_the_nest_is_offline"),
    /**
     * 机巢离线，查询不到
     */
    GEOAI_UOS_AIRCRAFT_NEST_IS_OFFLINE_THE_QUERY_CANNOT_BE_FOUND("geoai_uos_aircraft_nest_is_offline_the_query_cannot_be_found"),
    /**
     * 立即前往备降点执行成功
     */
    GEOAI_UOS_IMMEDIATELY_RESERVE_POINT_AND_EXECUTE_SUCCESSFULLY("geoai_uos_immediately_reserve_point_and_execute_successfully"),
    /**
     * 立即前往备降点执行失败
     */
    GEOAI_UOS_FAILED_TO_GO_TO_THE_LANDING_SITE_IMMEDIATELY("geoai_uos_failed_to_go_to_the_landing_site_immediately"),
    /**
     * 立即前往指定备降点执行成功
     */
    GEOAI_UOS_IMMEDIATELY_DESIGNATED_LANDING_POINT_AND_EXECUTE_SUCCESSFULLY("geoai_uos_immediately_designated_landing_point_and_execute_successfully"),
    /**
     * 立即前往指定备降点执行失败
     */
    GEOAI_UOS_FAILED_TO_EXECUTE_TO_THE_DESIGNATED_LANDING_POINT_IMMEDIATELY("geoai_uos_failed_to_execute_to_the_designated_landing_point_immediately"),
    /**
     * MQTT服务地址不正确，请检查MQTT服务地址
     */
    GEOAI_UOS_MQTT_SERVICE_ADDRESS_IS_INCORRECT_PLEASE_CHECK_THE_MQTT_SERVICE_ADDRESS("geoai_uos_mqtt_service_address_is_incorrect_please_check_the_mqtt_service_address"),
    /**
     * 查询不到对应机巢
     */
    GEOAI_UOS_CORRESPONDING_MACHINE_NEST_IS_NOT_AVAILABLE("geoai_uos_corresponding_machine_nest_is_not_available"),
    /**
     * 默认备降点坐标、前往备降点高度设置失败
     */
    GEOAI_UOS_DEFAULT_LANDING_POINT_COORDINATES_AND_ALTITUDE_TO_LANDING_POINT_FAILED_TO_BE_SET("geoai_uos_default_landing_point_coordinates_and_altitude_to_landing_point_failed_to_be_set"),
    /**
     * 飞机还在旋转桨叶，不能使用【一键回巢】功能
     */
    GEOAI_UOS_AIRCRAFT_IS_STILL_ROTATING_THE_PROPELLER_CANT_USE_ONE_KEY_RETURN_FUNCTION("geoai_uos_aircraft_is_still_rotating_the_propeller_cant_use_one_key_return_function"),
    /**
     * 【一键回巢】启动成功，无人机将飞往
     */
    GEOAI_UOS_ONE_KEY_TO_RETURN_TO_THE_NEST_IS_STARTED_SUCCESSFULLY_THE_DRONE_WILL_FLY_TO("geoai_uos_one_key_to_return_to_the_nest]_is_started_successfully_the_drone_will_fly_to"),
    /**
     * 一键开启失败
     */
    GEOAI_UOS_ONE_KEY_START_FAILED("geoai_uos_one_key_start_failed"),
    /**
     * 一键开启失败,机巢离线
     */
    GEOAI_UOS_ONE_KEY_OPEN_FAILED_NEST_IS_OFFLINE("geoai_uos_one_key_open_failed_nest_is_offline"),
    /**
     * 一键重置失败,机巢离线
     */
    GEOAI_UOS_ONE_KEY_RESET_FAILED_NEST_IS_OFFLINE("geoai_uos_one_key_reset_failed_nest_is_offline"),
    /**
     * 电池装载失败
     */
    GEOAI_UOS_BATTERY_LOADING_FAILED("geoai_uos_battery_loading_failed"),
    /**
     * 电池卸载成功
     */
    GEOAI_UOS_BATTERY_UNLOADING_SUCCESS("geoai_uos_battery_unloading_success"),
    /**
     * 电池卸载失败,机巢离线
     */
    GEOAI_UOS_BATTERY_UNLOAD_FAILED_NEST_OFFLINE("geoai_uos_battery_unload_failed_nest_offline"),
    /**
     * 舱门打开失败,机巢离线
     */
    GEOAI_UOS_HATCH_OPEN_FAILED_NEST_OFFLINE("geoai_uos_hatch_open_failed_nest_offline"),
    /**
     * 舱门关闭成功
     */
    GEOAI_UOS_HATCH_CLOSING_SUCCESS("geoai_uos_hatch_closing_success"),
    /**
     * 舱门关闭失败
     */
    GEOAI_UOS_HATCH_CLOSING_FAILED("geoai_uos_hatch_closing_failed"),
    /**
     * 舱门关闭失败,机巢离线
     */
    GEOAI_UOS_HATCH_CLOSING_FAILED_NEST_OFFLINE("geoai_uos_hatch_closing_failed_nest_offline"),
    /**
     * 升起平台成功
     */
    GEOAI_UOS_RAISE_PLATFORM_SUCCESS("geoai_uos_raise_platform_success"),
    /**
     * 升起平台失败
     */
    GEOAI_UOS_RAISING_PLATFORM_FAILED("geoai_uos_raising_platform_failed"),
    /**
     * 降落平台成功
     */
    GEOAI_UOS_LANDING_PLATFORM_SUCCESS("geoai_uos_landing_platform_success"),
    /**
     * 降落平台失败
     */
    GEOAI_UOS_LANDING_PLATFORM_FAILED("geoai_uos_landing_platform_failed"),
    /**
     * 夹紧归中成功
     */
    GEOAI_UOS_CLAMPING_CENTERING_SUCCESS("geoai_uos_clamping_centering_success"),
    /**
     * 夹紧归中失败
     */
    GEOAI_UOS_CLAMPING_CENTERING_FAILED("geoai_uos_clamping_centering_failed"),
    /**
     * 松开归中成功
     */
    GEOAI_UOS_LOOSE_CENTERING_SUCCESS("geoai_uos_loose_centering_success"),
    /**
     * 松开归中失败
     */
    GEOAI_UOS_UNCLAMPING_FAILED("geoai_uos_unclamping_failed"),
    /**
     * rtk重连成功
     */
    GEOAI_UOS_RTK_RECONNECT_SUCCESSFUL("geoai_uos_rtk_reconnect_successful"),
    /**
     * rtk重连失败
     */
    GEOAI_UOS_RTK_RECONNECT_FAILED("geoai_uos_rtk_reconnect_failed"),
    /**
     * rtk重连失败,机巢离线
     */
    GEOAI_UOS_RTK_RECONNECTION_FAILED_THE_NEST_IS_OFFLINE("geoai_uos_rtk_reconnection_failed_the_nest_is_offline"),
    /**
     * 遥控器切挡成功
     */
    GEOAI_UOS_REMOTE_CONTROL_CUT_BLOCK_SUCCESSFULLY("geoai_uos_remote_control_cut_block_successfully"),
    /**
     * 遥控器切挡失败
     */
    GEOAI_UOS_REMOTE_CONTROL_CUT_BLOCK_FAILED("geoai_uos_remote_control_cut_block_failed"),
    /**
     * 开关遥控器成功
     */
    GEOAI_UOS_SWITCHING_REMOTE_CONTROL_SUCCEEDED("geoai_uos_switching_remote_control_succeeded"),
    /**
     * 开关遥控器失败
     */
    GEOAI_UOS_SWITCHING_REMOTE_CONTROL_FAILED("geoai_uos_switching_remote_control_failed"),

    /**
     * 开关遥控器失败,机巢离线
     */
    GEOAI_UOS_SWITCHING_REMOTE_CONTROL_FAILED_THE_MACHINE_NEST_OFFLINE("geoai_uos_switching_remote_control_failed_the_machine_nest_offline"),
    /**
     * 总电源重启成功
     */
    GEOAI_UOS_TOTAL_POWER_REBOOT_SUCCESS("geoai_uos_total_power_reboot_success"),
    /**
     * 总电源重启失败,
     */
    GEOAI_UOS_TOTAL_POWER_RESTART_FAILED("geoai_uos_total_power_restart_failed"),
    /**
     * 总电源重启失败,机巢离线
     */
    GEOAI_UOS_TOTAL_POWER_RESTART_FAILEDNEST_OFFLINE("geoai_uos_total_power_restart_failednest_offline"),
    /**
     * 启动返航成功
     */
    GEOAI_UOS_START_RETURN_SUCCESSFUL("geoai_uos_start_return_successful"),
    /**
     * 启动返航失败,
     */
    GEOAI_UOS_FAILED_TO_RETURN_TO_FLIGHT("geoai_uos_failed_to_return_to_flight"),
    /**
     * 启动返航失败,机巢离线
     */
    GEOAI_UOS_START_RETURN_FAILED_NEST_OFFLINE("geoai_uos_start_return_failed_nest_offline"),
    /**
     * 重新推流成功
     */
    GEOAI_UOS_RE_STREAMING_SUCCESS("geoai_uos_re_streaming_success"),
    /**
     * 重新推流失败,
     */
    GEOAI_UOS_RE_PUSH_FAILED("geoai_uos_re_push_failed"),
    /**
     * 重新推流失败,机巢离线
     */
    GEOAI_UOS_RE_PUSH_FAILEDNEST_OFFLINE("geoai_uos_re_push_failednest_offline"),
    /**
     * 获取推流地址失败,
     */
    GEOAI_UOS_FAILED_TO_GET_THE_PUSH_ADDRESS("geoai_uos_failed_to_get_the_push_address"),
    /**
     * 机巢连接异常
     */
    GEOAI_UOS_NEST_CONNECTION_ABNORMAL("geoai_uos_nest_connection_abnormal"),
    /**
     * 设置云台角度成功
     */
    GEOAI_UOS_SET_GIMBAL_ANGLE_SUCCESS("geoai_uos_set_gimbal_angle_success"),
    /**
     * 设置云台角度失败,
     */
    GEOAI_UOS_FAILED_TO_SET_GIMBAL_ANGLE("geoai_uos_failed_to_set_gimbal_angle"),
    /**
     * 飞机充电电成功
     */
    GEOAI_UOS_AIRCRAFT_CHARGING_SUCCESS("geoai_uos_aircraft_charging_success"),
    /**
     * 飞机充电失败,
     */
    GEOAI_UOS_AIRCRAFT_CHARGING_FAILED("geoai_uos_aircraft_charging_failed"),
    /**
     * 飞机充电失败,机巢离线
     */
    GEOAI_UOS_THE_AIRCRAFT_CHARGING_FAILED_THE_NEST_IS_OFFLINE("geoai_uos_the_aircraft_charging_failed_the_nest_is_offline"),
    /**
     * 飞机断电成功
     */
    GEOAI_UOS_AIRCRAFT_POWER_OFF_SUCCESS("geoai_uos_aircraft_power_off_success"),
    /**
     * 飞机断电失败,
     */
    GEOAI_UOS_AIRCRAFT_POWER_OFF_FAILED("geoai_uos_aircraft_power_off_failed"),
    /**
     * 飞机断电失败,机巢离线
     */
    GEOAI_UOS_AIRCRAFT_POWER_FAILURENEST_OFFLINE("geoai_uos_aircraft_power_failurenest_offline"),
    /**
     * 切换红外光模式成功
     */
    GEOAI_UOS_SWITCHING_TO_INFRARED_LIGHT_MODE_SUCCEEDED("geoai_uos_switching_to_infrared_light_mode_succeeded"),
    /**
     * 切换红外光模式失败,
     */
    GEOAI_UOS_FAILED_TO_SWITCH_TO_IR_MODE("geoai_uos_failed_to_switch_to_ir_mode"),
    /**
     * 切换红外光模式失败,机巢离线
     */
    GEOAI_UOS_SWITCHING_INFRARED_LIGHT_MODE_FAILED_THE_NEST_IS_OFFLINE("geoai_uos_switching_infrared_light_mode_failed_the_nest_is_offline"),
    /**
     * 切换可见光模式成功
     */
    GEOAI_UOS_SWITCHING_VISIBLE_LIGHT_MODE_SUCCESSFULLY("geoai_uos_switching_visible_light_mode_successfully"),
    /**
     * 切换可见光模式失败,
     */
    GEOAI_UOS_FAILED_TO_SWITCH_TO_VISIBLE_MODE("geoai_uos_failed_to_switch_to_visible_mode"),
    /**
     * 切换可见光模式失败,机巢离线
     */
    GEOAI_UOS_SWITCHING_VISIBLE_MODE_FAILEDNEST_OFFLINE("geoai_uos_switching_visible_mode_failednest_offline"),
    /**
     * 切换双光模式成功
     */
    GEOAI_UOS_SWITCHING_DUAL_LIGHT_MODE_SUCCESSFULLY("geoai_uos_switching_dual_light_mode_successfully"),
    /**
     * 切换双光模式失败,
     */
    GEOAI_UOS_FAILED_TO_SWITCH_TO_DUAL_LIGHT_MODE("geoai_uos_failed_to_switch_to_dual_light_mode"),
    /**
     * 切换双光模式失败,机巢离线
     */
    GEOAI_UOS_FAILED_TO_SWITCH_TO_DUAL_LIGHT_MODE_THE_NEST_IS_OFFLINE("geoai_uos_failed_to_switch_to_dual_light_mode_the_nest_is_offline"),
    /**
     * 空调打开成功
     */
    GEOAI_UOS_TURN_ON_AIR_CONDITIONER_SUCCESSFULLY("geoai_uos_turn_on_air_conditioner_successfully"),
    /**
     * 空调打开失败,
     */
    GEOAI_UOS_AIR_CONDITIONER_FAILED_TO_TURN_ON("geoai_uos_air_conditioner_failed_to_turn_on"),
    /**
     * 空调打开失败,机巢离线
     */
    GEOAI_UOS_THE_AIR_CONDITIONER_FAILED_TO_TURN_ONTHE_NEST_IS_OFFLINE("geoai_uos_the_air_conditioner_failed_to_turn_onthe_nest_is_offline"),
    /**
     * 空调关闭成功
     */
    GEOAI_UOS_AIR_CONDITIONING_OFF_SUCCESSFULLY("geoai_uos_air_conditioning_off_successfully"),
    /**
     * 空调关闭失败,
     */
    GEOAI_UOS_AIR_CONDITIONER_SHUTDOWN_FAILED("geoai_uos_air_conditioner_shutdown_failed"),
    /**
     * 空调关闭失败,机巢离线
     */
    GEOAI_UOS_AIR_CONDITIONER_SHUTDOWN_FAILEDNEST_OFFLINE("geoai_uos_air_conditioner_shutdown_failednest_offline"),
    /**
     * 关闭驱动器电源成功
     */
    GEOAI_UOS_POWER_OFF_THE_DRIVE_SUCCESSFULLY("geoai_uos_power_off_the_drive_successfully"),
    /**
     * 关闭驱动器电源失败,
     */
    GEOAI_UOS_POWER_OFF_THE_DRIVE_FAILED("geoai_uos_power_off_the_drive_failed"),
    /**
     * 打开驱动器电源成功
     */
    GEOAI_UOS_TURN_ON_DRIVE_POWER_SUCCESSFULLY("geoai_uos_turn_on_drive_power_successfully"),
    /**
     * 打开驱动器电源失败,
     */
    GEOAI_UOS_POWER_ON_DRIVE_FAILED("geoai_uos_power_on_drive_failed"),
    /**
     * MPS复位指令下发成功
     */
    GEOAI_UOS_MPS_RESET_COMMAND_ISSUED_SUCCESSFULLY("geoai_uos_mps_reset_command_issued_successfully"),
    /**
     * CPS复位成功
     */
    GEOAI_UOS_CPS_RESET_SUCCESSFUL("geoai_uos_cps_reset_successful"),
    /**
     * CPS复位失败
     */
    GEOAI_UOS_CPS_RESET_FAILED("geoai_uos_cps_reset_failed"),
    /**
     * 中控系统重启成功
     */
    GEOAI_UOS_CPS_ZK_RESET_SUCCESSFUL("geoai_uos_cps_zk_reset_successful"),
    /**
     * 中控系统重启失败
     */
    GEOAI_UOS_CPS_REBOOT_FAILED("geoai_uos_cps_reboot_failed"),
    /**
     * 格式化无人机SD卡指令下发
     */
    GEOAI_UOS_FORMAT_UAV_SD_CARD_COMMAND_ISSUED("geoai_uos_format_uav_sd_card_command_issued"),
    /**
     * 终止任务启动进程成功
     */
    GEOAI_UOS_TERMINATE_TASK_START_PROCESS_SUCCESS("geoai_uos_terminate_task_start_process_success"),
    /**
     * 终止任务启动进程失败,
     */
    GEOAI_UOS_TERMINATE_MISSION_START_PROCESS_FAILED("geoai_uos_terminate_mission_start_process_failed"),
    /**
     * 终止任务结束进程成功
     */
    GEOAI_UOS_TERMINATE_MISSION_END_PROCESS_SUCCESSFULLY("geoai_uos_terminate_mission_end_process_successfully"),
    /**
     * 终止任务结束进程失败,
     */
    GEOAI_UOS_TERMINATE_MISSION_END_PROCESS_FAILED("geoai_uos_terminate_mission_end_process_failed"),
    /**
     * 终止任务所有进程成功
     */
    GEOAI_UOS_TERMINATE_TASK_ALL_PROCESSES_SUCCESSFULLY("geoai_uos_terminate_task_all_processes_successfully"),
    /**
     * 终止任务所有进程失败,
     */
    GEOAI_UOS_TERMINATE_ALL_PROCESSES_OF_THE_TASK_FAILED("geoai_uos_terminate_all_processes_of_the_task_failed"),
    /**
     * 打开无人机成功
     */
    GEOAI_UOS_TURN_ON_DRONE_SUCCESSFULLY("geoai_uos_turn_on_drone_successfully"),
    /**
     * 打开无人机失败,
     */
    GEOAI_UOS_FAILED_TO_OPEN_THE_DRONE("geoai_uos_failed_to_open_the_drone"),
    /**
     * 关闭无人机成功
     */
    GEOAI_UOS_SHUTTING_DOWN_THE_DRONE_SUCCEEDED("geoai_uos_shutting_down_the_drone_succeeded"),
    /**
     * 关闭无人机失败,
     */
    GEOAI_UOS_SHUTTING_DOWN_THE_DRONE_FAILED("geoai_uos_shutting_down_the_drone_failed"),
    /**
     * 更换电池成功
     */
    GEOAI_UOS_CHANGING_THE_BATTERY_SUCCEEDED("geoai_uos_changing_the_battery_succeeded"),
    /**
     * 更换电池失败,
     */
    GEOAI_UOS_BATTERY_REPLACEMENT_FAILED("geoai_uos_battery_replacement_failed"),
    /**
     * 重置平台成功
     */
    GEOAI_UOS_RESET_PLATFORM_SUCCESS("geoai_uos_reset_platform_success"),
    /**
     * 重置平台失败,
     */
    GEOAI_UOS_RESET_PLATFORM_FAILED("geoai_uos_reset_platform_failed"),
    /**
     * 切换镜头成功
     */
    GEOAI_UOS_SWITCHING_LENSES_SUCCESSFUL("geoai_uos_switching_lenses_successful"),
    /**
     * 切换镜头失败,
     */
    GEOAI_UOS_SWITCHING_LENS_FAILED("geoai_uos_switching_lens_failed"),
    /**
     * 拍照成功
     */
    GEOAI_UOS_TAKE_A_PICTURE_SUCCESSFULLY("geoai_uos_take_a_picture_successfully"),
    /**
     * 拍照失败,
     */
    GEOAI_UOS_FAILED_TO_TAKE_A_PICTURE("geoai_uos_failed_to_take_a_picture"),
    /**
     * cps版本查询失败
     */
    GEOAI_UOS_CPS_VERSION_QUERY_FAILED("geoai_uos_cps_version_query_failed"),
    /**
     * 开启成功
     */
    GEOAI_UOS_TURN_ON_SUCCESS("geoai_uos_turn_on_success"),
    /**
     * 关闭成功
     */
    GEOAI_UOS_TURN_OFF_SUCCESS("geoai_uos_turn_off_success"),
    /**
     * 开启失败
     */
    GEOAI_UOS_FAILED_TO_OPEN("geoai_uos_failed_to_open"),
    /**
     * 关闭失败
     */
    GEOAI_UOS_FAILED_TO_TURN_OFF("geoai_uos_failed_to_turn_off"),
    /**
     * 遥控器对频成功
     */
    GEOAI_UOS_REMOTE_CONTROL_FREQUENCY_PAIRING_SUCCESS("geoai_uos_remote_control_frequency_pairing_success"),
    /**
     * 遥控器对频失败
     */
    GEOAI_UOS_REMOTE_CONTROL_FREQUENCY_PAIRING_FAILED("geoai_uos_remote_control_frequency_pairing_failed"),
    /**
     * 停止
     */
    GEOAI_UOS_STOP("geoai_uos_stop"),
    /**
     * 开始
     */
    GEOAI_UOS_START("geoai_uos_start"),
    /**
     * 开始磁罗盘校准成功
     */
    GEOAI_UOS_START_MAGNETIC_COMPASS_CALIBRATION_SUCCESSFUL("geoai_uos_start_magnetic_compass_calibration_successful"),
    /**
     * 大疆账号登录成功
     */
    GEOAI_UOS_DJI_ACCOUNT_LOGIN_SUCCESS("geoai_uos_dji_account_login_success"),
    /**
     * 大疆账号登陆失败
     */
    GEOAI_UOS_DJI_ACCOUNT_LOGIN_FAILED("geoai_uos_dji_account_login_failed"),
    /**
     * 获取机巢网络状态失败
     */
    GEOAI_UOS_FAILED_TO_GET_THE_NETWORK_STATUS_OF_THE_NEST("geoai_uos_failed_to_get_the_network_status_of_the_nest"),
    /**
     * 清理成功
     */
    GEOAI_UOS_CLEANUP_SUCCESSFUL("geoai_uos_cleanup_successful"),
    /**
     * 清理失败
     */
    GEOAI_UOS_CLEANUP_FAILED("geoai_uos_cleanup_failed"),
    /**
     * 系统自检成功
     */
    GEOAI_UOS_SYSTEM_SELF_TEST_SUCCESS("geoai_uos_system_self_test_success"),
    /**
     * 系统自检失败
     */
    GEOAI_UOS_SYSTEM_SELF_TEST_FAILED("geoai_uos_system_self_test_failed"),
    /**
     * 查询不到对应机巢
     */
    GEOAI_UOS_THE_CORRESPONDING_NEST_CANNOT_BE_QUERIED("geoai_uos_the_corresponding_nest_cannot_be_queried"),
    /**
     * 原路返航触发成功
     */
    GEOAI_UOS_ORIGINAL_RETURN_TRIGGER_SUCCESSFUL("geoai_uos_original_return_trigger_successful"),
    /**
     * 原路返航触发失败
     */
    GEOAI_UOS_FAILURE_OF_RETURN_TRIGGER("geoai_uos_failure_of_return_trigger"),
    /**
     * 强制降落触发成功
     */
    GEOAI_UOS_FORCED_LANDING_TRIGGER_SUCCESSFUL("geoai_uos_forced_landing_trigger_successful"),
    /**
     * 强制降落触发失败
     */
    GEOAI_UOS_FORCED_LANDING_TRIGGER_FAILED("geoai_uos_forced_landing_trigger_failed"),
    /**
     * 自动降落触发成功
     */
    GEOAI_UOS_AUTOMATIC_LANDING_TRIGGER_SUCCESSFUL("geoai_uos_automatic_landing_trigger_successful"),
    /**
     * 自动降落触发失败
     */
    GEOAI_UOS_AUTO_LANDING_TRIGGER_FAILED("geoai_uos_auto_landing_trigger_failed"),
    /**
     * 热红外模式颜色设置成功
     */
    GEOAI_UOS_THERMAL_INFRARED_MODE_COLOR_SETTING_SUCCESSFUL("geoai_uos_thermal_infrared_mode_color_setting_successful"),
    /**
     * 热红外模式颜色设置出错
     */
    GEOAI_UOS_THERMAL_INFRARED_MODE_COLOR_SETTING_ERROR("geoai_uos_thermal_infrared_mode_color_setting_error"),
    /**
     * 重新降落触发成功
     */
    GEOAI_UOS_RE_LANDING_TRIGGER_SUCCESS("geoai_uos_re_landing_trigger_success"),
    /**
     * 重新降落触发失败
     */
    GEOAI_UOS_RE_LANDING_TRIGGER_FAILED("geoai_uos_re_landing_trigger_failed"),
    /**
     * 更新成功
     */
    GEOAI_UOS_UPDATE_SUCCESSFUL("geoai_uos_update_successful"),
    /**
     * 更新失败
     */
    GEOAI_UOS_UPDATE_FAILED("geoai_uos_update_failed"),
    /**
     * 重置软件推流成功
     */
    GEOAI_UOS_RESET_SOFTWARE_PUSH_SUCCESSFUL("geoai_uos_reset_software_push_successful"),
    /**
     * 重置软件推流失败,
     */
    GEOAI_UOS_RESET_SOFTWARE_PUSH_FAILED("geoai_uos_reset_software_push_failed"),
    /**
     * 重启硬件推流成功
     */
    GEOAI_UOS_RESTART_HARDWARE_PUSH_SUCCESS("geoai_uos_restart_hardware_push_success"),
    /**
     * 重启硬件推流失败
     */
    GEOAI_UOS_RESTART_HARDWARE_PUSH_FAILED("geoai_uos_restart_hardware_push_failed"),
    /**
     * 云冠暂不支持重置推流
     */
    GEOAI_UOS_CLOUDCREST_DOES_NOT_SUPPORT_RESETTING_PUSH_STREAMS_AT_THIS_TIME("geoai_uos_cloudcrest_does_not_support_resetting_push_streams_at_this_time"),
    /**
     * 获取推流方式失败
     */
    GEOAI_UOS_FAILED_TO_GET_THE_PUSH_METHOD("geoai_uos_failed_to_get_the_push_method"),
    /**
     * USB重连成功
     */
    GEOAI_UOS_USB_RECONNECT_SUCCESS("geoai_uos_usb_reconnect_success"),
    /**
     * USB重连超时
     */
    GEOAI_UOS_USB_RECONNECT_TIMEOUT("geoai_uos_usb_reconnect_timeout"),
    /**
     * USB重连失败
     */
    GEOAI_UOS_USB_RECONNECT_FAILED("geoai_uos_usb_reconnect_failed"),
    /**
     * 推流信息设置成功
     */
    GEOAI_UOS_PUSH_STREAM_INFORMATION_SETTING_SUCCESS("geoai_uos_push_stream_information_setting_success"),
    /**
     * 推流信息设置失败
     */
    GEOAI_UOS_PUSH_STREAM_INFORMATION_SETTING_FAILED("geoai_uos_push_stream_information_setting_failed"),
    /**
     * 设置推流地址成功
     */
    GEOAI_UOS_SET_PUSH_ADDRESS_SUCCESS("geoai_uos_set_push_address_success"),

    /**
     * 充电装置夹紧成功
     */
    GEOAI_UOS_CHARGE_DEVICE_TIGHT_SUCCESS("geoai_uos_charge_device_tight_success"),

    /**
     * 充电装置夹紧失败
     */
    GEOAI_UOS_CHARGE_DEVICE_TIGHT_FAILURE("geoai_uos_charge_device_tight_failure"),

    /**
     * 充电装置释放成功
     */
    GEOAI_UOS_CHARGE_DEVICE_LOOSE_SUCCESS("geoai_uos_charge_device_loose_success"),

    /**
     * 充电装置释放失败
     */
    GEOAI_UOS_CHARGE_DEVICE_LOOSE_FAILURE("geoai_uos_charge_device_loose_failure"),
    /**
     * 设置推流地址失败,
     */
    GEOAI_UOS_FAILED_TO_SET_PUSH_ADDRESS("geoai_uos_failed_to_set_push_address"),
    /**
     * 基站UUID:
     */
    GEOAI_UOS_BASE_STATION_UUID("geoai_uos_base_station_uuid:"),
    /**
     * ，已经存在系统中，名称为：
     */
    GEOAI_UOS__ALREADY_EXISTS_IN_THE_SYSTEM_THE_NAME_IS("geoai_uos__already_exists_in_the_system_the_name_is"),
    /**
     * ,不能重复添加
     */
    GEOAI_UOS__CAN_NOT_BE_REPEATEDLY_ADDED("geoai_uos__can_not_be_repeatedly_added"),
    /**
     * 编号：
     */
    GEOAI_UOS_NO("geoai_uos_no"),
    /**
     * ,不能取重复的编号
     */
    GEOAI_UOS_CAN_NOT_TAKE_DUPLICATE_NUMBER("geoai_uos_can_not_take_duplicate_number"),
    /**
     * 无人机信息保存失败
     */
    GEOAI_UOS_DRONE_INFORMATION_SAVING_FAILURE("geoai_uos_drone_information_saving_failure"),
    /**
     * 参数保存失败
     */
    GEOAI_UOS_PARAMETER_SAVING_FAILURE("geoai_uos_parameter_saving_failure"),
    /**
     * 电池卸载失败
     */
    GEOAI_UOS_BATTERY_UNLOADING_FAILURE("geoai_uos_battery_unloading_failure"),
    /**
     * 格式化cps指令下发
     */
    GEOAI_UOS_FORMATTING_CPS_COMMANDS_TO_ISSUE("geoai_uos_formatting_cps_commands_to_issue"),
    /**
     * 删除计划失败
     */
    GEOAI_UOS_DELETE_PLAN_FAILED("geoai_uos_delete_plan_failed"),
    /**
     * 任务暂停失败
     */
    GEOAI_UOS_TASK_PAUSE_FAILURE("geoai_uos_task_pause_failure"),
    /**
     * 告警定时任务恢复失败
     */
    GEOAI_UOS_ALARM_TIMING_TASK_RESUME_FAILED("geoai_uos_alarm_timing_task_resume_failed"),
    /**
     * 立即执行失败
     */
    GEOAI_UOS_IMMEDIATE_EXECUTION_FAILURE("geoai_uos_immediate_execution_failure"),
    /**
     * 获取多媒体列表失败
     */
    GEOAI_UOS_FAILED_TO_GET_MULTIMEDIA_LIST("geoai_uos_failed_to_get_multimedia_list"),
    /**
     * 上传指令发送成功，准备开始上传全部数据到服务器
     */
    GEOAI_UOS_UPLOAD_COMMAND_SENT_SUCCESSFULLY_READY_TO_START_UPLOADING_ALL_DATA_TO_THE_SERVER("geoai_uos_upload_command_sent_successfully_ready_to_start_uploading_all_data_to_the_server"),
    /**
     * 同步失败，机巢未连接
     */
    GEOAI_UOS_SYNCHRONIZATION_FAILURE_THE_NEST_IS_NOT_CONNECTED("geoai_uos_synchronization_failure_the_nest_is_not_connected"),
    /**
     * 当前基站正在同步数据，状态为
     */
    GEOAI_UOS_THE_CURRENT_BASE_STATION_IS_SYNCHRONIZING_DATA_THE_STATUS_IS("geoai_uos_the_current_base_station_is_synchronizing_data_the_status_is"),
    /**
     * 同步源数据开始失败
     */
    GEOAI_UOS_FAILED_TO_START_SYNCHRONIZING_SOURCE_DATA("geoai_uos_failed_to_start_synchronizing_source_data"),
    /**
     * 取消批量传输数据失败
     */
    GEOAI_UOS_FAILED_TO_CANCEL_BATCH_DATA_TRANSFER("geoai_uos_failed_to_cancel_batch_data_transfer"),
    /**
     * 查询不到架次
     */
    GEOAI_UOS_NO_SORTIE_QUERY("geoai_uos_no_sortie_query"),
    /**
     * 查询不到该机巢
     */
    GEOAI_UOS_THE_NEST_CANNOT_BE_QUERIED("geoai_uos_the_nest_cannot_be_queried"),
    /**
     * 检测到基站不是空闲状态，请检查基站，再进行同步数据操作
     */
    GEOAI_UOS_THE_BASE_STATION_IS_NOT_IDLE_PLEASE_CHECK_THE_BASE_STATION_AND_THEN_SYNCHRONIZE_THE_DATA_OPERATION("geoai_uos_the_base_station_is_not_idle_please_check_the_base_station_and_then_synchronize_the_data_operation"),
    /**
     * 检测到无人机未连接，若确认同步，基站将会执行开机动作（大约90秒）。
     */
    GEOAI_UOS_DETECTED_THAT_THE_UAV_IS_NOT_CONNECTED("geoai_uos_detected_that_the_uav_is_not_connected"),
    /**
     * 基站具备同步数据源条件，请确认同步
     */
    GEOAI_UOS_THE_BASE_STATION_HAS_THE_CONDITION_OF_SYNCHRONIZING_DATA_SOURCE_PLEASE_CONFIRM_THE_SYNCHRONIZATION("geoai_uos_the_base_station_has_the_condition_of_synchronizing_data_source_please_confirm_the_synchronization"),
    /**
     * 找不到该设备，请输入正确的终端设备id
     */
    GEOAI_UOS_CANT_FIND_THE_DEVICE_PLEASE_INPUT_THE_CORRECT_TERMINAL_DEVICE_ID("geoai_uos_cant_find_the_device_please_input_the_correct_terminal_device_id"),
    /**
     * 请选择照片
     */
    GEOAI_UOS_PLEASE_SELECT_PHOTO("geoai_uos_please_select_photo"),
    /**
     * 下载失败，请正确选择图片
     */
    GEOAI_UOS_DOWNLOAD_FAILED_PLEASE_SELECT_THE_CORRECT_PICTURE("geoai_uos_download_failed_please_select_the_correct_picture"),
    /**
     * 下载失败，该执行架次没有图片
     */
    GEOAI_UOS_DOWNLOAD_FAILED_THERE_IS_NO_PICTURE_IN_THE_EXECUTION_SORTIE("geoai_uos_download_failed_there_is_no_picture_in_the_execution_sortie"),
    /**
     * 下载失败，未找到对应图片
     */
    GEOAI_UOS_DOWNLOAD_FAILED_THE_CORRESPONDING_PICTURE_IS_NOT_FOUND("geoai_uos_download_failed_the_corresponding_picture_is_not_found"),
    /**
     * 停止同步数据指令发送成功
     */
    GEOAI_UOS_STOP_SYNCHRONIZING_DATA_COMMAND_WAS_SENT_SUCCESSFULLY("geoai_uos_stop_synchronizing_data_command_was_sent_successfully"),
    /**
     * 机巢离线，停止同步
     */
    GEOAI_UOS_THE_NEST_IS_OFFLINE_STOP_SYNCHRONIZATION("geoai_uos_the_nest_is_offline_stop_synchronization"),
    /**
     * 指令发送成功，开始上传数据到机巢
     */
    GEOAI_UOS_COMMAND_SENT_SUCCESSFULLY_START_UPLOADING_DATA_TO_THE_NEST("geoai_uos_command_sent_successfully_start_uploading_data_to_the_nest"),
    /**
     * 同步失败，机巢离线
     */
    GEOAI_UOS_SYNCHRONIZATION_FAILED_THE_NEST_IS_OFFLINE("geoai_uos_synchronization_failed_the_nest_is_offline"),
    /**
     * 同步失败，机巢未连接
     */
    GEOAI_UOS_SYNCHRONIZATION_FAILED_THE_NEST_IS_NOT_CONNECTED("geoai_uos_synchronization_failed_the_nest_is_not_connected"),
    /**
     * 查询不到该成果
     */
    GEOAI_UOS_THE_RESULT_CANNOT_BE_QUERIED("geoai_uos_the_result_cannot_be_queried"),
    /**
     * 查询不到该成果的缺陷
     */
    GEOAI_UOS_THE_DEFECT_OF_THE_RESULT_CANNOT_BE_QUERIED("geoai_uos_the_defect_of_the_result_cannot_be_queried"),
    /**
     * 没有获取到需要识别的图片
     */
    GEOAI_UOS_THE_IMAGE_THAT_NEEDS_TO_BE_RECOGNIZED_IS_NOT_OBTAINED("geoai_uos_the_image_that_needs_to_be_recognized_is_not_obtained"),
    /**
     * 未选中任何数据
     */
    GEOAI_UOS_NO_DATA_SELECTED("geoai_uos_no_data_selected"),
    /**
     * 图片识别失败
     */
    GEOAI_UOS_IMAGE_RECOGNITION_FAILED("geoai_uos_image_recognition_failed"),
    /**
     * 请求参数错误
     */
    GEOAI_UOS_REQUEST_PARAMETER_ERROR("geoai_uos_request_parameter_error"),
    /**
     * 下载失败,文件压缩出错
     */
    GEOAI_UOS_DOWNLOAD_FAILED_FILE_COMPRESSION_ERROR("geoai_uos_download_failed_file_compression_error"),
    /**
     * 同步数据源失败，请联系管理员
     */
    GEOAI_UOS_FAILED_TO_SYNCHRONIZE_DATA_SOURCE_PLEASE_CONTACT_ADMINISTRATOR("geoai_uos_failed_to_synchronize_data_source_please_contact_administrator"),
    /**
     * 同步视频数据失败
     */
    GEOAI_UOS_FAILED_TO_SYNCHRONIZE_VIDEO_DATA("geoai_uos_failed_to_synchronize_video_data"),
    /**
     * 数据已全部同步完毕，无需重复同步
     */
    GEOAI_UOS_ALL_DATA_HAS_BEEN_SYNCHRONIZED_NO_NEED_TO_REPEAT_SYNCHRONIZATION("geoai_uos_all_data_has_been_synchronized_no_need_to_repeat_synchronization"),
    /**
     * 发送上传指令，准备开始上传部分数据到服务器
     */
    GEOAI_UOS_SEND_UPLOAD_COMMAND_READY_TO_START_UPLOADING_SOME_DATA_TO_THE_SERVER("geoai_uos_send_upload_command_ready_to_start_uploading_some_data_to_the_server"),
    /**
     * 查询不到航线，推送分析失败
     */
    GEOAI_UOS_THE_ROUTE_CANNOT_BE_QUERIED_AND_THE_PUSH_ANALYSIS_FAILED("geoai_uos_the_route_cannot_be_queried_and_the_push_analysis_failed"),
    /**
     * 航线分不清是机巢的还是移动终端
     */
    GEOAI_UOS_I_CANT_TELL_WHETHER_THE_ROUTE_IS_FROM_THE_NEST_OR_THE_MOBILE_TERMINAL("geoai_uos_i_cant_tell_whether_the_route_is_from_the_nest_or_the_mobile_terminal"),
    /**
     * 查询不到航线对应的机巢，推送分析失败
     */
    GEOAI_UOS_CANT_FIND_THE_CORRESPONDING_NEST_OF_THE_ROUTE_PUSH_ANALYSIS_FAILED("geoai_uos_cant_find_the_corresponding_nest_of_the_route_push_analysis_failed"),
    /**
     * 查询不到航线对应照片，推送分析失败
     */
    GEOAI_UOS_FAILURE_TO_QUERY_THE_PHOTO_OF_THE_ROUTE_PUSH_ANALYSIS_FAILURE("geoai_uos_failure_to_query_the_photo_of_the_route_push_analysis_failure"),
    /**
     * 生成xml失败
     */
    GEOAI_UOS_FAILURE_TO_GENERATE_XML("geoai_uos_failure_to_generate_xml"),
    /**
     * 同步照片数据失败,没有对应的文件路径"
     */
    GEOAI_UOS_FAILED_TO_SYNCHRONIZE_PHOTO_DATA_NO_CORRESPONDING_FILE_PATH("geoai_uos_failed_to_synchronize_photo_data_no_corresponding_file_path"),
    /**
     * 归中打开成功
     */
    GEOAI_UOS_SUCCESSFUL_OPENING_IN_HOMING("geoai_uos_successful_opening_in_homing"),
    /**
     * 归中打开失败
     */
    GEOAI_UOS_FAILED_TO_OPEN_IN_HOMING("geoai_uos_failed_to_open_in_homing"),
    /**
     * 归中关闭成功
     */
    GEOAI_UOS_CLOSING_SUCCESSFUL("geoai_uos_closing_successful"),
    /**
     * 归中关闭失败
     */
    GEOAI_UOS_FAILED_TO_CLOSE_IN_HOMING("geoai_uos_failed_to_close_in_homing"),
    /**
     * 一键关闭成功
     */
    GEOAI_UOS_ONE_CLICK_CLOSE_SUCCESS("geoai_uos_one_click_close_success"),
    /**
     * 一键关闭失败
     */
    GEOAI_UOS_FAILED_TO_CLOSE_WITH_ONE_KEY("geoai_uos_failed_to_close_with_one_key"),
    /**
     * 机械爪收紧
     */
    GEOAI_UOS_MECHANICAL_JAW_TIGHTENING("geoai_uos_mechanical_jaw_tightening"),
    /**
     * 机械爪收紧失败
     */
    GEOAI_UOS_MECHANICAL_CLAW_TIGHTENING_FAILURE("geoai_uos_mechanical_claw_tightening_failure"),
    /**
     * 机械爪释放失败
     */
    GEOAI_UOS_MECHANICAL_JAW_RELEASE_FAILED("geoai_uos_mechanical_jaw_release_failed"),
    /**
     * 机械爪释放
     */
    GEOAI_UOS_JAW_RELEASE("geoai_uos_jaw_release"),
    /**
     * 机械爪重置失败
     */
    GEOAI_UOS_JAW_RESET_FAILED("geoai_uos_jaw_reset_failed"),
    /**
     * 机械爪重置
     */
    GEOAI_UOS_JAW_RESET("geoai_uos_jaw_reset"),
    /**
     * 设置天线角度
     */
    GEOAI_UOS_SET_ANTENNA_ANGLE("geoai_uos_set_antenna_angle"),
    /**
     * 设置天线角度失败
     */
    GEOAI_UOS_FAILED_TO_SET_ANTENNA_ANGLE("geoai_uos_failed_to_set_antenna_angle"),
    /**
     * 重置天线角度
     */
    GEOAI_UOS_RESET_ANTENNA_ANGLE("geoai_uos_reset_antenna_angle"),
    /**
     * 重置天线角度失败
     */
    GEOAI_UOS_FAILED_TO_RESET_ANTENNA_ANGLE("geoai_uos_failed_to_reset_antenna_angle"),
    /**
     * 天线重置
     */
    GEOAI_UOS_ANTENNA_RESET("geoai_uos_antenna_reset"),
    /**
     * 天线重置失败
     */
    GEOAI_UOS_ANTENNA_RESET_FAILED("geoai_uos_antenna_reset_failed"),
    /**
     * 天线关闭成功
     */
    GEOAI_UOS_ANTENNA_SHUTDOWN_SUCCESS("geoai_uos_antenna_shutdown_success"),
    /**
     * 天线关闭失败
     */
    GEOAI_UOS_ANTENNA_OFF_FAILED("geoai_uos_antenna_off_failed"),
    /**
     * 天线打开成功
     */
    GEOAI_UOS_ANTENNA_OPEN_SUCCESS("geoai_uos_antenna_open_success"),
    /**
     * 天线打开失败
     */
    GEOAI_UOS_ANTENNA_OPEN_FAILED("geoai_uos_antenna_open_failed"),
    /**
     * 机械臂Z重置
     */
    GEOAI_UOS_ROBOTIC_ARM_Z_RESET("geoai_uos_robotic_arm_z_reset"),
    /**
     * 机械臂Z重置失败
     */
    GEOAI_UOS_ARM_Z_RESET_FAILED("geoai_uos_arm_z_reset_failed"),
    /**
     * 机械臂Z终点
     */
    GEOAI_UOS_ROBOTIC_ARM_Z_END_POINT("geoai_uos_robotic_arm_z_end_point"),
    /**
     * 机械臂Z终点失败
     */
    GEOAI_UOS_ROBOTIC_ARM_Z_END_POINT_FAILED("geoai_uos_robotic_arm_z_end_point_failed"),
    /**
     * 机械臂Y重置
     */
    GEOAI_UOS_ROBOTIC_ARM_Y_RESET("geoai_uos_robotic_arm_y_reset"),
    /**
     * 机械臂重置失败
     */
    GEOAI_UOS_ROBOT_ARM_RESET_FAILED("geoai_uos_robot_arm_reset_failed"),
    /**
     * 机械臂Y终点
     */
    GEOAI_UOS_ROBOTIC_ARM_Y_END_POINT("geoai_uos_robotic_arm_y_end_point"),
    /**
     * 机械臂终点失败
     */
    GEOAI_UOS_ROBOTIC_ARM_END_POINT_FAILED("geoai_uos_robotic_arm_end_point_failed"),
    /**
     * 机械臂Y原点
     */
    GEOAI_UOS_ROBOTIC_ARM_Y_ORIGIN("geoai_uos_robotic_arm_y_origin"),
    /**
     * 机械臂X重置失败
     */
    GEOAI_UOS_ROBOT_ARM_X_RESET_FAILED("geoai_uos_robot_arm_x_reset_failed"),
    /**
     * 机械臂X终点
     */
    GEOAI_UOS_ROBOTIC_ARM_X_END_POINT("geoai_uos_robotic_arm_x_end_point"),
    /**
     * 机械臂X终点失败
     */
    GEOAI_UOS_ROBOTIC_ARM_X_END_POINT_FAILED("geoai_uos_robotic_arm_x_end_point_failed"),
    /**
     * 机械臂X中间点
     */
    GEOAI_UOS_ROBOTIC_ARM_X_MIDPOINT("geoai_uos_robotic_arm_x_midpoint"),
    /**
     * 机械臂X中间点失败
     */
    GEOAI_UOS_ROBOTIC_ARM_X_MIDPOINT_FAILED("geoai_uos_robotic_arm_x_midpoint_failed"),
    /**
     * 机械臂X原点
     */
    GEOAI_UOS_ROBOTIC_ARM_X_ORIGIN("geoai_uos_robotic_arm_x_origin"),
    /**
     * 机械臂X原点失败
     */
    GEOAI_UOS_ROBOTIC_ARM_X_ORIGIN_FAILED("geoai_uos_robotic_arm_x_origin_failed"),
    /**
     * 归中Y重置
     */
    GEOAI_UOS_RETURN_TO_CENTER_Y_RESET("geoai_uos_return_to_center_y_reset"),
    /**
     * 归中Y重置失败
     */
    GEOAI_UOS_RETURN_TO_CENTER_Y_RESET_FAILED("geoai_uos_return_to_center_y_reset_failed"),
    /**
     * 归中Y关闭
     */
    GEOAI_UOS_RETURN_TO_CENTER_Y_CLOSE("geoai_uos_return_to_center_y_close"),
    /**
     * 归中Y关闭失败
     */
    GEOAI_UOS_HOMING_Y_CLOSE_FAILED("geoai_uos_homing_y_close_failed"),
    /**
     * 归中Y打开
     */
    GEOAI_UOS_RETURN_TO_CENTER_Y_OPEN("geoai_uos_return_to_center_y_open"),
    /**
     * 归中Y打开失败
     */
    GEOAI_UOS_RETURN_TO_CENTER_Y_OPEN_FAILED("geoai_uos_return_to_center_y_open_failed"),
    /**
     * 归中X重置
     */
    GEOAI_UOS_RETURN_TO_CENTER_X_RESET("geoai_uos_return_to_center_x_reset"),
    /**
     * 归中X重置失败
     */
    GEOAI_UOS_RESET_FAILED("geoai_uos_reset_failed"),
    /**
     * 归中X关闭
     */
    GEOAI_UOS_RETURN_TO_CENTER_X_CLOSED("geoai_uos_return_to_center_x_closed"),
    /**
     * 归中X关闭失败
     */
    GEOAI_UOS_RETURN_TO_CENTER_X_CLOSE_FAILED("geoai_uos_return_to_center_x_close_failed"),
    /**
     * 归中X打开
     */
    GEOAI_UOS_RETURN_TO_CENTER_X_OPEN("geoai_uos_return_to_center_x_open"),
    /**
     * 平台重置
     */
    GEOAI_UOS_PLATFORM_RESET("geoai_uos_platform_reset"),
    /**
     * 平台重置失败
     */
    GEOAI_UOS_PLATFORM_RESET_FAILED("geoai_uos_platform_reset_failed"),
    /**
     * 平台下降
     */
    GEOAI_UOS_PLATFORM_DESCEND("geoai_uos_platform_descend"),
    /**
     * 平台下降失败
     */
    GEOAI_UOS_PLATFORM_DESCENT_FAILED("geoai_uos_platform_descent_failed"),
    /**
     * 舱门关闭
     */
    GEOAI_UOS_HATCH_CLOSED("geoai_uos_hatch_closed"),
    /**
     * 关闭引导成功
     */
    GEOAI_UOS_SHUTDOWN_BOOT_SUCCESS("geoai_uos_shutdown_boot_success"),
    /**
     * 关闭引导失败
     */
    GEOAI_UOS_SHUTDOWN_BOOT_FAILURE("geoai_uos_shutdown_boot_failure"),
    /**
     * 查询不到无人机型号
     */
    GEOAI_UOS_CANT_FIND_THE_DRONE_MODEL("geoai_uos_cant_find_the_drone_model"),
    /**
     * 获取不到相机参数
     */
    GEOAI_UOS_CANT_GET_THE_CAMERA_PARAMETERS("geoai_uos_cant_get_the_camera_parameters"),
    /**
     * 查询失败
     */
    GEOAI_UOS_QUERY_FAILED("geoai_uos_query_failed"),
    /**
     * 接口已过时，请参考接口文档并使用新接口
     */
    GEOAI_UOS_INTERFACE_IS_OUT_OF_DATE_PLEASE_REFER_TO_THE_INTERFACE_DOCUMENTATION_AND_USE_THE_NEW_INTERFACE("geoai_uos_interface_is_out_of_date_please_refer_to_the_interface_documentation_and_use_the_new_interface"),
    /**
     * 服务航线上传到云端航线失败
     */
    GEOAI_UOS_SERVICE_ROUTE_UPLOAD_TO_CLOUD_ROUTE_FAILED("geoai_uos_service_route_upload_to_cloud_route_failed"),
    /**
     * 基站航线删除成功
     */
    GEOAI_UOS_BASE_STATION_ROUTE_DELETION_SUCCESS("geoai_uos_base_station_route_deletion_success"),
    /**
     * 基站航线删除失败
     */
    GEOAI_UOS_BASE_STATION_ROUTE_DELETION_FAILED("geoai_uos_base_station_route_deletion_failed"),
    /**
     * 查找单位出错
     */
    GEOAI_UOS_ERROR_FINDING_UNIT("geoai_uos_error_finding_unit"),
    /**
     * 当前单位已添加同类型名称的图层
     */
    GEOAI_UOS_A_LAYER_WITH_THE_SAME_TYPE_OF_NAME_HAS_BEEN_ADDED_TO_THE_CURRENT_UNIT("geoai_uos_a_layer_with_the_same_type_of_name_has_been_added_to_the_current_unit"),
    /**
     * 发布地址径有误
     */
    GEOAI_UOS_WRONG_PUBLISH_ADDRESS_PATH("geoai_uos_wrong_publish_address_path"),
    /**
     * 添加图层信息成功
     */
    GEOAI_UOS_ADDING_LAYER_INFORMATION_SUCCEEDED("geoai_uos_adding_layer_information_succeeded"),
    /**
     * 添加图层信息失败
     */
    GEOAI_UOS_ADDING_LAYER_INFORMATION_FAILED("geoai_uos_adding_layer_information_failed"),
    /**
     * 修改图层信息成功
     */
    GEOAI_UOS_MODIFY_LAYER_INFORMATION_SUCCESS("geoai_uos_modify_layer_information_success"),
    /**
     * 修改图层信息失败
     */
    GEOAI_UOS_MODIFY_LAYER_INFORMATION_FAILED("geoai_uos_modify_layer_information_failed"),
    /**
     * 删除单位图层成功
     */
    GEOAI_UOS_DELETING_A_UNIT_LAYER_SUCCEEDED("geoai_uos_deleting_a_unit_layer_succeeded"),
    /**
     * 删除单位图层失败
     */
    GEOAI_UOS_DELETING_UNIT_LAYERS_FAILED("geoai_uos_deleting_unit_layers_failed"),
    /**
     * 当前路径文件为空或路径错误
     */
    GEOAI_UOS_THE_CURRENT_PATH_FILE_IS_EMPTY_OR_THE_PATH_IS_WRONG("geoai_uos_the_current_path_file_is_empty_or_the_path_is_wrong"),
    /**
     * 查询不到对应的任务
     */
    GEOAI_UOS_THE_CORRESPONDING_TASK_CANNOT_BE_QUERIED("geoai_uos_the_corresponding_task_cannot_be_queried"),
    /**
     * 批量任务最多选8个
     */
    GEOAI_UOS_THE_MAXIMUM_NUMBER_OF_BATCH_TASKS_IS_8("geoai_uos_the_maximum_number_of_batch_tasks_is_8"),
    /**
     * 参数不对
     */
    GEOAI_UOS_THE_PARAMETERS_ARE_NOT_CORRECT("geoai_uos_the_parameters_are_not_correct"),
    /**
     * 参数传输不对
     */
    GEOAI_UOS_PARAMETER_TRANSFER_IS_NOT_CORRECT("geoai_uos_parameter_transfer_is_not_correct"),
    /**
     * 立即执行任务动作成功
     */
    GEOAI_UOS_EXECUTE_THE_TASK_ACTION_SUCCESSFULLY_IMMEDIATELY("geoai_uos_execute_the_task_action_successfully_immediately"),
    /**
     * 查不到基站信息
     */
    GEOAI_UOS_CANT_FIND_THE_BASE_STATION_INFORMATION("geoai_uos_cant_find_the_base_station_information"),
    /**
     * 找不到相关的机巢信息
     */
    GEOAI_UOS_CANNOT_FIND_THE_RELATED_NEST_INFORMATION("geoai_uos_cannot_find_the_related_nest_information"),
    /**
     * 同步完成
     */
    GEOAI_UOS_SYNCHRONIZATION_COMPLETED("geoai_uos_synchronization_completed"),
    /**
     * 当前拍照点数
     */
    GEOAI_UOS_CURRENT_PHOTO_POINTS("geoai_uos_current_photo_points"),
    /**
     * 不存在该移动终端
     */
    GEOAI_UOS_THE_MOBILE_TERMINAL_DOES_NOT_EXIST("geoai_uos_the_mobile_terminal_does_not_exist"),
    /**
     * 当前找不到相关移动终端内容
     */
    GEOAI_UOS_THE_CONTENT_OF_THE_RELEVANT_MOBILE_TERMINAL_CANNOT_BE_FOUND_AT_PRESENT("geoai_uos_the_content_of_the_relevant_mobile_terminal_cannot_be_found_at_present"),
    /**
     * 终端删除失败
     */
    GEOAI_UOS_TERMINAL_DELETION_FAILED("geoai_uos_terminal_deletion_failed"),
    /**
     * 查询不到对应的设备
     */
    GEOAI_UOS_THE_CORRESPONDING_DEVICE_CANNOT_BE_FOUND("geoai_uos_the_corresponding_device_cannot_be_found"),
    /**
     * 请选择用户单位
     */
    GEOAI_UOS_PLEASE_SELECT_THE_USER_UNIT("geoai_uos_please_select_the_user_unit"),
    /**
     * 当前单位未创建标签
     */
    GEOAI_UOS_NO_TAG_HAS_BEEN_CREATED_FOR_THE_CURRENT_UNIT("geoai_uos_no_tag_has_been_created_for_the_current_unit"),
    /**
     * 添加用户成功
     */
    GEOAI_UOS_ADD_USER_SUCCESSFULLY("geoai_uos_add_user_successfully"),
    /**
     * 添加用户失败
     */
    GEOAI_UOS_ADDING_USER_FAILED("geoai_uos_adding_user_failed"),
    /**
     * 用户信息修改成功
     */
    GEOAI_UOS_USER_INFORMATION_MODIFICATION_SUCCESS("geoai_uos_user_information_modification_success"),
    /**
     * 用户信息修改失败
     */
    GEOAI_UOS_USER_INFORMATION_MODIFICATION_FAILED("geoai_uos_user_information_modification_failed"),
    /**
     * 用户删除失败
     */
    GEOAI_UOS_USER_DELETION_FAILED("geoai_uos_user_deletion_failed"),
    /**
     * 账号密码已重置
     */
    GEOAI_UOS_ACCOUNT_PASSWORD_HAS_BEEN_RESET("geoai_uos_account_password_has_been_reset"),
    /**
     * 账号密码重置失败, 请联系管理员！
     */
    GEOAI_UOS_PASSWORD_RESET_FAILED_PLEASE_CONTACT_THE_ADMINISTRATOR("geoai_uos_password_reset_failed_please_contact_the_administrator"),
    /**
     * 状态修改失败,请联系管理员！
     */
    GEOAI_UOS_STATUS_CHANGE_FAILED_PLEASE_CONTACT_ADMINISTRATOR("geoai_uos_status_change_failed_please_contact_administrator"),
    /**
     * 账号停用失败, 请联系管理员！
     */
    GEOAI_UOS_ACCOUNT_DEACTIVATION_FAILED_PLEASE_CONTACT_THE_ADMINISTRATOR("geoai_uos_account_deactivation_failed_please_contact_the_administrator"),
    /**
     * 密码修改成功，下次登录请用新密码！
     */
    GEOAI_UOS_PASSWORD_CHANGED_SUCCESSFULLY_PLEASE_USE_NEW_PASSWORD_FOR_NEXT_LOGIN("geoai_uos_password_changed_successfully_please_use_new_password_for_next_login"),
    /**
     * 密码修改失败!
     */
    GEOAI_UOS_PASSWORD_CHANGE_FAILED("geoai_uos_password_change_failed"),
    /**
     * 标签备注更新成功
     */
    GEOAI_UOS_TAG_NOTE_UPDATE_SUCCESS("geoai_uos_tag_note_update_success"),
    /**
     * 明细ID 不能为空
     */
    GEOAI_UOS_DETAIL_ID_CANNOT_BE_EMPTY("geoai_uos_detail_id_cannot_be_empty"),
    /**
     * 字典名称不能为空
     */
    GEOAI_UOS_DICTIONARY_NAME_CANNOT_BE_EMPTY("geoai_uos_dictionary_name_cannot_be_empty"),
    /**
     * 字典编码不能100个字符
     */
    GEOAI_UOS_DICTIONARY_CODE_CANNOT_BE_100_CHARACTERS("geoai_uos_dictionary_code_cannot_be_100_characters"),
    /**
     * 字典编码不能为空
     */
    GEOAI_UOS_DICTIONARY_CODE_CANNOT_BE_EMPTY("geoai_uos_dictionary_code_cannot_be_empty"),
    /**
     * 所属单位不能为空
     */
    GEOAI_UOS_UNIT_CANNOT_BE_EMPTY("geoai_uos_unit_cannot_be_empty"),
    /**
     * 名称不能为空
     */
    GEOAI_UOS_NAME_CANNOT_BE_EMPTY("geoai_uos_name_cannot_be_empty"),
    /**
     * 描述不能200个字符
     */
    GEOAI_UOS_DESCRIPTION_CANNOT_BE_200_CHARACTERS("geoai_uos_description_cannot_be_200_characters"),
    /**
     * 纬度不能为空
     */
    GEOAI_UOS_LATITUDE_CANNOT_BE_EMPTY("geoai_uos_latitude_cannot_be_empty"),
    /**
     * 纬度过大，请重新调整
     */
    GEOAI_UOS_LATITUDE_IS_TOO_LARGE_PLEASE_READJUST("geoai_uos_latitude_is_too_large_please_readjust"),
    /**
     * 纬度过小，请重新调整
     */
    GEOAI_UOS_LATITUDE_IS_TOO_SMALL_PLEASE_READJUST("geoai_uos_latitude_is_too_small_please_readjust"),
    /**
     * 纬度位上限5位，小数位上限8位
     */
    GEOAI_UOS_THE_UPPER_LIMIT_OF_LATITUDE_IS_5_DIGITS_THE_UPPER_LIMIT_OF_DECIMAL_DIGITS_IS_8_DIGITS("geoai_uos_the_upper_limit_of_latitude_is_5_digits_the_upper_limit_of_decimal_digits_is_8_digits"),
    /**
     * 经度不能为空
     */
    GEOAI_UOS_LONGITUDE_CAN_NOT_BE_EMPTY("geoai_uos_longitude_can_not_be_empty"),
    /**
     * 经度过大，请重新调整
     */
    GEOAI_UOS_LONGITUDE_IS_TOO_LARGE_PLEASE_READJUST("geoai_uos_longitude_is_too_large_please_readjust"),
    /**
     * 经度过小，请重新调整
     */
    GEOAI_UOS_LONGITUDE_IS_TOO_SMALL_PLEASE_READJUST("geoai_uos_longitude_is_too_small_please_readjust"),
    /**
     * 经度位上限5位，小数位上限8位
     */
    GEOAI_UOS_THE_MAXIMUM_NUMBER_OF_LONGITUDE_BITS_IS_5_AND_THE_MAXIMUM_NUMBER_OF_DECIMAL_PLACES_IS_8("geoai_uos_the_maximum_number_of_longitude_bits_is_5_and_the_maximum_number_of_decimal_places_is_8"),
    /**
     * 系统类型不能为空
     */
    GEOAI_UOS_SYSTEM_TYPE_CANNOT_BE_EMPTY("geoai_uos_system_type_cannot_be_empty"),
    /**
     * 系统展主题标题2~16个字符
     */
    GEOAI_UOS_SYSTEM_EXHIBITION_THEME_TITLE_2_16_CHARACTERS("geoai_uos_system_exhibition_theme_title_2_16_characters"),
    /**
     * 系统展示icon超不能200个字符
     */
    GEOAI_UOS_SYSTEM_DISPLAY_ICON_CAN_NOT_BE_200_CHARACTERS("geoai_uos_system_display_icon_can_not_be_200_characters"),
    /**
     * 系统展示favicon超不能200个字符
     */
    GEOAI_UOS_SYSTEM_DISPLAY_FAVICON_CAN_NOT_BE_200_CHARACTERS("geoai_uos_system_display_favicon_can_not_be_200_characters"),
    /**
     * 单位编码不能为空
     */
    GEOAI_UOS_UNIT_CODE_CAN_NOT_BE_EMPTY("geoai_uos_unit_code_can_not_be_empty"),
    /**
     * 单位编码不能30个字符
     */
    GEOAI_UOS_UNIT_CODE_CAN_NOT_BE_30_CHARACTERS("geoai_uos_unit_code_can_not_be_30_characters"),
    /**
     * 图层名称不能为空
     */
    GEOAI_UOS_LAYER_NAME_CANNOT_BE_EMPTY("geoai_uos_layer_name_cannot_be_empty"),
    /**
     * 图层发布路径不能为空
     */
    GEOAI_UOS_THE_LAYER_PUBLISH_PATH_CANNOT_BE_EMPTY("geoai_uos_the_layer_publish_path_cannot_be_empty"),
    /**
     * 图层类型不能为空
     */
    GEOAI_UOS_THE_LAYER_TYPE_CANNOT_BE_EMPTY("geoai_uos_the_layer_type_cannot_be_empty"),
    /**
     * 起降高度不能为空
     */
    GEOAI_UOS_THE_TAKEOFF_AND_LANDING_ALTITUDE_CANNOT_BE_EMPTY("geoai_uos_the_takeoff_and_landing_altitude_cannot_be_empty"),
    /**
     * 航线数据不能为空
     */
    GEOAI_UOS_ROUTE_DATA_CANNOT_BE_EMPTY("geoai_uos_route_data_cannot_be_empty"),

    /**
     * 飞机图传地址格式错误,请修改为正确的图传地址
     */
    GEOAI_UOS_PLEASE_CHANGE_THE_ADDRESS_TO_THE_CORRECT_ONE("geoai_uos_please_change_the_address_to_the_correct_one"),
    /**
     * 创建失败
     */
    GEOAI_UOS_FAILED_TO_CREATE("geoai_uos_failed_to_create"),
    /**
     * 下载失败，未找到对应视频
     */
    GEOAI_UOS_DOWNLOAD_FAILED_NO_CORRESPONDING_VIDEO_FOUND("geoai_uos_download_failed_no_corresponding_video_found"),
    /**
     * 下载失败，该执行架次没有视频
     */
    GEOAI_UOS_DOWNLOAD_FAILED_THERE_IS_NO_VIDEO_FOR_THIS_SORTIE("geoai_uos_download_failed_there_is_no_video_for_this_sortie"),
    /**
     * 下载失败，请正确选择视频
     */
    GEOAI_UOS_DOWNLOAD_FAILED_PLEASE_SELECT_THE_CORRECT_VIDEO("geoai_uos_download_failed_please_select_the_correct_video"),
    /**
     * 该架次下没有最新轨迹
     */
    GEOAI_UOS_NO_NEW_TRACK_FOR_THIS_SORTIE("geoai_uos_no_new_track_for_this_sortie"),
    /**
     * 该视频已经存在轨迹
     */
    GEOAI_UOS_THE_VIDEO_ALREADY_EXISTS("geoai_uos_the_video_already_exists"),
    /**
     * 移动终端添加成功
     */
    GEOAI_UOS_MOBILE_TERMINAL_ADDED_SUCCESSFULLY("geoai_uos_mobile_terminal_added_successfully"),
    /**
     * 移动终端修改成功
     */
    GEOAI_UOS_MOBILE_TERMINAL_MODIFIED_SUCCESSFULLY("geoai_uos_mobile_terminal_modified_successfully"),
    /**
     * 移动终端添加失败
     */
    GEOAI_UOS_FAILED_TO_ADD_MOBILE_TERMINAL("geoai_uos_failed_to_add_mobile_terminal"),
    /**
     * 移动终端修改失败
     */
    GEOAI_UOS_FAILED_TO_MODIFY_THE_MOBILE_TERMINAL("geoai_uos_failed_to_modify_the_mobile_terminal"),
    /**
     * 设置失败
     */
    GEOAI_UOS_SETTING_FAILED("geoai_uos_setting_failed"),
    /**
     * 任务标签保存失败
     */
    GEOAI_UOS_FAILED_TO_SAVE_TASK_LABEL("geoai_uos_failed_to_save_task_label"),
    /**
     * 将音频上传到机巢请求发送失败
     */
    GEOAI_UOS_UPLOADING_AUDIO_TO_MACHINE_NEST_REQUEST_SENDING_FAILED("geoai_uos_uploading_audio_to_machine_nest_request_sending_failed"),
    /**
     * 终止任务失败
     */
    GEOAI_UOS_TERMINATE_TASK_FAILED("geoai_uos_terminate_task_failed"),
    /**
     * 继续任务失败
     */
    GEOAI_UOS_CONTINUE_MISSION_FAILED("geoai_uos_continue_mission_failed"),
    /**
     * 查询不到架次信息
     */
    GEOAI_UOS_QUERY_SORTIE_INFORMATION_NOT_AVAILABLE("geoai_uos_query_sortie_information_not_available"),
    /**
     * 查询不到航线
     */
    GEOAI_UOS_ROUTE_NOT_QUERIED("geoai_uos_route_not_queried"),
    /**
     * 查询不到任务
     */
    GEOAI_UOS_MISSION_NOT_QUERIED("geoai_uos_mission_not_queried"),
    /**
     * 批量任务已在执行，请不要重复点击
     */
    GEOAI_UOS_BATCH_MISSION_IS_ALREADY_RUNNING_PLEASE_DONT_CLICK_IT_AGAIN("geoai_uos_batch_mission_is_already_running_please_dont_click_it_again"),
    /**
     * 批量任务开启成功
     */
    GEOAI_UOS_BATCH_MISSION_STARTED_SUCCESSFULLY("geoai_uos_batch_mission_started_successfully"),
    /**
     * 批量任务已经设置暂停，不会执行下一个任务
     */
    GEOAI_UOS_BATCH_TASK_HAS_BEEN_SET_TO_PAUSE_THE_NEXT_TASK_WILL_NOT_BE_EXECUTED("geoai_uos_batch_task_has_been_set_to_pause_the_next_task_will_not_be_executed"),
    /**
     * 批量任务已经设置停止，不会执行下一个任务
     */
    GEOAI_UOS_BATCH_TASK_HAS_BEEN_SET_TO_STOP_THE_NEXT_TASK_WILL_NOT_BE_EXECUTED("geoai_uos_batch_task_has_been_set_to_stop_the_next_task_will_not_be_executed"),
    /**
     * 批量任务已经设置结束
     */
    GEOAI_UOS_THE_BATCH_TASK_HAS_BEEN_SET_TO_END("geoai_uos_the_batch_task_has_been_set_to_end"),
    /**
     * 该任务已经取消，会跳过继续执行下一次任务
     */
    GEOAI_UOS_THE_TASK_HAS_BEEN_CANCELLED_IT_WILL_SKIP_TO_CONTINUE_THE_NEXT_TASK("geoai_uos_the_task_has_been_cancelled_it_will_skip_to_continue_the_next_task"),
    /**
     * 改状态下不能取消任务
     */
    GEOAI_UOS_THE_TASK_CANNOT_BE_CANCELED_IN_THE_CHANGED_STATE("geoai_uos_the_task_cannot_be_canceled_in_the_changed_state"),
    /**
     * 任务没有在暂停
     */
    GEOAI_UOS_THE_TASK_IS_NOT_ON_PAUSE("geoai_uos_the_task_is_not_on_pause"),
    /**
     * 继续任务
     */
    GEOAI_UOS_CONTINUE_TASK("geoai_uos_continue_task"),
    /**
     * 任务继续执行
     */
    GEOAI_UOS_THE_TASK_CONTINUES_TO_BE_EXECUTED("geoai_uos_the_task_continues_to_be_executed"),
    /**
     * 任务取消执行
     */
    GEOAI_UOS_TASK_CANCELLATION("geoai_uos_task_cancellation"),
    /**
     * 只有G900、G600才能执行多任务
     */
    GEOAI_UOS_ONLY_G900_AND_G600_CAN_PERFORM_MULTITASKING("geoai_uos_only_g900_and_g600_can_perform_multitasking"),
    /**
     * 查询不到基站
     */
    GEOAI_UOS_BASE_STATION_NOT_QUERIED("geoai_uos_base_station_not_queried"),
    /**
     * 注销监听器失败
     */
    GEOAI_UOS_LISTENER_CANCELLATION_FAILED("geoai_uos_listener_cancellation_failed"),
    /**
     * 清空任务列成功
     */
    GEOAI_UOS_EMPTYING_TASK_COLUMN_SUCCEEDED("geoai_uos_emptying_task_column_succeeded"),
    /**
     * 清空任务列失败
     */
    GEOAI_UOS_CLEAR_TASK_COLUMN_FAILED("geoai_uos_clear_task_column_failed"),
    /**
     * 暂停任务列失败
     */
    GEOAI_UOS_SUSPEND_TASK_COLUMN_FAILED("geoai_uos_suspend_task_column_failed"),
    /**
     * 暂停任务列成功
     */
    GEOAI_UOS_SUSPEND_TASK_QUEUE_SUCCESS("geoai_uos_suspend_task_queue_success"),
    /**
     * 任务队列已经执行完毕
     */
    GEOAI_UOS_TASK_QUEUE_HAS_BEEN_EXECUTED("geoai_uos_task_queue_has_been_executed"),
    /**
     * 继续任务列失败
     */
    GEOAI_UOS_CONTINUE_TASK_QUEUE_FAILED("geoai_uos_continue_task_queue_failed"),
    /**
     * 继续任务列成功
     */
    GEOAI_UOS_CONTINUE_TASK_QUEUE_SUCCEEDED("geoai_uos_continue_task_queue_succeeded"),
    /**
     * 终止任务列成功
     */
    GEOAI_UOS_TERMINATE_TASK_QUEUE_SUCCESS("geoai_uos_terminate_task_queue_success"),
    /**
     * 查询不到自动任务队列
     */
    GEOAI_UOS_THE_AUTOMATIC_TASK_QUEUE_WAS_NOT_QUERIED("geoai_uos_the_automatic_task_queue_was_not_queried"),
    /**
     * 任务置顶失败
     */
    GEOAI_UOS_TASK_TOPPING_FAILED("geoai_uos_task_topping_failed"),
    /**
     * 任务置顶成功
     */
    GEOAI_UOS_TASK_TOPPING_SUCCESS("geoai_uos_task_topping_success"),
    /**
     * 查询不到基站信息
     */
    GEOAI_UOS_THE_BASE_STATION_INFORMATION_CANNOT_BE_QUERIED("geoai_uos_the_base_station_information_cannot_be_queried"),
    /**
     * 查询不到基站，任务重发失败
     */
    GEOAI_UOS_QUERY_THE_BASE_STATION_TASK_RETRANSMISSION_FAILED("geoai_uos_query_the_base_station_task_retransmission_failed"),
    /**
     * 查询不到任务队列，任务重发失败
     */
    GEOAI_UOS_THE_TASK_RETRANSMISSION_FAILS_BECAUSE_THE_TASK_QUEUE_IS_NOT_QUERIED("geoai_uos_the_task_retransmission_fails_because_the_task_queue_is_not_queried"),
    /**
     * 批量任务列不是暂停状态，不能继续任务
     */
    GEOAI_UOS_BATCH_TASK_COLUMN_IS_NOT_SUSPENDED_STATE_CAN_NOT_CONTINUE_THE_TASK("geoai_uos_batch_task_column_is_not_suspended_state_can_not_continue_the_task"),
    /**
     * 立即执行批量数据同步失败
     */
    GEOAI_UOS_EXECUTE_BATCH_DATA_SYNCHRONIZATION_IMMEDIATELY_FAILS("geoai_uos_execute_batch_data_synchronization_immediately_fails"),
    /**
     * 立即执行批量数据同步成功
     */
    GEOAI_UOS_EXECUTE_BATCH_DATA_SYNCHRONIZATION_SUCCESSFULLY("geoai_uos_execute_batch_data_synchronization_successfully"),
    /**
     * 上传失败，机巢未连接
     */
    GEOAI_UOS_UPLOAD_FAILED_MACHINE_NEST_IS_NOT_CONNECTED("geoai_uos_upload_failed_machine_nest_is_not_connected"),
    /**
     * 任务实体、基站实体、任务类型查询不到
     */
    GEOAI_UOS_TASK_ENTITY_BASE_STATION_ENTITY_AND_TASK_TYPE_CANNOT_BE_QUERIED("geoai_uos_task_entity_base_station_entity_and_task_type_cannot_be_queried"),
    /**
     * 无人机类型检测不到，请检查基站设置是否填完整
     */
    GEOAI_UOS_THE_UAV_TYPE_CANNOT_BE_DETECTED_PLEASE_CHECK_WHETHER_THE_BASE_STATION_SETTINGS_ARE_FILLED_IN_COMPLETELY("geoai_uos_the_uav_type_cannot_be_detected_please_check_whether_the_base_station_settings_are_filled_in_completely"),
    /**
     * 航线检测不通过，起降高度与第一个点的距离须大于0.5米，请在航线规划的检查航线
     */
    GEOAI_UOS_ROUTE_DETECTION_DOES_NOT_PASS_THE_DISTANCE_BETWEEN_TAKEOFF_AND_LANDING_ALTITUDE_AND_THE_FIRST_POINT_MUST_BE_GREATER_THAN_05_METERS_PLEASE_CHECK_THE_ROUTE_IN_THE_ROUTE_PLANNING("geoai_uos_route_detection_does_not_pass_the_distance_between_takeoff_and_landing_altitude_and_the_first_point_must_be_greater_than_05_meters_please_check_the_route_in_the_route_planning"),
    /**
     * 基站类型和无人机类型检测不到，请检查基站设置是否填完整
     */
    GEOAI_UOS_THE_BASE_STATION_TYPE_AND_UAV_TYPE_CANNOT_BE_DETECTED_PLEASE_CHECK_WHETHER_THE_BASE_STATION_SETTINGS_ARE_FILLED_IN_COMPLETELY("geoai_uos_the_base_station_type_and_uav_type_cannot_be_detected_please_check_whether_the_base_station_settings_are_filled_in_completely"),
    /**
     * 无法判断航线是相对航线还是绝对航线
     */
    GEOAI_UOS_CAN_NOT_DETERMINE_WHETHER_THE_ROUTE_IS_A_RELATIVE_ROUTE_OR_AN_ABSOLUTE_ROUTE("geoai_uos_can_not_determine_whether_the_route_is_a_relative_route_or_an_absolute_route"),
    /**
     * 无法检测航线是否是绝对航线
     */
    GEOAI_UOS_UNABLE_TO_DETECT_WHETHER_THE_ROUTE_IS_AN_ABSOLUTE_ROUTE("geoai_uos_unable_to_detect_whether_the_route_is_an_absolute_route"),
    /**
     * 任务开启失败
     */
    GEOAI_UOS_MISSION_OPENING_FAILED("geoai_uos_mission_opening_failed"),
    /**
     * 任务上传失败
     */
    GEOAI_UOS_MISSION_UPLOAD_FAILED("geoai_uos_mission_upload_failed"),
    /**
     * 任务开启超时
     */
    GEOAI_UOS_MISSION_START_TIMEOUT("geoai_uos_mission_start_timeout"),
    /**
     * 初始化任务队列失败
     */
    GEOAI_UOS_FAILED_TO_INITIALIZE_TASK_QUEUE("geoai_uos_failed_to_initialize_task_queue"),
    /**
     * 注册监听器失败
     */
    GEOAI_UOS_FAILED_TO_REGISTER_A_LISTENER("geoai_uos_failed_to_register_a_listener"),
    /**
     * 自动任务队列开始执行
     */
    GEOAI_UOS_AUTOMATIC_TASK_QUEUE_STARTED_EXECUTION("geoai_uos_automatic_task_queue_started_execution"),
    /**
     * 基站状态离线,无法下发任务
     */
    GEOAI_UOS_THE_STATUS_OF_THE_BASE_STATION_IS_OFFLINE_UNABLE_TO_SEND_TASKS("geoai_uos_the_status_of_the_base_station_is_offline_unable_to_send_tasks"),
    /**
     * 当前基站正在执行任务，不允许发送任务
     */
    GEOAI_UOS_THE_BASE_STATION_IS_CURRENTLY_EXECUTING_TASKS_AND_IS_NOT_ALLOWED_TO_SEND_TASKS("geoai_uos_the_base_station_is_currently_executing_tasks_and_is_not_allowed_to_send_tasks"),
    /**
     * 该基站有批量自动任务正在运行
     */
    GEOAI_UOS_THE_BASE_STATION_HAS_A_BATCH_OF_AUTOMATIC_TASKS_RUNNING("geoai_uos_the_base_station_has_a_batch_of_automatic_tasks_running"),
    /**
     * 该基站有批量自动任务暂停中
     */
    GEOAI_UOS_THE_BASE_STATION_HAS_A_BATCH_OF_AUTOMATIC_TASKS_ON_HOLD("geoai_uos_the_base_station_has_a_batch_of_automatic_tasks_on_hold"),
    /**
     * 基站允许执行任务
     */
    GEOAI_UOS_THE_BASE_STATION_IS_ALLOWED_TO_EXECUTE_TASKS("geoai_uos_the_base_station_is_allowed_to_execute_tasks"),
    /**
     * 基站数据管理器状态为
     */
    GEOAI_UOS_THE_BASE_STATION_DATA_MANAGER_STATUS("geoai_uos_the_base_station_data_manager_status"),
    /**
     * 不允许下发任务
     */
    GEOAI_UOS_NOT_ALLOWED_TO_SEND_TASKS("geoai_uos_not_allowed_to_send_tasks"),
    /**
     * 当前基站状态为
     */
    GEOAI_UOS_THE_CURRENT_BASE_STATION_STATUS_IS("geoai_uos_the_current_base_station_status_is"),
    /**
     * 不允许发送任务
     */
    GEOAI_UOS_SENDING_TASKS_ARE_NOT_ALLOWED("geoai_uos_sending_tasks_are_not_allowed"),
    /**
     * 同单位标签名不能重复!
     */
    GEOAI_UOS_CANNOT_SAME_UNIT_TAG_NAME_REPEAT("geoai_uos_cannot_same_unit_tag_name_repeat"),
    /**
     * 必须是同单位下的标签哦!
     */
    GEOAI_UOS_MUST_BE_SAME_UNIT_UNDER_THE_LABEL("geoai_uos_must_be_same_unit_under_the_label"),
    /**
     * 不允许发送任务
     */
    GEOAI_UOS_CANNOT_ORDER_REPEAT("geoai_uos_sending_tasks_are_not_allowed"),
    /**
     * 起飞录制指令下发成功
     */
    GEOAI_UOS_SUCCESS_TAKEOFF_RECORDING_INSTRUCTIONS("geoai_uos_success_takeoff_recording_instructions"),
    /**
     * 起飞录制指令下发失败
     */
    GEOAI_UOS_FAIL_TAKEOFF_RECORDING_INSTRUCTIONS("geoai_uos_fail_takeoff_recording_instructions"),
    /**
     * 变焦航线，切换变焦镜头成功
     */
    GEOAI_UOS_SUCCESS_ZOOM_ROUTE("geoai_uos_success_zoom_route"),
    /**
     * 变焦航线，切换变焦镜头失败，
     */
    GEOAI_UOS_FAILED_ZOOM_ROUTE("geoai_uos_failed_zoom_route"),
    /**
     * 数据同步结束
     */
    GEOAI_UOS_END_OF_DATA_SYNCHRONIZATION("geoai_uos_end_of_data_synchronization"),
    /**
     * 数据同步成功
     */
    GEOAI_UOS_SUCCESS_DATA_SYNCHRONIZATION("geoai_uos_success_data_synchronization"),
    /**
     * syncFlightMissionMain同步结束，nestId为空
     */
    GEOAI_UOS_SYNCFLIGHTMISSIONMAIN_NESTID_EMPTY("geoai_uos_syncFlightMissionMain_nestId_empty"),
    /**
     * syncFlightMissionMain同步结束，开始时间为空
     */
    GEOAI_UOS_SYNCFLIGHTMISSIONMAIN_STARTTIME_EMPTY("geoai_uos_syncFlightMissionMain_starttime_empty"),
    /**
     * syncFlightMissionMain同步结束，结束时间为空
     */
    GEOAI_UOS_SYNCFLIGHTMISSIONMAIN_ENDTIME_EMPTY("geoai_uos_syncFlightMissionMain_endtime_empty"),
    /**
     * syncFlightMissionMain同步结束，基槽离线
     */
    GEOAI_UOS_SYNCFLIGHTMISSIONMAIN_BASE_SLOT_OFFLINE("geoai_uos_syncFlightMissionMain_base_slot_offline"),
    /**
     * 没有查询到任务队列
     */
    GEOAI_UOS_NO_TASK_QUEUE_IS_QUERIED("geoai_uos_no_task_queue_is_queried"),
    /**
     * 同步源数据结束
     */
    GEOAI_UOS_END_OF_SYNCHRONIZATION_OF_SOURCE_DATA("geoai_uos_end_of_synchronization_of_source_data"),
    /**
     * 任务开启成功
     */
    GEOAI_UOS_SUCCESS_TASK_OPEN("geoai_uos_success_task_open"),
    /**
     * 任务上传成功
     */
    GEOAI_UOS_SUCCESS_TASK_UPLOAD("geoai_uos_success_task_upload"),
    /**
     * 批量任务数据异常
     */
    GEOAI_UOS_BATCH_TASK_DATA_EXCEPTION("geoai_uos_batch_task_data_exception"),
    /**
     * 批量任务异常结束了
     */
    GEOAI_UOS_BATCH_MISSION_EXCEPTION_END("geoai_uos_batch_mission_exception_end"),
    /**
     * 飞行前检测通过
     */
    GEOAI_UOS_FLIGHT_DETECTION_PASSED("geoai_uos_flight_detection_passed"),
    /**
     * 飞行前检测有警告
     */
    GEOAI_UOS_FLIGHT_DETECTION_WARNING("geoai_uos_flight_detection_warning"),
    /**
     * 飞行前检测超时
     */
    GEOAI_UOS_FLIGHT_DETECTION_TIMEOUT("geoai_uos_flight_detection_timeout"),
    /**
     * 参数校验失败
     */
    GEOAI_UOS_FAIL_VERIFICATION_PARAM("geoai_uos_fail_verification_param"),

    /**
     * 照片类型-可见光
     */
    GEOAI_DATA_ANALYSIS_PIC_TYPE_VISIBLE("geoai_data_analysis_pic_type_visible"),

    /**
     * 照片类型-热红外
     */
    GEOAI_DATA_ANALYSIS_PIC_TYPE_INFRARED("geoai_data_analysis_pic_type_infrared"),
    /**
     * 文件服务器不存在该文件，请联系管理员
     */
    GEOAI_UOS_FILE_NOT_EXIST("geoai_uos_file_not_exist"),
    /**
     * 监测不到架次记录
     */
    GEOAI_UOS_CANT_NOT_EXIST_MISSION_RECORD("geoai_uos_cant_not_exist_mission_record"),
    /**
     * 传入对象为空
     */
    GEOAI_AUTH_THE_PASSED_OBJECT_IS_NULL("geoai_auth_the_passed_object_is_null"),
    /**
     * 请先选择单位！
     */
    GEOAI_AUTH_PLEASE_SELECT_THE_UNIT_FIRST("geoai_auth_please_select_the_unit_first"),
    /**
     * 消息内容字段长度大于10000！
     */
    GEOAI_AUTH_MESSAGE_CONTENT_FIELD_LENGTH_GREATER_THAN_10000("geoai_auth_message_content_field_length_greater_than_10000"),
    /**
     * 系统版本字段长度大于20！
     */
    GEOAI_AUTH_SYSTEM_VERSION_FIELD_LENGTH_GREATER_THAN_20("geoai_auth_system_version_field_length_greater_than_20"),
    /**
     * 更新公告，版本号字段不能为空！
     */
    GEOAI_AUTH_UPDATE_BULLETIN_VERSION_NUMBER_FIELD_CANNOT_BE_EMPTY("geoai_auth_update_bulletin_version_number_field_cannot_be_empty"),
    /**
     * 消息更新日志字段长度大于10000！
     */
    GEOAI_AUTH_MESSAGE_UPDATE_LOG_FIELD_LENGTH_GREATER_THAN_10000("geoai_auth_message_update_log_field_length_greater_than_10000"),
    /**
     * 查询不到相关数据
     */
    GEOAI_AUTH_THE_RELATED_DATA_CANNOT_BE_QUERIED("geoai_auth_the_related_data_cannot_be_queried"),
    /**
     * 未知错误，请联系管理员
     */
    GEOAI_AUTH_UNKNOWN_ERROR_PLEASE_CONTACT_THE_ADMINISTRATOR("geoai_auth_unknown_error_please_contact_the_administrator"),
    /**
     * 没有可以导出的数据，请检查!
     */
    GEOAI_UOS_THERE_IS_NO_DATA_THAT_CAN_BE_EXPORTED_PLEASE_CHECK("geoai_uos_there_is_no_data_that_can_be_exported_please_check"),
    /**
     * 当前问题不需要取消合并
     */
    GEOAI_UOS_THE_CURRENT_ISSUE_DOES_NOT_NEED_TO_BE_UNMERGED("geoai_uos_the_current_issue_does_not_need_to_be_unmerged"),
    /**
     * 正在核实中,请稍后再进行撤回/重置操作
     */
    GEOAI_UOS_IN_THE_PROCESS_OF_VERIFICATION_PLEASE_WITHDRAW_RESET_OPERATION_LATER("geoai_uos_in_the_process_of_verification_please_withdraw_reset_operation_later"),
    /**
     * 已核实过的数据，不允许操作标注，请优先撤回再操作
     */
    GEOAI_UOS_THE_VERIFIED_DATA_IS_NOT_ALLOWED_TO_OPERATE_THE_ANNOTATION_PLEASE_WITHDRAW_AND_OPERATE_AGAIN_FIRST("geoai_uos_the_verified_data_is_not_allowed_to_operate_the_annotation_please_withdraw_and_operate_again_first"),
    /**
     * 不支持的标注名称类型
     */
    GEOAI_UOS_UNSUPPORTED_TYPES_OF_ANNOTATION_NAMES("geoai_uos_unsupported_types_of_annotation_names"),
    /**
     * 所要修改的全景点不存在!
     */
    GEOAI_UOS_THE_PANORAMIC_POINT_TO_BE_MODIFIED_DOES_NOT_EXIST("geoai_uos_the_panoramic_point_to_be_modified_does_not_exist"),
    /**
     * 保存全景明细出错：全景明细数据不能为空
     */
    GEOAI_UOS_ERROR_IN_SAVING_PANORAMA_DETAILS__THE_PANORAMA_DETAILS_DATA_CANNOT_BE_EMPTY("geoai_uos_error_in_saving_panorama_details__the_panorama_details_data_cannot_be_empty"),
    /**
     * 请上传zip格式文件!
     */
    GEOAI_UOS_PLEASE_UPLOAD_ZIP_FORMAT_FILE("geoai_uos_please_upload_zip_format_file"),
    /**
     * 上传zip失败
     */
    GEOAI_UOS_FAILED_TO_UPLOAD_ZIP_FILE("geoai_uos_failed_to_upload_zip_file"),
    /**
     * 新增全景点明细数据时，全景点ID不能为空！
     */
    GEOAI_UOS_WHEN_ADDING_PANORAMA_DETAILS_THE_PANORAMA_ID_CANNOT_BE_EMPTY("geoai_uos_when_adding_panorama_details_the_panorama_id_cannot_be_empty"),
    /**
     * 传入待删除的数据为空，请检查！
     */
    GEOAI_UOS_THE_DATA_TO_BE_DELETED_IS_EMPTY_PLEASE_CHECK("geoai_uos_the_data_to_be_deleted_is_empty_please_check"),
    /**
     * 保存全景点出错：全景点数据不能为空
     */
    GEOAI_UOS_ERROR_SAVING_FULL_POINT__FULL_POINT_DATA_CANNOT_BE_EMPTY("geoai_uos_error_saving_full_point__full_point_data_cannot_be_empty"),
    /**
     * 航线航点不能只选择其一
     */
    GEOAI_UOS_CANT_SELECT_ONLY_ONE_WAYPOINT_FOR_THE_ROUTE("geoai_uos_cant_select_only_one_waypoint_for_the_route"),
    /**
     * 传入待更新的数据为空，请检查！
     */
    GEOAI_UOS_THE_DATA_TO_BE_UPDATED_IS_EMPTY_PLEASE_CHECK("geoai_uos_the_data_to_be_updated_is_empty_please_check"),
    /**
     * 查询的航线不是全景航线，请检查!
     */
    GEOAI_UOS_THE_QUERY_ROUTE_IS_NOT_A_PANORAMIC_ROUTE_PLEASE_CHECK("geoai_uos_the_query_route_is_not_a_panoramic_route_please_check"),
    /**
     * 没有可以导出的数据!
     */
    GEOAI_UOS_THERE_IS_NO_DATA_THAT_CAN_BE_EXPORTED("geoai_uos_there_is_no_data_that_can_be_exported"),
    /**
     * 一键返航失败，失败原因
     */
    GEOAI_UOS_ONE_CLICK_RETURN_FAILS_REASON_FOR_FAILURE("geoai_uos_one_click_return_fails_reason_for_failure"),
    /**
     * 机场重启失败，失败原因
     */
    GEOAI_UOS_AIRPORT_RESTART_FAILED_REASON_OF_FAILURE("geoai_uos_airport_restart_failed_reason_of_failure"),
    /**
     * 单位未开启图层展示状态，不允许执行该操作
     */
    GEOAI_UOS_THE_UNIT_HAS_NOT_OPENED_THE_LAYER_DISPLAY_STATUS_THE_OPERATION_IS_NOT_ALLOWED_TO_BE_EXECUTED("geoai_uos_the_unit_has_not_opened_the_layer_display_status_the_operation_is_not_allowed_to_be_executed"),
    /**
     * 图层数据不存在
     */
    GEOAI_UOS_LAYER_DATA_DOES_NOT_EXIST("geoai_uos_layer_data_does_not_exist"),
    /**
     * 只允许上级单位给下级单位新建电子围栏数据
     */
    GEOAI_UOS_ONLY_ALLOW_UPPER_LEVEL_UNIT_NEW_ELECTRONIC_FENCE_DATA("geoai_uos_only_allow_upper_level_unit_new_electronic_fence_data"),
    /**
     * 电子围栏信息不存在
     */
    GEOAI_UOS_ELECTRONIC_FENCE_INFORMATION_DOES_NOT_EXIST("geoai_uos_electronic_fence_information_does_not_exist"),
    /**
     * 地图操作成功
     */
    GEOAI_UOS_MAP_OPERATION_SUCCESSFUL("geoai_uos_map_operation_successful"),
    /**
     * 地图操作失败
     */
    GEOAI_UOS_MAP_OPERATION_FAILED("geoai_uos_map_operation_failed"),
    /**
     * 续执行任务动作成功
     */
    GEOAI_UOS_CONTINUED_TASK_ACTION_SUCCESSFUL("geoai_uos_continued_task_action_successful"),
    /**
     * 消息标记已读
     */
    GEOAI_UOS_MESSAGE_MARKED_AS_READ("geoai_uos_message_marked_as_read"),
    /**
     * 区域添加成功
     */
    GEOAI_UOS_AREA_ADDED_SUCCESSFULLY("geoai_uos_area_added_successfully"),
    /**
     * 区域添加失败
     */
    GEOAI_UOS_AREA_ADDITION_FAILED("geoai_uos_area_addition_failed"),
    /**
     * 区域修改成功
     */
    GEOAI_UOS_AREA_MODIFICATION_SUCCESSFUL("geoai_uos_area_modification_successful"),
    /**
     * 区域修改失败
     */
    GEOAI_UOS_AREA_MODIFICATION_FAILED("geoai_uos_area_modification_failed"),
    /**
     * 不存在该区域
     */
    GEOAI_UOS_THE_REGION_DOES_NOT_EXIST("geoai_uos_the_region_does_not_exist"),
    /**
     * 当前区域下还有绑定的机巢！请先处理，再删除区域！
     */
    GEOAI_UOS_MACHINE_NESTS_UNDER_THE_CURRENT_REGION_DELETE_THE_REGION("geoai_uos_machine_nests_under_the_current_region_delete_the_region"),
    /**
     * 获取RTK是否开启失败
     */
    GEOAI_UOS_FAILED_TO_GET_WHETHER_RTK_IS_ON_OR_NOT("geoai_uos_failed_to_get_whether_rtk_is_on_or_not"),
    /**
     * 开启RTK失败
     */
    GEOAI_UOS_FAILED_TO_TURN_ON_RTK("geoai_uos_failed_to_turn_on_rtk"),
    /**
     * 关闭RTK失败
     */
    GEOAI_UOS_FAILED_TO_TURN_OFF_RTK("geoai_uos_failed_to_turn_off_rtk"),
    /**
     * 获取RTK状态失败
     */
    GEOAI_UOS_FAILED_TO_GET_RTK_STATUS("geoai_uos_failed_to_get_rtk_status"),
    /**
     * 设置RTK状态失败
     */
    GEOAI_UOS_FAILED_TO_SET_RTK_STATUS("geoai_uos_failed_to_set_rtk_status"),
    /**
     * 获取RTK账号信息失败
     */
    GEOAI_UOS_FAILED_TO_GET_RTK_ACCOUNT_INFORMATION("geoai_uos_failed_to_get_rtk_account_information"),
    /**
     * 设置RTK账号信息失败
     */
    GEOAI_UOS_SET_RTK_ACCOUNT_INFORMATION_FAILED("geoai_uos_set_rtk_account_information_failed"),
    /**
     * 开启drtk电源触发失败
     */
    GEOAI_UOS_TURN_ON_DRTK_POWER_TRIGGER_FAILURE("geoai_uos_turn_on_drtk_power_trigger_failure"),
    /**
     * 获取失败
     */
    GEOAI_UOS_FAILED_TO_GET("geoai_uos_failed_to_get"),
    /**
     * 获取低电量智能关机状态失败
     */
    GEOAI_UOS_GET_LOW_BATTERY_SMART_SHUTDOWN_STATUS_FAILED("geoai_uos_get_low_battery_smart_shutdown_status_failed"),
    /**
     * 低电量智能返航成功
     */
    GEOAI_UOS_LOW_BATTERY_SMART_RETURN_SUCCESS("geoai_uos_low_battery_smart_return_success"),
    /**
     * 低电量智能返航失败
     */
    GEOAI_UOS_LOW_BATTERY_SMART_RETURN_FAILED("geoai_uos_low_battery_smart_return_failed"),
    /**
     * 精细巡检包解析错误
     */
    GEOAI_UOS_FINE_PATROL_PACKAGE_PARSING_ERROR("geoai_uos_fine_patrol_package_parsing_error"),
    /**
     * 保存解析数据失败
     */
    GEOAI_UOS_FAILED_TO_SAVE_PARSED_DATA("geoai_uos_failed_to_save_parsed_data"),
    /**
     * 读取点云数据失败
     */
    GEOAI_UOS_FAILED_TO_READ_POINT_CLOUD_DATA("geoai_uos_failed_to_read_point_cloud_data"),
    /**
     * 读取航线数据失败
     */
    GEOAI_UOS_FAILED_TO_READ_ROUTE_DATA("geoai_uos_failed_to_read_route_data"),
    /**
     * 导出电池信息时发生错误
     */
    GEOAI_UOS_ERROR_WHEN_EXPORTING_BATTERY_INFORMATION("geoai_uos_error_when_exporting_battery_information"),
    /**
     * 标签未关联单位!
     */
    GEOAI_UOS_TAG_NOT_ASSOCIATED_WITH_UNIT("geoai_uos_tag_not_associated_with_unit"),
    /**
     * 下载出错
     */
    GEOAI_UOS_ERROR_DOWNLOADING("geoai_uos_error_downloading"),
    /**
     * 标记出错
     */
    GEOAI_UOS_TAGGING_ERROR("geoai_uos_tagging_error"),
    /**
     * 事件唯一编码为空
     */
    GEOAI_UOS_EVENT_UNIQUE_CODE_IS_EMPTY("geoai_uos_event_unique_code_is_empty"),
    /**
     * 该缺陷名称已存在，不可重复!
     */
    GEOAI_UOS_THE_DEFECT_NAME_ALREADY_EXISTS_AND_CANNOT_BE_REPEATED("geoai_uos_the_defect_name_already_exists_and_cannot_be_repeated"),
    /**
     * 未获取到天气数据,可能是秘钥已经失效,或没有找到在线机巢
     */
    GEOAI_UOS_WEATHER_DATA_WAS_NOT_RETRIEVED("geoai_uos_weather_data_was_not_retrieved"),
    /**
     * 参数不能为空
     */
    GEOAI_UOS_THE_PARAMETER_CANNOT_BE_EMPTY("geoai_uos_the_parameter_cannot_be_empty"),
    /**
     * 更新成功
     */
    GEOAI_UOS_UPDATED_SUCCESSFULLY("geoai_uos_updated_successfully"),
    /**
     * 该点云正在分析中，请勿同时重复识别
     */
    GEOAI_UOS_THE_POINT_CLOUD_IS_BEING_ANALYZED_PLEASE_DO_NOT_REPEAT_THE_IDENTIFICATION_AT_THE_SAME_TIME("geoai_uos_the_point_cloud_is_being_analyzed_please_do_not_repeat_the_identification_at_the_same_time"),
    /**
     * 选中的点云文件没有.las文件，无法进行分析
     */
    GEOAI_UOS_THE_SELECTED_POINT_CLOUD_FILE_DOES_NOT_HAVE_A_LAS_FILE_SO_IT_CANNOT_BE_ANALYZED("geoai_uos_the_selected_point_cloud_file_does_not_have_a_las_file_so_it_cannot_be_analyzed"),
    /**
     * 未分析出违建点
     */
    GEOAI_UOS_NO_ILLEGAL_POINTS_ARE_ANALYZED("geoai_uos_no_illegal_points_are_analyzed"),
    /**
     * 导出失败，无违建点信息
     */
    GEOAI_UOS_EXPORT_FAILED_NO_ILLEGAL_POINT_INFORMATION("geoai_uos_export_failed_no_illegal_point_information"),
    /**
     * 该违建点未关联任务照片，无法匹配
     */
    GEOAI_UOS_THE_POINT_IS_NOT_ASSOCIATED_WITH_THE_TASK_PHOTO_CAN_NOT_MATCH("geoai_uos_the_point_is_not_associated_with_the_task_photo_can_not_match"),
    /**
     * 匹配不到照片
     */
    GEOAI_UOS_NO_MATCHING_PHOTOS("geoai_uos_no_matching_photos"),
    /**
     * 该违建点未保存
     */
    GEOAI_UOS_THE_POINT_IS_NOT_SAVED("geoai_uos_the_point_is_not_saved"),
    /**
     * 未识别到违建点
     */
    GEOAI_UOS_NO_VIOLATION_POINT_IS_IDENTIFIED("geoai_uos_no_violation_point_is_identified"),
    /**
     * 识别失败
     */
    GEOAI_UOS_IDENTIFICATION_FAILED("geoai_uos_identification_failed"),
    /**
     * 该正射文件无tif文件
     */
    GEOAI_UOS_THERE_IS_NO_TIF_FILE_IN_THE_ORTHOMOSAIC_FILE("geoai_uos_there_is_no_tif_file_in_the_orthomosaic_file"),
    /**
     * 识别失败，该目录里有非tif的文件
     */
    GEOAI_UOS_RECOGNITION_FAILED_THERE_ARE_NONTIF_FILES_IN_THE_DIRECTORY("geoai_uos_recognition_failed_there_are_nontif_files_in_the_directory"),
    /**
     * 识别出错
     */
    GEOAI_UOS_IDENTIFICATION_ERROR("geoai_uos_identification_error"),
    /**
     * 未查询到巡检计划记录信息
     */
    GEOAI_UOS_NO_INSPECTION_PLAN_RECORD_INFORMATION_WAS_QUERIED("geoai_uos_no_inspection_plan_record_information_was_queried"),
    /**
     * 只允许启停周期计划
     */
    GEOAI_UOS_ONLY_THE_START_STOP_CYCLE_PLAN_IS_ALLOWED("geoai_uos_only_the_start_stop_cycle_plan_is_allowed"),
    /**
     * 巡检计划不存在
     */
    GEOAI_UOS_INSPECTION_PLAN_DOES_NOT_EXIST("geoai_uos_inspection_plan_does_not_exist"),
    /**
     * 无法取消当前日期之前的执行计划
     */
    GEOAI_UOS_CANNOT_CANCEL_THE_EXECUTION_PLAN_BEFORE_THE_CURRENT_DATE("geoai_uos_cannot_cancel_the_execution_plan_before_the_current_date"),
    /**
     * 定期计划不允许取消
     */
    GEOAI_UOS_CANCELLATION_IS_NOT_ALLOWED_FOR_PERIODIC_PLANS("geoai_uos_cancellation_is_not_allowed_for_periodic_plans"),
    /**
     * 计划已删除，执行失败
     */
    GEOAI_UOS_THE_SCHEDULE_HAS_BEEN_DELETED_AND_THE_EXECUTION_HAS_FAILED("geoai_uos_the_schedule_has_been_deleted_and_the_execution_has_failed"),
    /**
     * 查询不到计划相关的架次
     */
    GEOAI_UOS_THE_SORTIE_RELATED_TO_THE_PLAN_CANNOT_BE_QUERIED("geoai_uos_the_sortie_related_to_the_plan_cannot_be_queried"),
    /**
     * 巡检计划已执行，不允许进行修改
     */
    GEOAI_UOS_THE_INSPECTION_PLAN_HAS_BEEN_EXECUTED_AND_NO_MODIFICATION_IS_ALLOWED("geoai_uos_the_inspection_plan_has_been_executed_and_no_modification_is_allowed"),
    /**
     * 计划飞行任务与已有计划存在日期冲突
     */
    GEOAI_UOS_THERE_IS_A_DATE_CONFLICT_BETWEEN_THE_PLANNED_MISSION_AND_THE_EXISTING_PLAN("geoai_uos_there_is_a_date_conflict_between_the_planned_mission_and_the_existing_plan"),
    /**
     * 基站信息不存在
     */
    GEOAI_UOS_BASE_STATION_INFORMATION_DOES_NOT_EXIST("geoai_uos_base_station_information_does_not_exist"),
    /**
     * 当前基站非换电式基站，不允许选择多个架次
     */
    GEOAI_UOS_THE_CURRENT_BASE_STATION_IS_NOT_A_POWER_EXCHANGE_BASE_STATION_MULTIPLE_SORTIES_ARE_NOT_ALLOWED("geoai_uos_the_current_base_station_is_not_a_power_exchange_base_station_multiple_sorties_are_not_allowed"),
    /**
     * 单位编码不能为空
     */
    GEOAI_UOS_THE_UNIT_CODE_CANNOT_BE_EMPTY("geoai_uos_the_unit_code_cannot_be_empty"),
    /**
     * 查询不到对应的任务架次
     */
    GEOAI_UOS_THE_CORRESPONDING_MISSION_CANNOT_BE_QUERIED("geoai_uos_the_corresponding_mission_cannot_be_queried"),
    /**
     * 机巢任务构建失败
     */
    GEOAI_UOS_NEST_MISSION_CONSTRUCTION_FAILED("geoai_uos_nest_mission_construction_failed"),
    /**
     * 任务开启指令下发失败
     */
    GEOAI_UOS_FAILED_TO_SEND_MISSION_START_COMMAND("geoai_uos_failed_to_send_mission_start_command"),
    /**
     * 任务开启指令下发失败：机巢没有连接
     */
    GEOAI_UOS_FAILED_TO_SEND_TASK_START_COMMAND_THE_NEST_IS_NOT_CONNECTED("geoai_uos_failed_to_send_task_start_command_the_nest_is_not_connected"),
    /**
     * 启动任务失败，没有查询到任务
     */
    GEOAI_UOS_FAILED_TO_START_TASK_NO_TASK_WAS_QUERIED("geoai_uos_failed_to_start_task_no_task_was_queried"),
    /**
     * 启动任务失败，查询不到nestUuid
     */
    GEOAI_UOS_FAILED_TO_START_TASK_CANT_QUERY_NESTUUID("geoai_uos_failed_to_start_task_cant_query_nestuuid"),
    /**
     * 启动任务失败，查询不到大疆航线
     */
    GEOAI_UOS_FAILED_TO_START_MISSION_CANT_FIND_DJI_ROUTE("geoai_uos_failed_to_start_mission_cant_find_dji_route"),
    /**
     * 启动任务失败,开启任务获取参数失败
     */
    GEOAI_UOS_FAILED_TO_START_MISSION_FAILED_TO_GET_PARAMETERS_FOR_MISSION_START("geoai_uos_failed_to_start_mission_failed_to_get_parameters_for_mission_start"),
    /**
     * 启动任务失败,无法获取基站状态
     */
    GEOAI_UOS_FAILED_TO_START_MISSIONCANT_GET_BASE_STATION_STATUS("geoai_uos_failed_to_start_missioncant_get_base_station_status"),
    /**
     * 启动任务失败,基站状态非空闲
     */
    GEOAI_UOS_FAILED_TO_START_THE_TASK_THE_STATUS_OF_THE_BASE_STATION_IS_NOT_IDLE("geoai_uos_failed_to_start_the_task_the_status_of_the_base_station_is_not_idle"),
    /**
     * 启动任务失败,没有收到基站请求任务资源
     */
    GEOAI_UOS_FAILED_TO_START_MISSION_NO_REQUEST_FOR_MISSION_RESOURCES_FROM_BASE_STATION("geoai_uos_failed_to_start_mission_no_request_for_mission_resources_from_base_station"),
    /**
     * 大疆机场启动任务成功
     */
    GEOAI_UOS_DJI_AIRPORT_START_MISSION_SUCCESSFULLY("geoai_uos_dji_airport_start_mission_successfully"),
    /**
     * 任务移除失败
     */
    GEOAI_UOS_TASK_REMOVAL_FAILED("geoai_uos_task_removal_failed"),
    /**
     * 暂停任务失败
     */
    GEOAI_UOS_FAILED_TO_SUSPEND_TASK("geoai_uos_failed_to_suspend_task"),
    /**
     * 移除任务成功
     */
    GEOAI_UOS_REMOVE_TASK_SUCCESS("geoai_uos_remove_task_success"),
    /**
     * 自动任务队列最多8个，最多还允许添加
     */
    GEOAI_UOS_THE_MAXIMUM_NUMBER_OF_AUTOMATIC_TASKS_IN_THE_QUEUE_IS_8_AND_THE_MAXIMUM_NUMBER_OF_TASKS_ALLOWED_TO_BE_ADDED("geoai_uos_the_maximum_number_of_automatic_tasks_in_the_queue_is_8_and_the_maximum_number_of_tasks_allowed_to_be_added"),
    /**
     * 查询不到架次列表
     */
    GEOAI_UOS_THE_SORTIE_LIST_CANNOT_BE_QUERIED("geoai_uos_the_sortie_list_cannot_be_queried"),
    /**
     * 架次已存在，部分架次增加成功
     */
    GEOAI_UOS_THE_SORTIES_ALREADY_EXIST_AND_SOME_OF_THEM_WERE_ADDED_SUCCESSFULLY("geoai_uos_the_sorties_already_exist_and_some_of_them_were_added_successfully"),

    /**
     * 所有架次增加成功
     */
    GEOAI_UOS_ALL_SORTIES_WERE_ADDED_SUCCESSFULLY("geoai_uos_all_sorties_were_added_successfully"),
    /**
     * 取消批量数据同步失败
     */
    GEOAI_UOS_CANCELLATION_OF_BATCH_DATA_SYNCHRONIZATION_FAILED("geoai_uos_cancellation_of_batch_data_synchronization_failed"),
    /**
     * 取消批量数据同步成功
     */
    GEOAI_UOS_CANCELLATION_OF_BATCH_DATA_SYNCHRONIZATION_SUCCEEDED("geoai_uos_cancellation_of_batch_data_synchronization_succeeded"),
    /**
     * 找不到日志文件包,请尝试上传日志
     */
    GEOAI_UOS_CANT_FIND_THE_LOG_FILE_PACKAGE_PLEASE_TRY_TO_UPLOAD_THE_LOG("geoai_uos_cant_find_the_log_file_package_please_try_to_upload_the_log"),
    /**
     * CPS日志文件包解析失败
     */
    GEOAI_UOS_CPS_LOG_FILE_PARSING_FAILED("geoai_uos_cps_log_file_parsing_failed"),
    /**
     * 传入参数不能为空
     */
    GEOAI_UOS_INPUT_PARAMETER_CANNOT_BE_EMPTY("geoai_uos_input_parameter_cannot_be_empty"),
    /**
     * 无维修项目
     */
    GEOAI_UOS_NO_MAINTENANCE_ITEMS("geoai_uos_no_maintenance_items"),
    /**
     * 没有对应的机巢
     */
    GEOAI_UOS_NO_CORRESPONDING_MACHINE_NEST("geoai_uos_no_corresponding_machine_nest"),
    /**
     * 查询不到用户基站信息
     */
    GEOAI_UOS_CANT_QUERY_USER_BASE_STATION_INFORMATION("geoai_uos_cant_query_user_base_station_information"),
    /**
     * 云台回中成功
     */
    GEOAI_UOS_CLOUD_STATION_BACK_TO_THE_SUCCESS("geoai_uos_cloud_station_back_to_the_success"),
    /**
     * 云台回中失败
     */
    GEOAI_UOS_FAILURE_TO_RETURN_TO_THE_CLOUD_STATION("geoai_uos_failure_to_return_to_the_cloud_station"),
    /**
     * 机巢不存在
     */
    GEOAI_UOS_THE_NEST_DOES_NOT_EXIST("geoai_uos_the_nest_does_not_exist"),
    /**
     * 根据经纬度获取详细地址失败
     */
    GEOAI_UOS_FAILED_TO_GET_DETAILED_ADDRESS_ACCORDING_TO_LATITUDE_AND_LONGITUDE("geoai_uos_failed_to_get_detailed_address_according_to_latitude_and_longitude"),
    /**
     * 修改成功
     */
    GEOAI_UOS_MODIFY_SUCCESSFULLY("geoai_uos_modify_successfully"),
    /**
     * 消息ID跟用户ID，不能为空
     */
    GEOAI_UOS_MESSAGE_ID_AND_USER_ID_CANNOT_BE_EMPTY("geoai_uos_message_id_and_user_id_cannot_be_empty"),
    /**
     * 调用【saveAndPushPubMessageForTask】方法，传入参数不能为空!
     */
    GEOAI_UOS_CALLING_SAVEANDPUSHPUBMESSAGEFORTASK_METHOD_THE_INCOMING_PARAMETER_CANT_BE_EMPTY("geoai_uos_calling_saveandpushpubmessagefortask_method_the_incoming_parameter_cant_be_empty"),
    /**
     * 调用【saveAndPushPubMessageForTask】方法，NestId不能为空!
     */
    GEOAI_UOS_CALL_SAVEANDPUSHPUBMESSAGEFORTASK_METHOD_NESTID_CANT_BE_EMPTY("geoai_uos_call_saveandpushpubmessagefortask_method_nestid_cant_be_empty"),
    /**
     * 调用【saveAndPushPubMessageForTask】方法，companyIds不能为空!
     */
    GEOAI_UOS_CALLING_SAVEANDPUSHPUBMESSAGEFORTASK_METHOD_COMPANYIDS_CANT_BE_EMPTY("geoai_uos_calling_saveandpushpubmessagefortask_method_companyids_cant_be_empty"),
    /**
     * 定时时间间隔相差不足5分钟
     */
    GEOAI_UOS_THE_TIME_INTERVAL_DIFFERENCE_IS_LESS_THAN_5_MINUTES("geoai_uos_the_time_interval_difference_is_less_than_5_minutes"),
    /**
     * 文件类型编码不能为空
     */
    GEOAI_UOS_THE_FILE_TYPE_CODE_CANNOT_BE_EMPTY("geoai_uos_the_file_type_code_cannot_be_empty"),
    /**
     * 文件不能为空
     */
    GEOAI_UOS_FILE_CANNOT_BE_EMPTY("geoai_uos_file_cannot_be_empty"),
    /**
     * "非照片类型文件(%s)无法导入！请检查"
     */
    GEOAI_UOS_NON_PHOTO_TYPE_FILES_CANNOT_BE_IMPORTED_PLEASE_CHECK("geoai_uos_non_photo_type_files_cannot_be_imported_please_check"),
    /**
     * 问题名称错误
     */
    GEOAI_UOS_PROBLEM_NAME_ERROR("geoai_uos_problem_name_error"),
    /**
     * 当前移动终端监控更改状态成功
     */
    GEOAI_UOS_CURRENT_MOBILE_TERMINAL_MONITORING_CHANGE_STATUS_SUCCESSFULLY("geoai_uos_current_mobile_terminal_monitoring_change_status_successfully"),
    /**
     * 查不到相关的架次数据
     */
    GEOAI_UOS_THE_RELEVANT_SORTIE_DATA_CANNOT_BE_CHECKED("geoai_uos_the_relevant_sortie_data_cannot_be_checked"),
    /**
     * 航线数据不能为null
     */
    GEOAI_UOS_ROUTE_DATA_CANNOT_BE_NULL("geoai_uos_route_data_cannot_be_null"),
    /**
     * 没有航线，任务保存失败
     */
    GEOAI_UOS_NO_ROUTE_MISSION_SAVE_FAILED("geoai_uos_no_route_mission_save_failed"),
    /**
     * 参数没有找到
     */
    GEOAI_UOS_PARAMETERS_NOT_FOUND("geoai_uos_parameters_not_found"),
    /**
     * 航线没有找到
     */
    GEOAI_UOS_ROUTE_NOT_FOUND("geoai_uos_route_not_found"),
    /**
     * 任务复制失败
     */
    GEOAI_UOS_MISSION_COPY_FAILED("geoai_uos_mission_copy_failed"),
    /**
     * 查询不到相关任务
     */
    GEOAI_UOS_THE_RELEVANT_MISSION_CANNOT_BE_QUERIED("geoai_uos_the_relevant_mission_cannot_be_queried"),
    /**
     * 航线错误
     */
    GEOAI_UOS_ROUTE_ERROR("geoai_uos_route_error"),
    /**
     * 低危航线
     */
    GEOAI_UOS_LOW_RISK_ROUTE("geoai_uos_low_risk_route"),
    /**
     * 设备列表为空
     */
    GEOAI_UOS_EQUIPMENT_LIST_IS_EMPTY("geoai_uos_equipment_list_is_empty"),
    /**
     * 保存出错
     */
    GEOAI_UOS_SAVE_ERROR("geoai_uos_save_error"),
    /**
     * 保存失败
     */
    GEOAI_UOS_FAILED_TO_SAVE("geoai_uos_failed_to_save"),
    /**
     * 构建航线失败
     */
    GEOAI_UOS_FAILED_TO_BUILD_ROUTE("geoai_uos_failed_to_build_route"),
    /**
     * 任务保存成功
     */
    GEOAI_UOS_MISSION_SAVED_SUCCESSFULLY("geoai_uos_mission_saved_successfully"),
    /**
     * 没有对应航线数据
     */
    GEOAI_UOS_NO_CORRESPONDING_ROUTE_DATA("geoai_uos_no_corresponding_route_data"),
    /**
     * 编辑出错
     */
    GEOAI_UOS_EDIT_ERROR("geoai_uos_edit_error"),
    /**
     * 终止分析
     */
    GEOAI_UOS_TERMINATION_OF_ANALYSIS("geoai_uos_termination_of_analysis"),
    /**
     * 终止分析结束
     */
    GEOAI_UOS_END_OF_TERMINATION_ANALYSIS("geoai_uos_end_of_termination_analysis"),
    /**
     * 报告
     */
    GEOAI_UOS_REPORT("geoai_uos_report"),
    /**
     * 该多架次任务已产生关联数据，不允许编辑，请复制该任务进行编辑
     */
    GEOAI_UOS_TASK_HAS_GENERATED_ASSOCIATED_DATA("geoai_uos_task_has_generated_associated_data"),
    /**
     * 保存失败，查询不到架次
     */
    GEOAI_UOS_SAVE_FAILED_NO_SORTIES_CAN_BE_SEARCHED("geoai_uos_save_failed_no_sorties_can_be_searched"),
    /**
     * 新增的SIZE个架次已存在，请检查
     */
    GEOAI_UOS_THE_NEW_SIZE_SORTIES_ALREADY_EXIST_PLEASE_CHECK("geoai_uos_the_new_size_sorties_already_exist_please_check"),
    /**
     * 当前机巢状态为：【STATUE】,不允许发送任务
     */
    GEOAI_UOS_CURRENT_STATUS_NEST_STATUE_NOT_ALLOWED_SENT("geoai_uos_current_status_nest_statue_not_allowed_sent"),
    /**
     * 推送分析结果成功
     */
    GEOAI_UOS_PUSH_ANALYSIS_RESULT_SUCCESS("geoai_uos_push_analysis_result_success"),

    //版本215
    /**
     * 执行成功
     */
    GEOAI_UOS_POWERCONTROLLER_001("geoai_uos_powercontroller_001"),
    /**
     * 当前设备数据与【单位航线台账】内的航点关联
     */
    GEOAI_UOS_POWERCONTROLLER_002("geoai_uos_powercontroller_002"),
    /**
     * 存在设备数据与【单位航线台账】内的航点关联
     */
    GEOAI_UOS_POWERCONTROLLER_003("geoai_uos_powercontroller_003"),
    /**
     * 执行失败
     */
    GEOAI_UOS_POWERCONTROLLER_004("geoai_uos_powercontroller_004"),
    /**
     * 时间格式不正确
     */
    GEOAI_UOS_POWERCONTROLLER_005("geoai_uos_powercontroller_005"),
    GEOAI_UOS_POWERCONTROLLER_006("geoai_uos_powercontroller_006"),
    /**
     * 参数异常
     */
    GEOAI_UOS_POWERCONTROLLER_007("geoai_uos_powercontroller_007"),
    /**
     * 执行失败
     */
    GEOAI_UOS_POWERCONTROLLER_008("geoai_uos_powercontroller_008"),
    /**
     * 巡检报告
     */
    GEOAI_UOS_POWERCONTROLLER_009("geoai_uos_powercontroller_009"),
    /**
     * 表计读数值不能超过10个
     */
    GEOAI_UOS_METERREADINGINFOREQVO_001("geoai_uos_meterreadinginforeqvo_001"),
    /**
     * 读数规则ID不能为空
     */
    GEOAI_UOS_METERREADINGINFOREQVO_002("geoai_uos_meterreadinginforeqvo_002"),
    /**
     * 表计读数值不符合规则
     */
    GEOAI_UOS_METERREADINGINFOREQVO_003("geoai_uos_meterreadinginforeqvo_003"),
    /**
     * 文件类型不支持
     */
    GEOAI_UOS_POWEREQUIPMENTSERVICEIMPL_001("geoai_uos_powerequipmentserviceimpl_001"),
    /**
     * 当前设备PMS_ID已存在
     */
    GEOAI_UOS_POWEREQUIPMENTSERVICEIMPL_002("geoai_uos_powerequipmentserviceimpl_002"),
    /**
     * 当前查询的设备信息不存在
     */
    GEOAI_UOS_POWEREQUIPMENTSERVICEIMPL_003("geoai_uos_powerequipmentserviceimpl_003"),
    /**
     * 当前删除的设备信息不存在
     */
    GEOAI_UOS_POWEREQUIPMENTSERVICEIMPL_004("geoai_uos_powerequipmentserviceimpl_004"),
    /**
     * 压缩包文件大小不允许大于2M
     */
    GEOAI_UOS_POWEREQUIPMENTSERVICEIMPL_005("geoai_uos_powerequipmentserviceimpl_005"),
    /**
     * 压缩包内存在重复的文件,请修正后上传
     */
    GEOAI_UOS_POWEREQUIPMENTSERVICEIMPL_006("geoai_uos_powerequipmentserviceimpl_006"),
    /**
     * 压缩包内缺少完整的文件数据,请补充后上传
     */
    GEOAI_UOS_POWEREQUIPMENTSERVICEIMPL_007("geoai_uos_powerequipmentserviceimpl_007"),
    /**
     * Json格式异常
     */
    GEOAI_UOS_POWEREQUIPMENTSERVICEIMPL_008("geoai_uos_powerequipmentserviceimpl_008"),
    /**
     * 导入的数据为空,请重新上传
     */
    GEOAI_UOS_POWEREQUIPMENTSERVICEIMPL_009("geoai_uos_powerequipmentserviceimpl_009"),
    /**
     * 该单位不存在部件数据，请在【部件库管理】内完善
     */
    GEOAI_UOS_POWEREQUIPMENTSERVICEIMPL_010("geoai_uos_powerequipmentserviceimpl_010"),
    /**
     * 该单位不存在航点台账数据，请在【航点台账管理】内完善
     */
    GEOAI_UOS_POWEREQUIPMENTSERVICEIMPL_011("geoai_uos_powerequipmentserviceimpl_011"),
    /**
     * 该单位不存在设备台账数据，请在【设备台账管理】内完善
     */
    GEOAI_UOS_POWEREQUIPMENTSERVICEIMPL_012("geoai_uos_powerequipmentserviceimpl_012"),
    /**
     * 解析失败，请将所有航线文件直接压缩后上传
     */
    GEOAI_UOS_POWEREQUIPMENTSERVICEIMPL_013("geoai_uos_powerequipmentserviceimpl_013"),
    /**
     * 当前设备PMS_ID已存在
     */
    GEOAI_UOS_POWEREQUIPMENTLEGERINFOMANAGERIMPL_001("geoai_uos_powerequipmentlegerinfomanagerimpl_001"),
    /**
     * 需要删除的设备不存在
     */
    GEOAI_UOS_POWEREQUIPMENTLEGERINFOMANAGERIMPL_002("geoai_uos_powerequipmentlegerinfomanagerimpl_002"),
    /**
     * 当前需要关联的设备台账不存在
     */
    GEOAI_UOS_POWEREQUIPMENTLEGERINFOMANAGERIMPL_003("geoai_uos_powerequipmentlegerinfomanagerimpl_003"),
    /**
     * 当前设备台账不存在
     */
    GEOAI_UOS_POWERWAYPOINTLEDGERINFOMANAGERIMPL_001("geoai_uos_powerwaypointledgerinfomanagerimpl_001"),
    /**
     * 获取算法识别功能列表失败，请稍后重试！
     */
    GEOAI_UOS_POWERDATASERVICEIMPL_001("geoai_uos_powerdataserviceimpl_001"),
    /**
     * 当前单位不存在已授权识别功能
     */
    GEOAI_UOS_POWERDATASERVICEIMPL_002("geoai_uos_powerdataserviceimpl_002"),
    /**
     * 查询单位算法识别开关状态失败，请稍后重试
     */
    GEOAI_UOS_POWERDATASERVICEIMPL_003("geoai_uos_powerdataserviceimpl_003"),
    /**
     * 更新单位算法识别开关状态失败，请稍后重试
     */
    GEOAI_UOS_POWERDATASERVICEIMPL_004("geoai_uos_powerdataserviceimpl_004"),
    /**
     * 当前单位不存在已授权识别功能
     */
    GEOAI_UOS_POWERDATASERVICEIMPL_005("geoai_uos_powerdataserviceimpl_005"),
    /**
     * 单个巡检类型最多设置10个识别功能
     */
    GEOAI_UOS_POWERDATASERVICEIMPL_006("geoai_uos_powerdataserviceimpl_006"),
    /**
     * 查询巡检类型失败，请稍后重试
     */
    GEOAI_UOS_POWERDATASERVICEIMPL_007("geoai_uos_powerdataserviceimpl_007"),
    /**
     * 当前表格数据大于1000条,请分批导入
     */
    GEOAI_UOS_POWEREQUIPMENTUPLOADLISTENER_001("geoai_uos_powerequipmentuploadlistener_001"),
    /**
     * 当前表格部分列存在空值,请修正数据后导入
     */
    GEOAI_UOS_POWEREQUIPMENTUPLOADLISTENER_002("geoai_uos_powerequipmentuploadlistener_002"),
    /**
     * 当前表格部分数据超出字数限制,请修正数据后导入
     */
    GEOAI_UOS_POWEREQUIPMENTUPLOADLISTENER_003("geoai_uos_powerequipmentuploadlistener_003"),
    /**
     * 请根据设备台账的表头规范进行导入
     */
    GEOAI_UOS_POWEREQUIPMENTUPLOADLISTENER_004("geoai_uos_powerequipmentuploadlistener_004"),
    /**
     * 存在PMS_ID重复的数据项,请修正数据后导入
     */
    GEOAI_UOS_POWEREQUIPMENTUPLOADLISTENER_005("geoai_uos_powerequipmentuploadlistener_005"),
    /**
     * 图片获取失败
     */
    GEOAI_UOS_POWERINSPECTIONSERVICEIMPL_001("geoai_uos_powerinspectionserviceimpl_001"),
    /**
     * 请先撤回照片核实状态后再操作
     */
    GEOAI_UOS_POWERINSPECTIONSERVICEIMPL_002("geoai_uos_powerinspectionserviceimpl_002"),
    /**
     * 存在核实状态为“已核实/误报”的数据，请先进行撤回
     */
    GEOAI_UOS_POWERINSPECTIONSERVICEIMPL_003("geoai_uos_powerinspectionserviceimpl_003"),

    /**
     * 存在设备状态为未处理的数据，不可进行核实
     */
    GEOAI_UOS_POWERINSPECTIONSERVICEIMPL_004("geoai_uos_powerinspectionserviceimpl_004"),
    /**
     * 不可重复核实，请重新选择
     */
    GEOAI_UOS_POWERINSPECTIONSERVICEIMPL_005("geoai_uos_powerinspectionserviceimpl_005"),
    GEOAI_UOS_POWERINSPECTIONSERVICEIMPL_006("geoai_uos_powerinspectionserviceimpl_006"),
    // -------------------------飞行检查-------------------------
    GEOAI_CHECK_NEST_OFFLINE("geoai_check_nest_offline"),
    GEOAI_CHECK_NEST_ABLE_TO_PERFORM_TASKS("geoai_check_nest_able_to_perform_tasks"),
    GEOAI_CHECK_NEST_UNABLE_TO_PERFORM_TASKS("geoai_check_nest_unable_to_perform_tasks"),
    GEOAI_CHECK_BATTERY_ERROR("geoai_check_battery_error"),
    GEOAI_CHECK_BATTERY_MISSING("geoai_check_battery_missing"),
    BATTERY_INUSE_CAPCITY("battery_inuse_capcity"),
    UNABLE_TO_GET_BATTERY_READY_TO_USE("unable_to_get_battery_to_use"),
    BATTERY_READY_TO_USE_NORMAL("battery_to_use_normal"),
    // -------------------------G900电池-------------------------
    GEOAI_CHECK_BATTERY_G900_RECYCLE_TIMES_100("geoai_check_battery_g900_recycle_times_100"),
    GEOAI_CHECK_BATTERY_G900_RECYCLE_TIMES_150("geoai_check_battery_g900_recycle_times_150"),
    GEOAI_CHECK_BATTERY_G900_CAPCITY_LOWER_50("geoai_check_battery_g900_capcity_lower_50"),
    GEOAI_CHECK_BATTERY_G900_CAPCITY_LOWER_75("geoai_check_battery_g900_capcity_lower_75"),

    // -------------------------G900充电式电池-------------------------
    GEOAI_CHECK_BATTERY_G900C_RECYCLE_TIMES("geoai_check_battery_g900c_recycle_times"),
    GEOAI_CHECK_BATTERY_G900C_PERCENTAGE("geoai_check_battery_g900c_percentage"),
    GEOAI_CHECK_BATTERY_G900C_CAPCITY_LOWER_20("geoai_check_battery_g900c_capcity_lower_20"),
    GEOAI_CHECK_BATTERY_G900C_CAPCITY_LOWER_50("geoai_check_battery_g900c_capcity_lower_50"),
    // -------------------------气象检查-------------------------
    AEROGRAPHY_CHECK_NORMAL("aerography_check_normal"),
    AEROGRAPHY_CHECK_ERROR("aerography_check_error"),
    AEROGRAPHY_CHECK_NO_DATA("aerography_check_no_data"),
    AEROGRAPHY_CHECK_ABNORMAL("aerography_check_abnormal"),
    AEROGRAPHY_CHECK_RAINING("aerography_check_raining"),

    //-------------------------任务架次-------------------------
    TASK_MISSION_FLIGHT("task_mission_flight"),
    TASK_MISSION_AIRLINE("task_mission_airline"),
    // -------------------------导出列表-------------------------
    GEOAI_UOS_EXPORT_INDUSTRY("geoai_uos_export_industry"),
    GEOAI_UOS_EXPORT_QUESTION("geoai_uos_export_question"),
    GEOAI_UOS_EXPORT_NUMBER_OF_DISCOVERIES("geoai_uos_export_number_of_discoveries"),
    GEOAI_UOS_EXPORT_FIRST_SHOT("geoai_uos_export_first_shot"),
    GEOAI_UOS_EXPORT_LATEST_SHOT("geoai_uos_export_latest_shot"),
    GEOAI_UOS_EXPORT_ADDRESS("geoai_uos_export_address"),
    GEOAI_UOS_EXPORT_LOCATION("geoai_uos_export_location"),
    GEOAI_UOS_EXPORT_LONGITUDE_AND_LATITUDE("geoai_uos_export_longitude_and_latitude"),
    GEOAI_UOS_EXPORT_MAP("geoai_uos_export_map"),
    GEOAI_UOS_EXPORT_TAG("geoai_uos_export_tag"),
    GEOAI_UOS_EXPORT_TASK("geoai_uos_export_task"),
    GEOAI_UOS_EXPORT_ORG("geoai_uos_export_org"),
    GEOAI_UOS_EXPORT_IMAGE("geoai_uos_export_image"),
    GEOAI_UOS_MISSIONSERVICEIMPL_MISSION("geoai_uos_missionserviceimpl_mission"),

    GEOAI_UOS_FILEREPORTSERVICEIMPL_01("geoai_uos_FileReportServiceImpl_01"),
    GEOAI_UOS_DOWNLOADING_THE_COMPRESSED_PACKAGE_FAILED("geoai_uos_Downloading_the_compressed_package_failed"),
    GEOAI_UOS_FAILED_TO_GENERATE_THUMBNAIL("geoai_uos_Failed_to_generate_thumbnail"),
    GEOAI_UOS_THE_FILE_SIZE_EXCEEDS_5_GB("geoai_uos_The_file_size_exceeds_5_GB"),

    /**
     * 大疆机场
     */
    GEOAI_UOS_DJI_DOCK_OFFLINE("geoai_uos_dji_dock_offline"),


    GEOAI_UOS_DJI_LIVE_QUALITY_ERR("geoai_uos_dji_live_quality_err"),

    GEOAI_UOS_DJI_QUERY_DOCK_STREAM_ERR("geoai_uos_dji_query_dock_stream_err"),

    GEOAI_UOS_DJI_QUERY_AIRCRAFT_STREAM_ERR("geoai_uos_dji_query_aircraft_stream_err"),

    GEOAI_UOS_DJI_COMMON_ACTION_ERR("geoai_uos_dji_common_action_err"),

    GEOAI_UOS_DJI_PARSE_ERR("geoai_uos_dji_parse_err"),
    GEOAI_UOS_NEST_MEDIA_STREAM_NOT_FOUND("geoai_uos_nest_media_stream_not_found"),
    GEOAI_UOS_NEST_MEDIA_SERVICE_NOT_FOUND("geoai_uos_nest_media_service_not_found"),
    GEOAI_UOS_NEST_MEDIA_SERVICE_ERROR_TOKEN("geoai_uos_nest_media_service_error_token"),
    GEOAI_UOS_NEST_MEDIA_SERVICE_NOT_MATCH("geoai_uos_nest_media_service_not_match"),
    GEOAI_UOS_NEST_MEDIA_PUSH_NOT_SETUP("geoai_uos_nest_media_push_not_setup"),
    GEOAI_UOS_NEST_MEDIA_STREAM_OFFLINE("geoai_uos_nest_media_stream_offline"),
    GEOAI_UOS_NEST_MEDIA_STREAM_DEL_ERR("geoai_uos_nest_media_stream_del_err"),
    // 电力
    GEOAI_UOS_POWER_COMPONENT_PARAM_001("geoai_uos_power_component_param_001"),
    GEOAI_UOS_POWER_COMPONENT_PARAM_002("geoai_uos_power_component_param_002"),
    GEOAI_UOS_POWER_COMPONENT_PARAM_003("geoai_uos_power_component_param_003"),
    GEOAI_UOS_POWER_COMPONENT_PARAM_004("geoai_uos_power_component_param_004"),
    GEOAI_UOS_POWER_COMPONENT_PARAM_005("geoai_uos_power_component_param_005"),
    GEOAI_UOS_POWER_COMPONENT_PARAM_006("geoai_uos_power_component_param_006"),
    GEOAI_UOS_POWER_COMPONENT_PARAM_007("geoai_uos_power_component_param_007"),
    GEOAI_UOS_POWER_COMPONENT_PARAM_008("geoai_uos_power_component_param_008"),
    GEOAI_UOS_POWER_COMPONENT_PARAM_009("geoai_uos_power_component_param_009"),
    GEOAI_UOS_POWER_COMPONENT_PARAM_010("geoai_uos_power_component_param_010"),
    GEOAI_UOS_POWER_COMPONENT_PARAM_011("geoai_uos_power_component_param_011"),
    GEOAI_UOS_POWER_COMPONENT_PARAM_012("geoai_uos_power_component_param_012"),

    GEOAI_UOS_POWER_COMPONENT_BUSINESS_001("geoai_uos_power_component_business_001"),
    GEOAI_UOS_POWER_COMPONENT_BUSINESS_002("geoai_uos_power_component_business_002"),

    GEOAI_UOS_METER_READING_DETAIL_ID_CANNOT_BE_BLANK("geoai_uos_meter_reading_detail_id_cannot_be_blank"),
    GEOAI_UOS_METER_READING_DETAIL_INFO_NOT_EXISTS("geoai_uos_meter_reading_detail_info_not_exists"),
    GEOAI_UOS_UNABLE_TO_OPERATE_THE_PHOTO_DATA_IN_AI_RECOGNITION("geoai_uos_unable_to_operate_the_photo_data_in_ai_recognition"),

    GEOAI_UOS_UNABLE_TO_OPERATE_THE_PHOTO_DATA_IN_VERIFICATION("geoai_uos_unable_to_operate_the_photo_data_in_verification"),
    GEOAI_UOS_UNABLE_TO_OPERATE_PHOTO_DATA_WITHOUT_ASSOCIATED_PART_INFO("geoai_uos_unable_to_operate_photo_data_without_associated_part_info"),
    GEOAI_UOS_UNABLE_TO_OPERATE_PHOTO_DATA_WITHOUT_READING_RULES("geoai_uos_unable_to_operate_photo_data_without_reading_rules"),
    GEOAI_UOS_METER_READ_VALUE_CANNOT_BE_EMPTY("geoai_uos_read_value_cannot_be_empty"),
    GEOAI_UOS_METER_READ_VALUE_IS_INCOMPLETE("geoai_uos_meter_read_value_is_incomplete"),

    // v2.1.4
    GEOAI_UOS_FAILED_TO_GET_ALGORITHM_FUNCTIONS("geoai_uos_failed_to_get_algorithm_functions"),
    GEOAI_UOS_SYSTEM_AI_ANALYSIS_TASK_EXCEED_LIMIT("geoai_uos_system_ai_analysis_task_exceed_limit"),
    GEOAI_UOS_ACCOUNT_AI_ANALYSIS_TASK_EXCEED_LIMIT("geoai_uos_account_ai_analysis_task_exceed_limit"),
    GEOAI_UOS_ORG_AI_ANALYSIS_TASK_EXCEED_LIMIT("geoai_uos_org_ai_analysis_task_exceed_limit"),
    GEOAI_UOS_FLIGHT_SORTIES_RESULT_DATA_NOT_EXIST("geoai_uos_flight_sorties_result_data_not_exist"),
    GEOAI_UOS_MULTIPLE_FLIGHT_PHOTO_NOT_BELONG_TO_SAME_MISSION("geoai_uos_multiple_flight_photo_not_belong_to_same_mission"),
    GEOAI_UOS_NO_VALID_FLIGHT_PHOTO_WAS_FOUND("geoai_uos_no_valid_flight_photo_was_found"),
    GEOAI_UOS_AI_ANALYSIS_NOT_CONFIGURE_PHOTO_INTRANET_ACCESS_ADDRESS("geoai_uos_ai_analysis_not_configure_photo_intranet_access_address"),
    GEOAI_UOS_FAILED_TO_CREATE_AI_ANALYSIS_TASK("geoai_uos_failed_to_create_ai_analysis_task"),
    GEOAI_UOS_AI_ANALYSIS_TASK_NOT_EXIST("geoai_uos_ai_analysis_task_not_exist"),
    GEOAI_UOS_SUSPEND_TASK_OPERATION_IS_NOT_ALLOWED("geoai_uos_suspend_task_operation_is_not_allowed"),
    GEOAI_UOS_RESUME_TASK_OPERATION_IS_NOT_ALLOWED("geoai_uos_resume_task_operation_is_not_allowed"),
    GEOAI_UOS_TERMINATE_TASK_OPERATION_IS_NOT_ALLOWED("geoai_uos_terminate_task_operation_is_not_allowed"),
    GEOAI_UOS_FAILED_TO_SUSPEND_AI_ANALYSIS_TASK("geoai_uos_failed_to_suspend_ai_analysis_task"),
    GEOAI_UOS_FAILED_TO_RESUME_AI_ANALYSIS_TASK("geoai_uos_failed_to_resume_ai_analysis_task"),
    GEOAI_UOS_FAILED_TO_TERMINATE_AI_ANALYSIS_TASK("geoai_uos_failed_to_terminate_ai_analysis_task"),
    GEOAI_UOS_AI_ANALYSIS_TASK_DATA_CONSISTENCY_EXCEPTION("geoai_uos_ai_analysis_task_data_consistency_exception"),
    GEOAI_UOS_AI_ANALYSIS_TASK_PAUSE_EXCEPTION("geoai_uos_ai_analysis_task_pause_exception"),
    GEOAI_UOS_AI_ANALYSIS_TASK_RESUME_EXCEPTION("geoai_uos_ai_analysis_task_resume_exception"),
    GEOAI_UOS_AI_ANALYSIS_TASK_TERMINATE_EXCEPTION("geoai_uos_ai_analysis_task_terminate_exception"),
    GEOAI_UOS_AI_ANALYSIS_TASK_TYPE_ERROR("geoai_uos_ai_analysis_task_type_error"),

    GEOAI_UOS_ORG_ALGORITHM_FUNCTION_UNSET("geoai_uos_org_algorithm_function_unset"),
    GEOAI_UOS_ORG_ALGORITHM_UNAUTHORIZED("geoai_uos_org_algorithm_unauthorized"),
    GEOAI_UOS_ORG_ALGORITHM_FUNCTION_UNMET("geoai_uos_org_algorithm_function_unmet"),
    GEOAI_UOS_ALGORITHM_SERVICE_CALL_EXCEPTION("geoai_uos_algorithm_service_call_exception"),

    GEOAI_UOS_REPEAT_FLIGHT_PHOTO("geoai_uos_repeat_flight_photo"),
    GEOAI_UOS_NOT_LEGAL_FLIGHT_PHOTO("geoai_uos_not_legal_flight_photo"),

    /**
     * 2.1.6网格化
     */
    GEOAI_UOS_GRID_SERVICE_01("geoai_uos_grid_service_01"),
    GEOAI_UOS_GRID_SERVICE_02("geoai_uos_grid_service_02"),
    GEOAI_UOS_GRID_SERVICE_03("geoai_uos_grid_service_03"),
    GEOAI_UOS_GRID_SERVICE_04("geoai_uos_grid_service_04"),
    GEOAI_UOS_GRID_SERVICE_05("geoai_uos_grid_service_05"),
    GEOAI_UOS_GRID_SERVICE_06("geoai_uos_grid_service_06"),
    GEOAI_UOS_GRID_SERVICE_07("geoai_uos_grid_service_07"),
    GEOAI_UOS_GRID_SERVICE_08("geoai_uos_grid_service_08"),
    GEOAI_UOS_GRID_SERVICE_09("geoai_uos_grid_service_09"),

    // v2.1.7
    GEOAI_UOS_OBTAIN_VIDEO_REC_FUNCTION_FAILED("geoai_uos_obtain_video_rec_function_failed"),
    GEOAI_UOS_HAS_ENABLED_VIDEO_AI_RECOGNITION("geoai_uos_has_enabled_video_ai_recognition"),
    GEOAI_UOS_OPEN_VIDEO_AI_RECOGNITION_ERROR("geoai_uos_open_video_ai_recognition_error"),
    GEOAI_UOS_EXIT_VIDEO_AI_RECOGNITION_ERROR("geoai_uos_exit_video_ai_recognition_error"),
    GEOAI_UOS_VIDEO_AI_REC_FUNCTION_UNAUTHORIZED("geoai_uos_video_ai_rec_function_unauthorized"),
    GEOAI_UOS_VIDEO_AI_SYS_CHANNEL_EXCEED("geoai_uos_video_ai_sys_channel_exceed"),
    GEOAI_UOS_VIDEO_AI_ORG_CHANNEL_EXCEED("geoai_uos_video_ai_org_channel_exceed"),
    GEOAI_UOS_VIDEO_AI_SERVICE_ERROR("geoai_uos_video_ai_service_error"),
    GEOAI_UOS_OPEN_VIDEO_AI_RECOGNITION_FAILED("geoai_uos_open_video_ai_recognition_failed"),
    GEOAI_UOS_EXIT_VIDEO_AI_RECOGNITION_FAILED("geoai_uos_exit_video_ai_recognition_failed"),
    GEOAI_UOS_NO_UAV_INFORMATION_IS_FOUND("geoai_uos_no_uav_information_is_found"),
    GEOAI_UOS_UNMATCHED_UAV_INFORMATION("geoai_uos_unmatched_uav_information"),
    GEOAI_UOS_NOT_CONFIGURED_WITH_UAV_STREAMING_INFO("geoai_uos_not_configured_with_uav_streaming_info"),
    GEOAI_UOS_NOT_ENABLED_VIDEO_AI_RECOGNITION("geoai_uos_not_enabled_video_ai_recognition"),
    GEOAI_UOS_CHECK_VIDEO_AI_RECOGNITION_ERROR("geoai_uos_check_video_ai_recognition_error"),
    GEOAI_UOS_CHECK_VIDEO_AI_RECOGNITION_FAILED("geoai_uos_check_video_ai_recognition_failed"),

    GEOAI_UOS_POWER_PICTURE_TASK_REPEAT_ADD("geoai_uos_power_picture_task_repeat_add"),


    // v2.2.4 南海任务工单
    GEOAI_UOS_NHWORKORDERSERVICEIMPL_001("geoai_uos_nhworkorderserviceimpl_001"),
    GEOAI_UOS_NHWORKORDERSERVICEIMPL_002("geoai_uos_nhworkorderserviceimpl_002"),
    GEOAI_UOS_NHWORKORDERSERVICEIMPL_003("geoai_uos_nhworkorderserviceimpl_003"),
    GEOAI_UOS_NHWORKORDERSERVICEIMPL_004("geoai_uos_nhworkorderserviceimpl_004"),
    GEOAI_UOS_NHWORKORDERSERVICEIMPL_005("geoai_uos_nhworkorderserviceimpl_005"),


    // 空域管理
    GEOAI_UOS_AIRSPACEMANAGESERVICEIMPL_01("geoai_uos_AirspaceManageServiceImpl_01"),
    GEOAI_UOS_AIRSPACEMANAGESERVICEIMPL_02("geoai_uos_AirspaceManageServiceImpl_02"),
    GEOAI_UOS_AIRSPACEMANAGESERVICEIMPL_03("geoai_uos_AirspaceManageServiceImpl_03"),
    GEOAI_UOS_AIRSPACEMANAGESERVICEIMPL_04("geoai_uos_AirspaceManageServiceImpl_04"),


    // AI参数校验
    GEOAI_UOS_POWER_PARAM_PHOTO_NOT_EMPTY("geoai_uos_power_param_photo_not_empty"),
    GEOAI_UOS_POWER_PARAM_PHOTO_MAX_200("geoai_uos_power_param_photo_max_200"),

    // 飞行前检查
    GEOAI_UOS_BEFORESTARTCHECKSERVICE_G600("geoai_uos_BeforeStartCheckService_g600"),
    GEOAI_UOS_BEFORESTARTCHECKSERVICE_G900("geoai_uos_BeforeStartCheckService_g900"),
    GEOAI_UOS_BEFORESTARTCHECKSERVICE_G900_CHARGE("geoai_uos_BeforeStartCheckService_g900_charge"),

    GEOAI_UOS_BEFORESTARTCHECKSERVICE_OTHER_NONE("geoai_uos_beforestartcheckservice_other_none"),
    GEOAI_UOS_BEFORESTARTCHECKSERVICE_OTHER("geoai_uos_beforestartcheckservice_other"),

    CPS_MOTOR_LANDING_GUIDANCE_DOWN_FAILED("cps_motor_landing_guidance_down_failed"),
    //平台优化版本
    GEOAI_UOS_CPSACCESSORYMANAGERIMPL_001("geoai_uos_cpsaccessorymanagerimpl_001"),
    GEOAI_UOS_CPSACCESSORYMANAGERIMPL_002("geoai_uos_cpsaccessorymanagerimpl_002"),
    GEOAI_UOS_CPSACCESSORYMANAGERIMPL_003("geoai_uos_cpsaccessorymanagerimpl_003"),
    GEOAI_UOS_CAMERAMANAGERIMPL_001("geoai_uos_cameramanagerimpl_001"),
    GEOAI_UOS_CAMERAMANAGERIMPL_002("geoai_uos_cameramanagerimpl_002"),
    GEOAI_UOS_CAMERAMANAGERIMPL_003("geoai_uos_cameramanagerimpl_003"),
    GEOAI_UOS_CAMERAMANAGERIMPL_004("geoai_uos_cameramanagerimpl_004"),
    GEOAI_UOS_CAMERAMANAGERIMPL_005("geoai_uos_cameramanagerimpl_005"),
    GEOAI_UOS_CAMERAMANAGERIMPL_006("geoai_uos_cameramanagerimpl_006"),
    GEOAI_UOS_CAMERAMANAGERIMPL_007("geoai_uos_cameramanagerimpl_007"),
    GEOAI_UOS_CPSMISSIONSERVICEIMPL_001("geoai_uos_cpsmissionserviceimpl_001"),
    GEOAI_UOS_CPSMISSIONSERVICEIMPL_002("geoai_uos_cpsmissionserviceimpl_002"),
    GEOAI_UOS_CPSMISSIONSERVICEIMPL_003("geoai_uos_cpsmissionserviceimpl_003"),
    GEOAI_UOS_CPSMISSIONSERVICEIMPL_004("geoai_uos_cpsmissionserviceimpl_004"),
    GEOAI_UOS_CPSMISSIONSERVICEIMPL_005("geoai_uos_cpsmissionserviceimpl_005"),
    GEOAI_UOS_CPSMISSIONMANAGERIMPL_001("geoai_uos_cpsmissionmanagerimpl_001"),
    GEOAI_UOS_CPSMISSIONMANAGERIMPL_002("geoai_uos_cpsmissionmanagerimpl_002"),
    GEOAI_UOS_GRID_PERMISSION_01("geoai_uos_grid_permission_01"),
    GEOAI_UOS_GRID_PERMISSION_02("geoai_uos_grid_permission_02"),
    GEOAI_UOS_GRID_PERMISSION_03("geoai_uos_grid_permission_03"),
    ;

    private String content;
}
