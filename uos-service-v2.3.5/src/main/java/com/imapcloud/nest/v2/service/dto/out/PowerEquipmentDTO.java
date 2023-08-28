package com.imapcloud.nest.v2.service.dto.out;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class PowerEquipmentDTO {



    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PowerEquipmentUploadDto {
        private String resultString;
        private Boolean flag;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PowerEquipmentFileDTO {

        @ExcelProperty(value = "设备名称",order = 1)
        public String equipmentName;

        @ExcelProperty(value = "设备类型",order = 2)
        public String equipmentType;

        @ExcelProperty(value = "间隔单元",order = 3)
        public String spacingUnitName;

        @ExcelProperty(value = "电压等级",order = 4)
        public String voltageLevel;

        @ExcelProperty(value = "变电站",order = 5)
        public String substationName;

        @ExcelProperty(value = "PMS_ID",order = 6)
        public String pmsId;

        public String orgCode;
    }
}
