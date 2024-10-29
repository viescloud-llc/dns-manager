package com.vincent.llc.dns.manager.feign;

import java.util.List;
import java.util.Optional;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.vincent.llc.dns.manager.model.nginx.NginxCertificateResponse;
import com.vincent.llc.dns.manager.model.nginx.NginxHealthCheckResponse;
import com.vincent.llc.dns.manager.model.nginx.NginxLoginRequest;
import com.vincent.llc.dns.manager.model.nginx.NginxLoginResponse;
import com.vincent.llc.dns.manager.model.nginx.NginxProxyHostRequest;
import com.vincent.llc.dns.manager.model.nginx.NginxProxyHostResponse;

public interface NginxClient {
    @GetMapping(value = "/api", produces = "application/json")
    public Optional<NginxHealthCheckResponse> healthCheck(@RequestHeader("Authorization") String token);

    @GetMapping(value = "/api/nginx/certificates", produces = "application/json")
    public Optional<NginxCertificateResponse> getAllCertificate(@RequestHeader("Authorization") String token);

    @GetMapping(value = "/api/nginx/proxy-hosts/{id}", produces = "application/json")
    public Optional<NginxProxyHostResponse> getProxyHost(@RequestHeader("Authorization") String token, @PathVariable("id") String id);

    @GetMapping(value = "/api/nginx/proxy-hosts", produces = "application/json")
    public Optional<List<NginxProxyHostResponse>> getAllProxyHost(@RequestHeader("Authorization") String token);

    @GetMapping(value = "/api/tokens", produces = "application/json")
    public Optional<NginxLoginResponse> getTokens(@RequestHeader("Authorization") String token);
    
    @PostMapping(value = "/api/tokens", produces = "application/json", consumes = "application/json")
    public Optional<NginxLoginResponse> login(@RequestBody NginxLoginRequest request);

    @PostMapping(value = "/api/nginx/proxy-hosts", produces = "application/json", consumes = "application/json")
    public Optional<NginxProxyHostResponse> createProxyHost(@RequestHeader("Authorization") String token, @RequestBody NginxProxyHostRequest request);

    @PostMapping(value = "/api/nginx/proxy-hosts/{id}/enable")
    public Optional<Boolean> enableProxyHost(@RequestHeader("Authorization") String token, @PathVariable("id") String id);

    @PostMapping(value = "/api/nginx/proxy-hosts/{id}/disable")
    public Optional<Boolean> disableProxyHost(@RequestHeader("Authorization") String token, @PathVariable("id") String id);

    @PutMapping(value = "/api/nginx/proxy-hosts/{id}", produces = "application/json", consumes = "application/json")
    public Optional<NginxProxyHostResponse> updateProxyHost(@RequestHeader("Authorization") String token, @PathVariable("id") String id, @RequestBody NginxProxyHostRequest request);
}
