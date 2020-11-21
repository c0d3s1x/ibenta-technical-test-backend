package au.com.ibenta.test.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import au.com.ibenta.test.persistence.UserEntity;
import au.com.ibenta.test.persistence.UserRepository;
import lombok.extern.slf4j.Slf4j;

/**
 * User service unit test to test code behavior attained through mocks.
 * 
 * @author randolfjosef.MAGARZO
 *
 */
@ExtendWith(value = { MockitoExtension.class })
@Slf4j
public class UserServiceUnitTests {

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private UserService userService;

	private static final Long GENERATED_ID = 1L;
	private static final String EMAIL = "johndoe@email.com";
	private static final String FIRST_NAME = "John";
	private static final String LAST_NAME = "Doe";
	private static final String PASSWORD = "s0m3p4ssw0rd";

	private static final String UPDATED = " Updated";

	private static final String OTHER = " Other";

	@Test
	@DisplayName("Test behavior of creating a user")
	public void testCreateUser() {

		// Setup
		final UserEntity expectedUser = new UserEntity();
		expectedUser.setId(GENERATED_ID);
		expectedUser.setEmail(EMAIL);
		expectedUser.setFirstName(FIRST_NAME);
		expectedUser.setLastName(LAST_NAME);
		expectedUser.setPassword(PASSWORD);

		UserEntity givenUser = new UserEntity();
		givenUser.setEmail(EMAIL);
		givenUser.setFirstName(FIRST_NAME);
		givenUser.setLastName(LAST_NAME);
		givenUser.setPassword(PASSWORD);

		// Mock
		when(userRepository.save(any())).thenReturn(expectedUser);

		// Execute
		log.debug("UserEntity to be created: [{}]", givenUser);
		UserEntity result = userService.create(givenUser);

		// Assert and verify
		verify(userRepository, times(1)).save(argThat(r -> {

			return r.getId() == null && expectedUser.getEmail().equals(r.getEmail())
					&& expectedUser.getFirstName().equals(r.getFirstName())
					&& expectedUser.getLastName().equals(r.getLastName())
					&& expectedUser.getPassword().equals(r.getPassword());
		}));

		log.debug("Created UserEntity: [{}]", result);
		assertThat(result.getId(), is(notNullValue()));
		assertThat(result.getId(), is(expectedUser.getId()));
		assertThat(result.getEmail(), is(notNullValue()));
		assertThat(result.getEmail(), is(expectedUser.getEmail()));
		assertThat(result.getFirstName(), is(notNullValue()));
		assertThat(result.getFirstName(), is(expectedUser.getFirstName()));
		assertThat(result.getLastName(), is(notNullValue()));
		assertThat(result.getLastName(), is(expectedUser.getLastName()));
		assertThat(result.getPassword(), is(notNullValue()));
		assertThat(result.getPassword(), is(expectedUser.getPassword()));

	}

	@Test
	@DisplayName("Test behavior of getting a user by id")
	public void testGetUser() {

		// Setup
		final UserEntity expectedUser = new UserEntity();
		expectedUser.setId(GENERATED_ID);
		expectedUser.setEmail(EMAIL);
		expectedUser.setFirstName(FIRST_NAME);
		expectedUser.setLastName(LAST_NAME);
		expectedUser.setPassword(PASSWORD);

		// Mock
		when(userRepository.getOne(any())).thenReturn(expectedUser);

		// Execute
		log.debug("ID to be fetched: [{}]", GENERATED_ID);
		UserEntity result = userService.get(GENERATED_ID);

		// Assert and verify
		verify(userRepository, times(1)).getOne(argThat(r -> {

			return expectedUser.getId().equals(r);
		}));

		log.debug("Fetched UserEntity: [{}]", result);
		assertThat(result.getId(), is(notNullValue()));
		assertThat(result.getId(), is(expectedUser.getId()));
		assertThat(result.getEmail(), is(notNullValue()));
		assertThat(result.getEmail(), is(expectedUser.getEmail()));
		assertThat(result.getFirstName(), is(notNullValue()));
		assertThat(result.getFirstName(), is(expectedUser.getFirstName()));
		assertThat(result.getLastName(), is(notNullValue()));
		assertThat(result.getLastName(), is(expectedUser.getLastName()));
		assertThat(result.getPassword(), is(notNullValue()));
		assertThat(result.getPassword(), is(expectedUser.getPassword()));

	}

