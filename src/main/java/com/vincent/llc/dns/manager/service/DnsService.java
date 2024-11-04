package com.vincent.llc.dns.manager.service;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.viescloud.llc.viesspringutils.exception.HttpResponseThrowers;
import com.viescloud.llc.viesspringutils.util.DateTime;
import com.vincent.llc.dns.manager.model.DnsRecord;
import com.vincent.llc.dns.manager.model.cloudflare.CloudflareRequest;
import com.vincent.llc.dns.manager.model.cloudflare.CloudflareResult;
import com.vincent.llc.dns.manager.model.nginx.NginxCertificateResponse;
import com.vincent.llc.dns.manager.model.nginx.NginxProxyHostResponse;

@Service
public class DnsService {
    public static final String VIESCLOUD_DOMAIN = "viescloud.com";
    public static final String VIESLOCAL_DOMAIN = "vieslocal.com";

    @Autowired
    private PublicNginxService publicNginxService;

    @Autowired
    private LocalNginxService localNginxService;

    @Autowired
    private ViescloudCloudflareService viescloudCloudflareService;

    @Autowired
    private VieslocalCloudflareService vieslocalCloudflareService;

    public void clearDnsRecordsCache() {
        this.publicNginxService.clearCache();
        this.localNginxService.clearCache();
        this.viescloudCloudflareService.clearCache();
        this.vieslocalCloudflareService.clearCache();
    }

    public List<DnsRecord> getDnsRecordList() {
        return new ArrayList<>(this.getDnsRecordMap().values());
    }

    public Map<String, DnsRecord> getDnsRecordMap() {
        this.syncDnsRecord();
        var recordMap = new HashMap<String, DnsRecord>();
        var dnsMap = new HashMap<String, String>();
        this.fetchAllPublicNginxDnsRecords(recordMap, dnsMap);
        this.fetchAllLocalNginxDnsRecords(recordMap, dnsMap);
        this.fetchAllCloudflareViescloudDnsRecords(recordMap, dnsMap);
        this.fetchAllCloudflareVieslocalDnsRecords(recordMap, dnsMap);
        return recordMap;
    }

    @SuppressWarnings("unchecked")
    public List<NginxCertificateResponse> getAllNginxCertificate(String type) {
        switch (type) {
            case VIESCLOUD_DOMAIN:
                return this.publicNginxService.getAllCertificate();
            case VIESLOCAL_DOMAIN:
                return this.localNginxService.getAllCertificate();
            default:
                return (List<NginxCertificateResponse>) HttpResponseThrowers.throwBadRequest(type + " is not a valid domain name.");
        }
    }

    private void fetchAllCloudflareVieslocalDnsRecords(Map<String, DnsRecord> recordMap, Map<String, String> dnsMap) {
        this.fetchAllCloudflareDnsRecords(recordMap, dnsMap, this.vieslocalCloudflareService, (dns, record) -> {
            record.getCloudflareViesLocalRecord().add(dns);
            return record;
        });
    }

    private void fetchAllCloudflareViescloudDnsRecords(Map<String, DnsRecord> recordMap, Map<String, String> dnsMap) {
        this.fetchAllCloudflareDnsRecords(recordMap, dnsMap, this.viescloudCloudflareService, (dns, record) -> {
            record.getCloudflareViescloudRecord().add(dns);
            return record;
        });
    }

    private void fetchAllCloudflareDnsRecords(Map<String, DnsRecord> recordMap, Map<String, String> dnsMap,
            CloudflareService cloudflareService, BiFunction<CloudflareResult, DnsRecord, DnsRecord> function) {
        var list = cloudflareService.getAllCloudflareCnameRecord();

        list.forEach(dns -> {

            if (!dnsMap.containsKey(dns.getName())) {
                return;
            }

            var url = dnsMap.get(dns.getName());

            DnsRecord record = null;

            if (!recordMap.containsKey(url)) {
                record = new DnsRecord();
                record.setUri(URI.create(url));
                recordMap.put(url, record);
            } else {
                record = recordMap.get(url);
            }

            record = function.apply(dns, record);
        });
    }

    private void fetchAllPublicNginxDnsRecords(Map<String, DnsRecord> recordMap, Map<String, String> dnsMap) {
        this.fetchAllNginxDnsRecords(recordMap, dnsMap, this.publicNginxService, (proxyHost, record) -> {
            record.setPublicNginxRecord(proxyHost);
            return record;
        });
    }

    private void fetchAllLocalNginxDnsRecords(Map<String, DnsRecord> recordMap, Map<String, String> dnsMap) {
        this.fetchAllNginxDnsRecords(recordMap, dnsMap, this.localNginxService, (proxyHost, record) -> {
            record.setLocalNginxRecord(proxyHost);
            return record;
        });
    }

    private void fetchAllNginxDnsRecords(Map<String, DnsRecord> recordMap, Map<String, String> dnsMap,
            NginxService service, BiFunction<NginxProxyHostResponse, DnsRecord, DnsRecord> function) {
        var proxyHosts = service.getAllProxyHost();

        proxyHosts.forEach(proxyHost -> {
            String url = String.format("%s://%s:%s", proxyHost.getForwardScheme(), proxyHost.getForwardHost(),
                    proxyHost.getForwardPort());
            DnsRecord record = null;

            if (!recordMap.containsKey(url)) {
                record = new DnsRecord();
                record.setUri(URI.create(url));
                recordMap.put(url, record);
            } else {
                record = recordMap.get(url);
            }

            record = function.apply(proxyHost, record);

            proxyHost.getDomainNames().forEach(domainName -> {
                if (!dnsMap.containsKey(domainName))
                    dnsMap.put(domainName, url);
            });
        });
    }

