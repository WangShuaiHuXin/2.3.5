package com.imapcloud.nest.v2.manager.event;

import lombok.Data;
import org.springframework.context.ApplicationEvent;

import java.util.LinkedList;
import java.util.List;

/**
 * 基站单位变更事件
 * @author Vastfy
 * @date 2022/07/14 10:52
 * @since 1.9.7
 */
public class NestUnitChangedEvent extends ApplicationEvent {

    public NestUnitChangedEvent(NestChangedInfo source) {
        super(source);
    }

    @Data
    public static class NestChangedInfo {

        /**
         * 基站ID
         */
        private String nestId;

        /**
         * 期望单位链
         */
        private List<LinkedList<String>> expectUnitChains;

        /**
         * 待新增单位链
         */
        private List<LinkedList<String>> increasedUnitChains;

        /**
         * 待移除单位链
         */
        private List<LinkedList<String>> decreasedUnitChains;

    }

}
