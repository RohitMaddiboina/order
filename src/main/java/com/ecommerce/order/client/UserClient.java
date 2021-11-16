package com.ecommerce.order.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import com.ecommerce.order.model.Cart;
import com.ecommerce.order.model.User;

@FeignClient(name = "user-service",url = "http://localhost:8080/fasscio")
public interface UserClient {

	public static final String   TOKEN_STRING  = "Authorization";

	@PostMapping("/validate")
	public ResponseEntity<Object> validateToken(@RequestHeader(TOKEN_STRING) String token);
	
	@GetMapping("/get")
	ResponseEntity<User> getUser(@RequestHeader(TOKEN_STRING) String token);

	@GetMapping("/wallet")
	ResponseEntity<Float> getUserWalletAmount(@RequestHeader(TOKEN_STRING) String token);

	@PutMapping("/debit/{amount}")
	public ResponseEntity<Object> debitFromUserWallet(@RequestHeader(TOKEN_STRING) String token,@PathVariable("amount") float amount);

	@PutMapping("/cedit/{amount}")
	public ResponseEntity<User> addAmountToUserWallet(@RequestHeader(TOKEN_STRING) String token,@PathVariable("amount") float amount);

}
