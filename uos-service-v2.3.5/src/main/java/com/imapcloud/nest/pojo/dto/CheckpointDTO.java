package com.imapcloud.nest.pojo.dto;

import java.util.List;

/**
 * 解析拍照点dto
 */
public class CheckpointDTO {

    /**
     * 航点编号
     */
    private String id;
    /**
     * 所属设备唯一码
     */
    private String pid;
    /**
     * 航点大地坐纵
     */
    private String pos;
    /**
     * 航点经纬度及高程
     */
    private String geopos;
    /**
     * 航点唯一码
     */
    private String guid_id;
    /**
     * 设备别名
     */
    private String byname;
    /**
     * 航点类型：0-拍照点，1-辅助点，2-无动作点
     */
    private Integer style;
    /**
     * 拍照参数
     */
    private ShootPara shootparas;
    /**
     * 拍照属性列表：【0-缺陷识别，1-表计读数，2-红外测温】
     */
    private List<Integer> photoPropList;


//	private Integer space_attr;

    private class ShootPara{
        /**
         * 拍照距离
         */
        String phodist;
        /**
         * 机头方向分量
         */
        String vec;
        /**
         * 飞行模式（机头方向、云台角度、横滚角）
         */
        String flightmode;
        /**
         * 拍摄对齐点大地坐标
         */
        String alginpoint;
        /**
         * 拍摄对齐点金纬度及高程
         */
        String alginGeoPos;
    }


    public Integer getStyle() {
        return style;
    }

    public void setStyle(Integer style) {
        this.style = style;
    }

    //	public Integer getSpace_attr() {
//		return space_attr;
//	}
//	public void setSpace_attr(Integer space_attr) {
//		this.space_attr = space_attr;
//	}
    public String getGuid_id() {
        return guid_id;
    }

    public void setGuid_id(String guid_id) {
        this.guid_id = guid_id;
    }

    public String getGeopos() {
        return geopos;
    }

    public void setGeopos(String geopos) {
        this.geopos = geopos;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public String getByname() {
        return byname;
    }

    public void setByname(String byname) {
        this.byname = byname;
    }

    public ShootPara getShootparas() {
        return shootparas;
    }

    public void setShootparas(ShootPara shootparas) {
        this.shootparas = shootparas;
    }

    public List<Integer> getPhotoPropList() {
        return photoPropList;
    }

    public void setPhotoPropList(List<Integer> photoPropList) {
        this.photoPropList = photoPropList;
    }
}
