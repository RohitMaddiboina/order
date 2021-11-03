package com.ecommerce.order.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

import com.ecommerce.order.model.Item;

@FeignClient(name="ItemClient",url = "http://localhost:8082")
public interface ItemClient {
	
	@PutMapping("/addQuantity/{itemId}/{quantity}")
	Item addQuantityToItems(@PathVariable int itemId,@PathVariable  int quantity);
	
	@PutMapping("/removeQuantity/{itemId}/{quantity}")
	Item removeQuantityFromItem(@PathVariable int itemId,@PathVariable  int quantity);

}
