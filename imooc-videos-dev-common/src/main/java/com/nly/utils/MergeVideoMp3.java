package com.nly.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MergeVideoMp3 {

    //可执行文件所在的位置
    private String ffmpegEXE;

    //构造函数
    public MergeVideoMp3(String ffmpegEXE) {
        super();
        this.ffmpegEXE = ffmpegEXE;
    }

    public void convertor(String videoInputPath,String mp3InputPath,double seconds, String videoOutputPath) throws IOException {
        //ffmpeg.exe -i one.mp4 -i gg.mp3 -t 10 -y tes.mp4
        //去除原视频的音频
        //ffmpeg.exe -i one.mp4 -vcodec copy -an newone.mp4

        //命令集
        List<String> command = new ArrayList<>();
        //添加ffmpeg
        command.add(ffmpegEXE);
        //添加-i
        command.add("-i");
        command.add(videoInputPath);

        command.add("-i");
        command.add(mp3InputPath);

        command.add("-t");
        command.add(String.valueOf(seconds));

        command.add("-y");
        command.add(videoOutputPath);

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

    public static void main(String[] args) throws IOException {

        MergeVideoMp3 ffMpegTest = new MergeVideoMp3("F:\\ffmpeg\\bin\\ffmpeg.exe");
        try {
            ffMpegTest.convertor("F:\\ffmpeg\\bin\\newone.mp4",
                    "F:\\ffmpeg\\bin\\two.mp3",
                    29.0,"F:\\ffmpeg\\bin\\tesone.mp4");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
