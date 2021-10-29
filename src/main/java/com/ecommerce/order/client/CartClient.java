package com.ecommerce.order.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import com.ecommerce.order.model.Cart;

@FeignClient(name = "CartClient",url = "http://localhost:8081/cart")
public interface CartClient {
	public static final String   TOKEN_STRING  = "Authorization";

	@PostMapping("/{itemId}")
	public ResponseEntity<Cart> addToCart(@RequestHeader(TOKEN_STRING) String token,@PathVariable("itemId") int itemId);
	@GetMapping("/{itemId}")
	public  ResponseEntity<Cart> getCartByEmailAndItemId(@RequestHeader(TOKEN_STRING) String token,@PathVariable("itemId") int itemId);
	@GetMapping("")
	public ResponseEntity<List<Cart>> getCartByEmail(@RequestHeader(TOKEN_STRING) String token);
	@DeleteMapping("/{itemId}")
	public  ResponseEntity<Cart>  removeFromCart(@RequestHeader(TOKEN_STRING) String token ,@PathVariable("itemId") int itemId);
	@DeleteMapping("/{itemId}/-")
	public  ResponseEntity<Cart>  removeOneItemFromCart(@RequestHeader(TOKEN_STRING) String token,@PathVariable("itemId") int itemId);
	@GetMapping("/count")
	public  ResponseEntity<Integer>  getCartCount(@RequestHeader(TOKEN_STRING) String token);
	

}
