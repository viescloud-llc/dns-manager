package com.vincent.llc.dns.manager.model.nginx;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NginxMeta {
    private String letsencryptEmail;
    private boolean dnsChallenge;
    private String dnsProvider;
    private String dnsProviderCredentials;
    private boolean letsencryptAgree;
    private boolean nginxOnline;
    private String nginxErr;
}
