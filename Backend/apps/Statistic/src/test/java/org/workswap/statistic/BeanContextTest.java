package org.workswap.statistic;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.workswap.StatisticApplication;
import org.workswap.listing.datasource.repository.ListingRepository;
import org.workswap.statistic.amqp.consumers.ListingsStatConsumer;

@SpringBootTest(
    classes = StatisticApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.NONE
)
@ActiveProfiles({"statistic", "test"})
class BeanContextTest {

    @Autowired
    private ApplicationContext context;

    @MockitoBean
    private ListingRepository listingRepository;

    @Test
    void listingsStatProducerExists() {
        System.out.println(
            context.getBeansOfType(ListingsStatConsumer.class)
        );
    }
}