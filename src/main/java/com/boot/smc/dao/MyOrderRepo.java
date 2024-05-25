package com.boot.smc.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.boot.smc.entities.MyOrder;

public interface MyOrderRepo extends JpaRepository<MyOrder, Long> {
		
	public MyOrder findById(String orderId);
}
