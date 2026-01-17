package com.ecom.microservice.app.repository;

import com.ecom.microservice.app.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository
        extends JpaRepository<Order, Long> {
}
