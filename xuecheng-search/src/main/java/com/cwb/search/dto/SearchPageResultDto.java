package com.cwb.search.dto;

import com.cwb.base.model.PageResult;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * @author Mr.M
 * @version 1.0
 *
 * @date 2022/9/25 17:51
 */

@ToString
@Getter
@Setter
public class SearchPageResultDto<T> extends PageResult {

    //大分类列表
    List<String> mtList;
    //小分类列表
    List<String> stList;

    public SearchPageResultDto(List<T> items, long counts, long page, long pageSize) {
        super(items, counts, page, pageSize);
    }

}
