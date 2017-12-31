package com.cvshealth.eccm.prototype.authserverpoc.security.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mobile.device.Device;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.cvshealth.eccm.prototype.authserverpoc.security.JwtAuthenticationRequest;
import com.cvshealth.eccm.prototype.authserverpoc.security.JwtAuthenticationResponse;
import com.cvshealth.eccm.prototype.authserverpoc.security.JwtTokenUtil;
import com.cvshealth.eccm.prototype.authserverpoc.security.service.JwtUserDetailsService;
import com.cvshealth.eccm.prototype.authserverpoc.security.service.JwtUserDetailsServiceImpl;

@RestController
public class AuthenticationRestController {

    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private UserDetailsService userDetailsService;
    
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    
    @RequestMapping(value = "${jwt.route.authentication.path}", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtAuthenticationRequest authenticationRequest, Device device) throws AuthenticationException {

        // Perform the security
        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticationRequest.getUsername(),
                        authenticationRequest.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Reload password post-security so we can generate token 
        // (or have Security not whack it in the first place) 
        ((JwtUserDetailsService)userDetailsService).setAuthenticationResult(authentication);
        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
        final String token = jwtTokenUtil.generateToken(userDetails, device);

        // Return the token
        return ResponseEntity.ok(new JwtAuthenticationResponse(token));
    }
}
