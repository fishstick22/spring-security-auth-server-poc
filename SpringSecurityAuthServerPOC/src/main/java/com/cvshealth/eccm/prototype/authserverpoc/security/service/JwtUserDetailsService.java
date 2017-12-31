package com.cvshealth.eccm.prototype.authserverpoc.security.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface JwtUserDetailsService extends UserDetailsService {

	public void setAuthenticationResult(Authentication authentication);
}
