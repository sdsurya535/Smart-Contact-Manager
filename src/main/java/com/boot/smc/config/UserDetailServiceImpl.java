package com.boot.smc.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.boot.smc.dao.UserRepo;
import com.boot.smc.entities.User;

public class UserDetailServiceImpl implements UserDetailsService {
	
	@Autowired
	private UserRepo userRepo;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
			User user = userRepo.getUserByUserName(username);
			
			if(user==null) {
				throw new UsernameNotFoundException("User Couldn't Found"); 
			}
			
			CustomUserDetails userDetails = new CustomUserDetails(user);
		return  userDetails;
	}

}
