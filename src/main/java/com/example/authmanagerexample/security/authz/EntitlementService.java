package com.example.authmanagerexample.security.authz;

import reactor.core.publisher.Mono;

import org.springframework.stereotype.Component;

@Component
public class EntitlementService {
	Mono<Boolean> hasEntitlement(String name, Action actRequired) {
		return Mono.just(false);
	}
}
