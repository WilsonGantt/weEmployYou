package we.employ.you.service;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import we.employ.you.dao.UserDAO;
import we.employ.you.exception.ValidationException;
import we.employ.you.model.User;
import we.employ.you.model.UserPassword;
import we.employ.you.util.PasswordUtil;

@Service
public class UserServiceImpl implements UserService {

	private final UserDAO userDAO;

	@Autowired
	public UserServiceImpl(UserDAO userDAO) {
		this.userDAO = userDAO;
	}

	@Override
	@Transactional
	public List<User> getUsers() {
		return userDAO.getUsers();
	}

	@Override
	@Transactional
	public User getUser(String userId) {
		return userDAO.getUser(userId);
	}

	@Override
	@Transactional
	public User getPersistedUser(String userId) {
		return userDAO.getPersistedUser(userId);
	}

	@Override
	@Transactional
	public byte[] getEmployeePhoto(String userId) {
		return userDAO.getEmployeePhoto(userId);
	}

	@Override
	@Transactional
	public void saveEmployeePhoto(String userId, byte[] photo) {
		userDAO.saveEmployeePhoto(userId, photo);
	}

	@Override
	@Transactional
	public boolean resetPassword(User user) throws Exception {
		List<UserPassword> userPasswords = this.userDAO.getUserPasswords(user.getUserId());

		//Checking if the new password matches any of the previous passwords
		if (userPasswords.stream().noneMatch(
				password -> password.getDecryptedPassword().equals(user.getPassword()))) {
			LocalDateTime creationDate = LocalDateTime.now();

			UserPassword userPassword = new UserPassword();
	        userPassword.setUserId(user.getUserId());
	        userPassword.setCreationDate(creationDate);
	        userPassword.setPassword(new PasswordUtil().encryptPassword(user.getPassword()));
	        userPassword.setCurrentPasswordIndicator(true);

	        if (user.getIsTemporaryPassword()) {
	            userPassword.setTemporaryPasswordExpirationDate(creationDate.plusDays(1));
	            userPassword.setTemporaryPasswordIndicator(true);

	            //Setting the current temporary password here to enable the sending the temporary password
	            //to the user
				user.setCurrentPassword(userPassword);
	        }

	        userPassword.setDeleteOldestPassword(userPasswords.size() >= 12);

			userDAO.resetPassword(userPassword);
			return true;
		} else {
			return false;
		}
	}

	@Override
	@Transactional
	public String saveUser(User user) throws ValidationException {
		return userDAO.saveUser(user);
	}

	@Override
	@Transactional
	public void deleteUser(String userId) throws ValidationException {
		userDAO.deleteUser(userId);
	}
}
