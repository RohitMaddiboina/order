package com.ecommerce.order.model;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Table(name="orders")
@Entity
@NoArgsConstructor
@Data
@ToString
public class Order {

	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	@Setter
	private String orderId;	
	private int quantity;
	private float amount;
	private boolean paymentStatus;

	@Enumerated(EnumType.STRING)
	private PaymentMethod paymentMethod;
//	private String paymentMethod;
	private Date orderedDate;
	@Lob 
	@Column(name="deliveryAddress", length=512)
	private String deliveryAddress;
	private String deliveryStatus;
	private boolean orderCancellationStatus;
	private String orderCancellationReason;
	private Date orderCancellationDate;
	private int cancallationQuatity;
	private boolean refundStatus;
	private Date refundDate;
	@ManyToOne
	private User user;
	@ManyToOne
	private Item item;

	
	public Order(int id, String orderId, int quantity, float amount, boolean paymentStatus, PaymentMethod paymentMethod,
			Date orderedDate, String deliveryAddress, String deliveryStatus, boolean orderCancellationStatus,
			String orderCancellationReason, Date orderCancellationDate, int cancallationQuatity, boolean refundStatus,
			Date refundDate, User user, Item item) {
		super();
		this.id = id;
		this.orderId = orderId;
		this.quantity = quantity;
		this.amount = amount;
		this.paymentStatus = paymentStatus;
		this.paymentMethod = paymentMethod;
		this.orderedDate = orderedDate;
		this.deliveryAddress = deliveryAddress;
		this.deliveryStatus = deliveryStatus;
		this.orderCancellationStatus = orderCancellationStatus;
		this.orderCancellationReason = orderCancellationReason;
		this.orderCancellationDate = orderCancellationDate;
		this.cancallationQuatity = cancallationQuatity;
		this.refundStatus = refundStatus;
		this.refundDate = refundDate;
		this.user = user;
		this.item = item;
	}


	@Getter(value=AccessLevel.NONE)
	@OneToMany(mappedBy="order")
	private List<Transactions> transactions; 
	
}
