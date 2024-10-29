package com.vincent.llc.dns.manager.service;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.viescloud.llc.viesspringutils.exception.HttpResponseThrowers;
import com.viescloud.llc.viesspringutils.repository.DatabaseCall;
import com.viescloud.llc.viesspringutils.util.DateTime;
import com.vincent.llc.dns.manager.feign.CloudflareClient;
import com.vincent.llc.dns.manager.feign.LocalNginxClient;
import com.vincent.llc.dns.manager.feign.NginxClient;
import com.vincent.llc.dns.manager.feign.PublicNginxClient;
import com.vincent.llc.dns.manager.model.DnsRecord;
import com.vincent.llc.dns.manager.model.nginx.NginxLoginRequest;

@Service
public class DnsService {

    private static final String KEY_RECORD_PREFIX = "com.vincent.llc.dns.manager.service.record";
    private static final String KEY_RECORDS = "all";

    private static final String KEY_JWT_PREFIX = "com.vincent.llc.dns.manager.service.jwt";
    private static final String KEY_JWT_LOCAL_NGINX = "localNginx";
    private static final String KEY_JWT_PUBLIC_NGINX = "publicNginx";
    
    @Autowired
    private CloudflareClient cloudflareClient;

    @Autowired
    private LocalNginxClient localNginxClient;

    @Autowired
    private PublicNginxClient publicNginxClient;

    @Value("${cloudflare.email}")
    private String cloudflareEmail;

    @Value("${cloudflare.key}")
    private String cloudflareKey;

    @Value("${cloudflare.viescloud.zoneId}")
    private String cloudflareViescloudZoneId;

    @Value("${cloudflare.vieslocal.zoneId}")
    private String cloudflareVieslocalZoneId;

    @Value("${nginx.email}")
    private String nginxEmail;

    @Value("${nginx.password}")
    private String nginxPassword;

    public DatabaseCall<String, List<DnsRecord>, ?> dnsRecordsCache;
    public DatabaseCall<String, Map<String, String>, ?> jwtCache;

    public DnsService(DatabaseCall<String, List<DnsRecord>, ?> dnsRecordsCache, DatabaseCall<String, Map<String, String>, ?> jwtCache) {
        this.dnsRecordsCache = dnsRecordsCache;
        this.dnsRecordsCache.init(KEY_RECORD_PREFIX);
        this.dnsRecordsCache.setTTL(DateTime.ofMinutes(60));

        this.jwtCache = jwtCache;
        this.jwtCache.init(KEY_JWT_PREFIX);
        this.jwtCache.setTTL(DateTime.ofDays(1));
    }

    public List<DnsRecord> getAllDnsRecord() {
        List<DnsRecord> records = dnsRecordsCache.get(KEY_RECORDS);
        if(records != null) {
            return records;
        }

        Map<String, DnsRecord> recordMap = new HashMap<>();
        this.fetchAllPublicDnsRecords(recordMap);
        this.fetchAllLocalDnsRecords(recordMap);
        
        var result = new ArrayList<>(recordMap.values());
        this.dnsRecordsCache.saveAndExpire(KEY_RECORDS, result);
        return result;
    }

    private void fetchAllPublicDnsRecords(Map<String, DnsRecord> recordMap) {
        var proxyHosts = this.publicNginxClient.getAllProxyHost(this.getPublicNginxJwtHeader())
                                               .orElseThrow(() -> HttpResponseThrowers.throwServerErrorException("Failed to get public nginx proxy host"));

        proxyHosts.parallelStream().forEach(proxyHost -> {
            String url = String.format("%s://%s:%s", proxyHost.getForwardScheme(), proxyHost.getForwardHost(), proxyHost.getForwardPort());
            DnsRecord record = null;
            
            if(!recordMap.containsKey(url)) {
                record = new DnsRecord();
                record.setUri(URI.create(url));
                recordMap.put(url, record);
            }
            else {
                record = recordMap.get(url);
            }

            record.setEnabledPublicNginx(proxyHost.isEnabled());
            record.setPublicNginxRecord(proxyHost);
        });
    }

    private void fetchAllLocalDnsRecords(Map<String, DnsRecord> recordMap) {
        var proxyHosts = this.localNginxClient.getAllProxyHost(this.getLocalNginxJwtHeader())
                                              .orElseThrow(() -> HttpResponseThrowers.throwServerErrorException("Failed to get local nginx proxy host"));

        proxyHosts.parallelStream().forEach(proxyHost -> {
            String url = String.format("%s://%s:%s", proxyHost.getForwardScheme(), proxyHost.getForwardHost(), proxyHost.getForwardPort());
            DnsRecord record = null;
            
            if(!recordMap.containsKey(url)) {
                record = new DnsRecord();
                record.setUri(URI.create(url));
                recordMap.put(url, record);
            }
            else {
                record = recordMap.get(url);
            }

            record.setEnabledLocalNginx(proxyHost.isEnabled());
            record.setLocalNginxRecord(proxyHost);
        });
    }

    private String getLocalNginxJwtHeader() {
        return getJwtHeader(KEY_JWT_LOCAL_NGINX, this.localNginxClient);
    }

    private String getPublicNginxJwtHeader() {
        return getJwtHeader(KEY_JWT_PUBLIC_NGINX, this.publicNginxClient);
    }

    private String getJwtHeader(String key, NginxClient client) {
        var jwtMap = this.jwtCache.get(key);
        if(jwtMap != null) {
            return String.format("Bearer %s", jwtMap.get("jwt"));
        }

        String jwt = client.login(new NginxLoginRequest(this.nginxEmail, this.nginxPassword))
                                   .orElseThrow(() -> HttpResponseThrowers.throwServerErrorException("Failed to login to local nginx"))
                                   .getToken();

        this.jwtCache.saveAndExpire(key, Map.of("jwt", jwt));

        return String.format("Bearer %s", jwt);
    }
}
