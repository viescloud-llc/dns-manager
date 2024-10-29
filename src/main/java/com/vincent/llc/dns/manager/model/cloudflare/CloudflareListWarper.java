package com.vincent.llc.dns.manager.model.cloudflare;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CloudflareListWarper {
    private List<CloudflareResult> result;
}
