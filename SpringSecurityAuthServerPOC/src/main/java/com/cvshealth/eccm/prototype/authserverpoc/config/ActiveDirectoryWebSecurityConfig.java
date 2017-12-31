package com.cvshealth.eccm.prototype.authserverpoc.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.ldap.authentication.ad.ActiveDirectoryLdapAuthenticationProvider;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.cvshealth.eccm.prototype.authserverpoc.filter.BasicAuthCustomFilter;
import com.cvshealth.eccm.prototype.authserverpoc.security.JwtAuthenticationEntryPoint;
import com.cvshealth.eccm.prototype.authserverpoc.security.JwtAuthenticationTokenFilter;

@Configuration
@EnableWebSecurity //(debug = true)
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ActiveDirectoryWebSecurityConfig extends WebSecurityConfigurerAdapter {
// https://medium.com/@dmarko484/spring-boot-active-directory-authentication-5ea04969f220	

    @Value("${ad.domain}")
    private String AD_DOMAIN;

    @Value("${ad.url}")
    private String AD_URL;
    
    @Value("${ad.rootDn}")
    private String AD_ROOTDN;
    
    @Autowired
    private JwtAuthenticationEntryPoint unauthorizedHandler;

    // https://stackoverflow.com/questions/42229066/spring-boot-ldap-security
    // changed searchfilter of provider
    @Bean
    public AuthenticationProvider activeDirectoryLdapAuthenticationProvider() {
        ActiveDirectoryLdapAuthenticationProvider provider = new ActiveDirectoryLdapAuthenticationProvider(AD_DOMAIN, AD_URL, AD_ROOTDN);
        provider.setConvertSubErrorCodesToExceptions(true);
        provider.setUseAuthenticationRequestCredentials(true);

        // problem here was my AD decended from pre-2000 NT Domain did not have 
        // attribute "userPrincipalName" populated on user CN so search for
        // jburkholder@muppets.dnsalias.org wasn't returning anything, so this
        // setting made no difference, had to fix my CN in the Domain first

        // provider.setSearchFilter("(&(objectClass=user)(sAMAccountName={0}))");

        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager() {
    	ProviderManager providerManager = new ProviderManager(Arrays.asList(activeDirectoryLdapAuthenticationProvider()));
    	
    	// As of version 3.1. Credentials are cleared after a successful authentication by default.
    	// if we want the cred in the token passed back to the client...
    	providerManager.setEraseCredentialsAfterAuthentication(false);
    	
        return providerManager;
    }
    
//    @Bean
//    public BasicAuthCustomFilter basicAuthFilterBean() throws Exception {
//        return new BasicAuthCustomFilter();
//    }

    @Bean
    public JwtAuthenticationTokenFilter authenticationTokenFilterBean() throws Exception {
        return new JwtAuthenticationTokenFilter();
    }
    
    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
    	httpSecurity
	        // we don't need CSRF because our token is invulnerable
	        .csrf().disable()
	
	        .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
	
	        // don't create session
	        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
	
	        .authorizeRequests()
            .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
            .antMatchers("/auth/**").permitAll()
            .anyRequest().authenticated().and().httpBasic();
        
        //http.addFilterBefore(basicAuthFilterBean(), BasicAuthenticationFilter.class);
        //http.addFilterAfter(filter, afterFilter);

    	// Custom JWT based security filter
        httpSecurity
                .addFilterBefore(authenticationTokenFilterBean(), UsernamePasswordAuthenticationFilter.class);

        // disable page caching
        httpSecurity.headers().cacheControl();
    }
    
//    @Override
//    protected void configure(AuthenticationManagerBuilder authManagerBuilder) throws Exception {
//        authManagerBuilder
//            .authenticationProvider(activeDirectoryLdapAuthenticationProvider())
//            .userDetailsService(userDetailsService());
//    }
}
