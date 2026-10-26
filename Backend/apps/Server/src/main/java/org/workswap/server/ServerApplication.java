package org.workswap.server;

import java.util.function.Consumer;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import io.github.kirya343.terminal.components.DiagnosticCommandProvider;
import io.github.kirya343.terminal.runtime.TerminalManaged;

import org.springframework.scheduling.annotation.EnableScheduling;

@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
@SpringBootApplication
@EnableScheduling
@EnableTransactionManagement
@TerminalManaged 
public class ServerApplication {

	public static void main(String[] args, Consumer<DiagnosticCommandProvider> registerProvider) {
        SpringApplication app = new SpringApplication(ServerApplication.class);
        app.setBannerMode(Banner.Mode.OFF);
    }
}
