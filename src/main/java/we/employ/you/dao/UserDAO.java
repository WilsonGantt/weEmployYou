package we.employ.you.dao;

import java.sql.SQLException;
import java.util.List;

import we.employ.you.exception.ValidationException;

import we.employ.you.model.User;
import we.employ.you.model.UserPassword;

/**
 * This DAO returns, saves, updates, and deletes <code>User</code> data from the database.
 */
public interface UserDAO {

    public User insertInitialData(User user) throws Exception;

    public List<User> getUsers();

    public User getUser(String userId);
    
    public List<User> getRecruiters();
    
    public User getPersistedUser(String userId);
    
    public byte[] getEmployeePhoto(String userId);

    public void saveEmployeePhoto(String userId, byte[] photo);

    public List<UserPassword> getUserPasswords(String userId);

    public void resetPassword(UserPassword userPassword);

    public String saveUser(User user) throws ValidationException;

    public void deleteUser(String userId) throws ValidationException;
}