	@Test
	@DisplayName("Test behavior of updating a user ")
	public void testUpdateUser() {

		// Setup
		final UserEntity expectedUser = new UserEntity();
		expectedUser.setId(GENERATED_ID);
		expectedUser.setEmail(UPDATED + EMAIL);
		expectedUser.setFirstName(UPDATED + FIRST_NAME);
		expectedUser.setLastName(UPDATED + LAST_NAME);
		expectedUser.setPassword(UPDATED + PASSWORD);

		// Mock
		when(userRepository.findById(any())).thenReturn(Optional.of(expectedUser));
		when(userRepository.save(any())).thenReturn(expectedUser);

		// Execute
		log.debug("UserEntity to be updated: [{}]", expectedUser);
		UserEntity result = userService.update(expectedUser);

		// Assert and verify
		verify(userRepository, times(1)).findById(argThat(r -> {

			return expectedUser.getId().equals(r);
		}));

		verify(userRepository, times(1)).save(argThat(r -> {

			return expectedUser.getId().equals(r.getId()) && expectedUser.getEmail().equals(r.getEmail())
					&& expectedUser.getFirstName().equals(r.getFirstName())
					&& expectedUser.getLastName().equals(r.getLastName())
					&& expectedUser.getPassword().equals(r.getPassword());
		}));

		log.debug("Updated UserEntity: [{}]", result);
		assertThat(result.getId(), is(notNullValue()));
		assertThat(result.getId(), is(expectedUser.getId()));
		assertThat(result.getEmail(), is(notNullValue()));
		assertThat(result.getEmail(), is(expectedUser.getEmail()));
		assertThat(result.getFirstName(), is(notNullValue()));
		assertThat(result.getFirstName(), is(expectedUser.getFirstName()));
		assertThat(result.getLastName(), is(notNullValue()));
		assertThat(result.getLastName(), is(expectedUser.getLastName()));
		assertThat(result.getPassword(), is(notNullValue()));
		assertThat(result.getPassword(), is(expectedUser.getPassword()));
	}

	@Test
	@DisplayName("Test behavior of updating non existing user")
	public void testUpdateUserFailsWhenUserIdDoesNotExist() {

		// Setup

		UserEntity givenUser = new UserEntity();
		givenUser.setId(GENERATED_ID + 1);
		givenUser.setEmail(UPDATED + EMAIL);
		givenUser.setFirstName(UPDATED + FIRST_NAME);
		givenUser.setLastName(UPDATED + LAST_NAME);
		givenUser.setPassword(UPDATED + PASSWORD);

		// Mock
		when(userRepository.findById(any())).thenReturn(Optional.empty());

		// Execute
		log.debug("UserEntity to be updated: [{}]", givenUser);
		boolean failureStatus = false;
		try {
			userService.update(givenUser);
		} catch (Exception e) {
			failureStatus = true;
			log.error("Error caught: {}", e.getMessage());
		}

		// Assert and verify
		verify(userRepository, times(1)).findById(argThat(r -> {

			return givenUser.getId().equals(r);
		}));

		verify(userRepository, times(0)).save(argThat(r -> {

			return givenUser.getId().equals(r.getId()) && givenUser.getEmail().equals(r.getEmail())
					&& givenUser.getFirstName().equals(r.getFirstName())
					&& givenUser.getLastName().equals(r.getLastName())
					&& givenUser.getPassword().equals(r.getPassword());
		}));

		assertTrue(failureStatus);
	}

	@Test
	@DisplayName("Test behavior of getting the list of all users")
	public void testListAllUsers() {

		// Setup
		final UserEntity expectedUser1 = new UserEntity();
		expectedUser1.setId(GENERATED_ID);
		expectedUser1.setEmail(EMAIL);
		expectedUser1.setFirstName(FIRST_NAME);
		expectedUser1.setLastName(LAST_NAME);
		expectedUser1.setPassword(PASSWORD);

		final UserEntity expectedUser2 = new UserEntity();
		expectedUser2.setId(GENERATED_ID + 1);
		expectedUser2.setEmail(OTHER + EMAIL);
		expectedUser2.setFirstName(OTHER + FIRST_NAME);
		expectedUser2.setLastName(OTHER + LAST_NAME);
		expectedUser2.setPassword(OTHER + PASSWORD);

		List<UserEntity> expectedUsers = new ArrayList<>();
		expectedUsers.add(expectedUser1);
		expectedUsers.add(expectedUser2);

		// Mock
		when(userRepository.findAll()).thenReturn(expectedUsers);

		// Execute

		Collection<UserEntity> result = userService.list();

		// Assert and verify
		verify(userRepository, times(1)).findAll();

		log.debug("Fetched UserEntities: [{}]", result);

		assertThat(result, is(notNullValue()));
		assertThat(result.size(), is(2));

		
	}

}
