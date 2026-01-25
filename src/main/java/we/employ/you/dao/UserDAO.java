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
    
    /**
     * Inserts an initial set of data into the database during the initial login
     * to the application.
     * @param connection the database connection used to count the data.
     * @param user
     * @return
     * @throws Exception if an error occurs during the transaction
     */
    public User insertInitialData(User user) throws Exception;
    
    /**
     * Returns an <code>ObservableList</code> of users from the database.
     * @param connection the database connection used to retrieve the user data
     * from the database
     * @param onlyRecruiters used to determine if only recruiters should be in
     * the collection
     * @return an <code>ObservableList</code> of users
     * @throws SQLException if an error occurs while accessing the database
     */
    public List<User> getUsers();
    
    /**
     * Returns a <code>User</code> with the provided user ID.
     * @param connection the database connection used to retrieve the user data
     * from the database
     * @param userId the user ID of the user from the database
     * @return a <code>User</code> from the database
     * @throws Exception thrown if the data is in an unexpected and invalid state,
     * or if the <code>User</code> data cannot be found in the database, of if
     * another unexpected database error occurs during the transaction
     */
    public User getUser(String userId);
    
    public List<User> getRecruiters();
    
    public User getPersistedUser(String userId);
    
    public byte[] getEmployeePhoto(String userId);

    public void saveEmployeePhoto(String userId, byte[] photo);
    
    /**
     * Returns a <code>List</code> of the logged in user's passwords from the database.
     * @param connection the database connection used to retrieve the user data
     * @param userId
     * @return userPasswords
     * @throws SQLException if an error occurs during the transaction
     */
    public List<UserPassword> getUserPasswords(String userId);
    
    /**
     * Resets the user's password to either a temporary password of a user created password.
     * @param connection the connection used to execute the update
     * @param userId the ID of the user with the updated password
     * @param password the newly created password which is encrypted
     * @param isTemporaryPassword determines if it is a temporary password
     * @param delete determines if the oldest password should be deleted.
     * @throws Exception if an error occurs during the transaction
     */
    public void resetPassword(UserPassword userPassword);
    
    /**
     * Insert a new <code>User</code> it the related data into the database.
     * @param connection the connection used the execute the transaction
     * @param user the user being inserted into the database
     * @throws SQLException if a database error occurs during the transaction
     * @throws ValidationException if a validation error occurs during the transaction
     */
    public String saveUser(User user) throws ValidationException;
    
    /**
     * Deletes the <code>User</code> from the database
     * @param connection the connection used the execute the transaction
     * @param userId the ID of the user being deleted
     * @throws SQLException if a database error occurs during the transaction
     * @throws ValidationException  if a validation error occurs during the transaction
     */
    public void deleteUser(String userId) throws ValidationException;
}
