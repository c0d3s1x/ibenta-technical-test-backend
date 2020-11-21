package au.com.ibenta.test.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import au.com.ibenta.test.persistence.UserEntity;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Api(tags = "users")
@RestController
@RequestMapping("/users")
public class UserController {

	private final IUserService userService;

	public UserController(IUserService userService) {
		this.userService = userService;
	}

	@PostMapping("")
	@ResponseStatus(code = HttpStatus.CREATED)
	public Mono<UserEntity> create(@RequestBody UserEntity user) {

		return userService.create(user);
	}

	@GetMapping("/{id}")
	public Mono<UserEntity> get(@PathVariable(name = "id") Long id) {

		return userService.get(id);
	}

	@PutMapping("/{id}")
	@ResponseStatus(code = HttpStatus.CREATED)
	public Mono<UserEntity> update(@RequestBody UserEntity user) {

		return userService.update(user);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(code = HttpStatus.CREATED)
	public Mono<Void> delete(@PathVariable(name = "id") Long id) {

		return userService.delete(id);
	}

	@GetMapping()
	public Flux<UserEntity> list() {

		return userService.list();
	}

}
