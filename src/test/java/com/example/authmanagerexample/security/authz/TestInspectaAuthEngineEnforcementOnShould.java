package com.example.authmanagerexample.security.authz;

import com.example.authmanagerexample.config.SecurityConfig;
import com.example.authmanagerexample.utils.controller.HelloController;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@AutoConfigureWebTestClient(timeout = "1d")
@WebFluxTest(properties = "inspecta.enforce=true")
@Import({SecurityConfig.class, HelloController.class})
class TestInspectaAuthEngineEnforcementOnShould {

	@Autowired
	WebTestClient webTestClient;

	@MockBean
	EntitlementService mockEntitlementService;

	@Test
	@WithMockUser("non-test-user")
	void reject_non_valid_test_user() {
		when(mockEntitlementService.hasEntitlement(any(String.class), any(Action.class)))
				.thenReturn(Mono.just(false));
		webTestClient.get()
				.uri("/hello")
				.exchange()
				.expectStatus().isForbidden();
	}

	@Test
	@WithMockUser("valid-test-user")
	void allow_valid_test_user() {
		when(mockEntitlementService.hasEntitlement(any(String.class), any(Action.class)))
				.thenReturn(Mono.just(true));
		webTestClient.get()
				.uri("/hello")
				.exchange()
				.expectStatus().isOk()
		;
	}

	@Test
	@WithMockUser("non-test-user")
	void forbid_user_with_enforce_on() {
		webTestClient.get()
				.uri("/hello2")
				.exchange()
				.expectStatus().isForbidden();
		verifyNoInteractions(mockEntitlementService);
	}

}