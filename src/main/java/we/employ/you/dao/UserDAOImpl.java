package we.employ.you.dao;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jakarta.persistence.EntityManager;
import org.hibernate.LobHelper;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import we.employ.you.exception.InvalidDataException;
import we.employ.you.exception.ResourceException;
import we.employ.you.exception.ValidationException;
import we.employ.you.model.Applicant;
import we.employ.you.model.Company;
import we.employ.you.model.CompanyInterviews;
import we.employ.you.model.Contact;
import we.employ.you.model.Interview;
import we.employ.you.model.User;
import we.employ.you.model.UserPassword;
import we.employ.you.model.UserRole;
import we.employ.you.util.PasswordUtil;

/**
 * This DAO returns, saves, updates, and deletes <code>User</code> data from the database.
 */
@Repository
public class UserDAOImpl implements UserDAO {

	private final EntityManager entityManager;

	@Autowired
    public UserDAOImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public User insertInitialData(User user) throws Exception {
        LocalDateTime creationDate = LocalDateTime.now();
        LocalDate now = creationDate.toLocalDate();

		Session session = this.entityManager.unwrap(Session.class);

        if (user != null) {
        	session.save(user);

        	UserRole role = new UserRole(user, "MAN");
	        session.save(role);

	        UserPassword password = new UserPassword();
	        password.setUserId("WEU00001");
	        password.setPassword(new PasswordUtil().encryptPassword(PasswordUtil.generatePassword()));
	        password.setCreationDate(creationDate);
	        password.setCurrentPasswordIndicator(true);
	        password.setTemporaryPasswordIndicator(true);
	        password.setTemporaryPasswordExpirationDate(creationDate.plusDays(1));
	        session.save(password);

	        user.setUserRole(role);
	        user.setCurrentPassword(password);
        } else {
            //Inserting the first recruiter in the database
            user = new User();
            user.setUserId("WEU00002");
            user.setFirstName("Abi");
            user.setLastName("Gantt");
            user.setEmail("abi@yahoo.com");
            user.setPhoneNumber("803-555-1234");
            user.setHireDate(LocalDate.now());

            String userId = String.valueOf(session.save(user));

            UserRole role = new UserRole(user, "REC");
	        session.save(role);

            UserPassword password = new UserPassword();
	        password.setUserId("WEU00002");
	        password.setPassword(new PasswordUtil().encryptPassword(PasswordUtil.generatePassword()));
	        password.setCreationDate(creationDate);
	        password.setCurrentPasswordIndicator(true);
	        password.setTemporaryPasswordIndicator(true);
	        password.setTemporaryPasswordExpirationDate(creationDate.plusDays(1));
	        session.save(password);

	        user.setUserRole(role);
	        user.setCurrentPassword(password);

            int firstInterviewId = 0;
            int secondInterviewId = 0;
            int thirdInterviewId = 0;

            //Inserting the first company into the database
            int companyId = (int) session.save(new Company("GANTLANTIS HEADQUARTERS"));

            Applicant firstApplicant = new Applicant();
            firstApplicant.setFirstName("Zack");
            firstApplicant.setLastName("Williams");
            firstApplicant.setRecruiter(new User(userId));
            firstApplicant.setJobTitle("Accountant");
            firstApplicant.setPhoneNumber("803-781-0347");
            firstApplicant.setEmail("zack.williams@gmail.com");
            firstApplicant.setBeginDate(creationDate);
            int applicantId = (int) session.save(firstApplicant);
            firstApplicant.setApplicantId(applicantId);

            Applicant secondApplicant = new Applicant();
            secondApplicant.setFirstName("Nuu");
            secondApplicant.setLastName("Kieadu");
            secondApplicant.setRecruiter(new User(userId));
            secondApplicant.setJobTitle("Ninja");
            secondApplicant.setPhoneNumber("803-749-9733");
            secondApplicant.setEmail("nuu.kieadu@gmail.com");
            secondApplicant.setBeginDate(creationDate);
            secondApplicant.setHireDate(now.plusDays(4));
            secondApplicant.setEmployer(new Company(companyId));
            applicantId = (int) session.save(secondApplicant);
            secondApplicant.setApplicantId(applicantId);

            Applicant thirdApplicant = new Applicant();
            thirdApplicant.setFirstName("Amy");
            thirdApplicant.setLastName("Smith");
            thirdApplicant.setRecruiter(new User(userId));
            thirdApplicant.setJobTitle("Landscaper");
            thirdApplicant.setPhoneNumber("803-555-1234");
            thirdApplicant.setEmail("amy_smith@aol.com");
            thirdApplicant.setBeginDate(creationDate);
            thirdApplicant.setHireDate(now.plusDays(5));
            thirdApplicant.setContractToHireIndicator(true);
            thirdApplicant.setEmployer(new Company(companyId));
            applicantId = (int) session.save(thirdApplicant);
            thirdApplicant.setApplicantId(applicantId);

            Contact contact = new Contact();
            contact.setFirstName("Eobard");
            contact.setLastName("Thawne");
            contact.setEmail("reverse_flash@injustice_league.com");
            contact.setCompany(new Company(companyId));
            int contactId = (int) session.save(contact);

            Interview firstInterview = new Interview();
            firstInterview.setApplicant(new Applicant(firstApplicant.getApplicantId()));
            firstInterview.setCompany(new Company(companyId));
            firstInterview.setJobPosition("Accountant");
            firstInterview.setContact(new Contact(contactId));

            if (creationDate.getDayOfWeek().equals(DayOfWeek.FRIDAY)) {
            	firstInterview.setInterviewDate(creationDate.plusDays(3));
            } else if (creationDate.getDayOfWeek().equals(DayOfWeek.SATURDAY)) {
            	firstInterview.setInterviewDate(creationDate.plusDays(2));
            } else {
            	firstInterview.setInterviewDate(creationDate.plusDays(1));
            }

            firstInterviewId = (int) session.save(firstInterview);

            Interview secondInterview = new Interview();
            secondInterview.setApplicant(new Applicant(secondApplicant.getApplicantId()));
            secondInterview.setCompany(new Company(companyId));
            secondInterview.setJobPosition("Assassin");
            secondInterview.setContact(new Contact(contactId));

            if (creationDate.getDayOfWeek().equals(DayOfWeek.FRIDAY)) {
            	secondInterview.setInterviewDate(creationDate.plusDays(3));
            } else if (creationDate.getDayOfWeek().equals(DayOfWeek.SATURDAY)) {
            	secondInterview.setInterviewDate(creationDate.plusDays(2));
            } else {
            	secondInterview.setInterviewDate(creationDate.plusDays(1));
            }

            secondInterviewId = (int) session.save(secondInterview);

            Interview thirdInterview = new Interview();
            thirdInterview.setApplicant(new Applicant(thirdApplicant.getApplicantId()));
            thirdInterview.setCompany(new Company(companyId));
            thirdInterview.setJobPosition("Web Developer");
            thirdInterview.setContact(new Contact(contactId));

            if (creationDate.getDayOfWeek().equals(DayOfWeek.FRIDAY)) {
            	thirdInterview.setInterviewDate(creationDate.plusDays(3));
            } else if (creationDate.getDayOfWeek().equals(DayOfWeek.SATURDAY)) {
            	thirdInterview.setInterviewDate(creationDate.plusDays(2));
            } else {
            	thirdInterview.setInterviewDate(creationDate.plusDays(1));
            }

            thirdInterviewId = (int) session.save(thirdInterview);

            session.save(new CompanyInterviews(firstInterviewId, companyId, contactId));
            session.save(new CompanyInterviews(secondInterviewId, companyId, contactId));
            session.save(new CompanyInterviews(thirdInterviewId, companyId, contactId));
        }

        return user;
    }

