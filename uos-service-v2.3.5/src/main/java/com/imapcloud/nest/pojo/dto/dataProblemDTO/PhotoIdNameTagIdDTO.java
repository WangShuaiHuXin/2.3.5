package com.imapcloud.nest.pojo.dto.dataProblemDTO;

import lombok.Data;

/**
 * 下载、删除、重置、AI识别时获取照片id、名称、标签id使用
 *
 * @author: zhengxd
 * @create: 2021/9/9
 **/
@Data
public class PhotoIdNameTagIdDTO {
    private Long photoId;
    private String photoName;
    private Integer tagId;

}
