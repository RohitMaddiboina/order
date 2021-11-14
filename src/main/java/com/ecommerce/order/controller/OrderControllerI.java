package com.ecommerce.order.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ecommerce.order.model.Order;
import com.ecommerce.order.model.RequestOrder;
import com.ecommerce.order.model.RequestOrderCancellation;
import com.ecommerce.order.model.Transactions;

@RequestMapping("/orders")
public interface OrderControllerI {

	String TOKEN_STRING = "Authorization";

	// This method will take address details and token,it will post the item details ordered by user ,post into order database
	@PostMapping("/")
	ResponseEntity<List<Order>> placeOrder(String token, RequestOrder requestOrder);
	
	//This method will display the items ordered by user and amount details,order data etc
	@GetMapping("/")
	ResponseEntity<List<Order>> getOrdersByUser(String token);

	//This mehtod will return wallet balance available or not
	@GetMapping("/isWalletAmountSufficient")
	ResponseEntity<Object> checkWalletAmountBeforePlaceOrder(String token);
	
	//This method will fetch mailid of user according to token placed
	@PutMapping("/")
	ResponseEntity<Order> cancelOrder(String token,RequestOrderCancellation cancelOrder);
	
	@GetMapping("/transactions")
	ResponseEntity<List<Transactions>> getUserTransactions(String token);

}