    @Override
    public List<User> getUsers() {
        String sqlQuery = "select new map(user.userId as userId, user.firstName as firstName, user.lastName as lastName, " +
                "user.phoneNumber as phoneNumber, user.email as email, " +
                "user.hireDate as hireDate, user.terminatedDate as terminatedDate, " +
                "userRole.userRole as userRole) " +
                "from User user" +
                "	inner join user.userRole userRole " +
                "order by userId";

		Session session = this.entityManager.unwrap(Session.class);
        @SuppressWarnings("unchecked")
		Query<Map<String, Object>> query = session.createQuery(sqlQuery);

        List<Map<String, Object>> results = query.list();
        List<User> users = new ArrayList<>();
        User user;

        for (Map<String, Object> result : results) {
        	user = new User(String.valueOf(result.get("userId")), String.valueOf(result.get("firstName")),
        			String.valueOf(result.get("lastName")));

        	user.setPhoneNumber(String.valueOf(result.get("phoneNumber")));
        	user.setEmail(String.valueOf(result.get("email")));

        	if (result.get("hireDate") != null) {
        		user.setHireDate((LocalDate) result.get("hireDate"));
        	}

        	if (result.get("terminatedDate") != null) {
        		user.setTerminatedDate((LocalDate) result.get("terminatedDate"));
        	}

        	user.setUserRole(new UserRole(user, String.valueOf(result.get("userRole"))));

        	users.add(user);
        }

        return users;
    }

