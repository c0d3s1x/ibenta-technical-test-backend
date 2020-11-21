package au.com.ibenta.test.service;

import java.util.Collection;

import au.com.ibenta.test.persistence.UserEntity;

/**
 * Abstraction / contract for User service functionalities.
 * 
 * @author randolfjosef.MAGARZO
 *
 */
public interface IUserService {

	UserEntity create(UserEntity entity);

	UserEntity get(Long id);

	UserEntity update(UserEntity entity);

	void delete(Long id);

	Collection<UserEntity> list();
}
