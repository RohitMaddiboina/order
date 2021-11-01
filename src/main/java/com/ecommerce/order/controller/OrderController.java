package com.ecommerce.order.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.order.client.CartClient;
import com.ecommerce.order.client.UserClient;
import com.ecommerce.order.model.Cart;
import com.ecommerce.order.model.Order;
import com.ecommerce.order.model.RequestOrder;
import com.ecommerce.order.service.OrderServiceImpl;
import com.ecommerce.order.util.JwtUtil;

//@CrossOrigin(origins ="http://localhost:4200")
@RestController
@CrossOrigin
@RequestMapping("/orders")
public class OrderController implements OrderControllerI {

	@Autowired
	private UserClient userClient;
	@Autowired
	private CartClient cartClient;
	@Autowired
	private JwtUtil jwt;
	@Autowired
	private OrderServiceImpl orderService;

	// This method will take address details and token,it will post the item details ordered by user ,post into order database
	@Override
	public ResponseEntity<List<Order>> placeOrder(@RequestHeader(TOKEN_STRING) String token,@Valid @RequestBody RequestOrder requestOrder) {
		String email=extractEmail(token);
		if(email!=null) {
			List<Cart> cart= cartClient.getCartByEmail(token).getBody();


			return new ResponseEntity<>(orderService.placeOrder(cart,requestOrder,userClient.getUserWalletAmount(token).getBody(),token),HttpStatus.ACCEPTED);
		}
		return  new ResponseEntity<>(HttpStatus.FORBIDDEN);
	}
	
	//This method will display the items ordered by user and amount details,order data etc
	@Override
	public ResponseEntity<List<Order>> getOrdersByUser(@RequestHeader(TOKEN_STRING) String token){
		String email=extractEmail(token);
		if(email!=null) {
			

			return new ResponseEntity<>(orderService.getOrdersByUser(email,token),HttpStatus.ACCEPTED);
		}
		return  new ResponseEntity<>(HttpStatus.FORBIDDEN);
	}

	//This mehtod will return wallet balance available or not
	@Override
	public ResponseEntity<Object> checkWalletAmountBeforePlaceOrder(@RequestHeader(TOKEN_STRING) String token){
		String email=extractEmail(token);
		if(email!=null) {
			return new ResponseEntity<>(orderService.checkWalletAmountBeforePlaceOrder(cartClient.getCartByEmail(token).getBody(),userClient.getUserWalletAmount(token).getBody()),HttpStatus.ACCEPTED);
		}
		return  new ResponseEntity<>(HttpStatus.FORBIDDEN);
	}

	//This method will fetch mailid of user according to token placed
	private String extractEmail(String token) {
		if(token!=null && token.startsWith("Bearer") && userClient.validateToken(token.substring(7)).getStatusCodeValue()==200){
				return jwt.extractUsername(token.substring(7));							 
		}
		return null;
	}
}
