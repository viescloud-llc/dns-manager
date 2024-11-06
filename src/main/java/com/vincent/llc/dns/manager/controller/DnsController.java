package com.vincent.llc.dns.manager.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vincent.llc.dns.manager.model.DnsRecord;
import com.vincent.llc.dns.manager.service.DnsService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

    @GetMapping("/nginx/certificates")
    public ResponseEntity<?> getAllNginxCertificate(@RequestParam String type) {
        return ResponseEntity.ok(this.dnsService.getAllNginxCertificate(type));
    }

    @PutMapping()
    public ResponseEntity<?> putDnsRecord(@RequestBody DnsRecord record, @RequestParam(required = false, defaultValue = "false") boolean cleanUnusedCloudflareCnameDns) {
        this.dnsService.putDnsRecord(record);
        if (cleanUnusedCloudflareCnameDns) {
            this.dnsService.cleanUnusedCloudflareCnameDns();
        }
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/clear-unused-dns")
    public ResponseEntity<?> clearUnusedDnsRecordsCache() {
        this.dnsService.cleanUnusedCloudflareCnameDns();
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/clear-cache")
    public ResponseEntity<?> clearDnsRecordsCache() {
        this.dnsService.clearDnsRecordsCache();
        return ResponseEntity.ok().build();
    }

    @DeleteMapping()
    public ResponseEntity<?> deleteDnsRecord(@RequestParam String uri, @RequestParam(required = false, defaultValue = "false") boolean cleanUnusedCloudflareCnameDns) {
        this.dnsService.deleteDnsRecord(uri);
        if (cleanUnusedCloudflareCnameDns) {
            this.dnsService.cleanUnusedCloudflareCnameDns();
        }
        return ResponseEntity.ok().build();
    }
}
