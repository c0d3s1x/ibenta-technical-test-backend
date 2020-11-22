package au.com.ibenta.test.service;

import au.com.ibenta.test.persistence.UserEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Abstraction / contract for User service functionalities.
 * 
 * @author randolfjosef.MAGARZO
 *
 */
public interface IUserService {

	Mono<UserEntity> create(UserEntity entity);

	Mono<UserEntity> get(Long id);

	Mono<UserEntity> update(UserEntity entity);

	Mono<Void> delete(Long id);

	Flux<UserEntity> list();

	Mono<UserEntity> patch(UserEntity entity);
}
