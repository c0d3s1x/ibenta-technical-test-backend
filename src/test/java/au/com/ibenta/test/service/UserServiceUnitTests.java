package au.com.ibenta.test.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.support.TransactionTemplate;

import au.com.ibenta.test.persistence.UserEntity;
import au.com.ibenta.test.persistence.UserRepository;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

/**
 * User service unit test to test code behavior attained through mocks.
 * 
 * @author randolfjosef.MAGARZO
 *
 */
@DisplayName("unit test for user service")
@Execution(ExecutionMode.CONCURRENT)
@ExtendWith(value = { MockitoExtension.class })
@Slf4j
public class UserServiceUnitTests {

	private static final Long GENERATED_ID = 1L;
	private static final String EMAIL = "johndoe@email.com";
	private static final String FIRST_NAME = "John";
	private static final String LAST_NAME = "Doe";
	private static final String PASSWORD = "s0m3p4ssw0rd";

	private static final String UPDATED = " Updated";

	private static final String OTHER = " Other";

	private static final int EXECUTOR_SIZE = 10;

	@Mock
	private UserRepository userRepository;

	@Spy
	private ExecutorService executor = Executors.newFixedThreadPool(EXECUTOR_SIZE);

	@Mock
	private TransactionTemplate transactionTemplate;

	@Spy
	private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

//	@InjectMocks
	private UserService userService;

	private UserEntity expectedUser = new UserEntity();
	private UserEntity givenUser = new UserEntity();

	@BeforeEach
	public void setup() {

		Scheduler reactiveJdbcScheduler = Schedulers.fromExecutor(executor);

		userService = new UserService(reactiveJdbcScheduler, transactionTemplate, userRepository, passwordEncoder);

		expectedUser = new UserEntity();
		expectedUser.setId(GENERATED_ID);
		expectedUser.setEmail(UPDATED + EMAIL);
		expectedUser.setFirstName(UPDATED + FIRST_NAME);
		expectedUser.setLastName(UPDATED + LAST_NAME);
		expectedUser.setPassword(UPDATED + PASSWORD);

		givenUser = new UserEntity();
		givenUser.setEmail(EMAIL);
		givenUser.setFirstName(FIRST_NAME);
		givenUser.setLastName(LAST_NAME);
		givenUser.setPassword(PASSWORD);
	}

	@Test
	@DisplayName("test behavior of creating a user")
	@RepeatedTest(value = 20)

	public void testCreateUser() {

		// Mock

		when(userRepository.save(argThat(r -> r.getId() == null))).thenReturn(expectedUser);

		/*
		 * We will mock the call to execute that it internally calls save
		 */
		when(transactionTemplate.execute(argThat(r -> {
			userRepository.save(givenUser);
			return true;
		}))).thenReturn(expectedUser);

		// Execute
		log.debug("UserEntity to be created: [{}]", givenUser);
		String originalPassword = givenUser.getPassword();
		Mono<UserEntity> reactiveResult = userService.create(givenUser);

		// Assert and verify

		StepVerifier.create(reactiveResult.log()).assertNext(result -> {

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
			assertThat(result.getPassword(), is(not(originalPassword)));

			log.info("Verifying method calls ...");

			verify(executor, times(1)).submit((Callable<?>) any());

			verify(transactionTemplate, times(1)).execute(any());

			verify(passwordEncoder, times(1)).encode(argThat(r -> originalPassword.equals(r)));

			verify(userRepository, times(1)).save(argThat(r -> {

				return r.getId() == null && !expectedUser.getEmail().equals(r.getEmail())
						&& !expectedUser.getFirstName().equals(r.getFirstName())
						&& !expectedUser.getLastName().equals(r.getLastName())
						&& !r.getPassword().equals(originalPassword);
			}));
		}).verifyComplete();

	}

