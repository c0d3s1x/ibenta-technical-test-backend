package au.com.ibenta.tracing;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import au.com.ibenta.config.ReactiveCustomConfigurationProperties;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Api(tags = "authentication")
@RestController
@RequestMapping("/authentication")
public class AuthenticationServiceTracingController {

	private final ReactiveCustomConfigurationProperties reactiveCustomConfigurationProperties;

	public AuthenticationServiceTracingController(
			ReactiveCustomConfigurationProperties reactiveCustomConfigurationProperties) {
		this.reactiveCustomConfigurationProperties = reactiveCustomConfigurationProperties;
	}

	@GetMapping("/health")

	public Mono<String> checkHealth() {

		log.info("Checking auth service status at [{}]",
				reactiveCustomConfigurationProperties.getAuthService().getUrl());
		
		return WebClient.create(reactiveCustomConfigurationProperties.getAuthService().getUrl()).get()
				.uri("/actuator/health").retrieve().bodyToMono(String.class);
	}
}
