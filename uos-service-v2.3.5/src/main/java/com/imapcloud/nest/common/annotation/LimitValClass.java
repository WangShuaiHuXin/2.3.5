package com.imapcloud.nest.common.annotation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author wmin
 */
public class LimitValClass implements ConstraintValidator<LimitVal, Integer> {
    private String[] values;

    @Override
    public void initialize(LimitVal limitVal) {
        this.values = limitVal.values();
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }

        boolean valid = false;
        for (String val : values) {
            if (val.equals(String.valueOf(value))) {
                valid = true;
                break;
            }
        }

        return valid;
    }
}
