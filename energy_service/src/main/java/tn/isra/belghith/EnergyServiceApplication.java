package tn.isra.belghith;

import org.springframework.boot.SpringApplication;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;

import org.springframework.context.annotation.Bean;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;

@EnableDiscoveryClient
@RefreshScope
@SpringBootApplication
public class EnergyServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(EnergyServiceApplication.class, args);
	}

	/*@Bean
	public MessageConverter convertion()
	{
		return new Jackson2JsonMessageConverter();	}*/
}
