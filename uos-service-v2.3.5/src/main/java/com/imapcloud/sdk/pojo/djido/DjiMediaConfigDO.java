package com.imapcloud.sdk.pojo.djido;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@AllArgsConstructor
public class DjiMediaConfigDO {

    private String bucket ;
    private String endpoint ;
    private String object_key_prefix;
    private String provider = "minio";
    private String region = "gz";
    private Credentials credentials;
    private String environment;

    public DjiMediaConfigDO(){
        credentials = new Credentials();
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Credentials{
        private String access_key_id ;

        private String access_key_secret ;

        private int expire = 3600;

        private String security_token ="";
    }

}

