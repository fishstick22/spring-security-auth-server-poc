package com.cvshealth.eccm.prototype.authserverpoc.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.filter.OncePerRequestFilter;

public class BasicAuthCustomFilter extends OncePerRequestFilter {
	
    private final Log logger = LogFactory.getLog(this.getClass());

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {
		// try to peek inside what is going on with the authentication
		String authHeaderToken = request.getHeader("Authorization");
		logger.info("checking basic authentication for user " + authHeaderToken);
		
		chain.doFilter(request, response);
	}

}
