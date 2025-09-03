package com.brokersystems.brokerapp;

import com.brokersystems.brokerapp.security.*;
import com.brokersystems.brokerapp.server.utils.FormLockManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.ldap.authentication.ad.ActiveDirectoryLdapAuthenticationProvider;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.header.writers.CacheControlHeadersWriter;
import org.springframework.security.web.session.HttpSessionEventPublisher;

import java.net.InetAddress;
import java.util.StringTokenizer;


@Configuration
@Import({ AppWebMVCConfig.class })
@EnableWebSecurity
//@EnableGlobalMethodSecurity(securedEnabled = true)
public class SecurityWebConfig extends WebSecurityConfigurerAdapter {


	@Autowired
	Environment env;

	@Autowired
	AuthenticationSuccessHandler authenticationSuccessHandler;

	@Autowired
	TokenSimpleUrlAuthenticationFailureHandler authenticationFailureHandler;

	@Autowired
	ReloadUserPerRequestSecurity reloadUserPerRequestSecurity;

	@Autowired
	BrokerUserDetailsService userDetailsService;

	@Autowired
	SecurityAuthPopulator securityAuthPopulator;
	@Autowired
	private FormLockManager formLockManager;


	@Bean
	public ActiveDirectoryLdapAuthenticationProvider activeDirectoryLdapAuthenticationProvider(String url){
		ActiveDirectoryLdapAuthenticationProvider activeDirectoryLdapAuthenticationProvider = new
				ActiveDirectoryLdapAuthenticationProvider(env.getProperty("ldap.domain"), url);
		activeDirectoryLdapAuthenticationProvider.setUseAuthenticationRequestCredentials(true);
		activeDirectoryLdapAuthenticationProvider.setUserDetailsContextMapper(securityAuthPopulator);
		return activeDirectoryLdapAuthenticationProvider;
	}

	@Autowired
	public void configureGlobality(AuthenticationManagerBuilder auth) throws Exception {
		if(env.getProperty("auth.type")!=null && "ldap".equalsIgnoreCase(env.getProperty("auth.type"))) {

			StringTokenizer tokenizer = new StringTokenizer(env.getProperty("ldap.url"),",");
			String ipToUse = "";

			while(tokenizer.hasMoreElements()){
				if(pingIpAddress((String)tokenizer.nextElement())){

					ipToUse = (String)tokenizer.nextElement();
					break;
				}
			}
			auth.authenticationProvider(activeDirectoryLdapAuthenticationProvider(ipToUse));
		}
		else{
			auth.userDetailsService(userDetailsService);
			auth.authenticationProvider(authenticationProvider());
		}

	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setUserDetailsService(userDetailsService);
		authenticationProvider.setPasswordEncoder(passwordEncoder());
		return authenticationProvider;
	}

	@Bean
	public HttpSessionEventPublisher httpSessionEventPublisher() {
		return new HttpSessionEventPublisher();
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/login*");
		web.ignoring().antMatchers("/js/**");
		web.ignoring().antMatchers("/libs/**");
	}

//	@Override
//	public void configure(HttpSecurity http) throws Exception {
//		http.securityContext().
//				securityContextRepository(reloadUserPerRequestSecurity)
//				.and()
//				.csrf()
//				.disable()
//				.addFilter(new CustomAuthenticationFilter())
//				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.ALWAYS).sessionFixation().newSession().
//				maximumSessions(1).and().and()
//				.headers()
//				.disable()
//				.authorizeRequests()
//				.antMatchers("/protected/**").authenticated()
//				.antMatchers("/v2/api-docs",
//						"/configuration/ui",
//						"/swagger-resources/**",
//						"/configuration/security",
//						"/swagger-ui.html",
//						"/webjars/**").permitAll()
//				.antMatchers("/test/**").permitAll()
//				.antMatchers("/ws/**").permitAll()
//				.antMatchers("/resetPassword/**").permitAll()
//				.antMatchers("/changePassword/**").permitAll()
//				.antMatchers("/changepass").permitAll()
//				.antMatchers("/services/**").permitAll()
//				.antMatchers("/policydocs/**").permitAll()
//				.antMatchers("/mobileMoney/**").permitAll()
//				.antMatchers("/logo",
//						"/forgot-password**",
//						"/reset-password**").permitAll()
//				.anyRequest().authenticated()
//				.and()
//				.formLogin()
//				.loginProcessingUrl("/j_spring_security_check")
//				.usernameParameter("j_username")
//				.passwordParameter("j_password")
//				.loginPage("/login")
//				.permitAll()
//				.successHandler(authenticationSuccessHandler)
//				.failureHandler(authenticationFailureHandler)
//				//.failureUrl("/login?error=true")
//				.and()
//				.logout()
//				.logoutSuccessHandler((request, response, authentication) -> {
//					if (authentication != null) {
//						String username = authentication.getName();
//						formLockManager.closeAllFormsForUser(username);
//					}
//					response.sendRedirect(request.getContextPath() + "/login"); // Corrected
//				})
//				.logoutSuccessUrl("/login")
//				.invalidateHttpSession(true)
//				.deleteCookies()
//				.logoutUrl("/logout");
//
//
//
//
//
//	}

