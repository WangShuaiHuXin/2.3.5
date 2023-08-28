package com.imapcloud.sdk.pojo.djido;

import lombok.Data;

/**
 * @author wmin
 */
@Data
public class DistanceLimitStatusPropertySetResultDO {

    private DistanceLimitStatus distanceLimitStatus;

    @Data
    public static class DistanceLimitStatus {

        private State state;

        private DistanceLimit distanceLimit;

        @Data
        public static class State {
            private Integer result;
        }

        @Data
        public static class DistanceLimit {
            private Integer result;
        }
    }


}
