package au.com.ibenta.test.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import au.com.ibenta.test.persistence.UserEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@DisplayName("unit test for user endpoints")
@ExtendWith(value = { MockitoExtension.class /* SpringExtension.class */ })
//@WebFluxTest(controllers = { UserController.class })
//@Import(value = { UserService.class })
public class UserControllerUnitTests {

//	@Autowired

	private UserEntity userRequest;

	private Mono<UserEntity> singleUserResponse;
	private Flux<UserEntity> multipleUsersResponse;

	@Mock
	private UserService userService;

	@InjectMocks
	private UserController userController;

	private WebTestClient webTestClient;

	@BeforeEach
	public void setup() {

//		userController = new UserController(userService);
		webTestClient = WebTestClient.bindToController(userController).build();

		userRequest = new UserEntity();
		userRequest.setId(99L);
		userRequest.setFirstName("John");
		userRequest.setLastName("Doe");
		userRequest.setEmail("johndoe@email.com");
		userRequest.setPassword("s0m3p4ssw0rd");

		UserEntity userEntity1 = new UserEntity();
		userEntity1.setId(1L);
		userEntity1.setFirstName("John");
		userEntity1.setLastName("Doe");
		userEntity1.setEmail("johndoe@email.com");
		userEntity1.setPassword("s0m3p4ssw0rd");

		UserEntity userEntity2 = new UserEntity();
		userEntity2.setId(1L);
		userEntity2.setFirstName("Jane");
		userEntity2.setLastName("Doe");
		userEntity2.setEmail("janedoe@email.com");
		userEntity2.setPassword("s0m3p4ssw0rd");

		singleUserResponse = Mono.just(userEntity1);

		multipleUsersResponse = Flux.just(userEntity1, userEntity2);

	}

	@Test
	@DisplayName("test create endpoint")
	public void testCreate() {

		when(userService.create(any())).thenReturn(singleUserResponse);

		UserEntity expected = singleUserResponse.block();

		webTestClient.post().uri("/users").contentType(MediaType.APPLICATION_JSON).bodyValue(userRequest).exchange()
				.expectStatus().isCreated().expectBody()

				// Id sent in request should be ignored by the implementation and should return
				// identity produced after saving.
				.jsonPath("$.id").exists().jsonPath("$.id").isEqualTo(expected.getId())

				.jsonPath("$.firstName").exists().jsonPath("$.firstName").isEqualTo(expected.getFirstName())

				.jsonPath("$.lastName").exists().jsonPath("$.lastName").isEqualTo(expected.getLastName())

				.jsonPath("$.email").exists().jsonPath("$.email").isEqualTo(expected.getEmail())

				.jsonPath("$.password").exists().jsonPath("$.password").isEqualTo(expected.getPassword());

		;

	}

	@Test
	@DisplayName("test get by id endpoint")
	public void testGetWhenUserExists() {

		when(userService.get(any())).thenReturn(singleUserResponse);

		UserEntity expected = singleUserResponse.block();

		webTestClient.get().uri("/users/{id}", userRequest.getId()).exchange().expectStatus().isOk()

				.expectBody()

				.jsonPath("$.id").exists().jsonPath("$.id").isEqualTo(expected.getId())

				.jsonPath("$.firstName").exists().jsonPath("$.firstName").isEqualTo(expected.getFirstName())

				.jsonPath("$.lastName").exists().jsonPath("$.lastName").isEqualTo(expected.getLastName())

				.jsonPath("$.email").exists().jsonPath("$.email").isEqualTo(expected.getEmail())

				.jsonPath("$.password").exists().jsonPath("$.password").isEqualTo(expected.getPassword());

		;
	}

	@Test
	@DisplayName("test get by id endpoint not found")
	public void testGetWhenUserDoesNotExist() {

		Long notExistingId = Long.MAX_VALUE;

		when(userService.get(any())).thenReturn(Mono.error(new UserNotFoundException(notExistingId)));

		webTestClient.get().uri("/users/{id}", notExistingId).exchange().expectStatus().isNotFound();

		;
	}

	@Test
	@DisplayName("test update endpoint")
	public void testUpdateWhenUserExists() {

		when(userService.update(any())).thenReturn(singleUserResponse);

		UserEntity expected = singleUserResponse.block();

		webTestClient.put().uri("/users/{id}", userRequest.getId()).contentType(MediaType.APPLICATION_JSON)
				.bodyValue(userRequest).exchange().expectStatus().isCreated().expectBody()

				.jsonPath("$.id").exists().jsonPath("$.id").isEqualTo(expected.getId())

				.jsonPath("$.firstName").exists().jsonPath("$.firstName").isEqualTo(expected.getFirstName())

				.jsonPath("$.lastName").exists().jsonPath("$.lastName").isEqualTo(expected.getLastName())

				.jsonPath("$.email").exists().jsonPath("$.email").isEqualTo(expected.getEmail())

				.jsonPath("$.password").exists().jsonPath("$.password").isEqualTo(expected.getPassword());

		;

	}

	@Test
	@DisplayName("test update endpoint not found")
	public void testUpdateWhenUserDoesNotExist() {

		Long notExistingId = Long.MAX_VALUE;
		when(userService.update(any())).thenReturn(Mono.error(new UserNotFoundException(notExistingId)));

		webTestClient.put().uri("/users/{id}", notExistingId).contentType(MediaType.APPLICATION_JSON)
				.bodyValue(userRequest).exchange().expectStatus().isNotFound();

		;

	}

	@Test
	@DisplayName("test delete endpoint")
	public void testDeleteWhenUserExists() {

		when(userService.delete(any())).thenReturn(Mono.empty());

		webTestClient.delete().uri("/users/{id}", userRequest.getId()).exchange().expectStatus().isCreated()

		;
	}

	@Test
	@DisplayName("test delete endpoint not found")
	public void testDeleteWhenUserDoesNotExist() {

		Long notExistingId = Long.MAX_VALUE;

		when(userService.delete(any())).thenReturn(Mono.error(new UserNotFoundException(notExistingId)));

		webTestClient.delete().uri("/users/{id}", notExistingId).exchange().expectStatus().isNotFound();

		;
	}

	@Test
	@DisplayName("test list endpoint")
	public void testList() {

		when(userService.list()).thenReturn(multipleUsersResponse);

		List<UserEntity> expected = multipleUsersResponse.collect(Collectors.toList()).block();

		webTestClient.get().uri("/users").exchange().expectStatus().isOk()

				.expectBody().jsonPath("$.length()").isEqualTo(2)

				.jsonPath("$[*].id").exists()

				.jsonPath("$[0].id").isEqualTo(expected.get(0).getId())

				.jsonPath("$[1].id").isEqualTo(expected.get(1).getId())

				.jsonPath("$[*].firstName").exists()

				.jsonPath("$[0].firstName").isEqualTo(expected.get(0).getFirstName())

				.jsonPath("$[1].firstName").isEqualTo(expected.get(1).getFirstName())

				.jsonPath("$[*].lastName").exists()

				.jsonPath("$[0].lastName").isEqualTo(expected.get(0).getLastName())

				.jsonPath("$[1].lastName").isEqualTo(expected.get(1).getLastName())

				.jsonPath("$[*].email").exists()

				.jsonPath("$[0].email").isEqualTo(expected.get(0).getEmail())

				.jsonPath("$[1].email").isEqualTo(expected.get(1).getEmail())

				.jsonPath("$[*].password").exists()

				.jsonPath("$[0].password").isEqualTo(expected.get(0).getPassword())

				.jsonPath("$[1].password").isEqualTo(expected.get(1).getPassword());

		;

		;
	}

}
