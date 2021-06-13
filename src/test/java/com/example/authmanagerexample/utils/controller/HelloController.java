package com.example.authmanagerexample.utils.controller;

import java.security.Principal;

import com.example.authmanagerexample.security.authz.Action;
import com.example.authmanagerexample.security.authz.Entitlement;
import reactor.core.publisher.Mono;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

	private final String t;

	HelloController() {
		this.t = "as";
	}

	@Entitlement(actRequired = Action.VIEW)
	@GetMapping("/hello")
	public Mono<String> getHello(@AuthenticationPrincipal Principal principal) {
		return Mono.just("hello " + principal.getName());
	}

	@GetMapping("/hello2")
	public Mono<String> getHello2(@AuthenticationPrincipal Principal principal) {
		return Mono.just("hello2 " + principal.getName());
	}
}
