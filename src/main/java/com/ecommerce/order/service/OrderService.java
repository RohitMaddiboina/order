package com.ecommerce.order.service;

import java.util.List;

import com.ecommerce.order.model.Cart;
import com.ecommerce.order.model.Order;
import com.ecommerce.order.model.RequestOrder;
import com.ecommerce.order.model.RequestOrderCancellation;

public interface OrderService {
	
	List<Order> placeOrder(List<Cart> cart, RequestOrder requestOrder, Float userWalletAmount,String token) ;

	boolean debitFromUserWallet(String token, float amount,RequestOrder requestOrder) ;

	boolean checkWalletAmountBeforePlaceOrder(List<Cart> cart, Float userWalletAmount);

	List<Order> getOrdersByUser(String email,String token);

	Order cancelOrder(RequestOrderCancellation cancelOrder, String token);
}
