package we.employ.you.dao;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import we.employ.you.model.Applicant;

import we.employ.you.model.Company;
import we.employ.you.model.CompanyInterviews;
import we.employ.you.model.Interview;
import we.employ.you.model.User;
import we.employ.you.model.Contact;

/**
 * This DAO returns an <code>ObservableList</code> of interviews from the database,
 * either grabbing all interview data or interview data based on a given applicant
 * ID, or other forms of criteria. Insert, update and delete operations on interview
 * data are also invoked by this object.
 */
@Repository
public class InterviewDAOImpl implements InterviewDAO {

    private final SessionFactory sessionFactory;

    @Autowired
    public InterviewDAOImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    /**
     * Returns an <code>ObservableList</code> of all the interviews in the database.
     * @return an <code>ObservableList</code> of interviews
     */
    @Override
    public List<Interview> getInterviews() {
    	List<Interview> interviews = new ArrayList<>();

        Session session = sessionFactory.getCurrentSession();

        String stringBuilder = "SELECT INTERVIEW.INTERVIEW_ID, " +
                "APPLICANT.APPLICANT_ID, APPLICANT.FIRST_NAME AS APPLICANT_FIRST_NAME, " +
                "APPLICANT.LAST_NAME AS APPLICANT_LAST_NAME, USER.FIRST_NAME AS USER_FIRST_NAME, " +
                "USER.LAST_NAME AS USER_LAST_NAME, COMPANY.COMPANY_NAME, " +
                "CONTACT.FIRST_NAME AS CONTACT_FIRST_NAME, CONTACT.LAST_NAME AS CONTACT_LAST_NAME, " +
                "INTERVIEW_DATE, JOB_POSITION, APPLICANT_ATTEND_IND, SUCCESSFUL_HIRE_IND " +
                "FROM WE_EMPLOY_YOU.INTERVIEW INTERVIEW" +
                "	INNER JOIN WE_EMPLOY_YOU.APPLICANT APPLICANT" +
                "		ON INTERVIEW.APPLICANT_ID = APPLICANT.APPLICANT_ID" +
                "	INNER JOIN WE_EMPLOY_YOU.USER USER" +
                "		ON APPLICANT.RECRUITER_ID = USER.USER_ID" +
                "	INNER JOIN WE_EMPLOY_YOU.COMPANY COMPANY" +
                "		ON INTERVIEW.COMPANY_ID = COMPANY.COMPANY_ID" +
                "	INNER JOIN WE_EMPLOY_YOU.CONTACT CONTACT" +
                "		ON INTERVIEW.CONTACT_ID = CONTACT.CONTACT_ID " +
                "ORDER BY COMPANY.COMPANY_NAME, APPLICANT.FIRST_NAME, APPLICANT.LAST_NAME";

        @SuppressWarnings("unchecked")
        NativeQuery<Object[]> query = session.createNativeQuery(stringBuilder);

        List<Object[]> results = query.list();

        Interview interview;
        for (Object[] result : results) {
        	interview = new Interview();
        	interview.setInterviewId((int) result[0]);
        	interview.setApplicant(new Applicant((int) result[1],
        			String.valueOf(result[2]), String.valueOf(result[3])));
        	interview.getApplicant().setRecruiter(new User(String.valueOf(result[4]), String.valueOf(result[5])));
        	interview.setCompany(new Company(String.valueOf(result[6])));
        	interview.setContact(new Contact(String.valueOf(result[7]), String.valueOf(result[8])));
        	interview.setInterviewDate(((Timestamp) result[9]).toLocalDateTime());
        	interview.setJobPosition(String.valueOf(result[10]));
        	interview.setAttendInterviewIndicator(String.valueOf(result[11]).equals("Y"));
        	interview.setSuccessfulHireIndicator(String.valueOf(result[12]).equals("Y"));
        	interviews.add(interview);
        }

        return interviews;
    }

