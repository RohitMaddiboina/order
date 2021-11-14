package com.ecommerce.order.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

import com.ecommerce.order.model.Item;

@FeignClient(name="item-service",url = "http://localhost:8082")
public interface ItemClient {
	
	@GetMapping("/getItem/{itemId}")
	Item getItem(@PathVariable("itemId") int itemId);
	
	@PutMapping("/addQuantity/{itemId}/{quantity}")
	Item addQuantityToItems(@PathVariable int itemId,@PathVariable  int quantity);
	
	@PutMapping("/removeQuantity/{itemId}/{quantity}")
	Item removeQuantityFromItem(@PathVariable int itemId,@PathVariable  int quantity);

}
