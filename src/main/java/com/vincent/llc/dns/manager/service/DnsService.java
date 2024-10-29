package com.vincent.llc.dns.manager.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vincent.llc.dns.manager.feign.CloudflareClient;
import com.vincent.llc.dns.manager.feign.NginxClient;

@Service
public class DnsService {
    
    @Autowired
    private CloudflareClient cloudflareClient;

    @Autowired
    private NginxClient nginxClient;

    public DnsService() {
    
    }
}