    /**
     * Returns an <code>ObservableList</code> of all the interviews in the database,
     * based on the given customer ID.
     * @return an <code>ObservableList</code> of interviews
     */
    @Override
    public List<Interview> getInterviews(int applicantId) {
    	Session session = sessionFactory.getCurrentSession();

        List<Interview> interviews = new ArrayList<>();
        Interview interview;
        Applicant applicant = session.get(Applicant.class, applicantId);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("SELECT interview.INTERVIEW_ID, company.COMPANY_ID, ");
        stringBuilder.append("COMPANY_NAME, JOB_POSITION, contact.CONTACT_ID, FIRST_NAME, LAST_NAME, ");
        stringBuilder.append("INTERVIEW_DATE, APPLICANT_ATTEND_IND, SUCCESSFUL_HIRE_IND ");
        stringBuilder.append("FROM we_employ_you.interview interview");
        stringBuilder.append("  INNER JOIN we_employ_you.company_interviews comp_interviews");
        stringBuilder.append("      ON comp_interviews.COMPANY_ID = interview.COMPANY_ID");
        stringBuilder.append("  INNER JOIN we_employ_you.company company");
        stringBuilder.append("      ON interview.COMPANY_ID = company.COMPANY_ID");
        stringBuilder.append("  INNER JOIN we_employ_you.contact contact");
        stringBuilder.append("      ON contact.CONTACT_ID = comp_interviews.CONTACT_ID");
        stringBuilder.append("  WHERE interview.APPLICANT_ID = :applicantId ");

        if (applicant.isPendingInterviewsOnly()) {
            stringBuilder.append("  AND DATE(INTERVIEW_DATE) >= :interviewDate");
        }

        @SuppressWarnings("unchecked")
		NativeQuery<Object[]> query = session.createNativeQuery(stringBuilder.toString());
		query.setParameter("applicantId", applicant.getApplicantId());

		if (applicant.isPendingInterviewsOnly()) {
			query.setParameter("interviewDate", LocalDate.now());
        }

		List<Object[]> results = query.list();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy h:mm a");

        for (Object[] result : results) {
        	interview = new Interview();
            interview.setInterviewId((int) result[0]);
            interview.setApplicant(applicant);
            interview.setCompany(new Company((int) result[1], String.valueOf(result[2])));
            interview.setJobPosition(String.valueOf(result[3]));
            interview.setContact(new Contact((int) result[4], String.valueOf(result[5]),
            		String.valueOf(result[6])));
            interview.setInterviewDate(((Timestamp) result[7]).toLocalDateTime());
            interview.setFormattedInterviewDate(formatter.format(interview.getInterviewDate()));
            interview.setAttendInterviewIndicator(String.valueOf(result[8]).equals("Y"));
            interview.setSuccessfulHireIndicator(String.valueOf(result[9]).equals("Y"));

            interviews.add(interview);
        }

        return interviews;
    }

    @Override
    public Interview getInterview(int interviewId) {
    	Session session = sessionFactory.getCurrentSession();

        String stringBuilder = "select new map(interview.interviewId as interviewId, " +
                "applicant.firstName as firstName, " +
                "applicant.lastName as lastName, " +
                "company.companyId as companyId, " +
                "company.companyName as companyName, " +
                "interview.jobPosition as jobPosition, " +
                "contact.contactId as contactId, " +
                "contact.firstName as contactFirstName, " +
                "contact.lastName as contactLastName, " +
                "interview.interviewDate as interviewDate, " +
                "interview.attendInterviewIndicator as attendInterviewIndicator, " +
                "interview.successfulHireIndicator as successfulHireIndicator) " +
                "from Interview interview" +
                "	inner join interview.applicant applicant" +
                "	inner join interview.company company" +
                "	inner join interview.contact contact" +
                "	where interview.interviewId = :interviewId";

        @SuppressWarnings("unchecked")
        Query<Map<String, Object>> query = session.createQuery(stringBuilder);
        query.setParameter("interviewId", interviewId);

        Map<String, Object> result = query.uniqueResult();

    	Interview interview = new Interview();
    	interview.setInterviewId((int) result.get("interviewId"));
        interview.setApplicant(new Applicant(String.valueOf(result.get("firstName")),
        		String.valueOf(result.get("lastName"))));
        interview.setCompany(new Company((int) result.get("companyId"),
        		String.valueOf(result.get("companyName"))));
        interview.setJobPosition(String.valueOf(result.get("jobPosition")));
        interview.setContact(new Contact((int) result.get("contactId"),
        		String.valueOf(result.get("contactFirstName")),
        		String.valueOf(result.get("contactLastName"))));
        interview.setInterviewDate(((LocalDateTime) result.get("interviewDate")));
        interview.setAttendInterviewIndicator((boolean) result.get("attendInterviewIndicator"));
        interview.setSuccessfulHireIndicator((boolean) result.get("successfulHireIndicator"));

    	return interview;
    }

    @Override
    public List<Company> getCompanies() {
    	Session session = sessionFactory.getCurrentSession();

    	@SuppressWarnings("unchecked")
		Query<Company> query = session.createQuery("from Company");

    	return query.list();
    }

    @Override
    public List<Contact> getContacts(int companyId) {
    	Session session = sessionFactory.getCurrentSession();

    	@SuppressWarnings("unchecked")
		Query<Contact> query = session.createQuery("from Contact contact where contact.company.companyId = :companyId");
    	query.setParameter("companyId", companyId);

    	return query.list();
    }

    /**
     * Saves the <code>Interview</code> into the database.
     * @param interview the interview being inserted into the database
     */
    @Override
    public void saveInterview(Interview interview) {
    	Session session = sessionFactory.getCurrentSession();

    	if (interview.getCompany().isNewCompany()) {
    		Company company = new Company(interview.getCompany().getCompanyName());
    		int companyId = (int) session.save(company);
    		interview.getCompany().setCompanyId(companyId);
    	}

    	if (interview.getContact().isNewContact()) {
    		Contact contact = new Contact(interview.getContact().getFirstName(),
    				interview.getContact().getLastName());
    		contact.setCompany(interview.getCompany());
    		int contactId = (int) session.save(contact);
    		interview.getContact().setContactId(contactId);
    	}

        int interviewId = (int) session.save(interview);

        CompanyInterviews companyInterviews = new CompanyInterviews();
        companyInterviews.setInterviewId(interviewId);
        companyInterviews.setCompanyId(interview.getCompany().getCompanyId());
        companyInterviews.setContactId(interview.getContact().getContactId());
        session.save(companyInterviews);
    }

