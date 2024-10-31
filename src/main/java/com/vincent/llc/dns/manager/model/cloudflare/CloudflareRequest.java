package com.vincent.llc.dns.manager.model.cloudflare;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Builder
public class CloudflareRequest implements Serializable {

    private String name;

    private String content;
    
    @Builder.Default
    private Boolean proxied = true;
    
    @Builder.Default
    private int ttl = 1;
    
    @Builder.Default
    private String type = "CNAME";

    private List<String> tags;
    
    private String comment;
}
