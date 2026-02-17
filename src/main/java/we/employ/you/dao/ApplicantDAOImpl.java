package we.employ.you.dao;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.hibernate.LobHelper;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import we.employ.you.exception.MissingDataException;
import we.employ.you.exception.ResourceException;
import we.employ.you.exception.ValidationException;
import we.employ.you.model.Applicant;
import we.employ.you.model.ApplicantFile;
import we.employ.you.model.Company;
import we.employ.you.model.Contact;
import we.employ.you.model.Interview;
import we.employ.you.model.User;

/**
 * This DAO initiates transactions by selecting, inserting, updating and deleting
 * applicant data from the database.
 */
@Repository
public class ApplicantDAOImpl implements ApplicantDAO {

	private final SessionFactory sessionFactory;

	@Autowired
    public ApplicantDAOImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    /**
     * Returns a <code>Applicant</code> from the database by using the given applicant ID.
     * @return an <code>ObservableList</code> of applicants
     */
	@Override
    public List<Applicant> getApplicants() {
        Session session = this.sessionFactory.getCurrentSession();

        String stringBuilder = "select new map(applicant.applicantId as applicantId, " +
                "applicant.firstName as firstName, " +
                "applicant.lastName as lastName, " +
                "applicant.jobTitle as jobTitle, " +
                "applicant.email as email, " +
                "applicant.phoneNumber as phoneNumber, " +
                "recruiter.userId as recruiterId, " +
                "recruiter.firstName as recruiterFirstName, " +
                "recruiter.lastName as recruiterLastName, " +
                "employer.companyId as employerId, " +
                "employer.companyName as employerName, " +
                "applicant.contractToHireIndicator as contractToHireIndicator, " +
                "applicant.hireDate as hireDate) " +
                "from Applicant applicant" +
                "	inner join applicant.recruiter recruiter" +
                "	left join applicant.employer employer " +
                "order by firstName, lastName";

        @SuppressWarnings("unchecked")
        Query<Map<String, Object>> query = session.createQuery(stringBuilder);

        List<Map<String, Object>> results = query.list();

        List<Applicant> applicants = new ArrayList<>();
        Applicant applicant;

        for (Map<String, Object> result : results) {
        	applicant = new Applicant();
        	applicant.setApplicantId((int) result.get("applicantId"));
        	applicant.setFirstName(String.valueOf(result.get("firstName")));
        	applicant.setLastName(String.valueOf(result.get("lastName")));
        	applicant.setJobTitle(String.valueOf(result.get("jobTitle")));
        	applicant.setEmail(String.valueOf(result.get("email")));
        	applicant.setPhoneNumber(String.valueOf(result.get("phoneNumber")));
        	applicant.setRecruiter(new User(String.valueOf(result.get("recruiterId")),
        		String.valueOf(result.get("recruiterFirstName")),
        			String.valueOf(result.get("recruiterLastName"))));

        	if (result.get("employerId") != null) {
	        	applicant.setEmployer(new Company((int) result.get("employerId"),
	        			String.valueOf(result.get("employerName"))));
        	} else {
        		applicant.setEmployer(new Company(0, "N/A"));
        	}

        	applicant.setContractToHireIndicator((boolean) result.get("contractToHireIndicator"));
        	applicant.setHireDate((LocalDate) result.get("hireDate"));

        	applicants.add(applicant);
        }

        return applicants;
    }

