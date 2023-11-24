package net.thumbtack.school.market;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.graphql.server.WebGraphQlInterceptor;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import reactor.core.publisher.Mono;

import java.time.Duration;

@SpringBootApplication
@EnableMethodSecurity(securedEnabled = true)
public class MarketApplication {

	public static void main(String[] args) {
		SpringApplication.run(MarketApplication.class, args);
	}

	@Bean
	public WebGraphQlInterceptor interceptor() {
		return (webInput, interceptorChain) -> {
			// Switch threads to prove ThreadLocal context propagation works
			return Mono.delay(Duration.ofMillis(10)).flatMap(aLong -> interceptorChain.next(webInput));
		};
	}

}
