package au.com.ibenta.test.service;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import au.com.ibenta.test.persistence.UserEntity;
import au.com.ibenta.test.persistence.UserRepository;

@Service
public class UserService implements IUserService {

	@Autowired
	private UserRepository userRepository;

	public UserEntity create(UserEntity entity) {
		return userRepository.save(entity);
	}

	public UserEntity get(Long id) {
		return userRepository.getOne(id);
	}

	public UserEntity update(UserEntity entity) {

		// Let's check first that the user exists. If not checked here, update becomes
		// similar to save.

		userRepository.findById(entity.getId())
				.orElseThrow(() -> new RuntimeException(String.format("User with ID [%s] not found", entity.getId())));

		return userRepository.save(entity);
	}

	public void delete(Long id) {
		userRepository.deleteById(id);
	}

	public Collection<UserEntity> list() {
		return userRepository.findAll();
	}

}
