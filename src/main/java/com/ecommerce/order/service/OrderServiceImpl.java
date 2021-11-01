package com.ecommerce.order.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecommerce.exception.OrderException;
import com.ecommerce.order.Dao.OrderDao;
import com.ecommerce.order.client.CartClient;
import com.ecommerce.order.client.UserClient;
import com.ecommerce.order.model.Cart;
import com.ecommerce.order.model.Order;
import com.ecommerce.order.model.RequestOrder;
import com.ecommerce.order.model.User;

@Service
public class OrderServiceImpl implements OrderService {

	@Autowired
	private OrderDao orderDao;

	@Autowired
	private CartClient cartClient;

	@Autowired
	private UserClient userClient;

	private static final List<String> PAYMENT_METHOD=new ArrayList<String>() {{add("WALLET");add("PAY_ON_DELIVERY");}};


	//	public boolean placeOrder(List<Cart> cart, @Valid RequestOrder requestOrder) {
	//		boolean status=false;
	//		if(PAYMENT_METHOD.contains(requestOrder.getPaymentMethod().toUpperCase())) {
	//			orderDao.placeOrder()
	//		}
	//		throw new OrderException("Invalid Payment Method");
	//	}


	public List<Order> placeOrder(List<Cart> cart, RequestOrder requestOrder, Float userWalletAmount,String token) {
		boolean status=false;
		if(PAYMENT_METHOD.contains(requestOrder.getPaymentMethod().toUpperCase())) {				// to check whether the payment method are of two type
//			if(requestOrder.getPaymentMethod().toUpperCase().equals(PAYMENT_METHOD.get(0))) {		// to use Wallet
				if(!checkWalletAmountBeforePlaceOrder(cart,userWalletAmount)&&requestOrder.getPaymentMethod().toUpperCase().equals(PAYMENT_METHOD.get(0))) {						//checking whether the wallet amount is available for the total price
					throw new OrderException("Insufficient Balance");		
				}
				Iterator<Cart> i=cart.iterator();
				List<Order> orders=new ArrayList<Order>();
				while(i.hasNext()) {
					Cart item=i.next();
					Order o=null;
					SimpleDateFormat formatter = new SimpleDateFormat(
							"yyyy-MM-dd");
					try {
						Date date= formatter.parse(formatter.format(new Date()));
						o=orderDao.placeOrder(item.getQuantity(),item.getQuantity()*item.getItem().getPrice(),debitFromUserWallet(token,item.getQuantity()*item.getItem().getPrice(),requestOrder),requestOrder.getPaymentMethod().toUpperCase(),date,
								requestOrder.getDeliveryAddress(),"In Progress",status, null, null, status, null, item.getUser(), item.getItem());

					} catch (ParseException e) {
						e.printStackTrace();
					}
					if(o.isPaymentStatus()||requestOrder.getPaymentMethod().toUpperCase().equals(PAYMENT_METHOD.get(1))) {
						cartClient.removeFromCart(token, item.getItem().getItemId()).getBody();
						orders.add(o);
					}
				}			

				//			orderDao.placeOrder()

				return orders;
			}
			throw new OrderException("Invalid Payment Method");
		}




		public boolean debitFromUserWallet(String token, float amount,RequestOrder requestOrder) {
			if(requestOrder.getPaymentMethod().toUpperCase().equals(PAYMENT_METHOD.get(0))) {
				boolean f=(boolean) userClient.debitFromUserWallet(token,amount).getBody();
				return f;
			}
			return false;
		}




		public  boolean checkWalletAmountBeforePlaceOrder(List<Cart> cart, Float userWalletAmount) {
			
			double totalAmt=cart.stream().mapToDouble(d->(d.getQuantity()*d.getItem().getPrice())).sum();
			if(userWalletAmount>totalAmt) {
				return true;
			}
			return  userWalletAmount>totalAmt;

		}




		public List<Order> getOrdersByUser(String email,String token) {
			User user=userClient.getUser(token).getBody();
			
			return orderDao.getOrdersByUserId(user.getId());
		}

	}
