package com.imapcloud.nest.v2.web.vo.resp;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.converters.inputstream.InputStreamImageConverter;
import lombok.Data;

import java.io.InputStream;
import java.io.Serializable;

@Data
public class InspectionExportRespVO implements Serializable {

    @ExcelProperty(value = "分析截图", index = 0/*, converter = InputStreamImageConverter.class*/)
    private byte[] meterImg;

    @ExcelProperty(value = "巡检照片", index = 1/*, converter = InputStreamImageConverter.class*/)
    private byte[] inspectionImg;

    @ExcelProperty(value = "部件名称", index = 2)
    private String componenName;

    @ExcelProperty(value = "设备名称", index = 3)
    private String equipmentName;

    @ExcelProperty(value = "分析类型", index = 4)
    private String inspectionType;

    @ExcelProperty(value = "分析结果", index = 5)
    private String inspectionResult;

    @ExcelProperty(value = "分析结论", index = 6)
    private String inspectionConclusion;

    @ExcelProperty(value = "设备类型", index = 7)
    private String equipmentType;

    @ExcelProperty(value = "间隔单元", index = 8)
    private String spacUnit;

    @ExcelProperty(value = "电压等级", index = 9)
    private String voltageLevel;

    @ExcelProperty(value = "拍摄时间", index = 10)
    private String photoGraphyTime;
}
