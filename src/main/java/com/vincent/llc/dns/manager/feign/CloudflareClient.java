package com.vincent.llc.dns.manager.feign;

import java.util.Map;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
// import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

// import com.viescloud.lcc.cloudflare.checker.model.CloudflareListWarper;
// import com.viescloud.lcc.cloudflare.checker.model.CloudflareWarper;

// import io.swagger.v3.oas.annotations.parameters.RequestBody;

// @FeignClient(name = "cloudflareClient", url = "${cloudflare.ip}")
public interface CloudflareClient {
    
    // @GetMapping(value = "client/v4/zones/{zoneId}/dns_records", produces = "application/json")
    // CloudflareListWarper getDNSList(
    //     @PathVariable("zoneId") String zoneId, 
    //     @RequestHeader("X-Auth-Email") String email, 
    //     @RequestHeader("X-Auth-Key") String key,
    //     @RequestParam(value = "page", required = false) Integer page, // Optional parameter
    //     @RequestParam(value = "per_page", required = false) Integer perPage // Optional parameter
    // );

    // @GetMapping(value = "client/v4/zones/{zoneId}/dns_records/{recordId}", produces = "application/json")
    // CloudflareWarper getDNS(
    //         @PathVariable("zoneId") String zoneId, 
    //         @PathVariable("recordId") String recordId, 
    //         @RequestHeader("X-Auth-Email") String email, 
    //         @RequestHeader("X-Auth-Key") String key
    // );

    // @PatchMapping(value = "client/v4/zones/{zoneId}/dns_records/{recordId}", produces = "application/json", consumes = "application/json")
    // CloudflareWarper patchDNS(
    //         @PathVariable("zoneId") String zoneId, 
    //         @PathVariable("recordId") String recordId, 
    //         @RequestBody Map<String, Object> data, 
    //         @RequestHeader("X-Auth-Email") String email, 
    //         @RequestHeader("X-Auth-Key") String key
    // );
}
