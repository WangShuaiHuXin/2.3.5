package com.imapcloud.nest.pojo.dto;

import java.util.List;
import java.util.Map;

public class PhotoPos {

        private String pid;

        private String pos;

        private List<Map> photoList;

        public String getPid() {
            return pid;
        }

        public void setPid(String pid) {
            this.pid = pid;
        }

        public String getPos() {
            return pos;
        }

        public void setPos(String pos) {
            this.pos = pos;
        }

        public List<Map> getPhotoList() {
            return photoList;
        }

        public void setPhotoList(List<Map> photoList) {
            this.photoList = photoList;
        }


    }