package com.ecommerce.order.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecommerce.order.model.Transactions;

@Repository
public interface TransactionRepo extends JpaRepository<Transactions, Integer>{

	List<Transactions> findByOrder_User_idOrderByDateOfTransactionDesc(int userId);

}
