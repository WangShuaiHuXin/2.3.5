package com.imapcloud.nest.pojo.dto;


import java.util.List;

/**
 * 全景删除标签的传参
 */
public class DeleteTagDTO {
    private Integer panoId;
    private List<Tag> tagList;

    public Integer getPanoId() {
        return panoId;
    }

    public void setPanoId(Integer panoId) {
        this.panoId = panoId;
    }

    public List<Tag> getTagList() {
        return tagList;
    }

    public void setTagList(List<Tag> tagList) {
        this.tagList = tagList;
    }

    public static class Tag {
        private String tagName;
        private String attr;
        private String value;

        public String getTagName() {
            return tagName;
        }

        public void setTagName(String tagName) {
            this.tagName = tagName;
        }

        public String getAttr() {
            return attr;
        }

        public void setAttr(String attr) {
            this.attr = attr;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
