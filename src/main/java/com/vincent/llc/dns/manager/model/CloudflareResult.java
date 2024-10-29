package com.vincent.llc.dns.manager.model;

import java.time.OffsetDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CloudflareResult {
    private String id;
    private String zoneID;
    private String zoneName;
    private String name;
    private String type;
    private String content;
    private boolean proxiable;
    private boolean proxied;
    private long ttl;
    private Settings settings;
    private Meta meta;
    private String comment;
    private Object[] tags;
    private OffsetDateTime createdOn;
    private OffsetDateTime modifiedOn;
    private OffsetDateTime commentModifiedOn;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Meta {
        private boolean autoAdded;
        private boolean managedByApps;
        private boolean managedByArgoTunnel;
    }
    
    public static class Settings {
    }
}


