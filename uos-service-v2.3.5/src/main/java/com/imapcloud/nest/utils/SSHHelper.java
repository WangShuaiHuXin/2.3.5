package com.imapcloud.nest.utils;

import com.alibaba.fastjson.JSON;
import com.imapcloud.nest.v2.common.properties.AIConfig;
import com.imapcloud.nest.v2.common.properties.GeoaiUosProperties;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SSH工具类
 *
 * @author 王建国
 * 2019-12-29
 */
@Slf4j
@Component
public class SSHHelper {

    @Resource
    private GeoaiUosProperties geoaiUosProperties;

    /**
     * 远程 执行命令并返回结果调用过程 是同步的（执行完才会返回）
     * @param command 命令
     */
    public String exec(String command) {
        AIConfig aiConfig = geoaiUosProperties.getAi();
        String host = aiConfig.getHost();
        int port = aiConfig.getPort();
        String user = aiConfig.getUsername();
        String psw = aiConfig.getPassword();
        StringBuilder build = new StringBuilder();
        Session session = null;
        ChannelExec openChannel = null;
        try {
            long startTime = System.currentTimeMillis();
            JSch jsch = new JSch();
            session = jsch.getSession(user, host, port);
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.setPassword(psw);
            session.setTimeout(60 * 1000);
            session.connect();
            long middleTime = System.currentTimeMillis();
            openChannel = (ChannelExec) session.openChannel("exec");
            log.info("ssh执行的命令:{}", command);
            openChannel.setCommand(command);
            openChannel.connect();

            InputStream in = openChannel.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String buf;
            while ((buf = reader.readLine()) != null) {
                build.append(buf).append("\n");
            }
            log.info("执行的结果:{}", build);
            long endTime = System.currentTimeMillis();
            log.info("ssh连接时间:{}", (middleTime - startTime) / 1000);
            log.info("命令执行时间:{}", (endTime - startTime) / 1000);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (openChannel != null && !openChannel.isClosed()) {
                openChannel.disconnect();
            }
            if (session != null && session.isConnected()) {
                session.disconnect();
            }
        }
        return build.toString();
    }

    /**
     * 传入远程地址，执行脚本
     *
     * @param host
     * @param account
     * @param pwd
     * @param command
     * @return
     */
    public String exec(String host, String account, String pwd, String command) {
        StringBuilder build = new StringBuilder();
        Session session = null;
        ChannelExec openChannel = null;
        try {
            long startTime = System.currentTimeMillis();
            JSch jsch = new JSch();
            session = jsch.getSession(account, host);
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.setPassword(pwd);
            session.setTimeout(60 * 1000);
            session.connect();
            long middleTime = System.currentTimeMillis();
            openChannel = (ChannelExec) session.openChannel("exec");
            log.info("ssh执行的命令:{}", command);
            openChannel.setCommand(command);
            openChannel.connect();

            InputStream in = openChannel.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String buf;
            while ((buf = reader.readLine()) != null) {
                build.append(buf).append("\n");
            }
            log.info("执行的结果:{}", build);
            long endTime = System.currentTimeMillis();
            log.info("ssh连接时间:{}", (middleTime - startTime) / 1000);
            log.info("命令执行时间:{}", (endTime - startTime) / 1000);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (openChannel != null && !openChannel.isClosed()) {
                openChannel.disconnect();
            }
            if (session != null && session.isConnected()) {
                session.disconnect();
            }
        }
        return build.toString();
    }

    public List<String> exector(String cmd) {
        String[] cmds = {"/bin/sh", "-c", cmd};
        Process process;
        List<String> list = new ArrayList<>();
        try {
            process = Runtime.getRuntime().exec(cmds);
            BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8"));
            String line = null;
            while ((line = br.readLine()) != null) {
                list.add(line);
            }
            try {
                process.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        log.info("结果exec:" + list.toString());
        return list;
    }

    public static String doPostGateWay(File[] file, String url) throws Exception {
        String BOUNDARY = java.util.UUID.randomUUID().toString();
        MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE, "--------------------" + BOUNDARY, Charset.defaultCharset());
        for (int i = 0; i < file.length; i++) {
            if (i == 0) {
                multipartEntity.addPart("multipartFile", new FileBody(file[i]));
            } else {
                multipartEntity.addPart("multipartFile" + i, new FileBody(file[i]));
            }
        }
        return doPostGateWay(multipartEntity, url, BOUNDARY);
    }

    public static String doPostGateWay(String url) throws Exception {
        return doPostGateWay(null, url, null);
    }

    public static String doPostGateWay(File file, String url) throws Exception {
        String BOUNDARY = java.util.UUID.randomUUID().toString();
        MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE, "--------------------" + BOUNDARY, Charset.defaultCharset());
        multipartEntity.addPart("multipartFile", new FileBody(file));
        return doPostGateWay(multipartEntity, url, BOUNDARY);
    }

