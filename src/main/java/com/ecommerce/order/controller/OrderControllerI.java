package com.ecommerce.order.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.ecommerce.order.model.Order;
import com.ecommerce.order.model.RequestOrder;

public interface OrderControllerI {

	String TOKEN_STRING = "Authorization";

	@PostMapping("/")
	ResponseEntity<List<Order>> placeOrder(String token, RequestOrder requestOrder);
	@GetMapping("/")
	ResponseEntity<List<Order>> getOrdersByUser(String token);

	@GetMapping("/isWalletAmountSufficient")
	ResponseEntity<Object> checkWalletAmountBeforePlaceOrder(String token);

}