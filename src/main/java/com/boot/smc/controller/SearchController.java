package com.boot.smc.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.boot.smc.dao.ContactRepo;
import com.boot.smc.dao.UserRepo;
import com.boot.smc.entities.Contact;
import com.boot.smc.entities.User;

@RestController
public class SearchController {
	
	@Autowired
	private UserRepo userRepo;
	
	@Autowired
	private ContactRepo contactRepo;
	
	@GetMapping("/search/{query}")
	public ResponseEntity<?> searchHandler(@PathVariable("query") String query,Principal principal){
		
		User user = userRepo.getUserByUserName(principal.getName());
		
		List<Contact> contacts = contactRepo.findByNameContainingAndUser(query, user);
		
		return ResponseEntity.ok(contacts);
		
	}
}
