package com.vincent.llc.dns.manager.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.viescloud.llc.viesspringutils.repository.DatabaseCall;
import com.viescloud.llc.viesspringutils.util.DateTime;
import com.vincent.llc.dns.manager.feign.CloudflareClient;
import com.vincent.llc.dns.manager.model.cloudflare.CloudflareResult;

@Service
public class VieslocalCloudflareService extends CloudflareService {

    @Value("${cloudflare.email}")
    private String cloudflareEmail;

    @Value("${cloudflare.key}")
    private String cloudflareKey;

    @Value("${cloudflare.vieslocal.zoneId}")
    private String cloudflareVieslocalZoneId;

    public VieslocalCloudflareService(CloudflareClient cloudflareClient,
            DatabaseCall<String, List<CloudflareResult>, ?> dnsListCache, DatabaseCall<String, CloudflareResult, ?> dnsCache) {
        super(cloudflareClient, dnsListCache, dnsCache);
        dnsListCache.init("com.vincent.llc.dns.manager.service.VieslocalCloudflareService.dnsListCache");
        dnsListCache.setTTL(DateTime.ofDays(1));
        dnsCache.init("com.vincent.llc.dns.manager.service.VieslocalCloudflareService.dnsCache");
        dnsCache.setTTL(DateTime.ofDays(1));
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
        return this.cloudflareVieslocalZoneId;
    }

    @Override
    protected String content() {
        return "vieslocal.com";
    }
    
}
