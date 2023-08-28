package com.imapcloud.nest.common.annotation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Collection;

/**
 * @author wmin
 */
public class CollectionNotEmptyClass implements ConstraintValidator<CollectionNotEmpty, Collection> {

    @Override
    public boolean isValid(Collection value, ConstraintValidatorContext context) {
        return value != null && value.size() > 0;
    }
}
