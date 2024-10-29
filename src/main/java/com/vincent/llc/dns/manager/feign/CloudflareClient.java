package com.vincent.llc.dns.manager.feign;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import com.vincent.llc.dns.manager.model.CloudflareListWarper;
import com.vincent.llc.dns.manager.model.CloudflareWarper;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

@FeignClient(name = "cloudflareClient", url = "${cloudflare.uri}")
public interface CloudflareClient {
    
    @GetMapping(value = "client/v4/zones/{zoneId}/dns_records", produces = "application/json")
    public CloudflareListWarper getDNSList(
        @PathVariable("zoneId") String zoneId, 
        @RequestHeader("X-Auth-Email") String email, 
        @RequestHeader("X-Auth-Key") String key,
        @RequestParam(value = "page", required = false) Integer page, // Optional parameter
        @RequestParam(value = "per_page", required = false) Integer perPage // Optional parameter
    );

    @GetMapping(value = "client/v4/zones/{zoneId}/dns_records/{recordId}", produces = "application/json")
    public CloudflareWarper getDNS(
            @PathVariable("zoneId") String zoneId, 
            @PathVariable("recordId") String recordId, 
            @RequestHeader("X-Auth-Email") String email, 
            @RequestHeader("X-Auth-Key") String key
    );

    @PatchMapping(value = "client/v4/zones/{zoneId}/dns_records/{recordId}", produces = "application/json", consumes = "application/json")
    public CloudflareWarper patchDNS(
            @PathVariable("zoneId") String zoneId, 
            @PathVariable("recordId") String recordId, 
            @RequestHeader("X-Auth-Email") String email, 
            @RequestHeader("X-Auth-Key") String key,
            @RequestBody Map<String, Object> data
    );
}
