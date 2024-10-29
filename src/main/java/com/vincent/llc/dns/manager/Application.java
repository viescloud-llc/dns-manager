package com.vincent.llc.dns.manager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

import com.viescloud.llc.viesspringutils.ViesApplication;

@EnableFeignClients
@SpringBootApplication
public class Application extends ViesApplication {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Override
	public String getApplicationName() {
		return "dns manager service";
	}

}
