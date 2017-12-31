package com.cvshealth.eccm.prototype.authserverpoc.security.service;

import java.util.Date;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.cvshealth.eccm.prototype.authserverpoc.security.JwtUser;
import com.cvshealth.eccm.prototype.authserverpoc.security.JwtUserFactory;

@Service
public class JwtUserDetailsServiceImpl implements JwtUserDetailsService {

	Authentication authentication;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// TODO stub this out for now, possibly add a app table to store user details
		// or fetch the AD results to create the user details
		JwtUser user = new JwtUser(0L, username, 
				authentication.getName(), 
				authentication.getName(), 
				username, 
				authentication.getCredentials().toString(), 
				authentication.getAuthorities(),
				true, getPasswordLastChangedDate());
		return user;
	}

	private Date getPasswordLastChangedDate() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setAuthenticationResult(Authentication authentication) {
		// TODO not sure this is needed, might be better way to get this data
		this.authentication = authentication;
	}

}
