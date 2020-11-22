package au.com.ibenta.test.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import com.google.common.base.Strings;

import au.com.ibenta.config.ReactiveCustomConfiguration;
import au.com.ibenta.test.persistence.UserEntity;
import au.com.ibenta.test.persistence.UserRepository;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

@Slf4j
@Service
public class UserService implements IUserService {

	/**
	 * * @see
	 * ReactiveCustomConfiguration#reactiveJdbcScheduler(au.com.ibenta.config.ReactiveCustomConfigurationProperties)
	 */
	private final Scheduler reactiveJdbcScheduler;

	/**
	 * @see ReactiveCustomConfiguration#transactionTemplate(org.springframework.transaction.PlatformTransactionManager)
	 */
	private final TransactionTemplate transactionTemplate;

	private final UserRepository userRepository;

	private final PasswordEncoder passwordEncoder;

	public UserService(Scheduler reactiveJdbcScheduler, TransactionTemplate transactionTemplate,
			UserRepository userRepository, PasswordEncoder passwordEncoder) {

		this.reactiveJdbcScheduler = reactiveJdbcScheduler;
		this.transactionTemplate = transactionTemplate;
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;

	}

	public Mono<UserEntity> create(UserEntity entity) {

		entity.setId(null);
		entity.setPassword(passwordEncoder.encode(entity.getPassword()));
		return Mono
				.defer(() -> Mono.fromCallable(() -> transactionTemplate.execute(action -> userRepository.save(entity)))
						.subscribeOn(reactiveJdbcScheduler));

	}

	public Mono<UserEntity> get(Long id) {

		return Mono.defer(() -> Mono.justOrEmpty(userRepository.findById(id))).subscribeOn(reactiveJdbcScheduler)
				.switchIfEmpty(Mono.defer(() -> {
					return Mono.error(new UserNotFoundException(id));
				}));
	}

	public Mono<UserEntity> update(final UserEntity entity) {

		return Mono.defer(() -> Mono.justOrEmpty(userRepository.findById(entity.getId()))
				.subscribeOn(reactiveJdbcScheduler).switchIfEmpty(Mono.defer(() -> {
					return Mono.error(new UserNotFoundException(entity.getId()));
				})).doOnNext(result -> entity.setPassword(passwordEncoder.encode(entity.getPassword())))
				.flatMap(r -> Mono
						.defer(() -> Mono.just(transactionTemplate.execute(status -> userRepository.save(entity)))
								.subscribeOn(reactiveJdbcScheduler))));

	}

	public Mono<Void> delete(Long id) {

		return Mono.defer(() -> Mono.justOrEmpty(userRepository.findById(id)).switchIfEmpty(Mono.defer(() -> {
			return Mono.error(new UserNotFoundException(id));
		})).then(Mono.fromCallable(() -> transactionTemplate.execute(action -> {
			userRepository.deleteById(id);
			return null;
		})).subscribeOn(reactiveJdbcScheduler).then()));

	}

	public Flux<UserEntity> list() {

		return Flux.defer(() -> Flux.fromIterable(userRepository.findAll()).subscribeOn(reactiveJdbcScheduler));
	}

	public Mono<UserEntity> patch(UserEntity userEntity) {

		return Mono.defer(() -> {

			return get(userEntity.getId()).flatMap(result -> {

				if (!Strings.isNullOrEmpty(userEntity.getEmail())) {
					result.setEmail(userEntity.getEmail());
				}

				if (!Strings.isNullOrEmpty(userEntity.getFirstName())) {
					result.setFirstName(userEntity.getFirstName());
				}

				if (!Strings.isNullOrEmpty(userEntity.getLastName())) {
					result.setLastName(userEntity.getLastName());
				}

				if (!Strings.isNullOrEmpty(userEntity.getPassword())) {
					result.setPassword(passwordEncoder.encode(userEntity.getPassword()));
				}

				return Mono.defer(() -> Mono.just(transactionTemplate.execute(status -> userRepository.save(result)))
						.subscribeOn(reactiveJdbcScheduler));
			});

		});

	}

}
