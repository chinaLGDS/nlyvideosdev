package com.nly.Controller;

import com.nly.pojo.Bgm;
import com.nly.pojo.Comments;
import com.nly.pojo.Users;
import com.nly.pojo.Videos;
import com.nly.service.BgmService;
import com.nly.service.VideosService;
import com.nly.utils.FetchVideoCover;
import com.nly.utils.JSONResult;
import com.nly.utils.MergeVideoMp3;
import com.nly.utils.PagedResult;
import com.sun.scenario.effect.Merge;
import enums.ViddeoStatusEnum;
import io.swagger.annotations.*;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.UUID;

@RestController
@RequestMapping("/video")
@Api(value = "视频相关业务的接口", tags = "视频相关业务的controller")
public class VideoController extends BasicController{

    @Autowired
    private BgmService bgmService;

    @Autowired
    private VideosService videosService;

    @ApiOperation(value = "用户上传视频",notes = "用户上传视频的接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId",value = "用户id",required = true,dataType = "String",paramType = "form"),
            @ApiImplicitParam(name = "bgmId",value = "背景音乐id",required = false,dataType = "String",paramType = "form"),
            @ApiImplicitParam(name = "videoSeconds",value = "背景音乐播放长度",required = true,dataType = "double",paramType = "form"),
            @ApiImplicitParam(name = "videoWidth",value = "视频宽度",required = true,dataType = "int",paramType = "form"),
            @ApiImplicitParam(name = "videoHeight",value = "视频高度",required = true,dataType = "int",paramType = "form"),
            @ApiImplicitParam(name = "desc",value = "视频描述",required = false,dataType = "String",paramType = "form")
    })
    @PostMapping(value = "/upload",headers="content-type=multipart/form-data")
    //单文件上传
    public JSONResult uploadFace(String userId,
                                 String bgmId, double videoSeconds, int videoWidth,int videoHeight,String desc,
                                 @ApiParam(value = "短视频",required = true) MultipartFile file) throws Exception {

        //userId判空
        if(StringUtils.isBlank(userId)){
            return JSONResult.errorMsg("用户id不能为空...");
        }
        //文件保存的命名空间
      //  String fileSpace ="G:/Nly-videos-dev";
        //保存到数据库中的相对路径
        String uploadPathDB ="/"+userId+"/video";
        String coverPathDB = "/" + userId + "/video";
        //文件上传的最终路径
        String finalVideoPath="";

        FileOutputStream fileOutputStream = null;
        InputStream fileInputStream = null;

        try {
            if(file != null ){
                //获取文件名字
                String filename =  file.getOriginalFilename();
                //abc.mp4
                String fileNamePrefix = filename.split("\\.")[0];

                //判空
                if(StringUtils.isNotBlank(filename)){
                    //文件上传的最终保存路径
                    finalVideoPath =  FILE_SPACE+uploadPathDB+"/"+filename;
                    //设置数据库最终保存的路径
                    uploadPathDB += ("/"+filename);
                    coverPathDB = coverPathDB + "/" + fileNamePrefix + ".jpg";
                    File outFile = new File(finalVideoPath);

                    if(outFile.getParentFile() != null|| !outFile.getParentFile().isDirectory()){
                        outFile.getParentFile().mkdirs();
                    }
                    fileOutputStream = new FileOutputStream(outFile);
                    fileInputStream = file.getInputStream();
                    IOUtils.copy(fileInputStream, fileOutputStream);
                }
            }
            else {
                return JSONResult.errorMsg("上传出错");
            }

        } catch (IOException e) {
            e.printStackTrace();
            return JSONResult.errorMsg("上传出错");
        }
        finally {
            if (fileOutputStream != null){
                fileOutputStream.flush();
                fileOutputStream.close();
            }
        }

        //判断bgmId是否为空，如果不为空，
        // 那就查询bgm的信息，并且合并视频，生成新的视频
        //保存新的视频
        if(StringUtils.isNotBlank(bgmId)){
            Bgm bgm = bgmService.queryBgmById(bgmId);
            String mp3InputPath = FILE_SPACE+bgm.getPath();
            MergeVideoMp3 tool = new MergeVideoMp3(FFMPEG_EXE);
            String videoInputPath = finalVideoPath;
            String videoOutputName = UUID.randomUUID().toString()+".mp4";
            uploadPathDB ="/"+userId+"/video"+"/"+videoOutputName;
            finalVideoPath = FILE_SPACE+uploadPathDB;
            tool.convertor(videoInputPath,mp3InputPath,videoSeconds,finalVideoPath);
        }

        //对视频进行截图

        FetchVideoCover ffMpegTest = new FetchVideoCover(FFMPEG_EXE);
        ffMpegTest.getCover(finalVideoPath,FILE_SPACE + coverPathDB);
        System.out.println();
        System.out.println("coverPathDB"+coverPathDB);

        Videos video = new Videos();
        video.setAudioId(bgmId);
        video.setUserId(userId);
        video.setVideoSeconds((float)videoSeconds);
        video.setVideoHeight(videoHeight);
        video.setVideoWidth(videoWidth);
        video.setVideoDesc(desc);
        video.setVideoPath(uploadPathDB);
        video.setCoverPath(coverPathDB);
        video.setStatus(ViddeoStatusEnum.SUCCESS.value);
        video.setCreateTime(new Date());
        video.setLikeCounts((long) 0);
        //保存视频信息到数据库
        String videoId = videosService.saveVideo(video);

        return JSONResult.ok(videoId);
    }

    @ApiOperation(value = "上传封面",notes = "上传封面的接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId",value = "用户id",required = true,dataType = "String",paramType = "form"),
            @ApiImplicitParam(name = "videoId",value = "视频主键id",required = true,dataType = "String",paramType = "form")
    })
    @PostMapping(value = "/uploadCover",headers="content-type=multipart/form-data")
    //单文件上传
    public JSONResult uploadCover(String userId,String videoId,
                                 @ApiParam(value = "视频封面",required = true) MultipartFile file) throws Exception {

        //userId判空
        if(StringUtils.isBlank(userId)||StringUtils.isBlank(videoId)){
            return JSONResult.errorMsg("用户id不能为空和视频主键id不能为空...");
        }
        //文件保存的命名空间
        //  String fileSpace ="G:/Nly-videos-dev";
        //保存到数据库中的相对路径
        String uploadPathDB ="/"+userId+"/video";

        //文件上传的最终路径
        String finalCoverPath="";

        FileOutputStream fileOutputStream = null;
        InputStream fileInputStream = null;

        try {
            if(file != null ){
                //获取文件名字
                String filename =  file.getOriginalFilename();
                //判空
                if(StringUtils.isNotBlank(filename)){
                    //文件上传的最终保存路径
                    finalCoverPath =  FILE_SPACE+uploadPathDB+"/"+filename;
                    //设置数据库最终保存的路径
                    uploadPathDB += ("/"+filename);

                    File outFile = new File(finalCoverPath);

                    if(outFile.getParentFile() != null|| !outFile.getParentFile().isDirectory()){
                        //创建父文件
                        outFile.getParentFile().mkdirs();
                    }
                    fileOutputStream = new FileOutputStream(outFile);
                    fileInputStream = file.getInputStream();
                    IOUtils.copy(fileInputStream, fileOutputStream);
                }
            }
            else {
                return JSONResult.errorMsg("上传出错");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return JSONResult.errorMsg("上传出错");
        }
        finally {
            if (fileOutputStream != null){
                fileOutputStream.flush();
                fileOutputStream.close();
            }
        }


        videosService.updateVideo(videoId,uploadPathDB);

        return JSONResult.ok();
    }


    /**
     * 分页跟搜索查询视频列表
     * isSaveRecord 1 - 需要保存
     *              0 - 不需要保存或者为空的时候
     * @param video
     * @param isSaveRecord
     * @param page
     * @return
     * @throws Exception
     */
    @PostMapping(value = "/showAll")
    public JSONResult show(@RequestBody Videos video,Integer isSaveRecord,
                           Integer page,Integer pageSize) throws Exception {
        if (page == null){
            page = 1;
        }if (pageSize == null){
            pageSize = PAGE_SIZE;
        }

       PagedResult pagedResult = videosService.getAllVideos(video,isSaveRecord,page,pageSize);
       return JSONResult.ok(pagedResult);
    }


    /**
     * 查询热词
     * @return
     * @throws Exception
     */
    @PostMapping(value = "/hot")
    public JSONResult hot() throws Exception {
        return JSONResult.ok(videosService.getHotwords());
    }


    /**
     * 用户喜欢/点赞视频
     * @param userId
     * @param videoId
     * @param videoCreaterId
     * @return
     * @throws Exception
     */
    @PostMapping(value = "/userLike")
    public JSONResult userLike(String userId,String videoId,String videoCreaterId) throws Exception {
       videosService.userLikeVideo(userId,videoId,videoCreaterId);
       return JSONResult.ok();
    }

    /**
     * 用户不喜欢/取消点赞视频
     * @param userId
     * @param videoId
     * @param videoCreaterId
     * @return
     * @throws Exception
     */
    @PostMapping(value = "/userUnLike")
    public JSONResult userUnLike(String userId,String videoId,String videoCreaterId) throws Exception {
        videosService.userUnLikeVideo(userId,videoId,videoCreaterId);
        return JSONResult.ok();
    }

    /**
     * 我收藏(点赞)过的视频列表
     * @param userId
     * @param page
     * @param pageSize
     * @return
     * @throws Exception
     */
    @PostMapping(value = "/showMyLike")
    public JSONResult showMyLike(String userId,
                           Integer page,Integer pageSize) throws Exception {
        if (StringUtils.isBlank(userId)){
            return JSONResult.ok();
        }
        if (page == null){
            page = 1;
        }if (pageSize == null){
            pageSize = 6;
        }

        PagedResult pagedResult = videosService.queryMyLikeVideos(userId,page,pageSize);
        return JSONResult.ok(pagedResult);
    }

    /**
     * 展示我的关注的视频列表
     * @param userId
     * @param page
     * @return
     * @throws Exception
     */
    @PostMapping(value = "/showMyFollow")
    public JSONResult showMyFollow(String userId,
                                 Integer page) throws Exception {
        if (StringUtils.isBlank(userId)){
            return JSONResult.ok();
        }
        if (page == null){
            page = 1;
        }

        int pageSize = 6;

        PagedResult pagedResult = videosService.queryMyFollowVideos(userId,page,pageSize);
        return JSONResult.ok(pagedResult);
    }

    /**
     * 保存评论
     * @param comment
     * @return
     * @throws Exception
     */
    @PostMapping(value = "/saveComment")
    public JSONResult saveComment(@RequestBody Comments comment,String fatherCommentId,String toUserId ) throws Exception {
        if (StringUtils.isNotBlank(fatherCommentId)|| StringUtils.isNotBlank(toUserId)){
            comment.setFatherCommentId(fatherCommentId);
            comment.setToUserId(toUserId);
        }

        videosService.saveComment(comment);
        return JSONResult.ok();
    }

    @PostMapping(value = "/getVideoComments")
    public JSONResult getVideoComments(String videoId,Integer page, Integer pageSize) throws Exception {
        if(StringUtils.isBlank(videoId)){
            return JSONResult.ok();
        }

        //分页查询视频列表，时间顺序倒序排序
        if(page == null){
            page = 1;
        }
        if (pageSize == null){
            pageSize = 10;
        }
        PagedResult list =videosService.getAllComments(videoId,page,pageSize);
        return JSONResult.ok(list );
    }












    }
