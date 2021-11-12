package com.ecommerce.order.Dao;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ecommerce.order.model.Item;
import com.ecommerce.order.model.Order;
import com.ecommerce.order.model.PaymentMethod;
import com.ecommerce.order.model.TransactionType;
import com.ecommerce.order.model.Transactions;
import com.ecommerce.order.model.User;
import com.ecommerce.order.repository.OrderRepo;
import com.ecommerce.order.repository.TransactionRepo;

@Component
public class OrderDaoImpl implements OrderDao {

	@Autowired
	private OrderRepo orderRepo;
	
	@Autowired
	private TransactionRepo transactionRepo;
	
	@SuppressWarnings("deprecation")
	public Order placeOrder(int quantity, float amount, boolean paymentStatus, PaymentMethod paymentMethod, Date orderedDate, String deliveryAddress,
			String deliveryStatus, boolean orderCancellationStatus, String orderCancellationReason, Date orderCancellationDate,int cancellationQuantity, boolean refundStatus, Date refundDate, User user,
			Item item) {
		Order order=orderRepo.save(new Order());
		
		
		Date currentDate=orderedDate;
		int year=currentDate.getYear();
		String month=currentDate.getMonth()+1>9?""+(currentDate.getMonth()+1):"0"+(currentDate.getMonth()+1);
		int presentDate=currentDate.getDate();
		String orderId="OID"+year+month+presentDate+(orderRepo.countByOrderedDate(orderedDate)+1);
		return orderRepo.save(new Order(order.getId(),orderId, quantity, amount, paymentStatus, paymentMethod, orderedDate, deliveryAddress, deliveryStatus, orderCancellationStatus, orderCancellationReason, orderCancellationDate,cancellationQuantity, refundStatus, refundDate, user, item));
		
	}

	public List<Order> getOrdersByUserId(int id) {
		
		return orderRepo.findByUser_IdOrderByIdDesc(id);
	}

	@Override
	public Order getOrderByOrderId(String orderId) {
		return orderRepo.findByOrderId(orderId);
	}

	@Override
	public Order updateOrder(Order order) {
		return orderRepo.save(order);
	}

	@Override
	public Transactions addTransaction(Transactions transaction) {
		Transactions t=transactionRepo.save(new Transactions());
		return transactionRepo.save(new Transactions(t.getId(),transaction.getOrder(), new Date(),transaction.getAmount() , transaction.getTransactionType(),transaction.getPaymentMethod()));
	}

	@Override
	public List<Transactions> getTransactionsByUser(int userId) {
		return transactionRepo.findByOrder_User_idOrderByDateOfTransactionDesc(userId);
	}
	
	

}
