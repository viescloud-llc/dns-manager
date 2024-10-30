package com.vincent.llc.dns.manager;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.viescloud.llc.viesspringutils.util.Json;
import com.vincent.llc.dns.manager.service.DnsService;

@SpringBootTest(classes = Application.class)
class ApplicationTests {
	
	@Autowired
	private DnsService dnsService;

	// @Test
	public void dnsServiceTest() {
		this.dnsService.clearDnsRecordsCache();
		var records = this.dnsService.getDnsRecordList();
		System.out.println(Json.builder().target(records).build().tryToJson());
	}

}
