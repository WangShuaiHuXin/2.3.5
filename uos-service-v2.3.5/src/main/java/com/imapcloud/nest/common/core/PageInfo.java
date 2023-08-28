package com.imapcloud.nest.common.core;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 通用分页数据体
 * @author Vastfy
 * @date 2022/04/18 11:11
 * @since 1.8.9
 */
@Getter
@Setter
public class PageInfo<T> implements Serializable {

    /**
     * 总记录数
     */
    private Long total;

    /**
     * 查询数据
     */
    private List<T> records;

    private PageInfo(Long total, List<T> records) {
        this.total = total;
        this.records = records;
    }

    public static <S> PageInfo<S> empty() {
        return new PageInfo<>(0L, Collections.emptyList());
    }

    public static <T> PageInfo<T> of(Long total, List<T> rows) {
        return new PageInfo<>(total, rows);
    }

    public <S> PageInfo<S> map(Function<? super T, ? extends S> function) {
        List<S> newRows = Objects.isNull(function) ? Collections.emptyList() : records.stream().map(function).collect(Collectors.toList());
        return new PageInfo<>(total, newRows);
    }

    public PageInfo<T> consume(Consumer<? super T> consumer) {
        if(Objects.nonNull(consumer)){
            records.forEach(consumer);
        }
        return this;
    }

    public static <T> PageInfo<T> convert(Page<T> page){
        if(Objects.isNull(page)){
            throw new IllegalArgumentException("page required non null");
        }
        return new PageInfo<>(page.getTotal(), page.getRecords());
    }

}