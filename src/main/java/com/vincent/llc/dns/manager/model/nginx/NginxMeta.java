package com.vincent.llc.dns.manager.model.nginx;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.viescloud.llc.viesspringutils.config.json.JsonIntOrBooleanDeserializer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NginxMeta implements Serializable {

    @JsonProperty("letsencrypt_email")
    private String letsencryptEmail;

    @JsonProperty("dns_challenge")
    private boolean dnsChallenge;

    @JsonProperty("dns_provider")
    private String dnsProvider;

    @JsonProperty("dns_provider_credentials")
    private String dnsProviderCredentials;

    @JsonDeserialize(using = JsonIntOrBooleanDeserializer.class)
    @JsonProperty("letsencrypt_agree")
    private boolean letsencryptAgree;

    @JsonDeserialize(using = JsonIntOrBooleanDeserializer.class)
    @JsonProperty("nginx_online")
    private boolean nginxOnline;

    @JsonProperty("nginx_err")
    private String nginxErr;
}
