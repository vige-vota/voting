package it.vige.labs.gc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JavaAppApplication {
	
	public final static String BROKER_NAME = "/vote-websocket";
	public final static String TOPIC_NAME = "/topic/vote";

	public static void main(String[] args) {
		SpringApplication.run(JavaAppApplication.class, args);
	}
}
