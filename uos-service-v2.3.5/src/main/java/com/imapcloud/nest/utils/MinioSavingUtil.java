package com.imapcloud.nest.utils;

import com.imapcloud.nest.common.netty.ws.ChannelService;
import com.imapcloud.nest.enums.WebSocketTopicEnum;
import com.imapcloud.nest.utils.spring.SpringContextUtils;
import com.imapcloud.sdk.pojo.entity.NestLogUploadState;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @deprecated 2.3.0.1，CPS升级至v2.4.1版本后，该类无用
 */
@Deprecated
public class MinioSavingUtil {
    private static ScheduledExecutorService nestLogScheduledExecutorService = SpringContextUtils.getBean("nestLogScheduledExecutorService", ScheduledThreadPoolExecutor.class);

    private static final ExecutorService executorService = new ThreadPoolExecutor(4, 16, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(64), new ThreadPoolExecutor.DiscardPolicy());

    private static final Map<String, SfExpandDto> sfMap = new ConcurrentHashMap<>();

    /**
     * 开始推送
     *
     * @param nestUuid
     */
    @Deprecated
    public static void startScheduleSendMinioSaving(String nestUuid) {
        SfExpandDto sfExpandDto = sfMap.get(nestUuid);
        if (sfExpandDto != null) {
            ScheduledFuture<?> scheduledFuture = sfExpandDto.getScheduledFuture();
            if (scheduledFuture != null) {
                scheduledFuture.cancel(true);
            }
        }
        ScheduledFuture<?> sf = nestLogScheduledExecutorService.scheduleAtFixedRate(() -> sendMinioSavingRunnable(nestUuid, NestLogUploadState.StateEnum.MINIO_SAVING), 2, 1, TimeUnit.SECONDS);
        SfExpandDto newSfExpandDto = new SfExpandDto();
        newSfExpandDto.setStartTime(System.currentTimeMillis());
        newSfExpandDto.setScheduledFuture(sf);
        sfMap.put(nestUuid, newSfExpandDto);
    }

    @Deprecated
    public static void cancelSf(String nestUuid) {
        SfExpandDto sfExpandDto = sfMap.get(nestUuid);
        if (sfExpandDto != null && sfExpandDto.getScheduledFuture() != null) {
            sfExpandDto.getScheduledFuture().cancel(true);
            sfExpandDto.getScheduledFuture().cancel(false);
        }
    }

    @Deprecated
    public static void sendMinioSavingRunnable(String nestUuid, NestLogUploadState.StateEnum state) {
        Map<String, Object> wsData = new HashMap<>(2);
        wsData.put("state", state.getExpress());
        String message = WebSocketRes.ok().topic(WebSocketTopicEnum.NEST_LOGS_PROCESS).data(wsData).toJSONString();
        ChannelService.sendMessageByType11Channel(nestUuid, message);
    }

    public static class SfExpandDto {
        private ScheduledFuture<?> scheduledFuture;
        private Long startTime;

        public ScheduledFuture<?> getScheduledFuture() {
            return scheduledFuture;
        }

        public void setScheduledFuture(ScheduledFuture<?> scheduledFuture) {
            this.scheduledFuture = scheduledFuture;
        }

        public Long getStartTime() {
            return startTime;
        }

        public void setStartTime(Long startTime) {
            this.startTime = startTime;
        }
    }
}
