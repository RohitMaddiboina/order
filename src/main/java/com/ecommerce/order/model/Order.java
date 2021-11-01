package com.ecommerce.order.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name="orders")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Order {

	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	
	private String orderId;	
	private int quantity;
	private float amount;
	private boolean paymentStatus;
	private String paymentMethod;
	private Date orderedDate;
	@Lob 
	@Column(name="deliveryAddress", length=512)
	private String deliveryAddress;
	private String deliveryStatus;
	private boolean orderCancellationStatus;
	private String orderCancellationReason;
	private Date orderCancellationDate;
	private boolean refundStatus;
	private Date refundDate;
	@ManyToOne
	private User user;
	@ManyToOne
	private Item item;
	
}
