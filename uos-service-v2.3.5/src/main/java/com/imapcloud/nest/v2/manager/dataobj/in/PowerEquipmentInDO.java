package com.imapcloud.nest.v2.manager.dataobj.in;

import com.alibaba.excel.annotation.ExcelProperty;
import com.geoai.common.core.bean.PageInfo;
import lombok.Data;

@Data
public class PowerEquipmentInDO {

        private String equipmentId;

        public String equipmentName;

        public String equipmentType;

        public String spacingUnitName;

        public String voltageLevel;

        public String substationName;

        public String pmsId;

        public String orgCode;


}
