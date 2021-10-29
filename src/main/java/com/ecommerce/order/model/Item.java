package com.ecommerce.order.model;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name="Item")
@Getter
@Entity
@NoArgsConstructor
public class Item {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int itemId;
	private String itemName;
	private String category;
	private String itemType;
	private String brand;
	private String model;
	private int quanitity;
	private float rating;
	private boolean active;
	private String discription;
	private float price;
	private String itemImage;

	@Getter(value = AccessLevel.NONE)
	@OneToMany(mappedBy="item")
	private List<Order> order;
}
