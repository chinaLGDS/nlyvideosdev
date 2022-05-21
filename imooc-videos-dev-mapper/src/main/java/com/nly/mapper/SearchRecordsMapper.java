package com.nly.mapper;


import com.nly.pojo.SearchRecords;
import com.nly.utils.MyMapper;

import java.util.List;


public interface SearchRecordsMapper extends MyMapper<SearchRecords> {

    public List<String> getHotwords();
}