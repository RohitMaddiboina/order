package com.ecommerce.order.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecommerce.order.model.Order;

@Repository
public interface OrderRepo extends JpaRepository<Order, Integer>{

	int countByOrderedDate(Date d);

	List<Order> findByUser_Id(int id);

	Order findByOrderId(String orderId);

	List<Order> findByUser_IdOrderByOrderIdDesc(int id);

}
