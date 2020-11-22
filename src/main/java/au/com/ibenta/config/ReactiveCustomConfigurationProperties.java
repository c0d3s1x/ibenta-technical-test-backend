package au.com.ibenta.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * A custom configuration properties for reactive configurations.
 * 
 * @author randolfjosef.MAGARZO
 *
 */
@Data
@ConfigurationProperties(prefix = "custom-reactive")
public class ReactiveCustomConfigurationProperties {

	Datasource dataSource = new Datasource();

	AuthenticationService authService = new AuthenticationService();
	
	@Data
	public class Datasource {

		/**
		 * The configuration for the executor service thread size to be used by Reactive
		 * Scheduler where JpaRepository calls will execute.
		 */
		private int executorThreadSize = 10;
	}
	
	@Data
	public class AuthenticationService{
		
		private String url = "http://localhost:8181";
	}
}
