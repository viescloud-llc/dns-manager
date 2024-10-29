package com.vincent.llc.dns.manager.model.nginx;

import java.time.OffsetDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class NginxProxyHostResponse extends NginxProxyHostRequest {
    private long id;
    private OffsetDateTime createdOn;
    private OffsetDateTime modifiedOn;
    private long ownerUserID;
    private boolean enabled;
    private NginxCertificateResponse certificate;
}