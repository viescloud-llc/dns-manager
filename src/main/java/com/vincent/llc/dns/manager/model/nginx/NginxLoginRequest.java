package com.vincent.llc.dns.manager.model.nginx;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NginxLoginRequest {
    private String identity;
    private String secret;
}
