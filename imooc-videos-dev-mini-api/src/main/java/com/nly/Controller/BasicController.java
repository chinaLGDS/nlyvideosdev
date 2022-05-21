package com.nly.Controller;

import com.nly.utils.RedisOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BasicController {

    @Autowired
    public RedisOperator redis;

    public static final String USER_REDIS_SESSION = "user-redis-session";
    //文件命名空间
    public static final String FILE_SPACE ="C:/G/Nly-videos-dev";
    //ffmpeg所在的目录
    public static final String FFMPEG_EXE = "F:\\ffmpeg\\bin\\ffmpeg.exe";

    //每页的记录数
    public static final Integer PAGE_SIZE = 5 ;
}
