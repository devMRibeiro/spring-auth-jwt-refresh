package com.mrsystems.spring.auth.security.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mrsystems.spring.auth.model.User;
import com.mrsystems.spring.auth.repository.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	private UserRepository userRespository;

	UserDetailsServiceImpl(UserRepository userRespository) {
		this.userRespository = userRespository;
	}
	
	@Override
	@Transactional
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
	
		User user = userRespository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("User Not Found with username"));
		
		return UserDetailsImpl.build(user);
	}
}