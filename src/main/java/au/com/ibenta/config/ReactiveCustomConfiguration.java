package au.com.ibenta.config;

import java.util.concurrent.Executors;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import lombok.extern.slf4j.Slf4j;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Configuration
@EnableConfigurationProperties(value = { ReactiveCustomConfigurationProperties.class })
public class ReactiveCustomConfiguration {

	/**
	 * A {@link Scheduler} backed by an Executor service to asynchronously call data
	 * operations to fully support reactive stack even though we're using
	 * {@link JpaRepository} in the data layer. JpaRepository does not support full
	 * reactive code similar to {@link ReactiveCrudRepository}.
	 */
	@Bean
	public Scheduler reactiveJdbcScheduler(
			ReactiveCustomConfigurationProperties reactiveCustomConfigurationProperties) {

		log.info("Using configured Executor Thread size for Reactive JDBC Scheduler: {}",
				reactiveCustomConfigurationProperties.getDataSource().getExecutorThreadSize());
		return Schedulers.fromExecutor(Executors
				.newFixedThreadPool(reactiveCustomConfigurationProperties.getDataSource().getExecutorThreadSize()));

	}

	/**
	 * A component used for data operations which modify data (insert/update), so
	 * support for transaction management can be achieved even when reactive code is
	 * being used.
	 * 
	 * @param transactionManager
	 * @return
	 */
	@Bean
	public TransactionTemplate transactionTemplate(PlatformTransactionManager transactionManager) {
		return new TransactionTemplate(transactionManager);
	}

}
