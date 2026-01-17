package com.ecommerce.notification;

import com.ecommerce.notification.payload.OrderCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;


@Service
@Slf4j
public class OrderEventConsumer {

    @Bean
    public Consumer<OrderCreatedEvent> orderCreated() {
        return event -> {
            log.info("Recieved order created event for order: {}", event.getOrderId());
            log.info("Recieved order created event for user id: {}", event.getUserId());
        };
    }

//    @RabbitListener(queues = "${rabbitmq.queue.name}")
//    public void handleOrderEvent(OrderCreatedEvent orderEvent) {
//        System.out.println("Received order event: " + orderEvent);
//
//        long orderId = orderEvent.getOrderId();
//        OrderStatus status = orderEvent.getStatus();
//
//        System.out.println("Order Id: " + orderId);
//        System.out.println("Order Status: " + status);
//
//        // Update Database
//        // Send Notification
//        // Send Emails
//        // Generate Invoice
//        // Send Seller Notification
}
