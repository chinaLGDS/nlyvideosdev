package com.nly.mapper;

import com.nly.pojo.Users;
import com.nly.utils.MyMapper;

public interface UsersMapper extends MyMapper<Users> {

    /**
     * 用户受喜欢的数量累加
     * @param userId
     */
    public void addReceiveLikeCount(String userId);

    /**
     * 用户受喜欢数量累减
     * @param userId
     */
    public void reduceReceiveLikeCount(String userId);

    /**
     * 增加粉丝数量
     * @param userId
     */
    public void addFansCount(String userId);
    /**
     * 增加关注数量
     * @param userId
     */
    public void addFollowsCount(String userId);
    /**
     * 减少粉丝数量
     * @param userId
     */
    public void reduceFansCount(String userId);
    /**
     * 减少关注数量
     * @param userId
     */
    public void reduceFollowCount(String userId);

}