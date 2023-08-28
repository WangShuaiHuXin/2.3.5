package com.imapcloud.nest.utils;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * SSH工具类
 * @author 王建国
 * 2019-12-29
 */
@Slf4j
@Component
public  class CMDHelper {


//    @Value("${AIIdentification.defectAppPath}")
//    private String defectAppPath;//AI识别工具路径
//    @Value("${AIIdentification.detectedFilePath}")
//    private String detectedFilePath;//AI识别工具路径
//    @Value("${AIIdentification.defectModelPath}")
//    private String defectModelPath;//AI识别工具路径
//
//    /**
//     * 直接调用windows的cmd进行命令执行是同步的（执行完才会返回）
//     * @param photoPath  照片路径
//     * @return
//     */
//    public List<String> exec(String photoPath, String photoName, Long photoId){
//        String correctResult = null;
//        try {
//            String execStr = getExecStr(photoPath, photoName, photoId);
//            Process process = Runtime.getRuntime().exec(execStr);
//            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
//            in = new BufferedReader(new InputStreamReader(process.getInputStream(),"gbk"));
//            //接收错误流
//            BufferedReader isError = new BufferedReader(new InputStreamReader(process.getErrorStream(),"gbk"));
//            StringBuilder sb= new StringBuilder();
//            StringBuilder sbError= new StringBuilder();
//            String line = null;
//            String lineError = null;
//            while ((line = in.readLine()) != null ) {
//                if (!line.startsWith("Fusing")) {
//                    sb.append(line);
//                }
//            }
//            correctResult = sb.toString();
//            log.info("取到的执行结果："+ sb); //2408 1427 3116 1929 jyzzb 0.96
//
//            //出错的时候会打印的
//            //Model Summary: 232 layers, 7254609 parameters, 0 gradients
//            while ((lineError= isError.readLine()) != null) {
//                sbError.append(lineError);
//                sbError.append("\n");
//            }
//            log.info("sbError"+ sbError);
//            in.close();
//            isError.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        String[] resultArray = StringUtils.isNotBlank(correctResult) ? correctResult.split(" ") : new String[0];
//        return Arrays.asList(resultArray);
//    }
//
//    //后面要加上路径
//    private String getExecStr(String photoPath, String photoName, Long photoId) {
//        String condaPath = System.getenv("anaconda");//获取conda的系统变量
//        log.info("原始的condaPath：" + condaPath);
//        String activateStr = condaPath+"\\activate.bat " + "&&" + "conda activate yolo" + "&&";
//        String cdOrderStr = "e: && " + "cd " + defectAppPath + "&&";
//        //photoPath 暂时先这样，后面要直接用引进的，要注意格式的转换
//        photoPath = " E:/yolo/yolov5/yolov5-master/data/4class_dataset/JPEGImages/DJI_00003.jpg ";
//        String resultPhotoPathOrderStr = " --rote " + detectedFilePath + photoId + "/" + " --device cpu ";
//        String resultPhotoNameOrderStr = " --detectedPhotoName " + photoName;
//        String pyOrderStr = "python detect.py --source" + photoPath + "--weights " + defectModelPath;
//        String ordeStr = cdOrderStr + pyOrderStr + resultPhotoPathOrderStr + resultPhotoNameOrderStr;
//        log.info("原始的ordeStr：" + ordeStr);
//        String execStr = activateStr + ordeStr;
//        log.info("执行的命令：" + execStr);
//        return execStr;
//    }
}