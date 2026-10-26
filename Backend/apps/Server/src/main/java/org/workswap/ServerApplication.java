package org.workswap;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import org.springframework.scheduling.annotation.EnableScheduling;

@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
@SpringBootApplication
@EnableScheduling
@EnableTransactionManagement
public class ServerApplication {

	public static void main(String[] args) {
        SpringApplication app = new SpringApplication(ServerApplication.class);
        app.setBannerMode(Banner.Mode.OFF);
    }
}
