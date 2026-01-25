package we.employ.you.dao;

import java.io.IOException;
import java.util.List;

import we.employ.you.exception.MissingDataException;
import we.employ.you.exception.ValidationException;
import we.employ.you.model.Applicant;
import we.employ.you.model.ApplicantFile;

/**
 * This DAO initiates transactions by selecting, inserting, updating and deleting
 * applicant data from the database.
 */
public interface ApplicantDAO {

    /**
     * Returns an <code>ObservableList</code> of all the applicants from the database.
     * @param connection the database connection used to retrieve the applicant data
     * @return applicants
     * @throws SQLException if an error occurs while accessing the database
     */
    public List<Applicant> getApplicants();

    /**
     * Returns the <code>Applicant</code> from the database with the given applicant ID.
     * @param connection the database connection used to retrieve the applicant data
     * @param applicantId used to collect his/her applicant data
     * @return applicants
     * @throws SQLException if an error occurs while accessing the database
     */
    public Applicant getApplicant(int applicantId);

    public byte[] getApplicantPhoto(int applicantId);

    public void saveApplicantPhoto(int applicantId, byte[] photo);

    /**
     * Saves a <code>Applicant</code> into the database.
     * @param connection the database connection used to save the applicant data
     * @param applicant the applicant to save in the database
     * @return the newly inserted applicant ID
     * @throws SQLException if an error occurs while accessing the database
     */
    public int saveApplicant(Applicant applicant);

    /**
     * Updates a <code>Applicant</code> in the database.
     * @param connection the database connection used to update the applicant data
     * @param applicant the applicant to update in the database
     * @throws SQLException if an error occurs while accessing the database
     */
    public void updateApplicant(Applicant applicant) throws ValidationException;

    /**
     * Deletes a <code>Applicant</code> from the database.
     * @param connection the database connection used to delete the applicant data
     * @param applicantId the applicant ID used to delete the applicant from the database
     * @throws MissingDataException if the applicant cannot be found with the given ID
     * @throws SQLException if an error occurs while accessing the database
     */
    public void deleteApplicant(int applicantId) throws MissingDataException;

    /**
     * Saves an applicant's resume into the database
     * @param connection the database connection used to save the resume
     * @param inputStream the stream of data consisting of the resume
     * @param applicant the applicant who owns the resume
     * @throws Exception if a database or IO error occurs during the transaction
     */
    public void saveApplicantFile(ApplicantFile applicantFile);

    /**
     *  Downloads the applicant's resume to the user's desktop
     * @param connection the database connection used to get and download the resume
     * @param applicant the <code>Applicant</code> who owns the resume
     * @return fileName the resume's file name
     * @throws Exception if a database or IO error occurs during the transaction
     */
    public byte[] downloadApplicantFile(int fileId) throws IOException, MissingDataException;

    public List<ApplicantFile> getApplicantFiles(int applicantId);
    
    public void deleteApplicantFile(int fileId);
}
