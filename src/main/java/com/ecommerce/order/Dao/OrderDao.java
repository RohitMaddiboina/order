package com.ecommerce.order.Dao;

import java.util.Date;
import java.util.List;

import com.ecommerce.order.model.Item;
import com.ecommerce.order.model.Order;
import com.ecommerce.order.model.User;

public interface OrderDao {
	Order placeOrder(int quantity, float amount, boolean paymentStatus, String paymentMethod, Date orderedDate, String deliveryAddress,
			String deliveryStatus, boolean orderCancellationStatus, String orderCancellationReason, Date orderCancellationDate, boolean refundStatus, Date refundDate, User user,
			Item item) ;
	List<Order> getOrdersByUserId(int id) ;
}
