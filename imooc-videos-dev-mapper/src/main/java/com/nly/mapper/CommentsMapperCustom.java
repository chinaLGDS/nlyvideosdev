package com.nly.mapper;


import com.nly.pojo.Comments;
import com.nly.pojo.vo.CommentsVO;
import com.nly.utils.MyMapper;

import java.util.List;


public interface CommentsMapperCustom extends MyMapper<Comments> {
    public List<CommentsVO>  queryComments(String videoId);
}