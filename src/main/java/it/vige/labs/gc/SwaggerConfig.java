package it.vige.labs.gc;

import static springfox.documentation.builders.PathSelectors.any;
import static springfox.documentation.builders.RequestHandlerSelectors.basePackage;
import static springfox.documentation.spi.DocumentationType.SWAGGER_2;
import static springfox.documentation.swagger.web.SecurityConfigurationBuilder.builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.LoginEndpointBuilder;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.GrantType;
import springfox.documentation.service.ImplicitGrant;
import springfox.documentation.service.LoginEndpoint;
import springfox.documentation.service.OAuth;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.service.SecurityScheme;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.SecurityConfiguration;

@Configuration
public class SwaggerConfig {

	@Value("${keycloak.auth-server-url}")
	private String authServerUrl;

	@Bean
	public Docket api() {
		return new Docket(SWAGGER_2).select().apis(basePackage(getClass().getPackageName())).paths(any()).build()
				.securitySchemes(buildSecurityScheme()).securityContexts(buildSecurityContext());
	}

	@Bean
	public SecurityConfiguration securityConfiguration() {

		Map<String, Object> additionalQueryStringParams = new HashMap<>();
		additionalQueryStringParams.put("nonce", "123456");

		return builder().clientId("voting").realm("vota-domain").appName("swagger-ui")
				.additionalQueryStringParams(additionalQueryStringParams).build();
	}

	private List<SecurityContext> buildSecurityContext() {
		List<SecurityReference> securityReferences = new ArrayList<>();

		securityReferences.add(SecurityReference.builder().reference("oauth2")
				.scopes(scopes().toArray(new AuthorizationScope[] {})).build());

		SecurityContext context = SecurityContext.builder().securityReferences(securityReferences).build();

		List<SecurityContext> ret = new ArrayList<>();
		ret.add(context);
		return ret;
	}

	private List<SecurityScheme> buildSecurityScheme() {
		List<SecurityScheme> lst = new ArrayList<>();

		LoginEndpoint login = new LoginEndpointBuilder()
				.url(authServerUrl + "/realms/vota-domain/protocol/openid-connect/auth").build();

		List<GrantType> gTypes = new ArrayList<>();
		gTypes.add(new ImplicitGrant(login, "acces_token"));

		lst.add(new OAuth("oauth2", scopes(), gTypes));
		return lst;
	}

	private List<AuthorizationScope> scopes() {
		return new ArrayList<>();
	}
}
