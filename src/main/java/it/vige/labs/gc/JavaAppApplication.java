package it.vige.labs.gc;

import static org.springframework.boot.SpringApplication.run;

import org.keycloak.adapters.KeycloakConfigResolver;
import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
@SpringBootApplication
public class JavaAppApplication {

	public final static String BROKER_NAME = "/vote-websocket";
	public final static String TOPIC_NAME = "/topic/vote";

	public final static String BROKER_V_NAME = "/votingpaper-websocket";
	public final static String TOPIC_V_NAME = "/topic/votingpaper";

	public static void main(String[] args) {
		run(JavaAppApplication.class, args);
	}

	@Bean
	public KeycloakConfigResolver KeycloakConfigResolver() {
		return new KeycloakSpringBootConfigResolver();
	}
}
