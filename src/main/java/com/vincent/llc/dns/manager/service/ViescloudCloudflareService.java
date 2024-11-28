package com.vincent.llc.dns.manager.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.viescloud.llc.viesspringutils.repository.DatabaseCall;
import com.viescloud.llc.viesspringutils.util.DateTime;
import com.vincent.llc.dns.manager.feign.CloudflareClient;
import com.vincent.llc.dns.manager.model.cloudflare.CloudflareResult;

@Service
public class ViescloudCloudflareService extends CloudflareService {

    @Value("${cloudflare.email}")
    private String cloudflareEmail;

    @Value("${cloudflare.key}")
    private String cloudflareKey;

    @Value("${cloudflare.viescloud.zoneId}")
    private String cloudflareViescloudZoneId;

    public ViescloudCloudflareService(CloudflareClient cloudflareClient,
            DatabaseCall<String, List<CloudflareResult>> dnsListCache, DatabaseCall<String, CloudflareResult> dnsCache) {
        super(cloudflareClient, dnsListCache, dnsCache);
        dnsListCache.init("com.vincent.llc.dns.manager.service.ViescloudCloudflareService.dnsListCache").ttl(DateTime.ofDays(1));
        dnsCache.init("com.vincent.llc.dns.manager.service.ViescloudCloudflareService.dnsCache").ttl(DateTime.ofDays(1));
    }

    @Override
    public String cloudflareEmail() {
        return this.cloudflareEmail;
    }

    @Override
    public String cloudflareKey() {
        return this.cloudflareKey;
    }

    @Override
    public String cloudflareZoneId() {
        return this.cloudflareViescloudZoneId;
    }
    
    @Override
    protected String content() {
        return "viescloud.com";
    }
}
