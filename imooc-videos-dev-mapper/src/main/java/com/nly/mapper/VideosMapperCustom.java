package com.nly.mapper;


import com.nly.pojo.Videos;
import com.nly.pojo.vo.VideosVO;
import com.nly.utils.MyMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface VideosMapperCustom extends MyMapper<Videos> {
    /**
     * 查询所有的视频列表
     * @return
     */
    public List<VideosVO> queryAllVideos(@Param("videoDesc") String videoDesc,
                                         @Param("userId") String userId);

    /**
     * 视频喜欢的数量累加
     * @param videoId
     */
    public void addVideoLikeCount(String videoId);

    /**
     * 视频喜欢的数量的累减
     * @param videoId
     */
    public void reduceVideoLikeCount(String videoId);

    /**
     * 查询收藏(点赞)的视频列表
     * @param userId
     * @return
     */
    public List<VideosVO> queryMyLikeVideos(@Param("userId") String userId);

    /**
     * 查询关注的视频
     * @param userId
     * @return
     */
    public List<VideosVO> queryMyFollowVideos(String userId);
}