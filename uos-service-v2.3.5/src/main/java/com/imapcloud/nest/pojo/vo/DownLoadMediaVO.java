package com.imapcloud.nest.pojo.vo;

import com.imapcloud.nest.v2.manager.dataobj.FileUrlMappingDO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName DownLoadMediaVO.java
 * @Description TODO
 * @createTime 2022年03月14日 14:40:00
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DownLoadMediaVO {
    /**
     * minIo文件名列表
     */
    @Deprecated
    private List<String> minIoNameList;
    /**
     * nms文件名列表
     */
    @Deprecated
    private List<String> nmsNameList;
    /**
     * 高清完整路径
     */
    @Deprecated
    private String originPath;
    /**
     * 录频路径
     */
    @Deprecated
    private String recordPath;

    /**
     * 原视频文件
     */
    private List<FileUrlMappingDO> originVideos;

    /**
     * 录屏文件
     */
    private List<FileUrlMappingDO> recordVideos;

}
