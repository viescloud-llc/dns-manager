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
import com.vincent.llc.dns.manager.controller.DnsController;

@SpringBootTest(classes = Application.class)
class ApplicationTests {
	
	@Autowired
	private DnsService dnsService;

	@Autowired
	private PublicNginxService publicNginxService;

	@Autowired
	private DnsController dnsController;

	// @Test
	public void dnsServiceTest() {
		// this.dnsService.clearDnsRecordsCache();
		// var records = this.dnsService.getDnsRecordList();
		// System.out.println(Json.builder().target(records).build().tryToJson());

		var list = publicNginxService.getAllCertificate();
		System.out.println(Json.builder().target(list).build().tryToJson());
	}


	// @Test
	public void testClearUnusedDns() {
		this.dnsService.clearDnsRecordsCache();
		this.dnsService.cleanUnusedCloudflareCnameDns();
	}

	// @Test
	public void testDelete() {
		this.dnsService.clearDnsRecordsCache();
		this.dnsController.deleteDnsRecord("http://111.111.111.123:123", true);
	}

}
