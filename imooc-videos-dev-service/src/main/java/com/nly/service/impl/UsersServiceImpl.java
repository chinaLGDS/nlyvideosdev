package com.nly.service.impl;



import com.nly.mapper.UsersFansMapper;
import com.nly.mapper.UsersLikeVideosMapper;
import com.nly.mapper.UsersMapper;
import com.nly.mapper.UsersReportMapper;
import com.nly.pojo.Users;
import com.nly.pojo.UsersFans;
import com.nly.pojo.UsersLikeVideos;
import com.nly.pojo.UsersReport;
import com.nly.service.UsersService;

import org.apache.commons.lang3.StringUtils;
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
 *  服务实现类
 * </p>
 *
 * @author nly
 *
 */
@Service
public class UsersServiceImpl  implements UsersService {

    @Autowired
    private UsersMapper usersMapper;

    @Autowired
    private UsersLikeVideosMapper usersLikeVideosMapper;

    @Autowired
    private UsersFansMapper usersFansMapper;

    @Autowired
    private UsersReportMapper usersReportMapper;


    @Autowired
    private Sid sid;

    /**
     * 查询用户名是否存在
     * @param username
     * @return
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public boolean queryUsernameIsExist(String username) {
        Users user = new Users();
        user.setUsername(username);
        Users result = usersMapper.selectOne(user);
        return result == null ? false:true;
    }

    /**
     * 保存用户信息
     * @param user
     */
    @Transactional(propagation=Propagation.REQUIRED)
    @Override
    public void saveUser(Users user) {

        String userId =  sid.nextShort();
        user.setId(userId);
        usersMapper.insert(user);

    }

    /**
     * 用户登录
     * @param username
     * @param password
     * @return
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Users queryUserForLogin(String username, String password) {
        Example userExample = new Example(Users.class);
        Criteria criteria = userExample.createCriteria();
        criteria.andEqualTo("username",username);
        criteria.andEqualTo("password",password);
        Users result = usersMapper.selectOneByExample(userExample);

        return result;

    }

    /**
     * 用户修改信息
     * @param users
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public void updataUserInfo(Users users) {
        Example userExample = new Example(Users.class);
        Criteria criteria = userExample.createCriteria();
        criteria.andEqualTo("id",users.getId());
        usersMapper.updateByExampleSelective(users,userExample);
    }

    /**
     * 查询用户信息
     * @param userId
     * @return
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Users queryUserInfo(String userId) {
        Example userExample = new Example(Users.class);
        Criteria criteria = userExample.createCriteria();
        criteria.andEqualTo("id",userId);
       Users user = usersMapper.selectOneByExample(userExample);

       return user;

    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public boolean isUserLikeVideo(String userId, String videoId) {
        if(StringUtils.isBlank(userId)||StringUtils.isBlank(videoId)){
            return false;
        }
        Example userExample = new Example(UsersLikeVideos.class);
        Criteria criteria = userExample.createCriteria();
        criteria.andEqualTo("userId",userId);
        criteria.andEqualTo("videoId",videoId);
        List<UsersLikeVideos> list = usersLikeVideosMapper.selectByExample(userExample);
        if (list != null && list.size()>0){
            return true;
        }
        return false;
    }

    /**
     * 增加用户和粉丝的关系
     * @param userId
     * @param fanId
     */
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void saveUserFanRelation(String userId, String fanId) {

        String relId =sid.nextShort();

        UsersFans usersFans = new UsersFans();
        usersFans.setId(relId);
        usersFans.setUserId(userId);
        usersFans.setFanId(fanId);

        usersFansMapper.insert(usersFans);

        usersMapper.addFansCount(userId);
        usersMapper.addFollowsCount(fanId);


    }

    /**
     * 删除用户和粉丝的关系
     * @param userId
     * @param fanId
     */
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void deleteUserFanRelation(String userId, String fanId) {

        Example userExample = new Example(UsersFans.class);
        Criteria criteria = userExample.createCriteria();

        criteria.andEqualTo("userId",userId);
        criteria.andEqualTo("fanId",fanId);

        usersFansMapper.deleteByExample(userExample);

        //减少粉丝数量和关注数量
        usersMapper.reduceFansCount(userId);
        usersMapper.reduceFollowCount(fanId);

    }

    @Override
    public boolean queryIfFollow(String userId, String fanId) {
        Example userExample = new Example(UsersFans.class);
        Criteria criteria = userExample.createCriteria();

        criteria.andEqualTo("userId",userId);
        criteria.andEqualTo("fanId",fanId);
        List<UsersFans> list = usersFansMapper.selectByExample(userExample);
        if (list != null&& !list.isEmpty() && list.size() > 0){
            return true;
        }

        return false;
    }

    /**
     * 举报用户
     * @param usersReport
     */
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void reportUser(UsersReport usersReport) {

        String urId = sid.nextShort();
        usersReport.setId(urId);
        usersReport.setCreateDate(new Date());
        usersReportMapper.insert(usersReport);

    }


}
