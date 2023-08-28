package com.imapcloud.nest.v2.manager.rest;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiRobotSendRequest;
import com.geoai.common.core.util.DateUtils;
import com.google.common.collect.Maps;
import com.imapcloud.nest.v2.manager.dataobj.in.DingTalkInDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 钉钉服务
 *
 * @author boluo
 * @date 2022-11-10
 */
@Slf4j
@Component
public class DingTalkManager implements ApplicationContextAware {

    @Resource
    private DingTalkClient systemDingTalkClient;

    private static ApplicationContext applicationContext;

    public void send(DingTalkInDO.SendInDO sendInDO) {

        log.info("#DingTalkManager.send# sendInDO={}", sendInDO);
        try {
            OapiRobotSendRequest request = new OapiRobotSendRequest();
            request.setMsgtype(sendInDO.getMsgType());
            OapiRobotSendRequest.Markdown markdown = new OapiRobotSendRequest.Markdown();
            markdown.setText(sendInDO.getText());
            markdown.setTitle(sendInDO.getTitle());
            request.setMarkdown(markdown);

            OapiRobotSendRequest.At at = new OapiRobotSendRequest.At();
            at.setIsAtAll(sendInDO.getAt());
            request.setAt(at);
            systemDingTalkClient.execute(request);
        } catch (Exception e) {
            log.error("#DingTalkManager.send# sendInDO={}", sendInDO, e);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        applicationContext = context;
    }

    public static void sendStart(String applicationName, long time, boolean success) {

        int minute = (int) (time / 60);
        int second = (int) (time % 60);
        String activeProfile = "未知";
        String url = "https://oapi.dingtalk.com/robot/send?access_token=437c339862c4a8172a1b33d24397fa89fb8ba8a3d6325c83747dc0ca36abd471";
        if (applicationContext != null) {
            activeProfile = applicationContext.getEnvironment().getActiveProfiles()[0];
            url = applicationContext.getEnvironment().getProperty("geoai.uos.dingTalk.systemRobot");
        }

        String text = String.format("## UOS启动通知 \n " +
                        "---------- \n" +
                        "### 项目：%s \n " +
                        "### 环境：%s \n " +
                        "### 时间：%s \n" +
                        "### 启动时长：%s \n" +
                        "### 状态：%s \n" +
                        "---------- \n"
                , applicationName
                , activeProfile
                , LocalDateTime.now().format(DateUtils.DATE_TIME_FORMATTER_OF_CN)
                , String.format("%02d:%02d", minute, second)
                , success ? "<font color=\"#00dd00\">启动成功</font>" : "<font color=\"#dd0000\">启动失败</font>"
        );
        Map<String, Object> param = Maps.newHashMap();
        param.put("msgtype", "markdown");
        Map<String, Object> markDown = Maps.newHashMap();
        markDown.put("title", "服务器启动状态");
        markDown.put("text", text);
        param.put("markdown", markDown);
        String post = HttpUtil.post(url ,JSONUtil.toJsonStr(param));
        log.info("#DingTalkManager.sendStart# result={}", post);
    }
}