    @Override
    public User getUser(String userId) {
    	User user = null;

        String sqlQuery = "select new map(user.firstName as firstName, user.lastName as lastName, " +
                "password.password as password, password.creationDate as creationDate, " +
                "password.temporaryPasswordIndicator as temporaryPasswordIndicator, " +
                "password.temporaryPasswordExpirationDate as temporaryPasswordExpirationDate, " +
                "user.hireDate as hireDate, user.terminatedDate as terminatedDate, " +
                "userRole.userRole as userRole) " +
                "from User user" +
                "	inner join user.userRole userRole" +
                "	inner join user.passwords password" +
                //Should only be one
                "		with password.currentPasswordIndicator is true" +
                "	where user.userId = :userId";

		Session session = this.entityManager.unwrap(Session.class);

        @SuppressWarnings("unchecked")
		Query<Map<String, Object>> query = session.createQuery(sqlQuery);
        query.setParameter("userId", userId);

        List<Map<String, Object>> results = query.list();

        //There should only be one current password
        if (results.size() > 1) {
        	throw new InvalidDataException("The query returned multiple records "
                    + "when only one record was expected.");
        }

        if (!results.isEmpty()) {
        	Map<String, Object> result = results.get(0);

	        user = new User(userId, String.valueOf(result.get("firstName")),
	        		String.valueOf(result.get("lastName")));

	    	if (result.get("hireDate") != null) {
	    		user.setHireDate((LocalDate) result.get("hireDate"));
	    	}

	    	if (result.get("terminatedDate") != null) {
	    		user.setTerminatedDate((LocalDate) result.get("terminatedDate"));
	    	}

	    	user.setUserRole(new UserRole(user, String.valueOf(result.get("userRole"))));

	    	UserPassword password = new UserPassword();
	    	password.setUser(user);
	    	password.setPassword(String.valueOf(result.get("password")));
	    	password.setCreationDate((LocalDateTime) result.get("creationDate"));
	    	password.setCurrentPasswordIndicator(true);
	    	password.setTemporaryPasswordIndicator((boolean) result.get("temporaryPasswordIndicator"));

	    	if (result.get("temporaryPasswordExpirationDate") != null) {
	    		password.setTemporaryPasswordExpirationDate(
	    				(LocalDateTime) result.get("temporaryPasswordExpirationDate"));
	    	}

	    	user.setCurrentPassword(password);
        }

        return user;
    }

