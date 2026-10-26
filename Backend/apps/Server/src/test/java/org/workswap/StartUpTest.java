package org.workswap;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.workswap.storage.config.S3Properties;

import software.amazon.awssdk.services.s3.S3Client;

@SpringBootTest(
    classes = ServerApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.NONE
)
@ActiveProfiles({"server", "test", "api"})
class StartUpTest {

    @MockitoBean
    S3Client s3Client;

    @MockitoBean
    S3Properties s3Properties;

    @MockitoBean
    RabbitTemplate rabbitTemplate;
    
    @Test
    void listingsStatProducerExists() {
        System.out.println("Hello");
    }
}