package com.nly.service.impl;




import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.nly.mapper.*;
import com.nly.pojo.Comments;
import com.nly.pojo.SearchRecords;
import com.nly.pojo.UsersLikeVideos;
import com.nly.pojo.Videos;
import com.nly.pojo.vo.CommentsVO;
import com.nly.pojo.vo.VideosVO;
import com.nly.service.VideosService;
import com.nly.utils.PagedResult;
import com.nly.utils.TimeAgoUtils;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;

import java.util.Date;
import java.util.List;


/**
 * <p>
 * 视频信息表 服务实现类
 * </p>
 *
 * @author nly
 * @since 2021-05-15
 */
@Service
public class VideosServiceImpl  implements VideosService {

    @Autowired
    private VideosMapper videosMapper;

    @Autowired
    private UsersMapper usersMapper;

    @Autowired
    private VideosMapperCustom videosMapperCustom;

    @Autowired
    private SearchRecordsMapper searchRecordsMapper;

    @Autowired
    private UsersLikeVideosMapper usersLikeVideosMapper;

    @Autowired
    private CommentsMapper commentMapper;

    @Autowired
    private CommentsMapperCustom commentsMapperCustom;

    @Autowired
    private Sid sid;


    /**
     * 保存视频，根据id获取封面图片路径
     * @param video
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public String  saveVideo(Videos video) {

        String id = sid.nextShort();
        video.setId(id);
        videosMapper.insertSelective(video);
        return  id;

    }

    /**
     * 修改视频封面
     * @param videoId
     * @param coverPath
     */
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void updateVideo(String videoId, String coverPath) {

        Videos video = new Videos();
        video.setId(videoId);
        video.setCoverPath(coverPath);
        videosMapper.updateByPrimaryKeySelective(video);

    }




    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public PagedResult getAllVideos(Videos video, Integer isSaveRecord, Integer page, Integer pageSize) {
        //查询视频描述
        String desc = video.getVideoDesc();
        String userId = video.getUserId();

        //保存热搜词
        if(isSaveRecord != null && isSaveRecord == 1 ){
            //保存desc
            SearchRecords  record =new SearchRecords();
            String RecordId = sid.nextShort();
            record.setId(RecordId);
            record.setContent(desc);
            searchRecordsMapper.insert(record);

        }

        PageHelper.startPage(page,pageSize);
        List<VideosVO> list = videosMapperCustom.queryAllVideos(desc,userId);
        //pageinfo处理相关的数据
        PageInfo<VideosVO> pageList = new PageInfo<>(list);
        //短视频后台管理里面，后面会将pagedResult提供给分页主键，他们的属性一一配置。最重要搞清楚他们的字段名字。
        PagedResult pagedResult = new PagedResult();
        pagedResult.setPage(page);
        //总页数
        pagedResult.setTotal(pageList.getPages());
        //查询出来分页的记录数
        pagedResult.setRows(list);
        //查询出来所有列表的数据量
        pagedResult.setRecords(pageList.getTotal());
        return pagedResult;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PagedResult queryMyLikeVideos(String userId, Integer page, Integer pageSize) {
        PageHelper.startPage(page,pageSize);
        List<VideosVO> list = videosMapperCustom.queryMyLikeVideos(userId);

        PageInfo<VideosVO> pageList = new PageInfo<>(list);

        PagedResult pagedResult = new PagedResult();
        pagedResult.setTotal(pageList.getPages());
        pagedResult.setRows(list);
        pagedResult.setRecords(pageList.getTotal());

        return pagedResult;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PagedResult queryMyFollowVideos(String userId, Integer page, Integer pageSize) {
        PageHelper.startPage(page,pageSize);
        List<VideosVO> list = videosMapperCustom.queryMyFollowVideos(userId);

        PageInfo<VideosVO> pageList = new PageInfo<>(list);

        PagedResult pagedResult = new PagedResult();
        pagedResult.setTotal(pageList.getPages());
        pagedResult.setRows(list);
        pagedResult.setRecords(pageList.getTotal());

        return pagedResult;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<String> getHotwords() {
        return searchRecordsMapper.getHotwords();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void userLikeVideo(String userId, String videoId, String videoCreaterId) {

        //保存用户和视频点赞跟喜欢关联关系表
        String likeId = sid.nextShort();
        UsersLikeVideos ulv = new UsersLikeVideos();
        ulv.setId(likeId);
        ulv.setUserId(userId);
        ulv.setVideoId(videoId);
        usersLikeVideosMapper.insert(ulv);

        //视频喜欢数量累加
        videosMapperCustom.addVideoLikeCount(videoId);

        //用户受喜欢的数量的累加
        usersMapper.addReceiveLikeCount(videoCreaterId);
    }

    @Override
    public void userUnLikeVideo(String userId, String videoId, String videoCreaterId) {

        //删除用户和视频点赞跟喜欢关联关系表
        Example example = new Example(UsersLikeVideos.class);
        Criteria criteria = example.createCriteria();
        //双引号对应的值是实体类里面的属性
        criteria.andEqualTo("userId",userId);
        criteria.andEqualTo("videoId",videoId);
        usersLikeVideosMapper.deleteByExample(example);

        //视频喜欢数量累减
        videosMapperCustom.reduceVideoLikeCount(videoId);

        //用户受喜欢的数量的累减
        usersMapper.reduceReceiveLikeCount(videoCreaterId);

    }

    /**
     * 保存用户评论
     * @param comment
     */
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void saveComment(Comments comment) {

        String id = sid.nextShort();
        comment.setId(id);
        comment.setCreateTime(new Date());
        commentMapper.insert(comment);

    }

    @Override
    public PagedResult getAllComments(String videoId, Integer page, Integer pageSize) {
        PageHelper.startPage(page,pageSize);

        List<CommentsVO> list = commentsMapperCustom.queryComments(videoId);
        for (CommentsVO c: list){
            String timeAgo = TimeAgoUtils.format(c.getCreateTime());
            c.setTimeAgoStr(timeAgo);
        }

        PageInfo<CommentsVO> pageList = new PageInfo<>(list);

        PagedResult grid = new PagedResult();
        grid.setTotal(pageList.getPages());
        grid.setRows(list);
        grid.setPage(page);
        grid.setRecords(pageList.getTotal());

        return grid;
    }
}
