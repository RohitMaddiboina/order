package com.ecommerce.order.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name="transactions")
@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Transactions {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	@ManyToOne
	private Order order;
	private Date dateOfTransaction;
	private float amount;
	@Enumerated(EnumType.STRING)
	private TransactionType transactionType;	
	@Enumerated(EnumType.STRING)
	private PaymentMethod paymentMethod;

}
