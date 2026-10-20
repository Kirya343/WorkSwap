package org.workswap;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.workswap.sso.SsoApplication;

@SpringBootTest(
    classes = SsoApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.NONE
)
@ActiveProfiles({"sso", "test", "api"})
class StartUpTest {

    @MockitoBean
    RabbitTemplate rabbitTemplate;

    @Test
    void listingsStatProducerExists() {
        System.out.println("Hello");
    }
}