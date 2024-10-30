package com.vincent.llc.dns.manager.model.nginx;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NginxLocation implements Serializable {
    private String path;

    @JsonProperty("advanced_config")
    private String advancedConfig;

    @JsonProperty("forward_scheme")
    private String forwardScheme;

    @JsonProperty("forward_host")
    private String forwardHost;

    @JsonProperty("forward_port")
    private long forwardPort;
}
