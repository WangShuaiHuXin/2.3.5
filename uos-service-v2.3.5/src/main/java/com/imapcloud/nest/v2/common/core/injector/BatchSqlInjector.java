package com.imapcloud.nest.v2.common.core.injector;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName BatchSqlInjector.java
 * @Description BatchSqlInjector
 * @createTime 2022年07月19日 17:21:00
 */
@Component
public class BatchSqlInjector extends DefaultSqlInjector {

    @Override
    public List<AbstractMethod> getMethodList(Class<?> mapperClass) {
        List<AbstractMethod> methodList = super.getMethodList(mapperClass);
        methodList.add(new SaveBatch());
        methodList.add(new SaveOrUpdateBatch());
        return methodList;
    }
}
