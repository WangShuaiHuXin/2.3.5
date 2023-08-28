package com.imapcloud.nest.common.constant;

/**
 * 架次相关常量
 *
 * @author: zhengxd
 * @create: 2020/8/13
 **/
public interface MissionConstant {
    /**
     * 架次记录的执行状态
     */
    interface MissionExecStatus {
        Integer NOT_BEGIN = 0;
        Integer RUNNING = 1;
        Integer COMPLETE = 2;
        Integer ERROR = 3;
    }

    /**
     * 图片数据传输状态
     * 0-未同步，1-同步到机巢，2-同步到平台，3-同步到机巢异常，4-同步到平台异常，5-停止同步
     */
    interface MissionExecDataStatus {
        Integer NOT_GAIN = 0;
        Integer GAIN_TO_CPS = 1;
        Integer GAIN_TO_SERVER = 2;
        Integer CPS_ERROR = 3;
        Integer SERVER_ERROR = 4;
        Integer STOP_GAIN = 5;
    }

    /**
     * 架次记录的飞机备份到机巢的状态
     * 0-暂不保存，1-保存到机巢，2-保存到服务器
     */
    interface MissionExecGainDataMode {
        Integer NOT_BACK_UP = 0;
        Integer BACK_UP = 1;
        Integer BACK_UP_UPLOAD = 2;
    }

    /**
     * 是否录屏
     * 0-否，1-是
     */
    interface MissionExecGainVideo {
        Integer NOT_RECORD = 0;
        Integer RECORD = 1;
    }

    /**
     * 视频类型
     * 1-录像的视频，2-同步的原视频，3-人工上传，4-超广角，5-变焦，6-红外，7-图传
     */
    interface MissionVideoType {
        Integer RECORD_VIDEO = 1;
        Integer DOWNLOAD_VIDEO = 2;
        Integer UPLOAD_VIDEO = 3;
        Integer WIDE_VIDEO = 4;
        Integer ZOOM_VIDEO = 5;
        Integer THRM_VIDEO = 6;
        Integer SCRN_VIDEO = 7;
    }

    /**
     * 录屏是否结束
     * 0-未结束， 1-已结束
     */
    interface MissionVideoRecordStatus {
        Integer NO_FINISH_RECORD = 0;
        Integer FINISH_RECORD = 1;
    }

    /**
     * 图片类型（0:缺陷,1:表计,2:红外,3:交通,4:河道,5:违建,6:定点取证）
     */
    interface PhotoType {
        Integer DEFECT = 0;
        Integer METER = 1;
        Integer INFRARED = 2;
    }


    interface MissionMedia {
        Integer JPEG = 0;
        Integer RAW_DNG = 1;
        Integer MOV = 2;
        Integer MP4 = 3;
        Integer UNKNOWN = 255;
    }

}
