package au.com.ibenta.test.model;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import lombok.Data;
import com.googlecode.jmapper.annotations.JMap;

@Data
public class User {

	@JMap
	@JsonProperty(access = Access.READ_ONLY)
	private Long id;

	@JMap
	@NotNull(message = "firstName is required.")
	@Size(min = 1, message = "firstName cannot be empty.")
	private String firstName;

	@JMap
	@NotNull(message = "lastName is required.")
	@Size(min = 1, message = "lastName cannot be empty.")
	private String lastName;

	@JMap
	@Email
	@NotNull(message = "email is required.")
	@Size(min = 1, message = "email cannot be empty.")
	private String email;

	@JMap
	@JsonProperty(access = Access.WRITE_ONLY)
	@NotNull(message = "password is required")
	@Size(min = 1, message = "password cannot be empty.")
	private String password;
}
