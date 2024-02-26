package com.nly.Controller;

import com.nly.pojo.Users;
import com.nly.pojo.UsersReport;
import com.nly.pojo.vo.PublisherVideo;
import com.nly.pojo.vo.UsersVO;
import com.nly.service.UsersService;
import com.nly.utils.JSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;

/**
 * 用户管理
 */
@RestController
@Api(value = "用户相关的业务接口", tags = "用户相关业务的controller")
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UsersService usersService;



    @ApiOperation(value = "用户上传头像",notes = "用户上传头像的接口")
    @ApiImplicitParam(name = "userId",value = "用户id",required = true,dataType = "String",paramType = "query")
    @PostMapping("/uploadFace")
    public JSONResult uploadFace(String userId,@RequestParam("file") MultipartFile[] files) throws Exception {

        //userId判空
        if(StringUtils.isBlank(userId)){
            return JSONResult.errorMsg("用户id不能为空...");
        }
        //文件保存的命名空间
        String fileSpace ="C:/G/Nly-videos-dev";
        //保存到数据库中的相对路径
        String uploadPathDB ="/"+userId+"/face";

        FileOutputStream fileOutputStream = null;
        InputStream fileInputStream = null;

        try {
            if(files != null && files.length > 0){
                //获取文件名字
                String filename =  files[0].getOriginalFilename();
                //判空
                if(StringUtils.isNotBlank(filename)){
                    //文件上传的最终保存路径
                    String finalFacePath =  fileSpace+uploadPathDB+"/"+filename;
                    //设置数据库最终保存的路径
                    uploadPathDB += ("/"+filename);

                    File outFile = new File(finalFacePath);

                    if(outFile.getParentFile() != null|| !outFile.getParentFile().isDirectory()){
                        outFile.getParentFile().mkdirs();
                    }
                    fileOutputStream = new FileOutputStream(outFile);
                    fileInputStream = files[0].getInputStream();
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
        Users user= new Users();
        user.setId(userId);
        user.setFaceImage(uploadPathDB);
        usersService.updataUserInfo(user);

        return JSONResult.ok(uploadPathDB);
    }

    @ApiOperation(value = "查询用户信息",notes = "查询用户信息的接口")
    @ApiImplicitParam(name = "userId",value = "用户id",required = true,dataType = "String",paramType = "query")
    @PostMapping("/query")
    public JSONResult query(String userId,String fanId) throws Exception {

        if(StringUtils.isBlank(userId)){
            return JSONResult.errorMsg("用户id不能为空");
        }
        Users userInfo = usersService.queryUserInfo(userId);
        if(userInfo == null){
            return  JSONResult.errorMsg("未查到该成员的用户信息");
        }
        UsersVO userVO = new UsersVO();
        BeanUtils.copyProperties(userInfo,userVO);

        userVO.setFollow(usersService.queryIfFollow(userId,fanId));

        return JSONResult.ok(userVO);
    }

    /**
     * 查询视频发布者信息
     * @param loginUserId
     * @param videoId
     * @param publisherUserId
     * @return
     * @throws Exception
     */
    @ApiOperation(value = "查询视频发布者信息",notes = "查询视频发布者信息的接口")
    @PostMapping("/queryPublisher")
    public JSONResult queryPublisher(String loginUserId,String videoId,
                                     String publisherUserId) throws Exception {

        //当前用户的登录id和videoId是可以为空的
        if(StringUtils.isBlank(publisherUserId )){
            return JSONResult.errorMsg("");
        }

        //查询视频发布者的信息
        Users userInfo = usersService.queryUserInfo(publisherUserId);
        UsersVO publisher = new UsersVO();
        BeanUtils.copyProperties(userInfo,publisher);

        //2.查询当前登陆者和视频的点赞的关系
        boolean userLikeVideo =usersService.isUserLikeVideo(loginUserId,videoId);

        //设置到PublisherVideo里
        PublisherVideo bean = new PublisherVideo();
        bean.setPublisher(publisher);
        bean.setUserLikeVideo(userLikeVideo);

        return JSONResult.ok(bean);

    }

    /**
     * 添加关注
     * @param userId
     * @param fanId
     * @return
     * @throws Exception
     */
    @ApiOperation(value = "添加关注",notes = "添加关注接口")
    @PostMapping("/beyourfans")
    public JSONResult beyourfans(String userId,String fanId) throws Exception {

        if(StringUtils.isBlank(userId)||StringUtils.isBlank(fanId)){
            return JSONResult.errorMsg("");
        }

        usersService.saveUserFanRelation(userId,fanId);

        return JSONResult.ok("关注成功");
    }

    /**
     * 取消关注
     * @param userId
     * @param fanId
     * @return
     * @throws Exception
     */
    @ApiOperation(value = "取消关注",notes = "取消关注的接口")
    @PostMapping("/dontbeyourfans")
    public JSONResult dontbeyourfans(String userId,String fanId) throws Exception {

        if(StringUtils.isBlank(userId)||StringUtils.isBlank(fanId)){
            return JSONResult.errorMsg("");
        }

        usersService.deleteUserFanRelation(userId,fanId);

        return JSONResult.ok("取关成功");
    }

    /**
     * 举报信息
     * @param usersReport
     * @return
     * @throws Exception
     */
    @ApiOperation(value = "举报信息",notes = "举报信息的接口")
    @PostMapping("/reportUser")
    public JSONResult reportUser(@RequestBody UsersReport usersReport) throws Exception {

       usersService.reportUser(usersReport);

        return JSONResult.errorMsg("举报成功");
    }



}
