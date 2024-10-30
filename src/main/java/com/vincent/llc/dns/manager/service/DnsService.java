package com.vincent.llc.dns.manager.service;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

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
import com.vincent.llc.dns.manager.model.cloudflare.CloudflareResult;
import com.vincent.llc.dns.manager.model.nginx.NginxLoginRequest;
import com.vincent.llc.dns.manager.model.nginx.NginxProxyHostResponse;

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

    public DatabaseCall<String, Map<String, DnsRecord>, ?> dnsRecordsCache;
    public DatabaseCall<String, String, ?> jwtCache;

    public DnsService(DatabaseCall<String, Map<String, DnsRecord>, ?> dnsRecordsCache, DatabaseCall<String, String, ?> jwtCache) {
        this.dnsRecordsCache = dnsRecordsCache;
        this.dnsRecordsCache.init(KEY_RECORD_PREFIX);
        this.dnsRecordsCache.setTTL(DateTime.ofMinutes(60));

        this.jwtCache = jwtCache;
        this.jwtCache.init(KEY_JWT_PREFIX);
        this.jwtCache.setTTL(DateTime.ofDays(1));
    }

    public void clearDnsRecordsCache() {
        this.dnsRecordsCache.deleteByKey(KEY_RECORDS);
    }

    public List<DnsRecord> getDnsRecordList() {
        return new ArrayList<>(this.getDnsRecordMap().values());
    }

    public Map<String, DnsRecord> getDnsRecordMap() {
        Map<String, DnsRecord> recordMap = dnsRecordsCache.get(KEY_RECORDS);
        if(recordMap != null) {
            return recordMap;
        }

        recordMap = new HashMap<>();
        var dnsMap = new HashMap<String, String>();
        this.fetchAllPublicNginxDnsRecords(recordMap, dnsMap);
        this.fetchAllLocalNginxDnsRecords(recordMap, dnsMap);
        this.fetchAllCloudflareViescloudDnsRecords(recordMap, dnsMap);
        this.fetchAllCloudflareVieslocalDnsRecords(recordMap, dnsMap);
        
        this.dnsRecordsCache.saveAndExpire(KEY_RECORDS, recordMap);
        return recordMap;
    }

    private void fetchAllCloudflareVieslocalDnsRecords(Map<String, DnsRecord> recordMap, Map<String, String> dnsMap) {
        this.fetchAllCloudflareDnsRecords(recordMap, dnsMap, this.cloudflareVieslocalZoneId, (dns, record) -> {
            record.getCloudflareViesLocalRecord().add(dns);
            return record;
        });
    }

    private void fetchAllCloudflareViescloudDnsRecords(Map<String, DnsRecord> recordMap, Map<String, String> dnsMap) {
        this.fetchAllCloudflareDnsRecords(recordMap, dnsMap, this.cloudflareViescloudZoneId, (dns, record) -> {
            record.getCloudflareViescloudRecord().add(dns);
            return record;
        });
    }

    private void fetchAllCloudflareDnsRecords(Map<String, DnsRecord> recordMap, Map<String, String> dnsMap, String cloudflareZoneId, BiFunction<CloudflareResult, DnsRecord, DnsRecord> function) {
        var list = this.cloudflareClient.getDNSList(cloudflareZoneId, cloudflareEmail, cloudflareKey, 1, 1000)
                                        .orElseThrow(() -> HttpResponseThrowers.throwServerErrorException("Failed to get dns list from cloudflare with zone id: " + cloudflareZoneId));

        list.getResult().forEach(dns -> {

            if(!dns.getType().toUpperCase().equals("CNAME") || !dnsMap.containsKey(dns.getName())) {
                return;
            }

            var url = dnsMap.get(dns.getName());
            
            DnsRecord record = null;
            
            if(!recordMap.containsKey(url)) {
                record = new DnsRecord();
                record.setUri(URI.create(url));
                recordMap.put(url, record);
            }
            else {
                record = recordMap.get(url);
            }

            record = function.apply(dns, record);
        });
    }

    private void fetchAllPublicNginxDnsRecords(Map<String, DnsRecord> recordMap, Map<String, String> dnsMap) {
        this.fetchAllNginxDnsRecords(recordMap, dnsMap, this.publicNginxClient, getPublicNginxJwtHeader(), (proxyHost, record) -> {
            record.setEnabledPublicNginx(proxyHost.isEnabled());
            record.setPublicNginxRecord(proxyHost);
            return record;
        });
    }

    private void fetchAllLocalNginxDnsRecords(Map<String, DnsRecord> recordMap, Map<String, String> dnsMap) {
        this.fetchAllNginxDnsRecords(recordMap, dnsMap, this.localNginxClient, getLocalNginxJwtHeader(), (proxyHost, record) -> {
            record.setEnabledLocalNginx(proxyHost.isEnabled());
            record.setLocalNginxRecord(proxyHost);
            return record;
        });
    }

    private void fetchAllNginxDnsRecords(Map<String, DnsRecord> recordMap, Map<String, String> dnsMap, NginxClient client, String jwtHeader, BiFunction<NginxProxyHostResponse, DnsRecord, DnsRecord> function) {
        var proxyHosts = client.getAllProxyHost(jwtHeader)
                               .orElseThrow(() -> HttpResponseThrowers.throwServerErrorException("Failed to get nginx proxy host"));

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

            record = function.apply(proxyHost, record);

            proxyHost.getDomainNames().forEach(domainName -> {
                if(!dnsMap.containsKey(domainName))
                    dnsMap.put(domainName, url);
            });
        });
    }

    private String getLocalNginxJwtHeader() {
        return getJwtHeader(KEY_JWT_LOCAL_NGINX, this.localNginxClient);
    }

    private String getPublicNginxJwtHeader() {
        return getJwtHeader(KEY_JWT_PUBLIC_NGINX, this.publicNginxClient);
    }

    private String getJwtHeader(String key, NginxClient client) {
        var jwt = this.jwtCache.get(key);
        if(jwt != null) {
            return String.format("Bearer %s", jwt);
        }

        jwt = client.login(new NginxLoginRequest(this.nginxEmail, this.nginxPassword))
                                   .orElseThrow(() -> HttpResponseThrowers.throwServerErrorException("Failed to login to local nginx"))
                                   .getToken();

        this.jwtCache.saveAndExpire(key, jwt);

        return String.format("Bearer %s", jwt);
    }
}
