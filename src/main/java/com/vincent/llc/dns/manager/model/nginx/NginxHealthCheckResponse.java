package com.vincent.llc.dns.manager.model.nginx;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NginxHealthCheckResponse implements Serializable {
    private String status;
}
