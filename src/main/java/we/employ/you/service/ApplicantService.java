package we.employ.you.service;

import java.io.IOException;
import java.util.List;

import we.employ.you.exception.MissingDataException;
import we.employ.you.exception.ValidationException;
import we.employ.you.model.Applicant;
import we.employ.you.model.ApplicantFile;
import we.employ.you.model.User;

public interface ApplicantService {

	public List<Applicant> getApplicants();

	public Applicant getApplicant(int applicantId);

	public byte[] getApplicantPhoto(int applicantId);

	public void saveApplicantPhoto(int applicantId, byte[] photo);

	public List<User> getRecruiters();

	public int saveApplicant(Applicant applicant);

	public void updateApplicant(Applicant applicant) throws ValidationException;

	public void deleteApplicant(int applicantId) throws MissingDataException;

	public void saveApplicantFile(ApplicantFile file);

	public byte[] downloadApplicantFile(int fileId) throws IOException, MissingDataException;
	
	public void deleteApplicantFile(int fileId);

	public List<ApplicantFile> getApplicantFiles(int applicantId);
}