    @Override
    @SuppressWarnings("unchecked")
	public User getPersistedUser(String userId) {
    	User user = new User();

		Session session = this.entityManager.unwrap(Session.class);

		Query<Map<String, Object>> query = session.createQuery(
			"select new map(user.firstName as firstName, " +
			"user.lastName as lastName, user.phoneNumber as phoneNumber, " +
			"user.email as email, user.hireDate as hireDate, " +
			"user.terminatedDate as terminatedDate, userRole.userRole as userRole) " +
			"from User user " +
			"	inner join user.userRole userRole " +
			"	where user.userId = :userId");
		query.setParameter("userId", userId);

		Map<String, Object> result = query.uniqueResult();

		user.setUserId(userId);
		user.setFirstName(String.valueOf(result.get("firstName")));
		user.setLastName(String.valueOf(result.get("lastName")));
		user.setPhoneNumber(String.valueOf(result.get("phoneNumber")));
		user.setEmail(String.valueOf(result.get("email")));
		user.setHireDate((LocalDate) result.get("hireDate"));
		user.setTerminatedDate((LocalDate) result.get("terminatedDate"));
		user.setUserRole(new UserRole(user, String.valueOf(result.get("userRole"))));

    	query = session.createQuery(
			"select new map(applicant.applicantId as applicantId, " +
			"applicant.firstName as firstName, applicant.lastName as lastName, " +
			"applicant.jobTitle as jobTitle, " +
			"case when employer.companyName is not null then employer.companyName " +
			"else 'Not Employed' end as companyName)" +
			"from Applicant applicant " +
			"	left join applicant.employer employer" +
			"	where applicant.recruiter.userId = :userId");
    	query.setParameter("userId", userId);

    	List<Map<String, Object>> results = query.list();
		List<Applicant> applicants = new ArrayList<>();
		Applicant applicant;

    	for (Map<String, Object> applicantData : results) {
			applicant = new Applicant((int) applicantData.get("applicantId"),
    			String.valueOf(applicantData.get("firstName")), String.valueOf(applicantData.get("lastName")));
    		applicant.setJobTitle(String.valueOf(applicantData.get("jobTitle")));
    		applicant.setEmployer(new Company(String.valueOf(applicantData.get("companyName"))));
    		applicants.add(applicant);
    	}

		user.setApplicants(applicants);

    	return user;
    }

    @Override
	public byte[] getEmployeePhoto(String userId) {
		byte[] photo = null;

		Session session = this.entityManager.unwrap(Session.class);

		@SuppressWarnings("unchecked")
		Query<byte[]> photoQuery = session.createQuery(
			"select userPhoto from User where userId = :userId");
		photoQuery.setParameter("userId", userId);

		LobHelper lobHelper = session.getLobHelper();

		if (photoQuery.uniqueResult() != null) {
			Blob blob = lobHelper.createBlob(photoQuery.uniqueResult());
			InputStream inputStream = null;
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

			try {
				inputStream = blob.getBinaryStream();

				byte[] buffer = new byte[10240];

				int bytesRead = 0;

				while ((bytesRead = inputStream.read(buffer)) != -1) {
					outputStream.write(buffer, 0, bytesRead);
				}
			} catch (IOException | SQLException exception) {
				throw new ResourceException("An unexpected error occurred while "
						+ "trying to acquire an BLOB input stream", exception);
			} finally {
				if (inputStream != null) {
					try {
						inputStream.close();
					} catch (IOException exception) {
						throw new ResourceException("An unexpected error occurred while "
								+ "trying to acquire an BLOB input stream", exception);
					}
				}
			}

			photo = outputStream.toByteArray();
		}

		return photo;
	}

	@Override
	public void saveEmployeePhoto(String userId, byte[] photo) {
		Session session = this.entityManager.unwrap(Session.class);

		Query<?> query = session.createQuery(
				"update User "
				+ "set userPhoto = :photo"
				+ "	where userId = :userId");
		query.setParameter("photo", photo);
		query.setParameter("userId", userId);
		query.executeUpdate();
	}

    @Override
    public List<User> getRecruiters() {
    	List<User> recruiters = new ArrayList<>();

        String sqlQuery = "select new map(user.userId as userId, " +
                "user.firstName as firstName, " +
                "user.lastName as lastName) " +
                "from User user " +
                "	inner join user.userRole userRole" +
                "	where userRole.userRole = 'REC'";

		Session session = this.entityManager.unwrap(Session.class);
    	@SuppressWarnings("unchecked")
		Query<Map<String, Object>> query = session.createQuery(sqlQuery);
    	List<Map<String, Object>> results = query.list();

    	if (!results.isEmpty()) {
    		User recruiter;
    		for (Map<String, Object> result : results) {
    			recruiter = new User();
    			recruiter.setUserId((String.valueOf(result.get("userId"))));
    			recruiter.setFirstName((String.valueOf(result.get("firstName"))));
    			recruiter.setLastName((String.valueOf(result.get("lastName"))));

    			recruiters.add(recruiter);
    		}

    	}

    	return recruiters;
    }

