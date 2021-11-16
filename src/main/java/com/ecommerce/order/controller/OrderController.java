package com.ecommerce.order.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.order.client.CartClient;
import com.ecommerce.order.client.UserClient;
import com.ecommerce.order.model.Cart;
import com.ecommerce.order.model.Order;
import com.ecommerce.order.model.RequestOrder;
import com.ecommerce.order.model.RequestOrderCancellation;
import com.ecommerce.order.model.Transactions;
import com.ecommerce.order.service.OrderService;
import com.ecommerce.order.util.JwtUtil;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfStructTreeController.returnType;

import springfox.documentation.annotations.ApiIgnore;

@RestController
public class OrderController implements OrderControllerI {

	@Autowired
	private UserClient userClient;
	@Autowired
	private CartClient cartClient;
	@Autowired
	private JwtUtil jwt;
	@Autowired
	private OrderService orderService;

	@Override
	public ResponseEntity<List<Order>> placeOrder(@ApiIgnore @RequestHeader(TOKEN_STRING) String token,
			@Valid @RequestBody RequestOrder requestOrder) {
		String email = extractEmail(token);
		if (email != null) {
			List<Cart> cart = cartClient.getCartByEmail(token).getBody();

			return new ResponseEntity<>(
					orderService.placeOrder(cart, requestOrder, userClient.getUserWalletAmount(token).getBody(), token),
					HttpStatus.ACCEPTED);
		}
		return new ResponseEntity<>(HttpStatus.FORBIDDEN);
	}

	@Override
	public ResponseEntity<List<Order>> getOrdersByUser(@ApiIgnore @RequestHeader(TOKEN_STRING) String token) {
		String email = extractEmail(token);
		if (email != null) {

			return new ResponseEntity<>(orderService.getOrdersByUser(email, token), HttpStatus.ACCEPTED);
		}
		return new ResponseEntity<>(HttpStatus.FORBIDDEN);
	}

	@Override
	public ResponseEntity<Object> checkWalletAmountBeforePlaceOrder(@ApiIgnore @RequestHeader(TOKEN_STRING) String token) {
		String email = extractEmail(token);
		if (email != null) {
			return new ResponseEntity<>(
					orderService.checkWalletAmountBeforePlaceOrder(cartClient.getCartByEmail(token).getBody(),
							userClient.getUserWalletAmount(token).getBody()),
					HttpStatus.ACCEPTED);
		}
		return new ResponseEntity<>(HttpStatus.FORBIDDEN);
	}

	private String extractEmail(String token) {
		if (token != null && token.startsWith("Bearer")) {
			if (userClient.validateToken(token.substring(7)).getStatusCodeValue() == 200) {
				return jwt.extractUsername(token.substring(7));
			}
		}
		return null;
	}

	@Override
	public ResponseEntity<Order> cancelOrder(@ApiIgnore @RequestHeader(TOKEN_STRING) String token,
			@RequestBody RequestOrderCancellation cancelOrder) {
		String email = extractEmail(token);
		if (email != null) {
			return new ResponseEntity<>(orderService.cancelOrder(cancelOrder, token), HttpStatus.ACCEPTED);
		}
		return new ResponseEntity<>(HttpStatus.FORBIDDEN);
	}

	@Override
	public ResponseEntity<List<Transactions>> getUserTransactions(@ApiIgnore @RequestHeader(TOKEN_STRING) String token) {
		String email = extractEmail(token);
		if (email != null) {
			return new ResponseEntity<>(orderService.getUserTransactions(token), HttpStatus.ACCEPTED);
		}
		return new ResponseEntity<>(HttpStatus.FORBIDDEN);
	}

	@Override
	public ResponseEntity<Void> createPdf(@ApiIgnore @RequestHeader(TOKEN_STRING) String token,@PathVariable("orderId") String orderId)
			throws DocumentException, IOException {
		
		orderService.createPdf(orderId);
		return ResponseEntity.ok().build();

	}

	@Override
	public ResponseEntity<Resource> viewPdf(@PathVariable("orderId") String orderId) throws IOException {
		HttpHeaders header = new HttpHeaders();
		header.add(HttpHeaders.CONTENT_DISPOSITION, "inline;filename=samplePdf.pdf");

		File file = new File("invoice_"+orderId+".pdf");
		Path path = Paths.get(file.getAbsolutePath());

		ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));

		return ResponseEntity.ok().headers(header).contentLength(file.length())
				.contentType(MediaType.parseMediaType("application/pdf")).body(resource);
	}
}
