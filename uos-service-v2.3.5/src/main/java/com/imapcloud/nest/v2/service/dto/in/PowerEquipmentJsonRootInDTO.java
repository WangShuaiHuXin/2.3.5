package com.imapcloud.nest.v2.service.dto.in;

import lombok.Data;

import java.util.List;

@Data
public class PowerEquipmentJsonRootInDTO {


        private String name;
        private List<PowerEquipmentJsonLeafInDTO> leafInDTOList;
        public void setName(String name) {
            this.name = name;
        }
        public String getName() {
            return name;
        }

        public void setList(List<PowerEquipmentJsonLeafInDTO> List) {
            this.leafInDTOList = List;
        }
        public List<PowerEquipmentJsonLeafInDTO> getList() {
            return leafInDTOList;
        }


}
