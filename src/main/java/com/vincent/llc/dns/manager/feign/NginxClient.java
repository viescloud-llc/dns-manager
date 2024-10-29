package com.vincent.llc.dns.manager.feign;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "nginxClient", url = "${nginx.uri}")
public class NginxClient {
    
}
