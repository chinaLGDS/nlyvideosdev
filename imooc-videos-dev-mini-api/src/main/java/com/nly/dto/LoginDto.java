package com.nly.dto;


import com.nly.pojo.Users;


/**
 * login返回值的数据类型
 * @author nly
 */

public class LoginDto {
    /**
     * 重定向或者跳转路径
     */
    private  String path;
    /**
     * 错误信息
     */
    private  String error;
    /**
     * 登录的用户信息
     */
    private Users user
            ;
}
