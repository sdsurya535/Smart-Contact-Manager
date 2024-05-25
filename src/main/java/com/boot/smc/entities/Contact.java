package com.boot.smc.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
public class Contact {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int cid;
	@NotBlank(message = "name field is required")
	@Size(min = 8,max = 25,message = "Name should be between 8-25 characters")
	private String name;
	private String nickname;
	
	@Email(regexp = "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$",message = "Blank or Invalid email")
	private String email;
	
	@NotBlank(message = "work field is required")
	private String work;
	
	@NotBlank(message = "phone number field is required")
	@Pattern(regexp="(^$|[0-9]{10})", message = "number is invalid")
	private String phone;
	
	
	private String imageCon;
	@Column(length = 8000)
	private String description;
	
	@ManyToOne
	@JsonIgnore
	private User user;
	
	
	public int getCid() {
		return cid;
	}
	public void setCid(int cid) {
		this.cid = cid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getWork() {
		return work;
	}
	public void setWork(String work) {
		this.work = work;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getImageCon() {
		return imageCon;
	}
	public void setImageCon(String imageCon) {
		this.imageCon = imageCon;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
//	@Override
//	public String toString() {
//		return "Contact [cid=" + cid + ", name=" + name + ", nickname=" + nickname + ", email=" + email + ", work="
//				+ work + ", phone=" + phone + ", imageCon=" + imageCon + ", description=" + description + ", user="
//				+ user + "]";
//	}
//	
	
	
	
}
