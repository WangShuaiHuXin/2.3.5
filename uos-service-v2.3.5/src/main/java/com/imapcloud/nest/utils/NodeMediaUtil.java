package com.imapcloud.nest.utils;

import com.alibaba.fastjson.JSONObject;
import com.imapcloud.nest.service.MediaServiceService;
import com.imapcloud.nest.utils.nms.NmsRes;
import com.imapcloud.nest.utils.redis.RedisKeyEnum;
import com.imapcloud.nest.utils.redis.RedisService;
import com.imapcloud.nest.v2.common.properties.GeoaiUosProperties;
import com.imapcloud.nest.v2.common.properties.IptvConfig;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.annotation.Resource;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;

/**
 * nodeMedia工具类
 *
 * @author: zhengxd
 * @create: 2020/10/27
 * @deprecated 2.3.2，已废弃NMS，替换为ZLM，将在后续版本中移除该类
 **/
@Deprecated
@Service
@Slf4j
//@EnableAsync
public class NodeMediaUtil {

    @Autowired
    private RedisService redisService;

    @Autowired
    private HttpRequestUtil httpRequestUtil;

    @Autowired
    private MediaServiceService mediaServiceService;

    @Resource
    private GeoaiUosProperties geoaiUosProperties;

    private final static String LOGIN = "/api/login";
    private final static String STREAM = "/api/stream";

    private final static String STREAMS = "/api/streams";
    private final static String RECORD = "/api/record/";
    private final static String SCREENSHOT = "/api/screenshot/";
    private final static String STATS = "/api/stats";
    private final static String HEALTH = "/api/health";
    //relay任务
    private final static String RELAY = "/api/relay";
    private final static String RELAYS = "/api/relays";
    private final static String RELAYRULE = "/api/relayrule";
    private final static String RE_NULL = "nill";
    public final String CODEC_ERR = "UNKNOWN";
    public final Integer CODE_SUCCESS = 200;
    public final Integer CODE_NOT_FOUND = 404;

    @Data
    public static class CapParam {
        private String filepath;
        private String filename;
        private Integer recordtime;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 登录获取token
     *
     * @param username 用户名
     * @param password 密码
     * @param host     连接地址
     * @return
     */
    public String login(String username, String password, String host) {
        // 登陆nodeMedia
        String url = host + LOGIN;
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        JSONObject reqParam = new JSONObject();
        reqParam.put("username", username);
        reqParam.put("password", password);
        NmsRes res = sendRequest(url, null, reqParam, HttpMethod.POST, NmsRes.class);
        Map map = (LinkedHashMap) res.getData();
        if (Objects.nonNull(map)) {
            return (String) map.get("token");
        }
        return null;
    }

    /**
     * 获取nms的token：
     * 先在redis中获取，获取不到则登陆nms后获取
     *
     * @return
     */
    public String getToken() {
        IptvConfig iptv = geoaiUosProperties.getIptv();
        String password = iptv.getPassword();
        // 在缓存里获取token,获取不到则登陆nms获取
        String redisKey = RedisKeyEnum.REDIS_KEY.className("NodeMediaUtil").methodName("init").identity("password", DigestUtils.md5DigestAsHex(password.getBytes())).type("String").get();
        String token = (String) redisService.get(redisKey);
        if (ToolUtil.isEmpty(token)) {
            token = login(iptv.getUsername(), password, iptv.getUrl());
            // 将token存到redis里
            redisService.set(redisKey, token);
        }
        return token;
    }

    /**
     * 健康监测
     *
     * @param token
     * @param host
     * @return 正常则返回 ok
     */
    public String healthTest(String token, String host) {
        String url = host + HEALTH;
        return sendRequest(url, token, null, HttpMethod.GET, String.class);
    }

    /**
     * 获取服务器状态
     *
     * @param token token
     * @param host  连接地址
     * @return
     */
    public NmsRes getServerState(String token, String host) {
        String url = host + STATS;
        return sendRequest(url, token, null, HttpMethod.GET, NmsRes.class);
    }

    /**
     * 获取服务器过期时间
     *
     * @param token token
     * @param host  连接地址
     * @return
     */
    public Integer getServerExpireTime(String token, String host) {
        NmsRes res = getServerState(token, host);
        String exptime = Optional.ofNullable(res)
                .flatMap(data -> Optional.ofNullable(data.getData()))
                .flatMap(jsonObject -> Optional.ofNullable(new JSONObject((LinkedHashMap) jsonObject).getJSONObject("nms")))
                .flatMap(jsonObject -> Optional.ofNullable(jsonObject.getString("exptime")))
                .orElse(RE_NULL);
        if (exptime != RE_NULL) {
            LocalDate exDate = LocalDate.parse(exptime);
            LocalDate today = LocalDate.now();
            Integer days = Math.toIntExact(exDate.toEpochDay() - today.toEpochDay());
            return days;
        }
        return -1;
    }

