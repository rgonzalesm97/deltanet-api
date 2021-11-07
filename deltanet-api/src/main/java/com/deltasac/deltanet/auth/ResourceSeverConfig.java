package com.deltasac.deltanet.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;

@Configuration
@EnableResourceServer
public class ResourceSeverConfig extends ResourceServerConfigurerAdapter {
	
	private Logger logger = LoggerFactory.getLogger(ResourceSeverConfig.class);

	@Override
	public void configure(HttpSecurity http) throws Exception {
		logger.info("ResourceSeverConfig - configure :: HttpSecurity");
		http.authorizeRequests().antMatchers(HttpMethod.GET,"/apiSolic1/solicitudes").permitAll()
		.anyRequest().authenticated();
	}

}
