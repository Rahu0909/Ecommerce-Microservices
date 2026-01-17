package com.ecom.microservice.app.service;

import com.ecom.microservice.app.dto.OrderCreatedEvent;
import com.ecom.microservice.app.dto.OrderItemDTO;
import com.ecom.microservice.app.dto.OrderResponse;
import com.ecom.microservice.app.model.CartItem;
import com.ecom.microservice.app.model.Order;
import com.ecom.microservice.app.model.OrderItem;
import com.ecom.microservice.app.model.OrderStatus;
import com.ecom.microservice.app.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartItemService cartService;
    private final ModelMapper modelMapper;
    private final StreamBridge streamBridge;

    public Optional<OrderResponse> createOrder(String userId) {
        //validate for cart items
        List<CartItem> cartItems = cartService.getCart(userId);
        if (cartItems.isEmpty()) {
            return Optional.empty();
        }
//        //validate for user
//        Optional<User> userOptional = userRepository.findById(Long.valueOf(userId));
//        if (userOptional.isEmpty()) {
//            return Optional.empty();
//        }
//        User user = userOptional.get();
        //calculate total price
        BigDecimal totalPrice = cartItems.stream()
                .map(CartItem::getPrice)
                .reduce(BigDecimal.ZERO,
                        BigDecimal::add);
        //Create order
        Order order = new Order();
        order.setUserId(userId);
        order.setStatus(OrderStatus.CONFIRMED);
        order.setTotalAmount(totalPrice);

        List<OrderItem> orderItems = cartItems.stream()
                .map(item -> new OrderItem(
                        null,
                        item.getProductId(),
                        item.getQuantity(),
                        item.getPrice(),
                        order
                ))
                .toList();
        order.setItems(orderItems);
        Order savedOrder = orderRepository.save(order);

        //Clear the cart
        cartService.clearCart(userId);

        // Publish order created event
        OrderCreatedEvent event = new OrderCreatedEvent(
                savedOrder.getId(),
                savedOrder.getUserId(),
                savedOrder.getStatus(),
                savedOrder.getItems()
                        .stream()
                        .map(item -> modelMapper.map(item, OrderItemDTO.class))
                        .toList(),
                savedOrder.getTotalAmount(),
                savedOrder.getCreatedAt()
        );
        streamBridge.send("createOrder-out-0", event);

        return Optional.of(modelMapper.map(savedOrder,
                OrderResponse.class));
    }
}
