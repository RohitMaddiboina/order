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
import com.ecommerce.order.client.ItemClient;
import com.ecommerce.order.client.UserClient;
import com.ecommerce.order.model.Cart;
import com.ecommerce.order.model.Item;
import com.ecommerce.order.model.Order;
import com.ecommerce.order.model.RequestOrder;
import com.ecommerce.order.model.RequestOrderCancellation;
import com.ecommerce.order.model.User;

@Service
public class OrderServiceImpl implements OrderService {

	@Autowired
	private OrderDao orderDao;

	@Autowired
	private CartClient cartClient;

	@Autowired
	private UserClient userClient;
	
	@Autowired 
	private ItemClient itemClient;

	private static final List<String> PAYMENT_METHOD=new ArrayList<String>() {{add("WALLET");add("PAY_ON_DELIVERY");}};


	//	public boolean placeOrder(List<Cart> cart, @Valid RequestOrder requestOrder) {
	//		boolean status=false;
	//		if(PAYMENT_METHOD.contains(requestOrder.getPaymentMethod().toUpperCase())) {
	//			orderDao.placeOrder()
	//		}
	//		throw new OrderException("Invalid Payment Method");
	//	}

	//This method will take cart details added by user and take it as parameter,wallet balance,token
		//and return the list of orders placed by user
	public List<Order> placeOrder(List<Cart> cart, RequestOrder requestOrder, Float userWalletAmount,String token) {
		boolean status=false;
		
		//payment method must be from wallet or Pay_on_Delivery
		if(PAYMENT_METHOD.contains(requestOrder.getPaymentMethod().toUpperCase())) {				// to check whether the payment method are of two type


			//this if checks whehter wallet not available and payment mehtod must be wallet balnce
			//this if ok then display message insufficient balance
				if(!checkWalletAmountBeforePlaceOrder(cart,userWalletAmount)&&requestOrder.getPaymentMethod().toUpperCase().equals(PAYMENT_METHOD.get(0))) {						//checking whether the wallet amount is available for the total price
					throw new OrderException("Insufficient Balance");		
				}
				
				// if wallet balance is ok ,it will place the orders one by one upto cart empty
				Iterator<Cart> i=cart.iterator();
				List<Order> orders=new ArrayList<Order>();
				while(i.hasNext()) {
					Cart item=i.next();
					Order o=null;
					SimpleDateFormat formatter = new SimpleDateFormat(
							"yyyy-MM-dd");
					//this try block parse date format ,
					try {
						Date date= formatter.parse(formatter.format(new Date()));
						
						//it will take parameter from items details ,placed to order object save the order object
						o=orderDao.placeOrder(item.getQuantity(),item.getQuantity()*item.getItem().getPrice(),debitFromUserWallet(token,item.getQuantity()*item.getItem().getPrice(),requestOrder),requestOrder.getPaymentMethod().toUpperCase(),date,
								requestOrder.getDeliveryAddress(),"In Progress",status, null, null, status, null, item.getUser(), item.getItem());

					} catch (ParseException e) {
						e.printStackTrace();
					}
					
					// payment is ok from wallet or payment method is pay on delevery,it remove item from cart placed by user
					if(o.isPaymentStatus()||requestOrder.getPaymentMethod().toUpperCase().equals(PAYMENT_METHOD.get(1))) {
						cartClient.removeFromCart(token, item.getItem().getItemId()).getBody();
						removeQuantityFromItem(item.getItem().getItemId(),item.getQuantity());
						orders.add(o);
					}
				}			


				return orders;
			}
		//if payment method is not valid it will below exception
			throw new OrderException("Invalid Payment Method");
		}



	// this method will debit from order payment- wallet amount
		public boolean debitFromUserWallet(String token, float amount,RequestOrder requestOrder) {
			if(requestOrder.getPaymentMethod().toUpperCase().equals(PAYMENT_METHOD.get(0))) {
				boolean f=(boolean) userClient.debitFromUserWallet(token,amount).getBody();
				return f;
			}
			return false;
		}



		//This method return will orderPayment is greater than wallet balacne
		public  boolean checkWalletAmountBeforePlaceOrder(List<Cart> cart, Float userWalletAmount) {
			
			double totalAmt=cart.stream().mapToDouble(d->(d.getQuantity()*d.getItem().getPrice())).sum();
			if(userWalletAmount>totalAmt) {
				return true;
			}
			boolean s= userWalletAmount>totalAmt;
			return s;
		}



		//This method fetch all orders placed by user
		public List<Order> getOrdersByUser(String email,String token) {
			User user=userClient.getUser(token).getBody();
			
			return orderDao.getOrdersByUserId(user.getId());
		}

		@Override
		public Order cancelOrder(RequestOrderCancellation cancelOrder, String token) {
			Order order=orderDao.getOrderByOrderId(cancelOrder.getOrderId());
			if(order==null) {
				throw new OrderException("Order Not Found");
			}
			if(order.isOrderCancellationStatus()) {
				throw new OrderException("Order Is Inactive");
			}
			order.setOrderCancellationDate(new Date());
			order.setOrderCancellationReason(cancelOrder.getReason());
			order.setOrderCancellationStatus(true);			
			if(order.isPaymentStatus()) {
				addAmountToUserWallet(token,order.getAmount());
				order.setRefundDate(new Date());
				order.setRefundStatus(true);
			}
			order=orderDao.updateOrder(order);
			if(order.isOrderCancellationStatus()) {								
				addQuantityToItems(order.getItem().getItemId(),order.getQuantity());
			}			
			return order;
		}
		
		private User addAmountToUserWallet(String token, float amount) {
			return userClient.addAmountToUserWallet(token,amount).getBody();
		}
		
		private Item addQuantityToItems(int itemId, int quantity) {
			return itemClient.addQuantityToItems(itemId,quantity);
		}		

		private Item removeQuantityFromItem(int itemId,int quantity) {
			return itemClient.removeQuantityFromItem(itemId,quantity);
		}

	}
