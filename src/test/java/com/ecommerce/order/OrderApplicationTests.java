package com.ecommerce.order;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import com.ecommerce.order.model.PaymentMethod;
import com.ecommerce.order.model.RequestOrder;


import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc

class OrderApplicationTests {

	 @Autowired
	 private MockMvc mock;

	 private static final String token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJyb2hpdHRlamExOThAZ21haWwuY29tIiwiZXhwIjoxNjM2MDMzNzE5LCJpYXQiOjE2MzU3NzQ1MTl9.TP3qQilvCG9O2nDUx74rfcXlWj8yljWNMsMVrRacAJQ";
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
	 
	 // get all orders to display to user
	 mock.perform(get("/orders/").header("Authorization", "Bearer " + token))
	 .andExpect(status().isAccepted())
	 .andExpect(jsonPath("$").exists());
	 
	 mock.perform(get("/orders/isWalletAmountSufficient").header("Authorization", "Bearer " + token))
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


