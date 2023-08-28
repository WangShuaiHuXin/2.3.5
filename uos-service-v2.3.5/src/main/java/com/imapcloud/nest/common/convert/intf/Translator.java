package com.imapcloud.nest.common.convert.intf;

import org.mapstruct.InheritConfiguration;

import java.util.List;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName Translator.java
 * @Description 对象间属性转换类
 * @createTime 2022年03月09日 09:56:00
 */
public interface Translator<D,E> {

    /**
     * entiy转换成DTO
     * @param entity entiy
     * @return DTO
     */
    D doToDto(E entity);

    /**
     * DTO转换成entiy
     * @param dto DTO
     * @return entiy
     */
    E dtoToDo(D dto);

    /**
     * 批量转换D到E
     * @param dList
     * @return
     */
    @InheritConfiguration(name = "dtoTodo")
    List<E> dtoToDo(List<D> dList);

    /**
     * 批量转换E到D
     * @param dList
     * @return
     */
    @InheritConfiguration(name = "doToDto")
    List<D> doToDto(List<E> dList);

}