	@Override
	public void configure(HttpSecurity http) throws Exception {
		http.securityContext()
				.securityContextRepository(reloadUserPerRequestSecurity)
				.and()
				.csrf()
				.disable()
				.addFilter(new CustomAuthenticationFilter())
				.sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
				.sessionFixation().newSession()
				.maximumSessions(1)
				.and()
				.and()
				.headers()
				.cacheControl() // Enable cache-control headers
				.and()
				.addHeaderWriter(new CacheControlHeadersWriter()) // Explicitly set cache-control headers
				.httpStrictTransportSecurity() // Enable HSTS if using HTTPS
				.and()
				.and()
				.authorizeRequests()
				.antMatchers("/protected/**").authenticated()
				.antMatchers("/v2/api-docs",
						"/configuration/ui",
						"/swagger-resources/**",
						"/configuration/security",
						"/swagger-ui.html",
						"/webjars/**").permitAll()
				.antMatchers("/test/**").permitAll()
				.antMatchers("/ws/**").permitAll()
				.antMatchers("/resetPassword/**").permitAll()
				.antMatchers("/changePassword/**").permitAll()
				.antMatchers("/changepass").permitAll()
				.antMatchers("/services/**").permitAll()
				.antMatchers("/policydocs/**").permitAll()
				.antMatchers("/mobileMoney/**").permitAll()
				.antMatchers("/logo",
						"/forgot-password**",
						"/reset-password**").permitAll()
				.anyRequest().authenticated()
				.and()
				.formLogin()
				.loginProcessingUrl("/j_spring_security_check")
				.usernameParameter("j_username")
				.passwordParameter("j_password")
				.loginPage("/login")
				.permitAll()
				.successHandler(authenticationSuccessHandler)
				.failureHandler(authenticationFailureHandler)
				.and()
				.logout()
				.logoutSuccessHandler((request, response, authentication) -> {
					if (authentication != null) {
						String username = authentication.getName();
						formLockManager.closeAllFormsForUser(username);
					}
					response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
					response.setHeader("Pragma", "no-cache");
					response.setHeader("Expires", "0");
					response.sendRedirect(request.getContextPath() + "/login");
				})
				.logoutSuccessUrl("/login")
				.invalidateHttpSession(true)
				.deleteCookies("JSESSIONID") // Specify cookies to delete
				.logoutUrl("/logout");
	}



	private boolean pingIpAddress(final String ldap){
		boolean found = false;
		for(int i=0;i<5;i++) {
			found = pingIp(ldap);
			try {
				Thread.sleep(1000l);
			} catch (InterruptedException e) {
			}
		}
		return found;

	}


	private  boolean pingIp(final String ldap) {
		try {
			String ipAddress  =ldap.substring(ldap.indexOf("//")+2,ldap.length()-1);
			InetAddress inet = InetAddress.getByName(ipAddress);
			if (inet.isReachable(5000)) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {

		}
		return false;
	}


}