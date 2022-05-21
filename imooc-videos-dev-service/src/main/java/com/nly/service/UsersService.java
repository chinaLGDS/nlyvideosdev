package com.nly.service;



import com.nly.pojo.Users;
import com.nly.pojo.UsersReport;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author nly
 * @since 2021-05-15
 */
@Service
public interface UsersService  {

    /**
     * 判断用户名是否存在
     * @param username
     * @return
     */
    public boolean queryUsernameIsExist(String username);

    /**
     * 保存用户(用户注册)
     * @param user
     */
    public void saveUser(Users user);

    /**
     * 用户登录
     * @param username
     * @param password
     * @return
     */
    public Users queryUserForLogin(String username, String password);

    /**
     * 用户修改信息
     * @param users
     */
    public  void updataUserInfo(Users users);

    /**
     * 查询用户的信息
     * @param userId
     * @return
     */
    public Users queryUserInfo(String userId);

    /**
     * 查询用户是否喜欢点赞视频
     * @param userId
     * @param videoId
     * @return
     */
    public boolean isUserLikeVideo(String userId,String videoId);

    /**
     * 增加用户和粉丝的关系
     * @param userId
     * @param fanId
     */
    public void saveUserFanRelation(String userId,String fanId);

    /**
     * 删除用户和粉丝的关系
     * @param userId
     * @param fanId
     */
    public void deleteUserFanRelation(String userId,String fanId);

    /**
     * 查询用户是否关注
     * @param userId
     * @param fanId
     * @return
     */
    public boolean queryIfFollow(String userId,String fanId);

    /**
     * 举报用户
     * @param usersReport
     */
    public void reportUser(UsersReport usersReport);


}