    public static String doPostGateWay(MultipartEntity multipartEntity, String url, String BOUNDARY) throws Exception {
        HttpPost request = new HttpPost(url);
        if (multipartEntity != null) {
            request.setEntity(multipartEntity);
        }
        if (BOUNDARY != null) {
            request.addHeader("Content-Type", "multipart/form-data; boundary=--------------------" + BOUNDARY);
        }
        //request.addHeader("Content-Type","image/jpeg");  //视情况而定

        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpResponse response = httpClient.execute(request);
        InputStream is = response.getEntity().getContent();
        BufferedReader in = new BufferedReader(new InputStreamReader(is));
        StringBuffer buffer = new StringBuffer();
        String line = "";
        while ((line = in.readLine()) != null) {
            buffer.append(line);
        }
        System.out.println("发送消息收到的返回：" + buffer.toString());
        return buffer.toString();
    }

    /**
     * 网关请求统一入口
     *
     * @param data
     * @param url
     * @param timeout
     * @return
     */
    private static int CONNECTTIMEOUT = 50000;
    private static int CONNECTIONREQUESTTIMEOUT = 50000;

    public static String doPostGateWay(Map data, String url, int timeout) {
        CloseableHttpResponse response = null;
        try {
            Map<String, String> map = new HashMap<String, String>();
            if (data == null) {
                data = map;
            }
            StringEntity entity = new StringEntity(JSON.toJSONString(data), "UTF-8");
            HttpPost httppost = new HttpPost(url);
            httppost.addHeader("accept", "application/json");
            httppost.addHeader("Charset", "UTF-8");
            httppost.addHeader("Content-Type", "application/json; charset=UTF-8");
            int defaultTimeOut = 50000;
            if (timeout > 0) {
                defaultTimeOut = timeout;
            }
            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(CONNECTTIMEOUT)
                    .setConnectionRequestTimeout(CONNECTIONREQUESTTIMEOUT).setSocketTimeout(defaultTimeOut).build();
            httppost.setConfig(requestConfig);
            log.debug("post url is {}.", url);
            httppost.setEntity(entity);
            httppost.setHeader("Content-Type", "application/json; charset=UTF-8");
            CloseableHttpClient httpclient = new DefaultHttpClient();
            response = httpclient.execute(httppost);
            int status = response.getStatusLine().getStatusCode();
            if (status == HttpStatus.OK.value()) {
                return EntityUtils.toString(response.getEntity(), "utf-8");
            } else {
                String error = EntityUtils.toString(response.getEntity(), "utf-8");
                log.error(error);
                throw new RuntimeException(error);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (null != response) {
                try {
                    EntityUtils.consume(response.getEntity());
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
    }

//    /**
//     * 从网络Url中下载文件
//     *
//     * @param urlStr
//     * @param fileName
//     * @param savePath
//     * @throws IOException
//     */
//    public static void downLoadFromUrl(String urlStr, String fileName, String savePath) throws IOException {
//        URL url = new URL(urlStr);
//        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//        //设置超时间为3秒
//        conn.setConnectTimeout(3 * 1000);
//        //防止屏蔽程序抓取而返回403错误
//        conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
//
//        //得到输入流
//        InputStream inputStream = conn.getInputStream();
//        //获取自己数组
//        byte[] getData = readInputStream(inputStream);
//
//        //文件保存位置
//        File saveDir = new File(savePath);
//        if (!saveDir.exists()) {
//            saveDir.mkdir();
//        }
//        File file = new File(saveDir + File.separator + fileName);
//        FileOutputStream fos = new FileOutputStream(file);
//        fos.write(getData);
//        if (fos != null) {
//            fos.close();
//        }
//        if (inputStream != null) {
//            inputStream.close();
//        }
//
//
//        System.out.println("info:" + url + " download success");
//
//    }


    /**
     * 从输入流中获取字节数组
     *
     * @param inputStream
     * @return
     * @throws IOException
     */
    public static byte[] readInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int len = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while ((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        bos.close();
        return bos.toByteArray();
    }
}