package com.cvshealth.eccm.prototype.authserverpoc.security;

import java.io.Serializable;

public class JwtAuthenticationRequest implements Serializable  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8920224743183646664L;
	
	private String username;
    private String password;

    public JwtAuthenticationRequest() {
        super();
    }

    public JwtAuthenticationRequest(String username, String password) {
        this.setUsername(username);
        this.setPassword(password);
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
