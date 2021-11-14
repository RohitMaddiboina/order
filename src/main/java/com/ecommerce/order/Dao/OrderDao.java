package com.ecommerce.order.Dao;

import java.util.Date;
import java.util.List;

import com.ecommerce.order.model.Item;
import com.ecommerce.order.model.Order;
import com.ecommerce.order.model.PaymentMethod;
import com.ecommerce.order.model.Transactions;
import com.ecommerce.order.model.User;

public interface OrderDao {
	Order placeOrder(int quantity, float amount, boolean paymentStatus, PaymentMethod paymentMethod, Date orderedDate, String deliveryAddress,
			String deliveryStatus, boolean orderCancellationStatus, String orderCancellationReason, Date orderCancellationDate,int cancellationQuantity, boolean refundStatus, Date refundDate, User user,
			Item item) ;
	List<Order> getOrdersByUserId(int id) ;
	
	Order getOrderByOrderId(String orderId);
	
	Order updateOrder(Order order);
	
	Transactions addTransaction(Transactions transaction);
	
	List<Transactions> getTransactionsByUser(int userId);
}
