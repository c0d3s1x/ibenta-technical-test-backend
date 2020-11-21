package au.com.ibenta.test.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class UserNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 8401234780498882099L;

	public UserNotFoundException(Long id) {
		super(String.format("User with ID [%s] not found", id));
	}

}
