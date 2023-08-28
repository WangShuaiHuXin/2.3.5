package com.imapcloud.nest.pojo.dto.dataProblemDTO;

import lombok.Data;

/**
 * 分析中台：照片-标签-问题类型DTO
 *
 * @author: zhengxd
 * @create: 2021/6/16
 **/
@Data
public class PhotoTagSourceDTO {
    /**
     * 标签id
     */
    private Integer tagId;
    /**
     * 问题来源（0-缺陷识别；1-表记读数；2-红外测温；3-排污检测；4-河道巡查；5-管道巡查；6-水库巡查；7-城市巡查；
     * 8-违建识别；9-非法摆摊；10-公安巡查；11-违法取证；12-治安巡逻；13-应急巡查；14-事故勘察；15-应急指挥；
     * 16-国土改造；17-违章识别；18-国土取证；19-环保巡查；20-气体监测；21-排污取证；22-交通巡查；23-故事取证）
     */
    private Integer problemSource;
    /**
     * 照片id
     */
    private Long photoId;
    /**
     * 任务id
     */
    private Integer taskId;
    /**
     * 照片名称
     */
    private String photoName;
    /**
     * 数据问题id
     */
    private Integer problemId;
    /**
     * 是否有问题（0-没问题；1-有问题）
     */
    private Integer flag;


}
