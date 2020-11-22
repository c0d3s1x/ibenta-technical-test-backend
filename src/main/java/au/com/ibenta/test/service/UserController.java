package au.com.ibenta.test.service;

import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import au.com.ibenta.test.model.GenericMapper;
import au.com.ibenta.test.model.User;
import au.com.ibenta.test.persistence.UserEntity;
import io.swagger.annotations.Api;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Api(tags = "users")
@RestController
@RequestMapping("/users")
public class UserController {

	private final IUserService userService;

	private final GenericMapper<UserEntity, User> mapper = new GenericMapper<>(UserEntity.class, User.class);

	public UserController(IUserService userService) {
		this.userService = userService;
	}

	@PostMapping("")
	@ResponseStatus(code = HttpStatus.CREATED)
	public Mono<User> create(@Valid @RequestBody User user) {

		return userService.create(mapper.to(user)).flatMap(mapper.ofMonoFrom());
	}

	@GetMapping("/{id}")
	public Mono<User> get(@Valid @PathVariable(name = "id") Long id) {

		return userService.get(id).flatMap(mapper.ofMonoFrom());
	}

	@PutMapping("/{id}")
	public Mono<User> update(@Valid @PathVariable("id") Long id, @Valid @RequestBody User user) {

		user.setId(id); // id is no longer exposed as writable
		return userService.update(mapper.to(user)).flatMap(mapper.ofMonoFrom());
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(code = HttpStatus.CREATED)
	public Mono<Void> delete(@Valid @PathVariable(name = "id") Long id) {

		return userService.delete(id);
	}

	@GetMapping()
	public Flux<User> list() {

		return userService.list().map(result -> mapper.from(result)).collect(Collectors.toList()).flux()
				.flatMap(Flux::fromIterable);
	}

	@PatchMapping("/{id}")
	public Mono<User> patch(@Valid @PathVariable("id") Long id, @RequestBody User user) {

		user.setId(id); // id is no longer exposed as writable
		return userService.patch(mapper.to(user)).flatMap(mapper.ofMonoFrom());
	}

}
