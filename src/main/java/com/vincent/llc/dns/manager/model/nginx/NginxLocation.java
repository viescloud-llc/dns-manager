package com.vincent.llc.dns.manager.model.nginx;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NginxLocation {
    private String path;
    private String advancedConfig;
    private String forwardScheme;
    private String forwardHost;
    private long forwardPort;
}
