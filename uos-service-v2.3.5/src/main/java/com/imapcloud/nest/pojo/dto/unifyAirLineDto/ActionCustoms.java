package com.imapcloud.nest.pojo.dto.unifyAirLineDto;

import com.imapcloud.sdk.pojo.constant.ActionTypeEnum;
import lombok.Data;

import java.util.List;

/**
 * @Classname ActionCustoms
 * @Description ActionCustoms
 * @Date 2022/12/16 11:06
 * @Author Carnival
 */
@Data
public class ActionCustoms {

    private ActionTypeEnum actionType;

    private Integer value;

    private List<Integer> photoPropList;

    private String byname;
}
