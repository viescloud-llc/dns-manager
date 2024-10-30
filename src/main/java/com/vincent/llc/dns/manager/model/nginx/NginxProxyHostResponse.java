package com.vincent.llc.dns.manager.model.nginx;

import java.time.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.viescloud.llc.viesspringutils.config.json.JsonIntOrBooleanDeserializer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class NginxProxyHostResponse extends NginxProxyHostRequest {

    private int id;

    @JsonProperty("owner_user_id")
    private long ownerUserID;

    @JsonDeserialize(using = JsonIntOrBooleanDeserializer.class)
    private boolean enabled;

    private NginxCertificateResponse certificate;
}