    /**
     * 截图
     *
     * @param filepath
     * @param filename
     * @param appName
     * @param token
     * @param host
     * @return
     */
    public NmsRes screenshot(String filepath, String filename, String appName, String token, String host) {
        String url = host + SCREENSHOT + appName;
        JSONObject reqParam = new JSONObject();
        reqParam.put("filepath", filepath);
        reqParam.put("filename", filename);
        return sendRequest(url, token, reqParam, HttpMethod.POST, NmsRes.class);
    }

    /**
     * 异步截图
     *
     * @param filepath
     * @param filename
     * @param appName
     * @param token
     * @param host
     * @return
     */
//    @Async("asyncMedia")
    public void screenshotAsync(String filepath, String filename, String appName, String token, String host) {
        String url = host + SCREENSHOT + appName;
        Map reqParam = new HashMap();
        reqParam.put("filepath", filepath);
        reqParam.put("filename", filename);
        sendRequestAsync(url, token, reqParam, HttpMethod.POST);
    }

    /**
     * 异步创建录像任务
     *
     * @param filepath   文件路径
     * @param filename   文件名
     * @param recordtime 录制时间
     * @param appName    推流app
     * @param token      token
     * @param host       host地址
     * @return
     */
    public void creatRecordAsync(String filepath, String filename, Integer recordtime, String appName, String token, String host) {
        String url = host + RECORD + appName;
        Map reqParam = new HashMap();
        reqParam.put("filepath", filepath);
        reqParam.put("filename", filename);
        reqParam.put("recordtime", recordtime);
        sendRequestAsync(url, token, reqParam, HttpMethod.POST);
    }

    /**
     * 响应式创建录像任务
     *
     * @param filepath   文件路径
     * @param filename   文件名
     * @param recordtime 录制时间
     * @param appName    推流app
     * @param token      token
     * @param host       host地址
     * @return
     */
//    @Async("asyncMedia")
    public void creatRecordReact(String filepath, String filename, Integer recordtime, String appName, String token, String host) {
        String url = host + RECORD + appName;
        CapParam reqParam = new CapParam();
        reqParam.setFilename(filename);
        reqParam.setFilepath(filepath);
        reqParam.setRecordtime(recordtime);
        reactRequest(url, token, reqParam, HttpMethod.POST);
        log.info("异步响应式录像任务请求发送成功");
    }

    /**
     * 异步结束录像任务
     *
     * @param appName 推流appName
     * @param token   token
     * @param host    host地址
     * @return
     */
//    @Async("asyncMedia")
    public void finishRecordAsync(String appName, String token, String host) {
        String url = host + RECORD + appName;
        sendRequestAsync(url, token, null, HttpMethod.DELETE);
        log.info("异步结束录像任务请求发送成功");
    }

    /**
     * 查询录像任务
     *
     * @param appName 流app/streamName
     * @param token   token
     * @param host    连接地址
     * @return
     */
    public NmsRes getRecord(String appName, String token, String host) {
        String url = host + RECORD + appName;
        return sendRequest(url, token, null, HttpMethod.GET, NmsRes.class);
    }


    /**
     * 结束录像
     *
     * @param appName 流app/streamName
     * @param token   token
     * @param host    连接地址
     * @return
     */
    public NmsRes finishRecord(String appName, String token, String host) {
        String url = host + RECORD + appName;
        return sendRequest(url, token, null, HttpMethod.DELETE, NmsRes.class);
    }

