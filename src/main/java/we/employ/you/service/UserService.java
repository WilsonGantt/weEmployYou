package we.employ.you.service;

import java.util.List;

import we.employ.you.exception.ValidationException;
import we.employ.you.model.User;

public interface UserService {

	public List<User> getUsers();
    
    public User getUser(String userId);
    
    public User getPersistedUser(String userId);
    
    public byte[] getEmployeePhoto(String userId);

	public void saveEmployeePhoto(String userId, byte[] photo);
    
    public boolean resetPassword(User user) throws Exception;
    
    public String saveUser(User user) throws ValidationException;
    
    public void deleteUser(String userId) throws ValidationException;
}
