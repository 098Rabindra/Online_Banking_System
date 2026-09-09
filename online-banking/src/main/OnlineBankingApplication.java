package com.bank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class OnlineBankingApplication {

	public static void main(String[] args) {
		System.setProperty("java.net.preferIPv6Addresses", "true");
		SpringApplication.run(OnlineBankingApplication.class, args);
	}

}   

/*
netstat -ano | findstr :8080
taskkill /PID 12345 /F
*/
// IFSC: KBIN0005124
// localStorage.clear();
