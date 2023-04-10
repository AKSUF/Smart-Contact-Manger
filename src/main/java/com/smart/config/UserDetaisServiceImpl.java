package com.smart.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.smart.entity.User;
import com.smart.repo.UserRepository;

public class UserDetaisServiceImpl implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;
	
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user=userRepository.getUserByUserName(username);
		
		System.out.println("fetching user from database");
		System.out.println(username);
		
		if(user==null) {
			throw new UsernameNotFoundException("Could not found user");
			
			
		}
		CustomUserDetails customUserDetails=new CustomUserDetails(user);
		
		System.out.println(user);
		return customUserDetails;
	}

}
