package com.ecommerce.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecommerce.order.model.User;

@Repository
public interface UserRepo extends JpaRepository<User, Integer>{

}
