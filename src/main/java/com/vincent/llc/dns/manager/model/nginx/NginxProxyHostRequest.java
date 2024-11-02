package com.vincent.llc.dns.manager.model.nginx;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.viescloud.llc.viesspringutils.config.json.JsonIntOrBooleanDeserializer;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class NginxProxyHostRequest implements Serializable {

    @JsonProperty("domain_names")
    private List<String> domainNames;

    @JsonProperty("forward_scheme")
    private String forwardScheme;

    @JsonProperty("forward_host")
    private String forwardHost;

    @JsonProperty("forward_port")
    private long forwardPort;

    @JsonDeserialize(using = JsonIntOrBooleanDeserializer.class)
    @JsonProperty("caching_enabled")
    private boolean cachingEnabled;

    @JsonDeserialize(using = JsonIntOrBooleanDeserializer.class)
    @JsonProperty("block_exploits")
    private boolean blockExploits;

    @JsonDeserialize(using = JsonIntOrBooleanDeserializer.class)
    @JsonProperty("allow_websocket_upgrade")
    private boolean allowWebsocketUpgrade;

    @JsonProperty("access_list_id")
    private Integer accessListID;

    @JsonProperty("certificate_id")
    private long certificateID;

    @JsonDeserialize(using = JsonIntOrBooleanDeserializer.class)
    @JsonProperty("ssl_forced")
    private boolean sslForced;

    @JsonDeserialize(using = JsonIntOrBooleanDeserializer.class)
    @JsonProperty("http2_support")
    private boolean http2Support;

    @JsonDeserialize(using = JsonIntOrBooleanDeserializer.class)
    @JsonProperty("hsts_enabled")
    private boolean hstsEnabled;

    @JsonDeserialize(using = JsonIntOrBooleanDeserializer.class)
    @JsonProperty("hsts_subdomains")
    private boolean hstsSubdomains;

    private NginxMeta meta;

    @JsonProperty("advanced_config")
    private String advancedConfig;

    private List<NginxLocation> locations;
}