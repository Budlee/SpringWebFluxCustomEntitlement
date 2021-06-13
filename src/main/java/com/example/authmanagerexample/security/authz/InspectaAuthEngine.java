package com.example.authmanagerexample.security.authz;

import java.util.Objects;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import reactor.core.publisher.Mono;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerMapping;

@Component
public class InspectaAuthEngine implements ReactiveAuthorizationManager<AuthorizationContext> {

	private static final Log logger = LogFactory.getLog(InspectaAuthEngine.class);

	private final RequestMappingHandlerMapping handlerMapping;
	private final boolean enforce;
	private final EntitlementService entitlementService;


	public InspectaAuthEngine(RequestMappingHandlerMapping requestMappingHandlerMapping,
			EntitlementService entitlementService,
			@Value("${inspecta.enforce:true}") boolean enforce) {
		this.handlerMapping = requestMappingHandlerMapping;
		this.entitlementService = entitlementService;
		this.enforce = enforce;
	}

	@Override
	public Mono<AuthorizationDecision> check(Mono<Authentication> authentication, AuthorizationContext authorizationContext) {
		return handlerMapping.getHandlerInternal(authorizationContext.getExchange())
				.mapNotNull(this::findEntitlement)
				.flatMap(entitlement -> this.checkUserEntitlement(authentication, entitlement))
				.defaultIfEmpty(new AuthorizationDecision(!enforce))
				;
	}

	private Mono<AuthorizationDecision> checkUserEntitlement(Mono<Authentication> monoAuth,
			Entitlement entitlement) {
		return monoAuth.flatMap(authentication -> entitlementService
				.hasEntitlement(authentication.getName(), entitlement.actRequired()))
				.map(AuthorizationDecision::new);
	}

	private Entitlement findEntitlement(HandlerMethod handlerMethod) {
		final Entitlement annotation = AnnotationUtils
				.getAnnotation(handlerMethod.getMethod(),
						Entitlement.class
				);
		if (Objects.isNull(annotation) && enforce) {
			logger.info("No entitlement found on mapped method");
			throw new AccessDeniedException("Unauthorized to access endpoint");
		}
		return annotation;
	}

}