    /**
     * Returns the <code>Applicant</code> from the database with the given applicant ID.
     * @param applicantId used to collect his/her applicant data
     * @return applicants
     */
	@Override
    public Applicant getApplicant(int applicantId) {
        Session session = this.sessionFactory.getCurrentSession();

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("select new map(applicant.applicantId as applicantId, ");
        stringBuilder.append("applicant.firstName as firstName, ");
        stringBuilder.append("applicant.lastName as lastName, ");
        stringBuilder.append("applicant.jobTitle as jobTitle, ");
        stringBuilder.append("applicant.email as email, ");
        stringBuilder.append("applicant.phoneNumber as phoneNumber, ");
        stringBuilder.append("recruiter.userId as recruiterId, ");
        stringBuilder.append("recruiter.firstName as recruiterFirstName, ");
        stringBuilder.append("recruiter.lastName as recruiterLastName, ");
        stringBuilder.append("employer.companyId as employerId, ");
        stringBuilder.append("employer.companyName as employerName, ");
        stringBuilder.append("applicant.contractToHireIndicator as contractToHireIndicator, ");
        stringBuilder.append("applicant.hireDate as hireDate) ");
        stringBuilder.append("from Applicant applicant");
        stringBuilder.append("	inner join applicant.recruiter recruiter");
        stringBuilder.append("	left join applicant.employer employer");
        stringBuilder.append("	where applicant.applicantId = :applicantId");

        @SuppressWarnings("unchecked")
        Query<Map<String, Object>> query = session.createQuery(stringBuilder.toString());
        query.setParameter("applicantId", applicantId);

        Map<String, Object> applicantResult = query.uniqueResult();

        Applicant applicant = new Applicant();
        applicant.setApplicantId((int) applicantResult.get("applicantId"));
    	applicant.setFirstName(String.valueOf(applicantResult.get("firstName")));
    	applicant.setLastName(String.valueOf(applicantResult.get("lastName")));
    	applicant.setJobTitle(String.valueOf(applicantResult.get("jobTitle")));
    	applicant.setEmail(String.valueOf(applicantResult.get("email")));
    	applicant.setPhoneNumber(String.valueOf(applicantResult.get("phoneNumber")));
    	applicant.setRecruiter(new User(String.valueOf(applicantResult.get("recruiterId")),
    		String.valueOf(applicantResult.get("recruiterFirstName")),
    			String.valueOf(applicantResult.get("recruiterLastName"))));

    	if (applicantResult.get("employerId") != null) {
        	applicant.setEmployer(new Company((int) applicantResult.get("employerId"),
        			String.valueOf(applicantResult.get("employerName"))));
    	}

    	applicant.setContractToHireIndicator((boolean) applicantResult.get("contractToHireIndicator"));
    	applicant.setHireDate((LocalDate) applicantResult.get("hireDate"));

        stringBuilder.setLength(0);
        stringBuilder.append("select new map(interview.interviewId as interviewId, ");
        stringBuilder.append("company.companyId as companyId, ");
        stringBuilder.append("company.companyName as companyName, ");
        stringBuilder.append("interview.jobPosition as jobPosition, ");
        stringBuilder.append("contact.contactId as contactId, ");
        stringBuilder.append("contact.firstName as firstName, ");
        stringBuilder.append("contact.lastName as lastName, ");
        stringBuilder.append("interview.interviewDate as interviewDate, ");
        stringBuilder.append("interview.attendInterviewIndicator as attendInterviewIndicator, ");
        stringBuilder.append("interview.successfulHireIndicator as successfulHireIndicator) ");
        stringBuilder.append("from Interview interview");
        stringBuilder.append("	inner join interview.company company");
        stringBuilder.append("	inner join interview.contact contact");
        stringBuilder.append("	where interview.applicant.applicantId = :applicantId");

        @SuppressWarnings("unchecked")
        Query<Map<String, Object>> interviewQuery = session.createQuery(stringBuilder.toString());
        interviewQuery.setParameter("applicantId", applicantId);

        List<Map<String, Object>> results = interviewQuery.list();

        Interview interview;
        List<Interview> interviews = new ArrayList<>();
        for (Map<String, Object> result : results) {
        	interview = new Interview();
        	interview.setInterviewId((int) result.get("interviewId"));
        	interview.setCompany(new Company((int) result.get("companyId"), String.valueOf(result.get("companyName"))));
        	interview.setJobPosition(String.valueOf(result.get("jobPosition")));
        	interview.setContact(new Contact((int) result.get("contactId"), String.valueOf(result.get("firstName")),
        			String.valueOf(result.get("lastName"))));
        	interview.setInterviewDate((LocalDateTime) result.get("interviewDate"));
        	interview.setAttendInterviewIndicator((boolean) (result.get("attendInterviewIndicator")));
        	interview.setSuccessfulHireIndicator((boolean) (result.get("successfulHireIndicator")));

        	interviews.add(interview);
        }

        applicant.setInterviews(interviews);

        applicant.setApplicantFiles(this.getApplicantFiles(applicantId));

        return applicant;
    }

    /**
     * Saves a <code>Applicant</code> into the database.
     * @param applicant the applicant to save in the database
     * @return the newly inserted applicant ID
     */
    @Override
    public int saveApplicant(Applicant applicant) {
        Session session = sessionFactory.getCurrentSession();

		applicant.setBeginDate(LocalDateTime.now());

        return (int) session.save(applicant);
    }

    /**
     * Updates a <code>Applicant</code> in the database.
     * @param applicant the applicant to update in the database
     */
    @Override
    public void updateApplicant(Applicant applicant) throws ValidationException {
    	Session session = sessionFactory.getCurrentSession();

    	if (applicant.getEmployer() != null && !applicant.getEmployer().getCompanyName().isBlank()) {
    		if (applicant.getInterviews().isEmpty()) {
    			throw new ValidationException("There must be at least one interview before saving an employer.");
    		}

            Interview applicantInterview = applicant.getInterviews().stream().filter(
                interview -> interview.getCompany().equals(applicant.getEmployer())).findAny().orElse(null);

            if (applicantInterview != null) {
            	if (applicant.getHireDate() != null) {
					if (applicant.getHireDate().isBefore(applicantInterview.getInterviewDate().toLocalDate())) {
            			throw new ValidationException("Hire Date cannot be before the interview date.");
					} else if (applicantInterview.getInterviewDate().isAfter(LocalDateTime.now())) {
						throw new ValidationException("Hire Date cannot be set if interview date is a future date.");
					}
            	}

                applicantInterview.setAttendInterviewIndicator(true);
                applicantInterview.setSuccessfulHireIndicator(true);

                session.update(applicantInterview);

                applicant.setEmployer(applicantInterview.getCompany());
            } else {
				throw new ValidationException("No interviews found by the given company name.");
			}
        } else if (applicant.getEmployer().getCompanyName().isBlank()) {
			applicant.setEmployer(null);
		}

        session.update(applicant);
    }

