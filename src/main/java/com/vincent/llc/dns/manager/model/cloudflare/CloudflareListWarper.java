package com.vincent.llc.dns.manager.model.cloudflare;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CloudflareListWarper implements Serializable {
    private List<CloudflareResult> result;
}