	@Test
	@DisplayName("test behavior of getting a user by id")
	@RepeatedTest(value = 20)
	public void testGetUser() {

		// Mock

		when(userRepository.findById(any())).thenReturn(Optional.of(expectedUser));

		// Execute
		log.debug("ID to be fetched: [{}]", expectedUser.getId());
		Mono<UserEntity> reactiveResult = userService.get(expectedUser.getId());

		StepVerifier.create(reactiveResult.log()).assertNext(result -> {

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

			log.info("Verifying method calls ...");

			verify(executor, times(1)).submit((Callable<?>) any());

			verify(userRepository, times(1)).findById((Long) argThat(r -> {

				return expectedUser.getId().equals(r);
			}));

		}).verifyComplete();

	}

	@Test
	@DisplayName("test behavior of updating a user ")
	@RepeatedTest(value = 20)
	public void testUpdateUser() {

		// Mock
		when(userRepository.findById(any())).thenReturn(Optional.of(givenUser));

		when(userRepository.save(any())).thenReturn(expectedUser);

		/*
		 * We will mock the call to execute that it internally calls save
		 */
		when(transactionTemplate.execute(argThat(r -> {
			userRepository.save(givenUser);
			return true;
		}))).thenReturn(expectedUser);

		// Execute
		String originalPassword = givenUser.getPassword();
		givenUser.setId(expectedUser.getId());
		log.debug("UserEntity to be updated: [{}]", givenUser);

		Mono<UserEntity> reactiveResult = userService.update(givenUser);

		// Assert and verify

		StepVerifier.create(reactiveResult.log()).assertNext(result -> {

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
			assertThat(result.getPassword(), is(not(originalPassword)));

			log.info("Verifying method calls ...");

			verify(passwordEncoder, times(1)).encode(argThat(r -> originalPassword.equals(r)));

			verify(executor, times(2)).submit((Callable<?>) any());

			verify(transactionTemplate, times(1)).execute(any());

			verify(userRepository, times(1)).findById((Long) argThat(r -> {

				return expectedUser.getId().equals(r);
			}));

			verify(userRepository, times(1)).save(argThat(r -> {

				return expectedUser.getId().equals(r.getId()) && !expectedUser.getEmail().equals(r.getEmail())
						&& !expectedUser.getFirstName().equals(r.getFirstName())
						&& !expectedUser.getLastName().equals(r.getLastName())
						&& !r.getPassword().equals(originalPassword);
			}));
		}).verifyComplete();

	}

	@Test
	@DisplayName("test behavior of updating non existing user")
	@RepeatedTest(value = 20)
	public void testUpdateUserFailsWhenUserIdDoesNotExist() {

		// Mock
		when(userRepository.findById(any())).thenReturn(Optional.empty());

		// Execute
		log.debug("UserEntity to be updated: [{}]", expectedUser);
		Mono<UserEntity> reactiveResult = userService.update(expectedUser);

		// Assert and verify

		StepVerifier.create(reactiveResult).verifyErrorSatisfies(error -> {
			log.error("Error result: {}", error.getMessage());

			log.info("Verifying method calls ...");

			verify(executor, times(1)).submit((Callable<?>) any());

			verify(transactionTemplate, times(0)).execute(any());

			verify(userRepository, times(1)).findById((Long) argThat(r -> {

				return expectedUser.getId().equals(r);
			}));

			verify(userRepository, times(0)).save(any());
		});

	}

	@Test
	@DisplayName("test behavior of getting the list of all users")
	@RepeatedTest(value = 20)
	public void testListAllUsers() {

		UserEntity anotherExpectedUser = new UserEntity();
		anotherExpectedUser.setId(GENERATED_ID + 1);
		anotherExpectedUser.setEmail(OTHER + EMAIL);
		anotherExpectedUser.setFirstName(OTHER + FIRST_NAME);
		anotherExpectedUser.setLastName(OTHER + LAST_NAME);
		anotherExpectedUser.setPassword(OTHER + PASSWORD);

		List<UserEntity> expectedUsers = new ArrayList<>();

		expectedUsers.add(expectedUser);
		expectedUsers.add(anotherExpectedUser);

		// Mock
		when(userRepository.findAll()).thenReturn(expectedUsers);

		// Execute

		Flux<UserEntity> reactiveResult = userService.list();

		List<UserEntity> collectedResults = new ArrayList<>();

		StepVerifier.create(reactiveResult.log()).thenConsumeWhile(result -> {
			log.info("Success results: {}", result);
			verify(userRepository, times(1)).findAll();
			verify(executor, times(1)).submit((Callable<?>) any());
			collectedResults.add(result);
			return true;
		}).verifyComplete();

		assertThat(collectedResults.size(), is(2));

	}