    /**
     * 创建relay任务
     *
     * @param mode         重连模式 0-永久自动重连 1-心跳保持型(用创建后的id查询流状态保活) 2-输入输出没有错误时自动重连 3-输入流结束后停止
     * @param inUrl        自定义取流地址
     * @param outUrl       自定义推流地址
     * @param disableAudio 是否关闭音频
     * @param token        token
     * @param host         请求ip:port
     * @param comment      描述
     * @return
     */
    public NmsRes createRelay(Integer mode, String inUrl, String outUrl, Boolean disableAudio, String token, String host, String comment) {
        String url = host + RELAY;
        //解析云直播流地址，如：https://live.iflyer360.com/live/4f609737.flv,或rtmp://live.iflyer360.com/live/4f609737
        String[] split = inUrl.trim().split("/");
        String app = split[split.length - 2];
        String name = split[split.length - 1];
        if (name.contains(".flv")) {
            //特殊符号在正则中需要转义
            name = name.split("\\.")[0];
        }
        if (outUrl == null || outUrl.isEmpty()) {
            outUrl = String.format("rtmp://127.0.0.1/%s/%s", app, name);
        }
        JSONObject param = new JSONObject();
        param.put("mode", mode);
        param.put("in_url", inUrl);
        param.put("out_url", outUrl);
        param.put("disable_audio", disableAudio);
        param.put("comment", comment);
        NmsRes res = sendRequest(url, token, param, HttpMethod.POST, NmsRes.class);
        Map data = (LinkedHashMap) res.getData();
        if (data != null) {
            data.put("app", app);
            data.put("name", name);
            res.setData(data);
        }
        return res;
    }

    /**
     * 根据查询单个relay任务
     *
     * @param id
     * @param token
     * @param host
     * @return 任务现在的状态 status 0-停止 1-正在连接 2-任务开始 3-输入流打开错误 4-输出流打开错误 5-正在准备重连
     */
    public NmsRes getRelay(String id, String token, String host) {
        String url = host + RELAY + String.format("/%s", id);
        return sendRequest(url, token, null, HttpMethod.GET, NmsRes.class);
    }

    /**
     * 查询所有relay任务
     *
     * @param token
     * @param host
     * @return 任务现在的状态 status 0-停止 1-正在连接 2-任务开始 3-输入流打开错误 4-输出流打开错误 5-正在准备重连
     */
    public NmsRes getRelays(String token, String host) {
        String url = host + RELAYS;
        return sendRequest(url, token, null, HttpMethod.GET, NmsRes.class);
    }

    /**
     * 删除relay任务
     *
     * @param id
     * @param token
     * @param host
     * @return
     */
    public NmsRes delRelay(String id, String token, String host) {
        String url = host + RELAY + String.format("/%s", id);
        return sendRequest(url, token, null, HttpMethod.DELETE, NmsRes.class);
    }

    /**
     * 删除流
     *
     * @param id
     * @param token
     * @param host
     * @return
     */
    public NmsRes delStream(String id, String token, String host) {
        String url = host + STREAM + String.format("/%s", id);
        return sendRequest(url, token, null, HttpMethod.DELETE, NmsRes.class);
    }


    /**
     * 获取流信息 app/name
     *
     * @param appName
     * @param token
     * @param host
     * @return
     */
    public NmsRes getStream(String appName, String token, String host) {
        String url = host + STREAMS + "/" + appName;
        return sendRequest(url, token, null, HttpMethod.GET, NmsRes.class);
    }

    /**
     * 根据流名称获取流信息
     *
     * @param app
     * @param name
     * @param token
     * @param host
     * @return
     */
    public NmsRes getStream(String app, String name, String token, String host) {
        String url;
        if (app == null || app.length() == 0) {
            url = host + STREAMS;
        } else {
            url = host + STREAMS + String.format("/%s", app);
            url = url + ((name == null || name.length() == 0) ? "" : String.format("/%s", name));
        }
        return sendRequest(url, token, null, HttpMethod.GET, NmsRes.class);
    }

    /**
     * 获取流的视频编码，一般用来验证是否推流正常
     *
     * @param app
     * @param name
     * @param token
     * @param host
     * @return
     */
    public String getVideoCodec(String app, String name, String token, String host) {
        NmsRes res = getStream(app, name, token, host);
        String codec = Optional.ofNullable(res)
                .flatMap(data -> Optional.ofNullable(data.getData()))
                .flatMap(jsonObject -> Optional.ofNullable(new JSONObject((LinkedHashMap) jsonObject).getJSONObject(name)))
                .flatMap(jsonObject -> Optional.ofNullable(jsonObject.getJSONObject("publisher")))
                .flatMap(jsonObject -> Optional.ofNullable(jsonObject.getJSONObject("video")))
                .flatMap(jsonObject -> Optional.ofNullable(jsonObject.getString("codec")))
                .orElse("UNKNOWN");
        return codec;
    }

