package com.vincent.llc.dns.manager.model;

import com.vincent.llc.dns.manager.service.CloudflareService;
import com.vincent.llc.dns.manager.service.NginxService;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DnsPackage {
    private NginxService nginxService;
    private CloudflareService cloudflareService;
    private String mainDomainName;
    private boolean proxied;
}
