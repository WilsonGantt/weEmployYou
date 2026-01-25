package we.employ.you.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import we.employ.you.exception.ValidationException;
import we.employ.you.model.ErrorResponse;
import we.employ.you.model.PasswordResetResponse;
import we.employ.you.model.Response;
import we.employ.you.model.User;
import we.employ.you.service.UserService;
import we.employ.you.util.PasswordUtil;

@RestController
public class UserController {

	private final UserService userService;

	@Autowired
	public UserController(UserService userService) {
		this.userService = userService;
	}

	@CrossOrigin(origins = "*")
	@GetMapping(value = "/getEmployees", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<User>> getUsers() {
		return ResponseEntity.ok(userService.getUsers());
	}

	@CrossOrigin(origins = "*")
	@GetMapping(value = "/getEmployee/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public User getUser(@PathVariable String userId) {
		return userService.getPersistedUser(userId);
	}

	@CrossOrigin(origins = "*")
	@GetMapping(value = "/getEmployeePhoto/{employeeId}")
	@ResponseBody
	public byte[] getApplicantPhoto(@PathVariable("employeeId") String employeeId) {
		return userService.getEmployeePhoto(employeeId);
	}

	@CrossOrigin(origins = "*")
	@PostMapping(value = "/saveEmployeePhoto")
	public void saveApplicantPhoto(@RequestParam("employeeId") String employeeId,
			@RequestParam("employeePhoto") MultipartFile photo) throws IOException {
		userService.saveEmployeePhoto(employeeId, photo.getBytes());
	}

	@CrossOrigin(origins = "*")
	@PutMapping(value = "/resetPassword", consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PasswordResetResponse> resetPassword(@RequestBody User user) throws Exception {
		if (user.getIsTemporaryPassword()) {
			user.setPassword(PasswordUtil.generatePassword());
		}

		PasswordResetResponse response = new PasswordResetResponse();

		if (userService.resetPassword(user)) {
			response.setStatus("Success");

			if (user.getIsTemporaryPassword()) {
				PasswordUtil.writeCredentialsToDesktop(user);
			}
		} else {
			response.setStatus("Errors");
			response.setErrorMessage("The password cannot be the same as the previous twelve passwords");
		}

		return ResponseEntity.ok(response);
	}

	@CrossOrigin(origins = "*")
	@PostMapping(value = "/saveEmployee", consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<? extends Response> saveUser(@RequestBody User user) throws Exception {
		Response response;

		try {
			String userId = userService.saveUser(user);

			response = user;

			//A new employee
			if (userId != null) {
				((User) response).setUserId(userId);
				//The reset password function can be used for new employees
				user.setPassword(PasswordUtil.generatePassword());
				user.setIsTemporaryPassword(true);
				userService.resetPassword(user);
				PasswordUtil.writeCredentialsToDesktop(user);
			}
		} catch (ValidationException exception) {
			response = new ErrorResponse();
			response.setResponseMessage(exception.getMessage());
		}

		return ResponseEntity.ok(response);
	}

	@CrossOrigin(origins = "*")
	@DeleteMapping(value = "/deleteEmployee/{userId}", consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> deleteUser(@PathVariable String userId) {
		Response response = new Response();

		try {
			userService.deleteUser(userId);
			response.setResponseMessage("success");
		} catch (ValidationException exception) {
			response.setResponseMessage(exception.getMessage());
		}

		return ResponseEntity.ok(response);
	}
}
