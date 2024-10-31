package com.vincent.llc.dns.manager.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vincent.llc.dns.manager.model.DnsRecord;
import com.vincent.llc.dns.manager.service.DnsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/dns")
public class DnsController {
    
    @Autowired
    private DnsService dnsService;

    @GetMapping()
    public ResponseEntity<?> getAllDnsRecord(@RequestParam(required = false, defaultValue = "false") boolean map) {
        var records = map ? dnsService.getDnsRecordMap() : dnsService.getDnsRecordList();
        return ResponseEntity.ok(records);
    }
    

    @PatchMapping()
    public ResponseEntity<?> patchDnsRecord(@RequestBody DnsRecord record) {
        
        return ResponseEntity.ok("");
    }
}
