package com.nly.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * 获取视频截图
 */
public class FetchVideoCover {

    //可执行文件所在的位置
    private String ffmpegEXE;

    //构造函数
    public FetchVideoCover() {
        super();
    }
    public FetchVideoCover(String ffmpegEXE) {
        this.ffmpegEXE = ffmpegEXE;
    }

    public void getCover(String videoInputPath,String coverOutputPath) throws IOException {
        //ffmpeg.exe -ss 00:00:01 -y -i date.mp4 -vframes 1 new.jpg

        //命令集
        List<String> command = new ArrayList<String>();
        //添加ffmpeg
        command.add(ffmpegEXE);
        //指定截取第一秒
        command.add("-ss");
        command.add("00:00:01");

        command.add("-y");
        command.add("-i");
        command.add(videoInputPath);

        command.add("-vframes");
        command.add("1");

        command.add(coverOutputPath);

        for (String c : command){
            System.out.print(c);
        }

        //执行cmd命令
        ProcessBuilder builder = new ProcessBuilder(command);
        Process process = builder.start();

        //通过process读取errorstream。读取就是释放。
        InputStream errorStream = process.getErrorStream();
        InputStreamReader inputStreamReader = new InputStreamReader(errorStream);
        BufferedReader br = new BufferedReader(inputStreamReader);

        String line="";
        while ((line = br.readLine())!=null){
         }

         //关闭
        if(br != null){
            br.close();
        }
        if(inputStreamReader != null){
            inputStreamReader.close();
        }
        if(errorStream != null){
            errorStream.close();
        }
        //在命令处理的时候会产生一些流，分为InputStream和error Stream.
    }

    public String getFfmpegEXE() {
        return ffmpegEXE;
    }

    public void setFfmpegEXE(String ffmpegEXE) {
        this.ffmpegEXE = ffmpegEXE;
    }

    public static void main(String[] args) throws IOException {

        FetchVideoCover ffMpegTest = new FetchVideoCover("C:\\D\\ffmpeg\\bin\\ffmpeg.exe");

        try {
            ffMpegTest.getCover("C:\\D\\ffmpeg\\bin\\exercise.mp4","C:\\D\\ffmpeg\\bin\\exe.jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
