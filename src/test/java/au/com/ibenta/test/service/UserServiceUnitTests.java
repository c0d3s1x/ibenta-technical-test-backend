package au.com.ibenta.test.service;

import static org.hamcrest.CoreMatchers.is;
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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.support.TransactionTemplate;

import au.com.ibenta.test.persistence.UserEntity;
import au.com.ibenta.test.persistence.UserRepository;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

/**
 * User service unit test to test code behavior attained through mocks.
 * 
 * @author randolfjosef.MAGARZO
 *
 */
@DisplayName("unit test for user service")
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

//	@InjectMocks
	private UserService userService;

	private UserEntity expectedUser = new UserEntity();
	private UserEntity givenUser = new UserEntity();

	@BeforeEach
	public void setup() {

		Scheduler reactiveJdbcScheduler = Schedulers.fromExecutor(executor);

		userService = new UserService(reactiveJdbcScheduler, transactionTemplate, userRepository);

		expectedUser = new UserEntity();
		expectedUser.setId(GENERATED_ID);
		expectedUser.setEmail(EMAIL);
		expectedUser.setFirstName(FIRST_NAME);
		expectedUser.setLastName(LAST_NAME);
		expectedUser.setPassword(PASSWORD);

		givenUser = new UserEntity();
		givenUser.setEmail(EMAIL);
		givenUser.setFirstName(FIRST_NAME);
		givenUser.setLastName(LAST_NAME);
		givenUser.setPassword(PASSWORD);
	}

	@Test
	@DisplayName("test behavior of creating a user")
	public void testCreateUser() {

		// Mock
		/*
		 * We're using Mockito.lenient().when as Mockito throws
		 * UnnecessaryStubbingException since it appears that the test code is not using
		 * the stub but it is, just within the reactve subscribe method.
		 */

		Mockito.lenient().when(userRepository.save(argThat(r -> r.getId() == null))).thenReturn(expectedUser);

		/*
		 * We will mock the call to execute that it internally calls save
		 */
		Mockito.lenient().when(transactionTemplate.execute(argThat(r -> {
			userRepository.save(givenUser);
			return true;
		}))).thenReturn(expectedUser);

		// Execute
		log.debug("UserEntity to be created: [{}]", givenUser);
		Mono<UserEntity> reactiveResult = userService.create(givenUser);

		// Assert and verify

		// Reactively subscribe first, before verify
		log.info("Subscribing to reactive result");
		reactiveResult.log().subscribe(result -> {

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

			log.info("Verifying method calls ...");

			verify(executor, times(1)).submit((Callable<?>) any());

			verify(transactionTemplate, times(1)).execute(any());

			verify(userRepository, times(1)).save(argThat(r -> {

				return r.getId() == null && expectedUser.getEmail().equals(r.getEmail())
						&& expectedUser.getFirstName().equals(r.getFirstName())
						&& expectedUser.getLastName().equals(r.getLastName())
						&& expectedUser.getPassword().equals(r.getPassword());
			}));
		});

	}

	@Test
	@DisplayName("test behavior of getting a user by id")
	public void testGetUser() {

		// Mock

		/*
		 * We're using Mockito.lenient().when as Mockito throws
		 * UnnecessaryStubbingException since it appears that the test code is not using
		 * the stub but it is, just within the reactve subscribe method.
		 */
		Mockito.lenient().when(userRepository.getOne(any())).thenReturn(expectedUser);

		// Execute
		log.debug("ID to be fetched: [{}]", expectedUser.getId());
		Mono<UserEntity> reactiveResult = userService.get(expectedUser.getId());

		// Reactively subscribe first, before verify
		log.info("Subscribing to reactive result");
		reactiveResult.log().subscribe(result -> {

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

			verify(userRepository, times(1)).getOne((Long) argThat(r -> {

				return expectedUser.getId().equals(r);
			}));
		});

	}

	@Test
	@DisplayName("test behavior of updating a user ")
	public void testUpdateUser() {

		// Mock

		/*
		 * We're using Mockito.lenient().when as Mockito throws
		 * UnnecessaryStubbingException since it appears that the test code is not using
		 * the stub but it is, just within the reactve subscribe method.
		 */

		Mockito.lenient().when(userRepository.findById(any())).thenReturn(Optional.of(expectedUser));
		Mockito.lenient().when(userRepository.save(any())).thenReturn(expectedUser);

		/*
		 * We will mock the call to execute that it internally calls save
		 */
		Mockito.lenient().when(transactionTemplate.execute(argThat(r -> {
			userRepository.save(expectedUser);
			return true;
		}))).thenReturn(expectedUser);

		// Execute
		log.debug("UserEntity to be updated: [{}]", expectedUser);
		Mono<UserEntity> reactiveResult = userService.update(expectedUser);

		// Assert and verify

		// Reactively subscribe first, before verify
		log.info("Subscribing to reactive result");
		reactiveResult.log().subscribe(result -> {

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

			log.info("Verifying method calls ...");

			verify(executor, times(1)).submit((Callable<?>) any());

			verify(transactionTemplate, times(1)).execute(any());

			verify(userRepository, times(1)).findById((Long) argThat(r -> {

				return expectedUser.getId().equals(r);
			}));

			verify(userRepository, times(1)).save(argThat(r -> {

				return expectedUser.getId().equals(r.getId()) && expectedUser.getEmail().equals(r.getEmail())
						&& expectedUser.getFirstName().equals(r.getFirstName())
						&& expectedUser.getLastName().equals(r.getLastName())
						&& expectedUser.getPassword().equals(r.getPassword());
			}));
		});

	}

	@Test
	@DisplayName("test behavior of updating non existing user")
	public void testUpdateUserFailsWhenUserIdDoesNotExist() {

		// Mock
		when(userRepository.findById(any())).thenReturn(Optional.empty());

		// Execute
		log.debug("UserEntity to be updated: [{}]", expectedUser);
		Mono<UserEntity> reactiveResult = userService.update(expectedUser);

		// Assert and verify

		// Reactively subscribe first, before verify
		log.info("Subscribing to reactive result");
		reactiveResult.log().subscribe(result -> {

			log.error("Success result: {}", result);
			throw new AssertionError("Expected to fail but is successful");

		}, error -> {

			log.error("Error result: {}", error.getMessage());

			log.info("Verifying method calls ...");

			verify(executor, times(1)).submit((Callable<?>) any());

			verify(transactionTemplate, times(0)).execute(any());

			verify(userRepository, times(1)).getOne((Long) argThat(r -> {

				return expectedUser.getId().equals(r);
			}));

			verify(userRepository, times(0)).save(any());
		});
	}

	@Test
	@DisplayName("test behavior of getting the list of all users")
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

		reactiveResult.count().map(Long::intValue).subscribe(count -> {
			assertThat(count, is(2));
		});

		reactiveResult.log().subscribe(results -> {

			log.info("Success results: {}", results);

			verify(userRepository, times(1)).findAll();

		});

	}

}
