package com.vincent.llc.dns.manager.model.nginx;

import java.io.Serializable;
import java.time.OffsetDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NginxCertificateResponse implements Serializable {
    private long id;
    
    @JsonProperty("created_on")
    private OffsetDateTime createdOn;

    @JsonProperty("modified_on")
    private OffsetDateTime modifiedOn;

    @JsonProperty("owner_user_id")
    private int ownerUserID;

    private String provider;

    @JsonProperty("nice_name")
    private String niceName;

    @JsonProperty("domain_names")
    private List<String> domainNames;

    @JsonProperty("expires_on")
    private OffsetDateTime expiresOn;

    private NginxMeta meta;
}