	@Test
	@DisplayName("test behavior of partially updating a user ")
	@RepeatedTest(value = 20)
	public void testPartialUpdateUser() {

		// Partially update only the email, copy all from expected data.
		String updatedEmail = "newemail@email.com";
		givenUser.setEmail(updatedEmail);
		givenUser.setId(expectedUser.getId());
		givenUser.setFirstName(null);
		givenUser.setLastName(null);
		givenUser.setPassword(null);

		// Mock
		when(userRepository.findById(any())).thenReturn(Optional.of(expectedUser));

		when(userRepository.save(any())).thenReturn(expectedUser);

		/*
		 * We will mock the call to execute that it internally calls save
		 */
		when(transactionTemplate.execute(argThat(r -> {
			userRepository.save(expectedUser);
			return true;
		}))).thenReturn(expectedUser);

		// Execute

		log.debug("UserEntity to be updated: [{}]", givenUser);

		Mono<UserEntity> reactiveResult = userService.patch(givenUser);

		// Assert and verify

		StepVerifier.create(reactiveResult.log()).assertNext(result -> {

			log.debug("Previous UserEntity [{}]", expectedUser);
			log.debug("Updated UserEntity: [{}]", result);
			assertThat(result.getId(), is(notNullValue()));
			assertThat(result.getId(), is(expectedUser.getId()));
			assertThat(result.getEmail(), is(notNullValue()));
			assertThat(result.getEmail(), is(updatedEmail));
			assertThat(result.getFirstName(), is(notNullValue()));
			assertThat(result.getFirstName(), is(expectedUser.getFirstName()));
			assertThat(result.getLastName(), is(notNullValue()));
			assertThat(result.getLastName(), is(expectedUser.getLastName()));
			assertThat(result.getPassword(), is(notNullValue()));
			assertThat(result.getPassword(), is(expectedUser.getPassword()));

			log.info("Verifying method calls ...");

			verify(passwordEncoder, times(0)).encode(any());

			verify(executor, times(2)).submit((Callable<?>) any());

			verify(transactionTemplate, times(1)).execute(any());

			verify(userRepository, times(1)).findById((Long) argThat(r -> {

				return expectedUser.getId().equals(r);
			}));

			verify(userRepository, times(1)).save(argThat(r -> {

				return expectedUser.getId().equals(r.getId()) && r.getEmail().equals(updatedEmail)
						&& expectedUser.getFirstName().equals(r.getFirstName())
						&& expectedUser.getLastName().equals(r.getLastName())
						&& expectedUser.getPassword().equals(r.getPassword());

			}));

		}).verifyComplete();

	}

	@Test
	@DisplayName("test behavior of partially updating non existing user")
	@RepeatedTest(value = 20)
	public void testPartialUpdateUserFailsWhenUserIdDoesNotExist() {

		// Mock
		when(userRepository.findById(any())).thenReturn(Optional.empty());

		// Execute
		log.debug("UserEntity to be updated: [{}]", expectedUser);
		Mono<UserEntity> reactiveResult = userService.update(expectedUser);

		// Assert and verify

		StepVerifier.create(reactiveResult.log()).verifyErrorSatisfies(error -> {

			log.error("Error result: {}", error.getMessage());

			log.info("Verifying method calls ...");

			verify(executor, times(1)).submit((Callable<?>) any());

			verify(transactionTemplate, times(0)).execute(any());

			verify(userRepository, times(1)).findById((Long) argThat(r -> {

				return expectedUser.getId().equals(r);
			}));

			verify(userRepository, times(0)).save(any());
		});

	}

}
