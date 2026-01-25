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
     * @return applicants
     */
    public List<Applicant> getApplicants();

    /**
     * Returns the <code>Applicant</code> from the database with the given applicant ID.
     * @param applicantId used to collect his/her applicant data
     * @return applicants
     */
    public Applicant getApplicant(int applicantId);

    public byte[] getApplicantPhoto(int applicantId);

    public void saveApplicantPhoto(int applicantId, byte[] photo);

    /**
     * Saves a <code>Applicant</code> into the database.
     * @param applicant the applicant to save in the database
     * @return the newly inserted applicant ID
     */
    public int saveApplicant(Applicant applicant);

    /**
     * Updates a <code>Applicant</code> in the database.
     * @param applicant the applicant to update in the database
     */
    public void updateApplicant(Applicant applicant) throws ValidationException;

    /**
     * Deletes a <code>Applicant</code> from the database.
     * @param applicantId the applicant ID used to delete the applicant from the database
     * @throws MissingDataException if the applicant cannot be found with the given ID
     */
    public void deleteApplicant(int applicantId) throws MissingDataException;

    /**
     * Saves an applicant's resume into the database
     * @param applicantFile the applicant who owns the resume
     */
    public void saveApplicantFile(ApplicantFile applicantFile);

    /**
     *  Downloads the applicant's resume to the user's desktop
     * @param fileId the database connection used to get and download the resume
     * @return fileName the resume's file name
     */
    public byte[] downloadApplicantFile(int fileId) throws IOException, MissingDataException;

    public List<ApplicantFile> getApplicantFiles(int applicantId);
    
    public void deleteApplicantFile(int fileId);
}
