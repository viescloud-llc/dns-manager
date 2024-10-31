package com.vincent.llc.dns.manager.service;

import java.util.List;

import com.viescloud.llc.viesspringutils.exception.HttpResponseThrowers;
import com.viescloud.llc.viesspringutils.repository.DatabaseCall;
import com.vincent.llc.dns.manager.feign.CloudflareClient;
import com.vincent.llc.dns.manager.model.cloudflare.CloudflareRequest;
import com.vincent.llc.dns.manager.model.cloudflare.CloudflareResult;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public abstract class CloudflareService {
    private static final String DEFAULT_CNAME_ALL_KEY = "cname-all";

    protected abstract String cloudflareEmail();
    protected abstract String cloudflareKey();
    protected abstract String cloudflareZoneId();

    protected final CloudflareClient cloudflareClient;
    protected final DatabaseCall<String, List<CloudflareResult>, ?> dnsCache;

    public void clearCache() {
        this.dnsCache.deleteByKey(DEFAULT_CNAME_ALL_KEY);
    }

    public List<CloudflareResult> getAllCloudflareCnameRecord() {
        return this.getAllCloudflareRecord("CNAME");
    }

    public List<CloudflareResult> getAllCloudflareRecord(String type) {
        var result = this.dnsCache.get(DEFAULT_CNAME_ALL_KEY);
        if(result != null) {
            return result;
        }
        
        result = this.cloudflareClient.getDNSList(this.cloudflareZoneId(), this.cloudflareEmail(), this.cloudflareKey(), 1, 1000)
                                    .orElseThrow(() -> HttpResponseThrowers.throwServerErrorException("Failed to get dns list from cloudflare with zone id: " + this.cloudflareZoneId()))
                                    .getResult();

        // filter only CNAME
        result = result.parallelStream().filter(r -> r.getType().toUpperCase().equals(type)).toList();

        this.dnsCache.saveAndExpire(DEFAULT_CNAME_ALL_KEY, result);        
        
        return result;
    }

    public CloudflareResult getCloudflareCnameRecordByName(String name) {
        return this.getAllCloudflareCnameRecord().parallelStream().filter(r -> r.getName().equals(name)).findFirst().orElse(null);
    }

    public void postCloudflareRecord(CloudflareRequest request) {
        this.cloudflareClient.createDNS(this.cloudflareZoneId(), this.cloudflareEmail(), this.cloudflareKey(), request)
                             .orElseThrow(() -> HttpResponseThrowers.throwServerErrorException("Failed to create dns in cloudflare"));
        this.dnsCache.deleteByKey(DEFAULT_CNAME_ALL_KEY);
    }

    public void postCloudflareRecord(CloudflareResult result) {
        CloudflareRequest request = result;
        this.postCloudflareRecord(request);
    }

    public void putCloudflareRecord(CloudflareRequest request, String id) {
        this.cloudflareClient.putDNS(this.cloudflareZoneId(), this.cloudflareEmail(), this.cloudflareKey(), id, request)
                             .orElseThrow(() -> HttpResponseThrowers.throwServerErrorException("Failed to update dns in cloudflare"));
        this.dnsCache.deleteByKey(DEFAULT_CNAME_ALL_KEY);
    }

    public void putCloudflareRecord(CloudflareResult result) {
        if(result.getId() == null) {
            HttpResponseThrowers.throwBadRequest("Can't update cloudflare record without id");
        }
        this.putCloudflareRecord(result, result.getId());
    }

    public void deleteCloudflareRecord(String id) {
        this.cloudflareClient.deleteDNS(this.cloudflareZoneId(), this.cloudflareEmail(), this.cloudflareKey(), id)
                             .orElseThrow(() -> HttpResponseThrowers.throwServerErrorException("Failed to delete dns in cloudflare"));
        this.dnsCache.deleteByKey(DEFAULT_CNAME_ALL_KEY);
    }
}
