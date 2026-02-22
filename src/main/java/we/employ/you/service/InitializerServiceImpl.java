package we.employ.you.service;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDate;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import we.employ.you.dao.UserDAO;
import we.employ.you.model.User;
import we.employ.you.util.PasswordUtil;

@Service
public class InitializerServiceImpl implements InitializerService {

	private UserDAO userDAO;
	
	@Autowired
	public InitializerServiceImpl(UserDAO userDAO) {
		this.userDAO = userDAO;
	}
	
	@Override
	@Transactional
	public String initialize() {
		String message;
		try {
			User user = new User();
	        user.setUserId("WEU00001");
	        user.setFirstName("Wilson");
	        user.setLastName("Gantt");
	        user.setEmail("wilsongantt@yahoo.com");
	        user.setPhoneNumber("803-237-9696");
	        user.setHireDate(LocalDate.now());
	        
	        userDAO.insertInitialData(user);
	        PasswordUtil.writeCredentialsToDesktop(user);
	        user = userDAO.insertInitialData(null);
	        PasswordUtil.writeCredentialsToDesktop(user);
	        message = "success";
		} catch (Exception exception) {
			StringWriter errorOut = new StringWriter();
	        exception.printStackTrace(new PrintWriter(errorOut));
	        
	        message = "bombed \n\n" + errorOut.toString();
		}
		
		return message;
	}
}
