package com.imapcloud.nest.v2.service.dto.out;

import lombok.Data;

/**
 * 表计详情照片信息
 *
 * @author Vastfy
 * @date 2022/12/7 13:35
 * @since 2.1.5
 */
@Data
public class MeterDetailPhotoOutDTO {
    private String dataId;

    private String detailId;

    private Long photoId;

}
