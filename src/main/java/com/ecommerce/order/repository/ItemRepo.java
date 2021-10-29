package com.ecommerce.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecommerce.order.model.Item;

@Repository
public interface ItemRepo extends JpaRepository<Item, Integer>{

}
