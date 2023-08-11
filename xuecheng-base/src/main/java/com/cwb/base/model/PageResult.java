package com.cwb.base.model;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * @author CWB
 * @version 1.0
 */
@SuppressWarnings({"all"})
@Data
@ToString
public class PageResult<T> implements Serializable {

    // 数据列表
    private List<T> items;


    private long counts;//总记录数
    private long page;//当前页码


    private long pageSize;//每页记录数

    public PageResult(List<T> items, long counts, long page, long pageSize) {
        this.items = items;
        this.counts = counts;
        this.page = page;
        this.pageSize = pageSize;
    }



}



