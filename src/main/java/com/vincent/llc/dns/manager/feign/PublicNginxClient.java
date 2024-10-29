package com.vincent.llc.dns.manager.feign;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "publicNginxClient", url = "${nginx.public.uri}")
public interface PublicNginxClient extends NginxClient {

    
}
