package com.vincent.llc.dns.manager.service;

import java.util.List;
import java.util.Optional;

import com.viescloud.llc.viesspringutils.exception.HttpResponseThrowers;
import com.viescloud.llc.viesspringutils.repository.DatabaseCall;
import com.viescloud.llc.viesspringutils.util.JsonUtils;
import com.viescloud.llc.viesspringutils.util.ReflectionUtils;
import com.vincent.llc.dns.manager.feign.NginxClient;
import com.vincent.llc.dns.manager.model.nginx.NginxCertificateResponse;
import com.vincent.llc.dns.manager.model.nginx.NginxLoginRequest;
import com.vincent.llc.dns.manager.model.nginx.NginxProxyHostRequest;
import com.vincent.llc.dns.manager.model.nginx.NginxProxyHostResponse;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public abstract class NginxService {

    private final String JWT_KEY = "jwt";
    private final String PROXY_HOSTS_KEY = "all";
    
    protected abstract String nginxEmail();
    protected abstract String nginxPassword();

    protected final NginxClient nginxClient;
    protected DatabaseCall<String, String> jwtCache;
    protected DatabaseCall<String, List<NginxProxyHostResponse>> proxyHostsCache;
    protected DatabaseCall<String, NginxProxyHostResponse> proxyHostCache;

    public void clearCache() {
        var proxyHosts = this.getAllProxyHost();
        proxyHosts.parallelStream().forEach(this::deleteProxyHostCache);
    }

    public List<NginxCertificateResponse> getAllCertificate() {
        return this.nginxClient.getAllCertificate(this.getJwtHeader())
                               .orElseThrow(() -> HttpResponseThrowers.throwServerErrorException("Failed to get nginx certificates"));
    }

    public List<NginxProxyHostResponse> getAllProxyHost() {
        var proxyHosts = this.proxyHostsCache.get(PROXY_HOSTS_KEY);
        if(proxyHosts != null) {
            return proxyHosts;
        }

        proxyHosts = this.nginxClient.getAllProxyHost(this.getJwtHeader())
                                     .orElseThrow(() -> HttpResponseThrowers.throwServerErrorException("Failed to get nginx proxy hosts"));

        proxyHosts.parallelStream().forEach(this::saveProxyHostCache);
        this.proxyHostsCache.saveAndExpire(PROXY_HOSTS_KEY, proxyHosts);

        return proxyHosts;
    }

    public List<String> getAllDomainNameList() {
        return this.getAllProxyHost().parallelStream().map(r -> r.getDomainNames()).flatMap(List::stream).toList();
    }

    public NginxProxyHostResponse getProxyHostById(int id) {
        var proxyHost = this.proxyHostCache.get(String.valueOf(id));
        if(proxyHost != null) {
            return proxyHost;
        }

        return this.getAllProxyHost().parallelStream().filter(r -> id == r.getId()).findFirst().orElse(null);
    }

    public NginxProxyHostResponse getProxyHostByUri(String uri) {
        var proxyHost = this.proxyHostCache.get(uri);
        if(proxyHost != null) {
            return proxyHost;
        }

        return this.getAllProxyHost().parallelStream().filter(r -> uri.equals(getUri(r))).findFirst().orElse(null);
    }

    public boolean enableProxyHost(int id) {
        var proxyHost = this.getProxyHostById(id);
        this.nginxClient.enableProxyHost(this.getJwtHeader(), String.valueOf(id))
                             .orElseThrow(() -> HttpResponseThrowers.throwServerErrorException("Failed to enable nginx proxy host"));
        
        this.deleteProxyHostCache(proxyHost);
        return true;
    }

    public boolean disableProxyHost(int id) {
        var proxyHost = this.getProxyHostById(id);
        this.nginxClient.disableProxyHost(this.getJwtHeader(), String.valueOf(id))
                              .orElseThrow(() -> HttpResponseThrowers.throwServerErrorException("Failed to disable nginx proxy host"));
        
        this.deleteProxyHostCache(proxyHost);
        return true;
    }

    public void createProxyHost(NginxProxyHostRequest request) {
        this.nginxClient.createProxyHost(this.getJwtHeader(), request)
                         .orElseThrow(() -> HttpResponseThrowers.throwServerErrorException("Failed to create nginx proxy host"));
        
        this.proxyHostsCache.deleteByKey(PROXY_HOSTS_KEY);
    }

    public void createProxyHost(NginxProxyHostResponse response) {
        NginxProxyHostRequest request = Optional.ofNullable(JsonUtils.tryClone(response, NginxProxyHostRequest.class))
                                                .orElseThrow(() -> HttpResponseThrowers.throwServerErrorException("Failed to cast request"));
        this.createProxyHost(request);
    }

    public void putProxyHost(NginxProxyHostRequest request, int id) {
        var proxyHost = this.getProxyHostById(id);

        if(!ReflectionUtils.isEquals(proxyHost, request)) {
            this.nginxClient.updateProxyHost(this.getJwtHeader(), id, request)
                            .orElseThrow(() -> HttpResponseThrowers.throwServerErrorException("Failed to update nginx proxy host"));
            
            this.deleteProxyHostCache(proxyHost);
        }
    }

    public void putProxyHost(NginxProxyHostResponse response) {
        if(response.getId() == 0) {
            HttpResponseThrowers.throwBadRequest("Can't update nginx proxy host without id");
        }

        NginxProxyHostRequest request = Optional.ofNullable(JsonUtils.tryClone(response, NginxProxyHostRequest.class))
                                                .orElseThrow(() -> HttpResponseThrowers.throwServerErrorException("Failed to cast request"));
        this.putProxyHost(request, response.getId());
    }

    public void deleteProxyHost(int id) {
        var proxyHost = this.getProxyHostById(id);

        if (proxyHost != null && proxyHost.getId() != 0) {
            this.nginxClient.deleteProxyHost(this.getJwtHeader(), id)
                            .orElseThrow(() -> HttpResponseThrowers.throwServerErrorException("Failed to delete nginx proxy host"));
            
            this.deleteProxyHostCache(proxyHost);
        }
    }

    public void deleteProxyHostByUri(String uri) {
        var proxyHost = this.getProxyHostByUri(uri);
        if(proxyHost != null && proxyHost.getId() != 0) {
            this.nginxClient.deleteProxyHost(this.getJwtHeader(), proxyHost.getId())
                        .orElseThrow(() -> HttpResponseThrowers.throwServerErrorException("Failed to delete nginx proxy host"));
        
            this.deleteProxyHostCache(proxyHost);
        }
    }

    // ------------------Helper-------------------

    private void saveProxyHostCache(NginxProxyHostResponse response) {
        this.proxyHostCache.saveAndExpire(getUri(response), response);
        this.proxyHostCache.saveAndExpire(String.valueOf(response.getId()), response);
        this.proxyHostsCache.deleteByKey(PROXY_HOSTS_KEY);
    }

    private void deleteProxyHostCache(NginxProxyHostResponse response) {
        this.proxyHostsCache.deleteByKey(PROXY_HOSTS_KEY);
        this.proxyHostCache.deleteByKey(getUri(response));
        this.proxyHostCache.deleteByKey(String.valueOf(response.getId()));
    }

    private String getUri(NginxProxyHostResponse response) {
        NginxProxyHostRequest request = response;
        return this.getUri(request);
    }

    private String getUri(NginxProxyHostRequest request) {
        return String.format("%s://%s:%s", request.getForwardScheme(), request.getForwardHost(), request.getForwardPort());
    }

    private String getJwtHeader() {
        var jwt = this.jwtCache.get(JWT_KEY);
        if(jwt != null) {
            return String.format("Bearer %s", jwt);
        }

        jwt = this.nginxClient.login(new NginxLoginRequest(this.nginxEmail(), this.nginxPassword()))
                                   .orElseThrow(() -> HttpResponseThrowers.throwServerErrorException("Failed to login to local nginx"))
                                   .getToken();

        this.jwtCache.saveAndExpire(JWT_KEY, jwt);

        return String.format("Bearer %s", jwt);
    }
}
