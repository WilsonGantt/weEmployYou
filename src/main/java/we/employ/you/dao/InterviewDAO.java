package we.employ.you.dao;

import java.sql.SQLException;
import java.util.List;

import we.employ.you.model.Company;
import we.employ.you.model.Contact;
import we.employ.you.model.Interview;

/**
 * This DAO returns an <code>ObservableList</code> of interviews from the database,
 * either grabbing all interview data or interview data based on a given applicant
 * ID, or other forms of criteria. Insert, update and delete operations on interview
 * data are also invoked by this object.
 */
public interface InterviewDAO {

    public List<Interview> getInterviews();

    /**
     * Returns an <code>ObservableList</code> of all the interviews in the database,
     * based on the given applicant ID.
     * @param applicantId the database connection used to retrieve the data
     * @return an <code>ObservableList</code> of interviews
     */
    public List<Interview> getInterviews(int applicantId);

    public Interview getInterview(int interviewId);

    public List<Company> getCompanies();

    public List<Contact> getContacts(int companyId);

    /**
     * Saves the <code>Interview</code> into the database.
     * @param interview the interview being inserted into the database
     */
    public void saveInterview(Interview interview);

    /**
     * Updates the database with the <code>Interview</code> passed as a method
     * parameter.
     * @param interview the interview being inserted into the database
     */
    public void updateInterview(Interview interview);

    /**
     * Deletes the interview data from the database with the given interview ID.
     * @param interviewId the ID used to delete the selected record
     */
    public void deleteInterview(int interviewId);

    /**
     * Returns an <code>ObservableList</code> of <code>InterviewStats</code>, used
     * for reporting purposes
     * @param criteria the database connection used to retrieve the data
     * @return an <code>ObservableList</code> of <code>InterviewStats</code>
     */
    public List<InterviewStats> getInterviewStats(InterviewStatsCriteria criteria);

    /**
     * This nested class is used for reporting the total number of interviews
     * by employment status.
     */
    public class InterviewStats {
        private String applicant;
        private String orientationDate;
        private String employmentStatus;
        private String hireDate;

        /**
         * Constructor which initializes an <code>InterviewStats</code> object
         * and it's members.
         * @param applicant the applicant to set
         * @param orientationDate the orientationDate to set
         * @param employmentStatus the employmentStatus to set
         * @param hireDate
         */
        public InterviewStats(String applicant, String orientationDate,
                String employmentStatus, String hireDate) {
            this.applicant = applicant;
            this.orientationDate = orientationDate;
            this.employmentStatus = employmentStatus;
            this.hireDate = hireDate;
        }

        /**
         * Returns the applicant
         * @return applicant
         */
        public String getApplicant() {
            return applicant;
        }

        /**
         * Sets the applicant
         * @param applicant the applicant to set
         */
        public void setApplicant(String applicant) {
            this.applicant = applicant;
        }

        /**
         * Returns the orientationDate
         * @return orientationDate
         */
        public String getOrientationDate() {
            return orientationDate;
        }

        /**
         * Sets the orientationDate.
         * @param orientationDate the orientationDate to set
         */
        public void setType(String orientationDate) {
            this.orientationDate = orientationDate;
        }

        /**
         * Returns the applicant's employment status
         * @return employmentStatus
         */
        public String getEmploymentStatus() {
            return employmentStatus;
        }

        /**
         * Sets the applicant's employment status
         */
        public void setEmploymentStatus(String employmentStatus) {
            this.employmentStatus = employmentStatus;
        }

        /**
         * Returns the applicant's hire date
         */
        public String getHireDate() {
            return hireDate;
        }

        /**
         * Sets the applicant's hire date
         */
        public void setHireDate(String hireDate) {
            this.hireDate = hireDate;
        }
    }

    public static class InterviewStatsCriteria {
    	private boolean isUnemployed;
    	private boolean allEmployed;
    	private boolean isFullTime;

    	public InterviewStatsCriteria() {
    		super();
    	}

		public boolean isUnemployed() {
			return isUnemployed;
		}

		public void setIsUnemployed(boolean isUnemployed) {
			this.isUnemployed = isUnemployed;
		}

		public boolean isAllEmployed() {
			return allEmployed;
		}

		public void setIsAllEmployed(boolean allEmployed) {
			this.allEmployed = allEmployed;
		}

		public boolean isFullTime() {
			return isFullTime;
		}

		public void setIsFullTime(boolean isFullTime) {
			this.isFullTime = isFullTime;
		}

    }
}
