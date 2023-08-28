package com.imapcloud.nest.v2.manager.dataobj.in;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 文件标签设置信息
 * @author Vastfy
 * @date 2023/03/13 11:12
 * @since 1.0.0
 */
@Data
public class FileTagSettingInDO implements Serializable {

    /**
     * 文件地址
     */
    private String filePath;

    /**
     * 文件标签列表
     */
    private List<FileTag> fileTags;

    @Data
    public static class FileTag {
        private String key;
        private String value;
    }

}
