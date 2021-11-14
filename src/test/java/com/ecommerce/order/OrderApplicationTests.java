package com.ecommerce.order;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import org.springframework.test.web.servlet.MockMvc;



import com.ecommerce.order.model.PaymentMethod;
import com.ecommerce.order.model.RequestOrder;
import com.ecommerce.order.model.RequestOrderCancellation;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc

class OrderApplicationTests {

	 @Autowired
	 private MockMvc mock;

	 private static final String token = ""
	 		+ "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0aXJ1bWFsYXNldHR5dms5OEBnbWFpbC5jb20iLCJleHAiOjE2Mzc"
	 		+ "wNzQ4NDEsImlhdCI6MTYzNjgxNTY0MX0.FKTfBnyVzFAkerISkzmtcAG5oXRWOpmcIf2YN6xho7Q";
	 @Test
	 void placeOrder() throws Exception {

	 RequestOrder requestOrder=new RequestOrder();
	 
	 
	 	requestOrder.setPaymentMethod(PaymentMethod.WALLET);
	 	requestOrder.setDeliveryAddress("Guntur Narasaraopeta");
	 
	 ObjectMapper mapper = new ObjectMapper();
	 
	 String jsonData = mapper.writeValueAsString(requestOrder);
	 mock.perform(post("/orders/").content(jsonData).contentType("application/json").header("Authorization", "Bearer " + token))
	 .andExpect(status().isAccepted())
	 .andExpect(jsonPath("$").exists());
	 
	 // ---------------------------get all orders to display to user// ResponseEntity<List<Order>> getOrdersByUser(String token);---------------------------------
	 mock.perform(get("/orders/").header("Authorization", "Bearer " + token))
	 .andExpect(status().isAccepted())
	 .andExpect(jsonPath("$").exists());
	 
	
	/*@GetMapping("/isWalletAmountSufficient")
	 * 
		ResponseEntity<Object> checkWalletAmountBeforePlaceOrder(String token); */
	 
	 mock.perform(get("/orders/isWalletAmountSufficient").header("Authorization", "Bearer " + token))
	 .andExpect(status().isAccepted())
	 .andExpect(jsonPath("$").exists());
	 
	 
	/*This method will fetch mailid of user according to token placed
		@PutMapping("/")
		ResponseEntity<Order> cancelOrder(String token,RequestOrderCancellation cancelOrder); */
	 
	
//	 @Test
//	 void cancelOrder() throws Exception 
//	 {
		 
	
		 
		 RequestOrderCancellation  cancel=new RequestOrderCancellation();
		 
		 cancel.setOrderId("OID12111143");
		 cancel.setQuantity(1);
		 cancel.setReason("Money is not available");
		 
		 String jsonDataTran = mapper.writeValueAsString(cancel);
		 mock.perform(put("/orders/").content(jsonDataTran).contentType("application/json").header("Authorization", "Bearer " + token))
		 .andExpect(status().isAccepted())
		 .andExpect(jsonPath("$").exists());
	// }
	
	 
	 
	 /*  @GetMapping("/transactions")
		ResponseEntity<List<Transactions>> getUserTransactions(String token); */
		 
		 mock.perform(get("/orders/transactions").header("Authorization", "Bearer " + token))
		 .andExpect(status().isAccepted())
		 .andExpect(jsonPath("$").exists()); 
	 
	 


	 }
	 
	


	 @Test
	 void NTests() throws Exception
	 {

	 RequestOrder requestOrder=new RequestOrder();
	 
	// requestOrder.set
	 
	 requestOrder.setPaymentMethod(PaymentMethod.WALLET);
	 requestOrder.setDeliveryAddress("Guntur Narasaraopeta");
	 ObjectMapper mapper = new ObjectMapper();
	 String jsonData = mapper.writeValueAsString(requestOrder);

	 mock.perform(post("/orders/").content(jsonData).contentType("application/json").header("Authorization",token))
	 .andExpect( status().isForbidden() ) ;

	 mock.perform(post("/orders/").header("Authorization","Bearer "+token))
	 .andExpect( status().isBadRequest() ) ;

	 mock.perform(get("/orders/").header("Authorization", token))
	 .andExpect( status().isForbidden() );

	 mock.perform(get("/orders/isWalletAmountSufficient").header("Authorization", token))
	 .andExpect(status().isForbidden() );

// This is for payment method is empty 
	  RequestOrder requestOrder1=new RequestOrder();
//	 requestOrder1.setPaymentMethod("");
	 requestOrder1.setDeliveryAddress("");
	 ObjectMapper mapper1 = new ObjectMapper();
	 String jsonData1= mapper1.writeValueAsString(requestOrder1);

	 mock.perform(post("/orders/").content(jsonData1).contentType("application/json").header("Authorization","Bearer "+token))
	 .andExpect( status().isBadRequest() ) ;

	 //-----------------post is not supported when get method is used

	 mock.perform(post("/orders/isWalletAmountSufficient").header("Authorization", "Bearer " + token))
	 .andExpect(status().isMethodNotAllowed() );


	 }


	 }


