package com.boot.smc.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "orders")
public class MyOrder {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long myOrderId;
	
	private String id;
	
	private String amount;
	
	private String receipt;
	
	private String status;
	
	@ManyToOne
	private User user;
	
	private String paymentId;

	public Long getMyOrderId() {
		return myOrderId;
	}

	public void setMyOrderId(Long myOrderId) {
		this.myOrderId = myOrderId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getReceipt() {
		return receipt;
	}

	public void setReceipt(String receipt) {
		this.receipt = receipt;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(String paymentId) {
		this.paymentId = paymentId;
	}
	
	
	
	
	
	
}