    @Override
    public List<UserPassword> getUserPasswords(String userId) {
		Session session = this.entityManager.unwrap(Session.class);
        @SuppressWarnings("unchecked")
		Query<UserPassword> query = session.createQuery(
				"from UserPassword password"
				+ " where password.userId = :userId"
				+ " order by password.creationDate desc");
        query.setParameter("userId", userId);

        return query.list();
    }

	@Override
    public void resetPassword(UserPassword userPassword) {
		Session session = this.entityManager.unwrap(Session.class);

        //Deleting the oldest password within the history to keep the number
        //of saved passwords at 12
        if (userPassword.isDeleteOldestPassword()) {
			@SuppressWarnings("unchecked")
			Query<LocalDateTime> query = session.createQuery(
				"select min(creationDate) " +
				"from UserPassword" +
				"	where userId = :userId");
			query.setParameter("userId", userPassword.getUserId());
			LocalDateTime creationDate = query.uniqueResult();

            Query<?> deleteQuery = session.createQuery(
            		"delete from UserPassword"
            		+ "	where userId = :userId "
            		+ "		and creationDate = :creationDate");
            deleteQuery.setParameter("userId", userPassword.getUserId());
            deleteQuery.setParameter("creationDate", creationDate);
            deleteQuery.executeUpdate();
        }

        //Updating the current indicator status of the latest password to N
        Query<?> query = session.createQuery(
        	"update UserPassword set currentPasswordIndicator = false"
        	+ " where currentPasswordIndicator is true"
        	+ "	and userId = :userId");
        query.setParameter("userId", userPassword.getUserId()).executeUpdate();

        //Saving the new password
        session.save(userPassword);
    }

    @Override
    public String saveUser(User user) throws ValidationException {
    	String userId = null;

		Session session = this.entityManager.unwrap(Session.class);

        if (user.getUserId() == null) {
            //Building the new user ID. The initial ID is WEU00001. The subsequent
            //ID's will be WEU00002, WEU00003, etc.
            @SuppressWarnings("unchecked")
			Query<String> query = session.createQuery("select max(userId) from User user");

            userId = query.uniqueResult();

            //Adding one to increment to the next user ID
            String idNumber = String.valueOf(Integer.parseInt(userId.substring(3)) + 1);

            userId = "WEU";

            //Adding zeros before the number
            for (int count = idNumber.length(); count < 5; count++) {
                userId = userId + "0";
            }

            userId = userId + idNumber;

            user.setUserId(userId);
            user.getUserRole().setUser(user);

            session.save(user);

            user.setUserId(userId);
            user.getUserRole().setUser(user);

            session.save(user.getUserRole());
        } else {
        	//Preventing the overwriting of the manager user
        	//role for the very first user
        	@SuppressWarnings("unchecked")
			Query<Long> query = session.createQuery(
					"select count(userId) from User user"
					+ "	where user.userRole.userRole = 'MAN'");

            if (query.uniqueResult() == 1 && user.getUserRole().getUserRole().equals(UserRole.RECRUITER_USER_ROLE) &&
            		user.getUserId().equals("WEU00001")) {
                throw new ValidationException("There must be at least one " +
                    "manager in the system.");
            }

			user.getUserRole().setUser(user);
			session.update(user.getUserRole());
            session.update(user);
        }

        return userId;
    }

    @Override
    public void deleteUser(String userId) throws ValidationException {
		Session session = this.entityManager.unwrap(Session.class);
    	//Preventing the deletion if there are still applicants associated
        //with this user (recruiter)
    	@SuppressWarnings("unchecked")
		Query<Applicant> query = session.createQuery(
				"from Applicant where recruiter.userId = :userId");
        query.setParameter("userId", userId);

        if (!query.list().isEmpty()) {
            throw new ValidationException("The applicants for this recruiter " +
                "must be transferred to another recruiter before deleting.");
        }

        User user = session.get(User.class, userId);

        session.delete(user);
    }
}