    @Override
    public void updateInterview(Interview interview) {
    	Session session = sessionFactory.getCurrentSession();

    	boolean updateCompanyInterviews = false;

    	if (interview.getCompany().isNewCompany()) {
    		Company newCompany = new Company(interview.getCompany().getCompanyName());
    		int companyId = (int) session.save(newCompany);
    		newCompany.setCompanyId(companyId);
    		interview.setCompany(newCompany);

    		updateCompanyInterviews = true;
    	}

        if (interview.getContact().isNewContact()) {
        	Contact contact = new Contact();
        	contact.setFirstName(interview.getContact().getFirstName());
        	contact.setLastName(interview.getContact().getLastName());
        	contact.setCompany(interview.getCompany());
        	int contactId = (int) session.save(contact);
        	contact.setContactId(contactId);
        	interview.setContact(contact);

        	updateCompanyInterviews = true;
        }

        session.update(interview);

        if (updateCompanyInterviews) {
	        CompanyInterviews companyInterviews =
	        		session.get(CompanyInterviews.class, interview.getInterviewId());
	        companyInterviews.setCompanyId(interview.getCompany().getCompanyId());
	        companyInterviews.setContactId(interview.getContact().getContactId());
        }

		if (LocalDateTime.now().isAfter(interview.getInterviewDate())) {
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("update Applicant applicant ");
			stringBuilder.append("set applicant.employer = :employer, ");
			stringBuilder.append("applicant.hireDate = :hireDate");

			if (!interview.isSuccessfulHireIndicator()) {
				stringBuilder.append(", applicant.contractToHireIndicator = false");
			}

			stringBuilder.append("	where applicant.applicantId = :applicantId");

			Query<?> query = session.createQuery(stringBuilder.toString());

			if (interview.isSuccessfulHireIndicator()) {
				query.setParameter("employer", interview.getCompany());
				query.setParameter("hireDate", LocalDate.now());
			} else {
				query.setParameter("employer", null);
				query.setParameter("hireDate", null);
			}

			query.setParameter("applicantId", interview.getApplicant().getApplicantId());
        	query.executeUpdate();
		}
    }

    @Override
    public void deleteInterview(int interviewId) {
    	Session session = sessionFactory.getCurrentSession();

    	CompanyInterviews companyInterviews = session.get(CompanyInterviews.class, interviewId);
    	session.delete(companyInterviews);

        Interview interview = session.get(Interview.class, interviewId);
        session.delete(interview);
    }

    @Override
    public List<InterviewStats> getInterviewStats(InterviewStatsCriteria criteria) {
        List<InterviewStats> interviewStats = new ArrayList<>();
        InterviewStats stats;

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("SELECT CONCAT(FIRST_NAME, ' ', LAST_NAME) AS APPLICANT, BEGIN_DATE, ");
        stringBuilder.append("CASE WHEN HIRE_DATE IS NULL THEN 'Unemployed'");
        stringBuilder.append("  WHEN HIRE_DATE IS NOT NULL AND CONTRACT_TO_HIRE_IND = 'Y' THEN 'Contractor'");
        stringBuilder.append("      ELSE 'Full Time' END AS STATUS, HIRE_DATE ");
        stringBuilder.append("FROM we_employ_you.applicant");

        if (criteria.isUnemployed()) {
            stringBuilder.append("  WHERE HIRE_DATE IS NULL ");
        } else {
            if (criteria.isAllEmployed()) {
				stringBuilder.append("  WHERE HIRE_DATE IS NOT NULL ");
			} else if (criteria.isFullTime()) {
				stringBuilder.append("  WHERE CONTRACT_TO_HIRE_IND = 'N' AND HIRE_DATE IS NOT NULL ");
			} else {
				stringBuilder.append("  WHERE CONTRACT_TO_HIRE_IND = 'Y' ");
            }
        }

		stringBuilder.append("ORDER BY APPLICANT");

        Session session = sessionFactory.getCurrentSession();

        @SuppressWarnings("unchecked")
		NativeQuery<Object[]> query = session.createNativeQuery(stringBuilder.toString());

		DateTimeFormatter orientationDateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy h:mm a");
        String orientationDate;
        DateTimeFormatter hireDateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        String hireDate = null;

        List<Object[]> results = query.list();

        for (Object[] result : results) {
        	orientationDate =
                orientationDateFormatter.format(((Timestamp) result[1]).toLocalDateTime());

            if (result[3] != null) {
                hireDate = hireDateFormatter.format(((Date) result[3]).toLocalDate());
            }

            stats = new InterviewStats(String.valueOf(result[0]), orientationDate,
                String.valueOf(result[2]), hireDate);

            interviewStats.add(stats);
        }

        return interviewStats;
    }
}
