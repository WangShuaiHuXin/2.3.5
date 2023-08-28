package com.imapcloud.nest.utils.mongo.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Collections;
import java.util.List;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class MongoPage<T> {
    private Long total;
    private Long pages;
    private Integer currentPage;
    private Integer pageSize;
    private List<T> records;
}
