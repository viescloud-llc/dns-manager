package com.vincent.llc.dns.manager.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.viescloud.llc.viesspringutils.repository.DatabaseCall;
import com.viescloud.llc.viesspringutils.util.DateTime;
import com.vincent.llc.dns.manager.feign.LocalNginxClient;
import com.vincent.llc.dns.manager.model.nginx.NginxProxyHostResponse;

@Service
public class LocalNginxService extends NginxService {

    @Value("${nginx.email}")
    private String nginxEmail;

    @Value("${nginx.password}")
    private String nginxPassword;

    public LocalNginxService(LocalNginxClient nginxClient, DatabaseCall<String, String, ?> jwtCache,
            DatabaseCall<String, List<NginxProxyHostResponse>, ?> proxyHostsCache, DatabaseCall<String, NginxProxyHostResponse, ?> proxyHostCache) {
        super(nginxClient, jwtCache, proxyHostsCache, proxyHostCache);
        jwtCache.init("com.vincent.llc.dns.manager.service.jwt.local");
        jwtCache.setTTL(DateTime.ofDays(1));
        proxyHostsCache.init("com.vincent.llc.dns.manager.service.proxyHosts.local");
        proxyHostsCache.setTTL(DateTime.ofDays(1));
        proxyHostCache.init("com.vincent.llc.dns.manager.service.proxyHost.local");
        proxyHostCache.setTTL(DateTime.ofDays(1));
    }

    @Override
    public String nginxEmail() {
        return this.nginxEmail;
    }

    @Override
    public String nginxPassword() {
        return this.nginxPassword;
    }
    
}
