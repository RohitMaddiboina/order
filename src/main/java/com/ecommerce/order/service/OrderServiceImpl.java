package com.ecommerce.order.service;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.ecommerce.exception.OrderException;
import com.ecommerce.order.Dao.OrderDao;
import com.ecommerce.order.client.CartClient;
import com.ecommerce.order.client.ItemClient;
import com.ecommerce.order.client.UserClient;
import com.ecommerce.order.model.Cart;
import com.ecommerce.order.model.Item;
import com.ecommerce.order.model.Order;
import com.ecommerce.order.model.PaymentMethod;
import com.ecommerce.order.model.RequestOrder;
import com.ecommerce.order.model.RequestOrderCancellation;
import com.ecommerce.order.model.TransactionType;
import com.ecommerce.order.model.Transactions;
import com.ecommerce.order.model.User;
import com.ecommerce.order.pdf.HeaderFooterPageEvent;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

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

	// private enum paymentMethod {
	// WALLET,
	// PAY_ON_DELIVERY
	// }
	// public boolean placeOrder(List<Cart> cart, @Valid RequestOrder requestOrder)
	// {
	// boolean status=false;
	// if(PAYMENT_METHOD.contains(requestOrder.getPaymentMethod().toUpperCase())) {
	// orderDao.placeOrder()
	// }
	// throw new OrderException("Invalid Payment Method");
	// }

	// This method will take cart details added by user and take it as
	// parameter,wallet balance,token
	// and return the list of orders placed by user
	public List<Order> placeOrder(List<Cart> cart, RequestOrder requestOrder, Float userWalletAmount, String token) {
		cart.forEach(d -> {
			Item i = itemClient.getItem(d.getItem().getItemId());
			if (i.getQuanitity() < d.getQuantity()) {
				throw new OrderException(i.getItemName() + " Item Out Of Stock");
			}
		});

		boolean status = false;
		// payment method must be from wallet or Pay_on_Delivery

		// this if checks whehter wallet not available and payment mehtod must be wallet
		// balnce
		// this if ok then display message insufficient balance
		if (!checkWalletAmountBeforePlaceOrder(cart, userWalletAmount)
				&& requestOrder.getPaymentMethod().equals(PaymentMethod.WALLET)) { // checking whether the wallet amount
																					// is available for the total price
			throw new OrderException("Insufficient Balance");
		}

		// if wallet balance is ok ,it will place the orders one by one upto cart empty
		Iterator<Cart> i = cart.iterator();
		List<Order> orders = new ArrayList<>();
		while (i.hasNext()) {
			Cart item = i.next();
			Order o = null;
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			// this try block parse date format ,
			try {
				Date date = formatter.parse(formatter.format(new Date()));

				// it will take parameter from items details ,placed to order object save the
				// order object

				o = orderDao.placeOrder(item.getQuantity(), item.getQuantity() * item.getItem().getPrice(),
						debitFromUserWallet(token, item.getQuantity() * item.getItem().getPrice(), requestOrder),
						requestOrder.getPaymentMethod(), date, requestOrder.getDeliveryAddress(), "In Progress", status,
						null, null, 0, status, null, item.getUser(), item.getItem());

			} catch (ParseException e) {
				e.printStackTrace();
			}

			if (o != null) {
				// payment is ok from wallet or payment method is pay on delevery,it remove item
				// from cart placed by user
				if (o.isPaymentStatus() || requestOrder.getPaymentMethod().equals(PaymentMethod.PAY_ON_DELIVERY)) {
					cartClient.removeFromCart(token, item.getItem().getItemId()).getBody();
					removeQuantityFromItem(item.getItem().getItemId(), item.getQuantity());
					orders.add(o);
					if (o.isPaymentStatus()) {
						orderDao.addTransaction(new Transactions(1, o, new Date(), o.getAmount(), TransactionType.DEBIT,
								o.getPaymentMethod()));
					}
				}
			}
		}

		return orders;
	}

	/* this method will debit from order payment- wallet amount */
	public boolean debitFromUserWallet(String token, float amount, RequestOrder requestOrder) {
		if (requestOrder.getPaymentMethod().equals(PaymentMethod.WALLET)) {
			return (boolean) userClient.debitFromUserWallet(token, amount).getBody();
		}
		return false;
	}

	// This method return will orderPayment is greater than wallet balacne
	public boolean checkWalletAmountBeforePlaceOrder(List<Cart> cart, Float userWalletAmount) {
		double totalAmt = cart.stream().mapToDouble(d -> (d.getQuantity() * d.getItem().getPrice())).sum();
		if (userWalletAmount > totalAmt) {
			return true;
		}
		return userWalletAmount > totalAmt;
	}

	// This method fetch all orders placed by user
	public List<Order> getOrdersByUser(String email, String token) {
		User user = userClient.getUser(token).getBody();
		if (user != null) {
			return orderDao.getOrdersByUserId(user.getId());
		}
		throw new OrderException("Error While Fetching The User Orders");
	}

	@Override
	public Order cancelOrder(RequestOrderCancellation cancelOrder, String token) {
		Order order = orderDao.getOrderByOrderId(cancelOrder.getOrderId());
		if (order == null) {
			throw new OrderException("Order Not Found");
		}
		if (order.getQuantity() == 0) {
			throw new OrderException("Order Is Inactive");
		}
		order.setOrderCancellationDate(new Date());
		order.setOrderCancellationReason(cancelOrder.getReason());
		order.setQuantity(order.getQuantity() - cancelOrder.getQuantity());
		order.setCancallationQuatity(order.getCancallationQuatity() + cancelOrder.getQuantity());
		order.setOrderCancellationStatus(true);
		order.setDeliveryStatus("Cancelled");
		if (order.isPaymentStatus()) {
			addAmountToUserWallet(token, order.getAmount());
			orderDao.addTransaction(
					new Transactions(1, order, new Date(), order.getItem().getPrice() * cancelOrder.getQuantity(),
							TransactionType.CREDIT, order.getPaymentMethod()));
			order.setRefundDate(new Date());
			order.setRefundStatus(true);
		}
		order = orderDao.updateOrder(order);
		if (order.isOrderCancellationStatus()) {
			addQuantityToItems(order.getItem().getItemId(), order.getQuantity());
		}
		return order;
	}

	private User addAmountToUserWallet(String token, float amount) {
		return userClient.addAmountToUserWallet(token, amount).getBody();
	}

	private Item addQuantityToItems(int itemId, int quantity) {
		return itemClient.addQuantityToItems(itemId, quantity);
	}

	private Item removeQuantityFromItem(int itemId, int quantity) {
		return itemClient.removeQuantityFromItem(itemId, quantity);
	}

	@Override
	public List<Transactions> getUserTransactions(String token) {
		User user = userClient.getUser(token).getBody();
		if (user != null) {
			return orderDao.getTransactionsByUser(user.getId());
		}
		throw new OrderException("Error While Fetching User Transaction");
	}
	
	@Override
	public void createPdf(String orderId) throws FileNotFoundException, DocumentException {
		 Document document = new Document(PageSize.A4, 36, 36, 90, 36);
		PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream("invoice_"+orderId+".pdf"));
		
		HeaderFooterPageEvent headerFooterPageEvent = new HeaderFooterPageEvent();
		writer.setPageEvent(headerFooterPageEvent);
//		Font font = FontFactory.getFont(FontFactory.COURIER,20, BaseColor.BLACK);
		
		Order order = orderDao.getOrderByOrderId(orderId);
		
		PdfPTable table = new PdfPTable(5);
		addTableHeader(table);
		addRows(table,order);
		document.open();
		document.add(new Paragraph("Your Order Invoice"));
		document.add(new Paragraph("\n"));
		document.add(table);
		document.close();
		
	}
	
	private void addTableHeader(PdfPTable table) {
	    Stream.of("OrderID", "OrderedItem", "Price","Payment Method","Ordered Date")
	      .forEach(columnTitle -> {
	        PdfPCell header = new PdfPCell();
	       
	        header.setBackgroundColor(BaseColor.LIGHT_GRAY);
	        header.setBorderWidth(1);
	        header.setPhrase(new Phrase(columnTitle));
	        table.addCell(header);
	    });
	}
	private void addRows(PdfPTable table,Order order) {
	    table.addCell(order.getOrderId());
	    table.addCell(order.getItem().getItemName());
	    table.addCell(order.getAmount()+"");
	    table.addCell(order.getPaymentMethod()+"");
	    table.addCell(DateFormat.getInstance().format(order.getOrderedDate()));
	}
}
