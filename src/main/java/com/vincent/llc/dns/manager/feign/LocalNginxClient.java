package com.vincent.llc.dns.manager.feign;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "localNginxClient", url = "${nginx.local.uri}")
public interface LocalNginxClient extends NginxClient {
    
}
