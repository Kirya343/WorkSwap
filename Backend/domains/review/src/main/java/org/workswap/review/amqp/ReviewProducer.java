package org.workswap.review.amqp;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.workswap.shared.events.review.ReviewCreatedEvent;

import lombok.RequiredArgsConstructor;

@Service
@Profile("server")
@RequiredArgsConstructor
public class ReviewProducer {

    private final RabbitTemplate template;

    public void reviewCreated(ReviewCreatedEvent event) {

        template.convertAndSend("reviewsQueue", event);
    }
}
