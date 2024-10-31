package com.vincent.llc.dns.manager;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.viescloud.llc.viesspringutils.util.Json;
import com.vincent.llc.dns.manager.model.cloudflare.CloudflareRequest;
import com.vincent.llc.dns.manager.model.cloudflare.CloudflareResult;
import com.vincent.llc.dns.manager.service.DnsService;
import com.vincent.llc.dns.manager.service.PublicNginxService;

@SpringBootTest(classes = Application.class)
class ApplicationTests {
	
	@Autowired
	private DnsService dnsService;

	@Autowired
	private PublicNginxService publicNginxService;

	// @Test
	public void dnsServiceTest() {
		// this.dnsService.clearDnsRecordsCache();
		// var records = this.dnsService.getDnsRecordList();
		// System.out.println(Json.builder().target(records).build().tryToJson());

		var list = publicNginxService.getAllDomainNameList();
		System.out.println(Json.builder().target(list).build().tryToJson());
	}

}