    /**
     * Deletes a <code>Applicant</code> from the database.
     * @param applicantId the customer ID used to delete the applicant from the database
     * @throws MissingDataException if the customer cannot be found with the given ID
     */
    @Override
    public void deleteApplicant(int applicantId) throws MissingDataException {
    	Session session = sessionFactory.getCurrentSession();

    	Applicant applicant = session.get(Applicant.class, applicantId);

    	if (applicant == null) {
    		throw new MissingDataException("A applicant with ID " + applicantId +
                    " not was found in the database.");
    	}

    	session.delete(applicant);
    }

    /**
     * Saves an applicant's resume into the database
     * @param applicantFile the applicant who owns the resume
     */
    @Override
    public void saveApplicantFile(ApplicantFile applicantFile) {
    	Session session = sessionFactory.getCurrentSession();

    	//Keeping only one resume in the database
    	if (applicantFile.isResumeIndicator()) {
    		@SuppressWarnings("unchecked")
			Query<Integer> query = session.createQuery(
				"select fileId from ApplicantFile" +
				"	where applicant = :applicant" +
				"	and resumeIndicator is true");
    		query.setParameter("applicant", applicantFile.getApplicant());

    		if (query.uniqueResult() != null) {
				applicantFile.setFileId(query.uniqueResult());
				session.update(applicantFile);
    		} else {
    			session.save(applicantFile);
    		}
    	} else {
    		session.save(applicantFile);
    	}
    }

    /**
     *  Downloads the applicant's resume to the user's desktop
     * @param fileId the <code>Applicant</code> who owns the resume
     * @return fileName the resume's file name
     */
    @Override
    public byte[] downloadApplicantFile(int fileId) throws IOException, MissingDataException {
        Session session = sessionFactory.getCurrentSession();

        ApplicantFile applicantFile = session.get(ApplicantFile.class, fileId);

        if (applicantFile == null) {
        	throw new MissingDataException("The applicant file with ID " + fileId + " was not found.");
        }

        return applicantFile.getFile();
    }

    @Override
    public List<ApplicantFile> getApplicantFiles(int applicantId) {
    	List<ApplicantFile> applicantFiles = new ArrayList<>();

    	Session session = sessionFactory.getCurrentSession();

    	@SuppressWarnings("unchecked")
		Query<Map<String, Object>> query = session.createQuery(
			"select new map(file.fileId as fileId, "
			+ "file.fileName as fileName, "
			+ "file.resumeIndicator as resumeIndicator) "
			+ "from ApplicantFile file"
			+ "	where file.applicant.applicantId = :applicantId");

    	query.setParameter("applicantId", applicantId);

        List<Map<String, Object>> results = query.list();

        if (!results.isEmpty()) {
	        results.forEach(result -> {
	        	applicantFiles.add(new ApplicantFile((int) result.get("fileId"),
	        			String.valueOf(result.get("fileName")), (boolean) result.get("resumeIndicator")));
	        });
        }

    	return applicantFiles;
    }

    @Override
    public void deleteApplicantFile(int fileId) {
    	Session session = sessionFactory.getCurrentSession();

    	Query<?> query =
    		session.createQuery("delete from ApplicantFile where fileId = :fileId");
    	query.setParameter("fileId", fileId);
    	query.executeUpdate();
    }

	@Override
	public byte[] getApplicantPhoto(int applicantId) {
		byte[] photo = null;

		Session session = sessionFactory.getCurrentSession();

		@SuppressWarnings("unchecked")
		Query<byte[]> photoQuery = session.createQuery(
			"select applicantPhoto from Applicant where applicantId = :applicantId");
		photoQuery.setParameter("applicantId", applicantId);

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
	public void saveApplicantPhoto(int applicantId, byte[] photo) {
		Session session = sessionFactory.getCurrentSession();

		Query<?> query = session.createQuery(
				"update Applicant "
				+ "set applicantPhoto = :photo"
				+ "	where applicantId = :applicantId");
		query.setParameter("photo", photo);
		query.setParameter("applicantId", applicantId);
		query.executeUpdate();
	}
}
