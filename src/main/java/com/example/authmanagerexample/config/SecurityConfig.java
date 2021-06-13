package com.example.authmanagerexample.config;

import com.example.authmanagerexample.security.authz.InspectaAuthEngine;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authorization.AuthorizationContext;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@Import(InspectaAuthEngine.class)
public class SecurityConfig {

	private final ReactiveAuthorizationManager<AuthorizationContext> inspectaEngine;

	public SecurityConfig(ReactiveAuthorizationManager<AuthorizationContext> inspectaEngine) {
		this.inspectaEngine = inspectaEngine;
	}

	@Bean
	public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
		http
				.authorizeExchange().anyExchange().access(inspectaEngine)
				.and()
				.httpBasic(withDefaults());
		return http.build();
	}

}
