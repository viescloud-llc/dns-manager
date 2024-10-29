package com.vincent.llc.dns.manager.model.nginx;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NginxLoginResponse {
    private String token;
    private OffsetDateTime expires;
}
