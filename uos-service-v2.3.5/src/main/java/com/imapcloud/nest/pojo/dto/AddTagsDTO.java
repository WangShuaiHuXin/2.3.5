package com.imapcloud.nest.pojo.dto;

import java.util.List;
import java.util.Map;

/**
 * 全景增加标签的传参
 */
public class AddTagsDTO {

    private Integer panoId;

    private List<TagEntity> tagList;

    public Integer getPanoId() {
        return panoId;
    }

    public void setPanoId(Integer panoId) {
        this.panoId = panoId;
    }

    public List<TagEntity> getTagList() {
        return tagList;
    }

    public void setTagList(List<TagEntity> tagList) {
        this.tagList = tagList;
    }

    public static class TagEntity {
        private String tagName;
        private Map attrStr;

        public String getTagName() {
            return tagName;
        }

        public void setTagName(String tagName) {
            this.tagName = tagName;
        }

        public Map getAttrStr() {
            return attrStr;
        }

        public void setAttrStr(Map attrStr) {
            this.attrStr = attrStr;
        }
    }
}
