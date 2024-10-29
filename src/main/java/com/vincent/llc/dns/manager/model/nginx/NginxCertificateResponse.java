package com.vincent.llc.dns.manager.model.nginx;

import java.time.OffsetDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NginxCertificateResponse {
    private long id;
    private OffsetDateTime createdOn;
    private OffsetDateTime modifiedOn;
    private long ownerUserID;
    private String provider;
    private String niceName;
    private List<String> domainNames;
    private OffsetDateTime expiresOn;
    private NginxMeta meta;
}

