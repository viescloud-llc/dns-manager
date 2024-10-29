package com.vincent.llc.dns.manager.model.nginx;

import java.time.OffsetDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NginxProxyHostResponse {
    private long id;
    private OffsetDateTime createdOn;
    private OffsetDateTime modifiedOn;
    private long ownerUserID;
    private List<String> domainNames;
    private String forwardHost;
    private long forwardPort;
    private long accessListID;
    private long certificateID;
    private long sslForced;
    private long cachingEnabled;
    private long blockExploits;
    private String advancedConfig;
    private NginxMeta meta;
    private long allowWebsocketUpgrade;
    private long http2Support;
    private String forwardScheme;
    private long enabled;
    private List<NginxLocation> locations;
    private long hstsEnabled;
    private long hstsSubdomains;
}