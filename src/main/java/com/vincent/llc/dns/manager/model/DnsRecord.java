package com.vincent.llc.dns.manager.model;

import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import com.vincent.llc.dns.manager.model.cloudflare.CloudflareResult;
import com.vincent.llc.dns.manager.model.nginx.NginxProxyHostRequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DnsRecord implements Serializable {
    private URI uri;
    private boolean enabledPublicNginx;
    private boolean enabledLocalNginx;

    private NginxProxyHostRequest localNginxRecord;
    private NginxProxyHostRequest publicNginxRecord;

    @Builder.Default
    private List<CloudflareResult> cloudflareViescloudRecord = new ArrayList<>();

    @Builder.Default
    private List<CloudflareResult> cloudflareViesLocalRecord = new ArrayList<>();
}