    /**
     * 获取流的视频编码，一般用来验证是否推流正常
     *
     * @param appName
     * @param token
     * @param host
     * @return
     */
    public String getVideoCodec(String appName, String token, String host) {
        NmsRes res = getStream(appName, token, host);
        String codec = Optional.ofNullable(res)
                .flatMap(data -> Optional.ofNullable(data.getData()))
                .flatMap(jsonObject -> Optional.ofNullable(new JSONObject((LinkedHashMap) jsonObject).getJSONObject(appName.split("/")[0])))
                .flatMap(jsonObject -> Optional.ofNullable(jsonObject.getJSONObject(appName.split("/")[1])))
                .flatMap(jsonObject -> Optional.ofNullable(jsonObject.getJSONObject("publisher")))
                .flatMap(jsonObject -> Optional.ofNullable(jsonObject.getJSONObject("video")))
                .flatMap(jsonObject -> Optional.ofNullable(jsonObject.getString("codec")))
                .orElse(CODEC_ERR);
        return codec;
    }

    /**
     * 创建转发规则
     *
     * @param node  转发的目标节点（rtmp://ip:port）
     * @param app
     * @param token
     * @param host
     * @return
     */
    public NmsRes createRelayRule(String node, String app, String token, String host) {
        String url = host + RELAYRULE;
        JSONObject param = new JSONObject();
        JSONObject in = new JSONObject();
        JSONObject out = new JSONObject();
        //入方向
        in.put("node", "");
        in.put("app", app);
        in.put("name", "*");
        in.put("arg", "");
        //转发到的节点
        out.put("node", node);
        out.put("app", app);
        out.put("name", "*");
        out.put("arg", "");
        //最终参数
        param.put("ispull", false);
        param.put("in", in);
        param.put("out", out);
        return sendRequest(url, token, param, HttpMethod.POST, NmsRes.class);
    }

    /**
     * 删除转发规则
     *
     * @param id
     * @param token
     * @param host
     * @return
     */
    public NmsRes delRelayRule(String id, String token, String host) {
        String url = host + RELAYRULE + String.format("/%s", id);
        return sendRequest(url, token, null, HttpMethod.DELETE, NmsRes.class);
    }

    /**
     * 根据流地址获取app/name
     *
     * @param streamUrl
     * @return
     */
    public String getAppName(String streamUrl) {
        //解析云直播流地址，如：https://live.iflyer360.com/live/4f609737.flv,或rtmp://live.iflyer360.com/live/4f609737
        String[] split = streamUrl.trim().split("/");
        String app = split[split.length - 2];
        String name = split[split.length - 1];
        if (name.contains(".flv")) {
            //特殊符号在正则中需要转义
            name = name.split("\\.")[0];
        }
        return app + String.format("/%s", name);
    }

//    /**
//     * 判断该流是否需要转发
//     */
//    public Boolean needRelay(String streamUrl) {
//        //判断流是否为第三方，不是的话则无需转发
//        List<String> thirdPartyDomains = mediaServiceService.getThirdPartyDomains();
//        Long match = thirdPartyDomains.stream().filter(d -> streamUrl.contains(d)).count();
//        if (match < 0) {
//            return false;
//        }
//        return true;
//    }

    /**
     * 获取base64加密字串
     *
     * @param str
     * @return
     */
    public String base64Enco(String str) {
        BASE64Encoder base64Encoder = new BASE64Encoder();
        return base64Encoder.encode(str.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 获取base64字串的原始字串
     *
     * @param str
     * @return
     */
    public String base64Deco(String str) {
        BASE64Decoder base64Decoder = new BASE64Decoder();
        byte[] bytes = new byte[0];
        try {
            bytes = base64Decoder.decodeBuffer(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new String(bytes);
    }

    public void execRemoteCmd() {

    }

    /**
     * 创建录屏请求方法，增加录屏时间参数，保证不会因未正常终止导致录像占用
     *
     * @param token
     * @param url
     * @return
     */
    private <T> T sendRequest(String url, String token, Object reqParam, HttpMethod method, Class T) {
        ResponseEntity<T> result = httpRequestUtil.sendRequest2(url, method, reqParam, T, null, token);
        return result.getBody();
    }

    /**
     * 创建录屏请求方法，增加录屏时间参数，保证不会因未正常终止导致录像占用
     *
     * @param token
     * @param url
     * @return
     */
    private void sendRequestAsync(String url, String token, Map reqParam, HttpMethod method) {
        httpRequestUtil.sendRequestAsync(url, method, reqParam, token);
    }

    /**
     * 通过非阻塞方式发送http请求
     *
     * @param url
     * @param token
     * @param reqParam
     * @param method
     */
    private void reactRequest(String url, String token, CapParam reqParam, HttpMethod method) {
        httpRequestUtil.sendRequestFlux(url, token, reqParam, method);
    }

}
