package com.ecommerce.order.Dao;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ecommerce.order.model.Item;
import com.ecommerce.order.model.Order;
import com.ecommerce.order.model.User;
import com.ecommerce.order.repository.OrderRepo;

@Component
public class OrderDaoImpl implements OrderDao {

	@Autowired
	private OrderRepo orderRepo;
	
	@SuppressWarnings("deprecation")
	public Order placeOrder(int quantity, float amount, boolean paymentStatus, String paymentMethod, Date orderedDate, String deliveryAddress,
			String deliveryStatus, boolean orderCancellationStatus, String orderCancellationReason, Date orderCancellationDate, boolean refundStatus, Date refundDate, User user,
			Item item) {
		Order order=orderRepo.save(new Order());
		
		
		Date currentDate=orderedDate;
		int year=currentDate.getYear();
		String month=currentDate.getMonth()+1>9?""+(currentDate.getMonth()+1):"0"+(currentDate.getMonth()+1);
		int presentDate=currentDate.getDate();
		String orderId="OID"+year+month+presentDate+(orderRepo.countByOrderedDate(orderedDate)+1);
		return orderRepo.save(new Order(order.getId(),orderId, quantity, amount, paymentStatus, paymentMethod, orderedDate, deliveryAddress, deliveryStatus, orderCancellationStatus, orderCancellationReason, orderCancellationDate, refundStatus, refundDate, user, item));
		
	}

	public List<Order> getOrdersByUserId(int id) {
		
		return orderRepo.findByUser_Id(id);
	}

}