    public void putDnsRecordList(List<DnsRecord> recordList) {
        recordList.forEach(this::putDnsRecord);
    }

    public void putDnsRecord(DnsRecord record) {
        String uri = record.getUri().toString();
        var currentDnsRecord = this.getDnsRecordMap().get(uri);
        var publicNginxRecord = record.getPublicNginxRecord();
        var localNginxRecord = record.getLocalNginxRecord();

        if (publicNginxRecord != null) {
            this.putNginxRecord(uri, publicNginxRecord, publicNginxService);
            this.putCloudflareRecord(publicNginxRecord, this.viescloudCloudflareService, VIESCLOUD_DOMAIN, true);
        }

        if (localNginxRecord != null) {
            this.putNginxRecord(uri, localNginxRecord, localNginxService);
            this.putCloudflareRecord(localNginxRecord, this.vieslocalCloudflareService, VIESLOCAL_DOMAIN, false);
        }

        if (currentDnsRecord.getPublicNginxRecord() != null && publicNginxRecord == null) {
            this.deleteNginxRecord(this.localNginxService, currentDnsRecord.getPublicNginxRecord());
        }

        if (currentDnsRecord.getLocalNginxRecord() != null && localNginxRecord == null) {
            this.deleteNginxRecord(this.localNginxService, currentDnsRecord.getLocalNginxRecord());
        }
    }

    private void putNginxRecord(String uri, NginxProxyHostResponse record, NginxService service) {
        if (service.getProxyHostByUri(uri) == null)
            service.createProxyHost(record);
        else
            service.putProxyHost(record);
    }

    private void putCloudflareRecord(NginxProxyHostResponse record, CloudflareService cloudflareService, String dns, boolean proxied) {
        if(ObjectUtils.isEmpty(record.getDomainNames())) {
            return;
        }

        record.getDomainNames().forEach(domainName -> {
            putCloudflareDns(cloudflareService, dns, proxied, domainName);
        });
    }

    private void putCloudflareDns(CloudflareService cloudflareService, String dns, boolean proxied, String domainName) {
        if (cloudflareService.getCloudflareCnameRecordByName(domainName) == null) {
            var now = DateTime.now();
            var dateTime = String.format("%s-%s-%s at %s-%s-%s ETC", now.getMonth(), now.getDay(), now.getYear(), now.getHour(), now.getMinute(), now.getSecond());
            var request = CloudflareRequest.builder()
                    .name(domainName)
                    .content(dns)
                    .proxied(proxied)
                    .ttl(1)
                    .type("CNAME")
                    .comment(String.format("Auto-added by DNS Manager on: %s", dateTime))
                    .build();
   
            cloudflareService.postCloudflareRecord(request);
        }
    }

    public void deleteDnsRecord(String uri) {
        var record = this.getDnsRecordMap().get(uri);
        if (record != null) {
            deleteNginxRecord(this.publicNginxService, record.getPublicNginxRecord());
            deleteNginxRecord(this.localNginxService, record.getLocalNginxRecord());
        }
    }

    private void deleteNginxRecord(NginxService service, NginxProxyHostResponse response) {
        if(response != null && response.getId() != 0)
            service.deleteProxyHost(response.getId());
    }

    public void cleanUnusedCloudflareCnameDns() { 
        cleanUnusedCloudflareCnameDns(this.viescloudCloudflareService, this.publicNginxService);
        cleanUnusedCloudflareCnameDns(this.vieslocalCloudflareService, this.localNginxService);
    }

    private void cleanUnusedCloudflareCnameDns(CloudflareService cloudflareService, NginxService nginxService) {
        var cloudflareDnsResult = new ArrayList<>(cloudflareService.getAllCloudflareCnameRecord());
        var domainNames = new ArrayList<>(nginxService.getAllDomainNameList());
        
        for (String domainName : domainNames) {
            cloudflareDnsResult.removeIf(cloudflareDns -> cloudflareDns.getName().equalsIgnoreCase(domainName));
        }
        
        cloudflareDnsResult.forEach(e -> {
            cloudflareService.deleteCloudflareRecord(e.getId());
        });
    }

    private void syncDnsRecord() {
        this.syncDnsRecord(this.viescloudCloudflareService, this.publicNginxService);
        this.syncDnsRecord(this.vieslocalCloudflareService, this.localNginxService);
    }

    private void syncDnsRecord(CloudflareService cloudflareService, NginxService nginxService) {
        var cloudflareDnsResult = new ArrayList<>(cloudflareService.getAllCloudflareCnameRecord());
        var domainNames = new ArrayList<>(nginxService.getAllDomainNameList());

        for (String domainName : domainNames) {
            var cloudflareDns = cloudflareDnsResult.parallelStream().filter(e -> e.getName().equalsIgnoreCase(domainName)).findFirst().orElse(null);
            if (cloudflareDns == null) {
                if(domainName.endsWith(VIESCLOUD_DOMAIN)) {
                    this.putCloudflareDns(cloudflareService, VIESCLOUD_DOMAIN, true, domainName);
                }
                else if(domainName.endsWith(VIESLOCAL_DOMAIN)) {
                    this.putCloudflareDns(cloudflareService, VIESLOCAL_DOMAIN, false, domainName);
                }
            }
        }
    }
}
