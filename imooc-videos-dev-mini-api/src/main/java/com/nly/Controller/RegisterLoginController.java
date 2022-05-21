package com.nly.Controller;

import com.nly.model.WXSessionModel;
import com.nly.pojo.Users;
import com.nly.pojo.vo.UsersVO;
import com.nly.service.UsersService;

import com.nly.utils.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.jws.soap.SOAPBinding;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@Api(value = "用户注册登录的接口",tags={"注册跟登录的controller"})
public class RegisterLoginController extends BasicController {

    @Autowired
    private UsersService usersService;
    @Autowired
    private RedisOperator redis;

    @ApiOperation(value = "用户注册",notes = "用户注册的接口")
    @PostMapping("/regist")
    public JSONResult regist(@RequestBody Users user) throws Exception {


        //需求：判断用户名，密码不为空？
        if(StringUtils.isBlank(user.getUsername())||StringUtils.isBlank(user.getPassword())){
            return JSONResult.errorMsg("用户名和密码不能为空");
        }
        //判断用户名是否存在，定义在service层
        boolean usernameIsExist = usersService.queryUsernameIsExist(user.getUsername());

        //保存用户，注册信息
        if (!usernameIsExist) {
            //完善用户信息
            user.setNickname(user.getUsername());
            user.setPassword(MD5Utils.getMD5Str(user.getPassword()));
            user.setReceiveLikeCounts(0);
            user.setFansCounts(0);
            user.setFollowCounts(0);
            usersService.saveUser(user);
        }
        else{
            return JSONResult.errorMsg("用户名已经存在，请换一个试试");
        }
        user.setPassword("");

        /*String uniqueToken = UUID.randomUUID().toString();
        redis.set(USER_REDIS_SESSION+":"+user.getId(),uniqueToken,1000*60*30);

        UsersVO usersVO = new UsersVO();
        BeanUtils.copyProperties(user,usersVO);
        usersVO.setUserToken(uniqueToken);*/
        UsersVO usersVO = setUserRedisSessionToken(user);
        return JSONResult.ok(usersVO);
    }



    @ApiOperation(value = "用户登录",notes = "用户登录的接口")
    @PostMapping("/login")
    public JSONResult login(@RequestBody Users user) throws Exception {
        //获取用户名跟密码
        String username = user.getUsername();
        String password = user.getPassword();


        //判空
        if(StringUtils.isBlank(username)||(StringUtils.isBlank(password))){
            return JSONResult.errorMsg("用户名或密码不能为空");
        }

        //判断用户是否存在
        Users userResult = usersService.queryUserForLogin(username,MD5Utils.getMD5Str(user.getPassword()));

        //返回
        if(userResult != null){
            userResult.setPassword("");
            UsersVO usersVO = setUserRedisSessionToken(userResult);
            return JSONResult.ok(usersVO);
        }
        else{
            return JSONResult.errorMsg("用户名或密码不正确");
        }
    }

    @ApiOperation(value = "微信登录",notes = "微信登录的接口")
    @PostMapping("/wxlogin")
    public JSONResult wxlogin(String code,@RequestBody Users user) throws Exception {

        System.out.println("wxLogin-code"+code);

        String url = "https://api.weixin.qq.com/sns/jscode2session";

        Map<String, String> param = new HashMap<>();
        param.put("appid","wxa1ab226438bfa52e");
        param.put("secret","13b83a53c379150c840308c70a809da4");
        param.put("js_code",code);
        param.put("grant_type","authorization_code");
        String  wxResult = HttpClientUtil.doGet(url,param);
        System.out.println(wxResult);
        WXSessionModel model = JsonUtils.jsonToPojo(wxResult, WXSessionModel.class);

        //存入session到redis
         redis.set("user-redis-session:"+model.getOpenid(),
                 model.getSession_key(),
                 1000 * 60 *30);
        //获取用户名跟密码
        String username = user.getUsername();
        String password = user.getPassword();


        //判空
        if(StringUtils.isBlank(username)||(StringUtils.isBlank(password))){
            return JSONResult.errorMsg("用户名或密码不能为空");
        }

        //判断用户是否存在
        Users userResult = usersService.queryUserForLogin(username,MD5Utils.getMD5Str(user.getPassword()));

        //返回
        if(userResult != null){
            userResult.setPassword("");
            UsersVO usersVO = setUserRedisSessionToken(userResult);
            return JSONResult.ok(usersVO);
        }
        else{
            return JSONResult.errorMsg("用户名或密码不正确");
        }
    }

    /**
     * 用户注销--清理掉redis-session
     * @return
     * @throws Exception
     */
    @ApiOperation(value = "用户注销",notes = "用户注销的接口")
    @ApiImplicitParam(name = "userId",value = "用户id",required = true,dataType = "String",paramType = "query")
    @PostMapping("/logout")
    public JSONResult logout(String userId) throws Exception {
        redis.del(USER_REDIS_SESSION+":"+userId);
            return JSONResult.ok();
        }



    /**
     * 设置Token
     * @param userModel
     * @return
     */
    public UsersVO setUserRedisSessionToken(Users userModel){
        String uniqueToken = UUID.randomUUID().toString();
        redis.set(USER_REDIS_SESSION+":"+userModel.getId(),uniqueToken,1000*60*30);

        UsersVO usersVO = new UsersVO();
        BeanUtils.copyProperties(userModel,usersVO);
        usersVO.setUserToken(uniqueToken);
        return usersVO;
    }



}
