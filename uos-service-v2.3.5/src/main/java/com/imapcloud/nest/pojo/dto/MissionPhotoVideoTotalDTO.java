package com.imapcloud.nest.pojo.dto;

import lombok.Data;

import java.util.List;

/**
 * 首页-获取用户每个标签对应的图片、视频
 *
 * @author: zhengxd
 * @create: 2021/1/19
 **/
@Data
public class MissionPhotoVideoTotalDTO {

    private List<PhotoTagBean> photoList;
    private List<VideoTagBean> videoList;
    private List<PhotoNestPageBean> photoNestPageList;


    @Data
    public static class PhotoTagBean {
        private Integer photoCount;
        private String tagId;
        private String tagName;
    }

    @Data
    public static class VideoTagBean {
        private Integer videoCount;
        private String tagId;
        private String tagName;
    }

    @Data
    public static class PhotoNestPageBean {
        private Long photoId;
        private String photoThumbnail;
        private String photoUrl;
    }

    @Data
    public static class VideoNestPageBean {
        private Long videoId;
        private String videoThumbnail;
        private String videoUrl;
    }
}
