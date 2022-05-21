package com.nly.Controller.interceptor;

import com.nly.utils.JSONResult;
import com.nly.utils.JsonUtils;
import com.nly.utils.RedisOperator;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

public class MiniInterceptor implements HandlerInterceptor {


    private static final Logger log = LoggerFactory.getLogger(MiniInterceptor.class);


    @Autowired
    public RedisOperator redis;

    public static final String USER_REDIS_SESSION = "user-redis-session";

    /**
     * 拦截请求，预处理，在调用controller之前
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        log.info(request.toString());
        log.info(String.valueOf(request.getHeaders("methodMB")));

       String userId = request.getHeader("userId");
       String userToken = request.getHeader("userToken");
        if (StringUtils.isNotBlank(userId)&&StringUtils.isNotBlank(userToken)){

            String uniqueToken = redis.get(USER_REDIS_SESSION+":"+userId);
            //判空或空字符串
            if (StringUtils.isEmpty(uniqueToken)&&StringUtils.isBlank(uniqueToken)) {
                System.out.println("请登录...");
                returnErrorResponse(response,new JSONResult().errorTokenMsg("请登录"));
                return false;
            }else {
                if (!uniqueToken.equals(userToken)){
                    System.out.println("账号被挤出...");
                    returnErrorResponse(response,new JSONResult().errorTokenMsg("账号被挤出..."));
                    return false;
                }
            }

        }
        else {
            System.out.println("请登录...");
            returnErrorResponse(response,new JSONResult().errorTokenMsg("请登录"));
            return false;
        }

        //false 表示请求被拦截。true:请求ok，可以执行

        return true;
    }


     //处理错误信息，以流的形式
     public void returnErrorResponse(HttpServletResponse response, JSONResult result)throws IOException, UnsupportedEncodingException {
        OutputStream out = null;
        try {
            response.setCharacterEncoding("utf-8");
            response.setContentType("text/json");
            out = response.getOutputStream();
            out.write(JsonUtils.objectToJson(result).getBytes("utf-8"));
            out.flush();
        }finally {
            if (out!=null){
                out.close();
            }
        }

     }

    /**
     * 请求controller之后，渲染视图之前
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    /**
     * 请求controller之后，视图渲染之后
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }

}
