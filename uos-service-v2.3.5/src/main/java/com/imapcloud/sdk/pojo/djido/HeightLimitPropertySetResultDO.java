package com.imapcloud.sdk.pojo.djido;

import lombok.Data;

/**
 * @author wmin
 * 设备属性设置的返回值
 * 注意：属性设置返回结果类要严格遵循以下命名规范
 * 如设置限高的返回结果:
 * {
 * "bid" : "c8cf6960-60c7-489c-a012-9efd0c0e746a",
 * "data" : {
 * "height_limit" : {
 * "result" : 319999
 * }
 * },
 * "tid" : "59ff5377-f2ad-4b9b-a97b-60ede3d61259",
 * "timestamp" : 1675933676322
 * }
 * 看data中的值 height_limit,所以结果解析类命名为HeightLimitPropertySetResultDO
 */
@Data
public class HeightLimitPropertySetResultDO {
    private HeightLimit heightLimit;

    @Data
    public static class HeightLimit {
        private Integer result;
    }

}
