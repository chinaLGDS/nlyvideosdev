package com.nly.service;


import com.nly.pojo.Comments;
import com.nly.pojo.Videos;
import com.nly.utils.PagedResult;
import io.swagger.models.auth.In;
import org.springframework.data.domain.PageRequest;

import java.util.List;

/**
 * <p>
 * 视频信息表 服务类
 * </p>
 *
 * @author nly
 * @since 2021-05-15
 */
public interface VideosService {

    /**
     * 保存视频
     * @param video
     */
    public String saveVideo(Videos video);

    /**
     * 修改视频的封面
     * @param videoId
     * @param coverPath
     * @return
     */
    public void updateVideo(String videoId,String coverPath);

    /**
     * 分页查询视频列表
     * @param page
     * @param pageSize
     * @return
     */
    public PagedResult getAllVideos(Videos video,Integer isSaveRecord,
            Integer page,Integer pageSize);


    /**
     * 我收藏(点赞)过的视频列表
     * @param userId
     * @param page
     * @param pageSize
     * @return
     */
    public PagedResult queryMyLikeVideos(String userId,
                                    Integer page,Integer pageSize);

    /**
     * 查询我关注的人的视频列表
     * @param userId
     * @param page
     * @param pageSize
     * @return
     */
    public PagedResult queryMyFollowVideos(String userId,
                                         Integer page,Integer pageSize);

    /**
     * 获取热搜词列表
     * @return
     */
    public List<String> getHotwords();

    /**
     * 用户喜欢/点赞视频
     * @param userId
     * @param videoId
     * @param videoCreaterId
     */
    public void userLikeVideo(String userId,String videoId,String videoCreaterId);

    /**
     * 用户不喜欢视频/取消点赞
     * @param userId
     * @param videoId
     * @param videoCreaterId
     */
    public void userUnLikeVideo(String userId,String videoId,String videoCreaterId);

    /**
     * 保存用户评论
     * @param comment
     */
    public void saveComment(Comments comment);

    public PagedResult getAllComments(String videoId, Integer page,Integer pageSize);

}
