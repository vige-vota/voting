package it.vige.labs.gc;

import static org.springframework.boot.SpringApplication.run;

import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JavaAppApplication {

	public final static String BROKER_NAME = "/vote-websocket";
	public final static String TOPIC_NAME = "/topic/vote";

	public static void main(String[] args) {
		run(JavaAppApplication.class, args);
	}
}
