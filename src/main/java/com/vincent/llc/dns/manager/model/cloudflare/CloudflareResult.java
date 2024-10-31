package com.vincent.llc.dns.manager.model.cloudflare;

import java.io.Serializable;
import java.time.OffsetDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class CloudflareResult extends CloudflareRequest {
    private String id;
    private String zoneID;
    private String zoneName;
    private String content;
    private boolean proxiable;
    private Object settings;
    private Meta meta;
    private String comment;
    private OffsetDateTime createdOn;
    private OffsetDateTime modifiedOn;
    private OffsetDateTime commentModifiedOn;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Meta implements Serializable {
        private boolean autoAdded;
        private boolean managedByApps;
        private boolean managedByArgoTunnel;
    }
}


