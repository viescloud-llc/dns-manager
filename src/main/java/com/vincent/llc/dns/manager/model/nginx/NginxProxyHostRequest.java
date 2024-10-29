package com.vincent.llc.dns.manager.model.nginx;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NginxProxyHostRequest implements Serializable {
    private List<String> domainNames;
    private String forwardScheme;
    private String forwardHost;
    private long forwardPort;
    private boolean cachingEnabled;
    private boolean blockExploits;
    private boolean allowWebsocketUpgrade;
    private String accessListID;
    private long certificateID;
    private boolean sslForced;
    private boolean http2Support;
    private boolean hstsEnabled;
    private boolean hstsSubdomains;
    private NginxMeta meta;
    private String advancedConfig;
    private List<NginxLocation> locations;
}