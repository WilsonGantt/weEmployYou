package we.employ.you.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import we.employ.you.model.LoginResponse;
import we.employ.you.model.User;
import we.employ.you.model.UserRole;
import we.employ.you.service.UserService;
import we.employ.you.util.LogUtil;

@RestController
public class LoginController {

	private final UserService userService;

	private int failedPasswordCount = 0;

	@Autowired
	public LoginController(UserService userService) {
		this.userService = userService;
	}

	@PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<LoginResponse> loginUser(@RequestBody User user) {
		String loginMessage = "Success";
		String userRole = "";

		try {
			String userId = user.getUserId();
			String password = user.getPassword();

			if (userId.isBlank() && password.isBlank()) {
				loginMessage = "Please Login with your Username and Password.";
			} else if (userId.isBlank()) {
				loginMessage = "Please Login with your Username.";
	        } else if (password.isBlank()) {
	        	loginMessage = "Please Login with your Password.";
	        } else {
	        	User persistedUser = this.userService.getUser(userId);

	        	if (persistedUser == null) {
	        		loginMessage = "A user with ID " + userId.toUpperCase() + " was not found.";
	        	} else if (persistedUser.getTerminatedDate() != null) {
	        		loginMessage = "This user is no longer an employee of We Employ You Recruiting";
	        	} else if (!persistedUser.getCurrentPassword().getDecryptedPassword().equals(password)) {
	        		loginMessage = "Authenication not successful";
                    failedPasswordCount++;

                    //Instead of locking out the user, the password will be displayed
                    //on the screen
                    if (failedPasswordCount >= 3) {
                    	loginMessage = "Your password is " + persistedUser.getCurrentPassword().getDecryptedPassword();
                    }
                } else {
                    if (persistedUser.getCurrentPassword().getTemporaryPasswordIndicator()) {
                        if (LocalDateTime.now().isAfter(persistedUser.getCurrentPassword().getTemporaryPasswordExpirationDate())) {
                        	loginMessage = "The temporary password has expired. "
                                    + "Contact your manager for a new temporary password.";
                        } else {
                            loginMessage = "Reset Password";
                        }
                    } else {
                        LocalDateTime expirationDate = persistedUser.getCurrentPassword().getCreationDate().plusDays(30);

                        if (expirationDate.isBefore(LocalDateTime.now())) {
                        	loginMessage = "Reset Password";
                        }
                    }
                }

	        	if (loginMessage.equals("Success") || loginMessage.equals("Reset Password")) {
					failedPasswordCount = 0;
	    			userRole = persistedUser.getUserRole().getUserRole()
	    					.equals(UserRole.MANAGER_USER_ROLE) ? "Manager" : "Recruiter";
	    		}
	        }
		} catch (Exception exception) {
			loginMessage = "An unexcepted error occurred. Please contact Wilson for support.";
			LogUtil.logStackTrace(exception);
		}

		LoginResponse response = new LoginResponse();
		response.setLoginMessage(loginMessage);
		response.setUserRole(userRole);

		return ResponseEntity.ok(response);
	}
}
