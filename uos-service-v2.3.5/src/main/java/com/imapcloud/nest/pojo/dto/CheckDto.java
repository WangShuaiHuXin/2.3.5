package com.imapcloud.nest.pojo.dto;

import com.imapcloud.nest.enums.CheckTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 检查结果
 *
 * @author daolin
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class CheckDto{
    /**
     * 是否通过：-1-未知, 0-警告，1-通过，2-异常
     */
    private Integer pass;

    /**
     * 当前状态
     */
    private String checkState;
    /**
     * 描述
     */
    private String msg;
    /**
     * 阶段
     */
    private Integer period;
    /**
     * 检查类型
     */
    private CheckTypeEnum checkType;

    public enum PassTypeEnum {
        UNKNOWN(-1),
        WARN(0),
        PASS(1),
        ERROR(2);
        private int value;

        PassTypeEnum(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

}
