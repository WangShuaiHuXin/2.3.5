package com.imapcloud.nest.pojo.dto;

import java.util.List;


public class CheckPointJson {
    private String SUBSTATIONGUID;
    private Double OFFX;
    private Double OFFY;
    private Double ZONE;
    private String HEMISPHERE;
    private List<CheckpointDTO> ROUTELIST;

    public String getSUBSTATIONGUID() {
        return SUBSTATIONGUID;
    }

    public void setSUBSTATIONGUID(String SUBSTATIONGUID) {
        this.SUBSTATIONGUID = SUBSTATIONGUID;
    }

    public Double getOFFX() {
        return OFFX;
    }

    public void setOFFX(Double OFFX) {
        this.OFFX = OFFX;
    }

    public Double getOFFY() {
        return OFFY;
    }

    public void setOFFY(Double OFFY) {
        this.OFFY = OFFY;
    }

    public Double getZONE() {
        return ZONE;
    }

    public void setZONE(Double ZONE) {
        this.ZONE = ZONE;
    }

    public String getHEMISPHERE() {
        return HEMISPHERE;
    }

    public void setHEMISPHERE(String HEMISPHERE) {
        this.HEMISPHERE = HEMISPHERE;
    }

    public List<CheckpointDTO> getROUTELIST() {
        return ROUTELIST;
    }

    public void setROUTELIST(List<CheckpointDTO> ROUTELIST) {
        this.ROUTELIST = ROUTELIST;
    }
}
