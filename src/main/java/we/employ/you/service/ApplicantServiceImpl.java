package we.employ.you.service;

import java.io.IOException;
import java.util.List;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import we.employ.you.dao.ApplicantDAO;
import we.employ.you.dao.UserDAO;
import we.employ.you.exception.MissingDataException;
import we.employ.you.exception.ValidationException;
import we.employ.you.model.Applicant;
import we.employ.you.model.ApplicantFile;
import we.employ.you.model.User;

@Service
public class ApplicantServiceImpl implements ApplicantService {

	private final ApplicantDAO applicantDAO;

	@Autowired
	private UserDAO userDAO;

	@Autowired
	public ApplicantServiceImpl(ApplicantDAO applicantDAO) {
		this.applicantDAO = applicantDAO;
	}

	@Override
	@Transactional
	public List<Applicant> getApplicants() {
		return applicantDAO.getApplicants();
	}

	@Override
	@Transactional
	public Applicant getApplicant(int applicantId) {
		return applicantDAO.getApplicant(applicantId);
	}

	@Override
	@Transactional
	public byte[] getApplicantPhoto(int applicantId) {
		return applicantDAO.getApplicantPhoto(applicantId);
	}

	@Override
	@Transactional
	public void saveApplicantPhoto(int applicantId, byte[] photo) {
		applicantDAO.saveApplicantPhoto(applicantId, photo);
	}

	@Override
	@Transactional
	public List<User> getRecruiters() {
		return userDAO.getRecruiters();
	}

	@Override
	@Transactional
	public int saveApplicant(Applicant applicant) {
		return applicantDAO.saveApplicant(applicant);
	}

	@Override
	@Transactional
	public void updateApplicant(Applicant applicant) throws ValidationException {
		applicantDAO.updateApplicant(applicant);
	}

	@Override
	@Transactional
	public void deleteApplicant(int applicantId) throws MissingDataException {
		applicantDAO.deleteApplicant(applicantId);
	}

	@Override
	@Transactional
	public void saveApplicantFile(ApplicantFile file) {
		applicantDAO.saveApplicantFile(file);
	}

	@Override
	@Transactional
	public byte[] downloadApplicantFile(int fileId) throws IOException, MissingDataException {
		return applicantDAO.downloadApplicantFile(fileId);
	}
	
	@Override
	@Transactional
	public void deleteApplicantFile(int fileId) {
		applicantDAO.deleteApplicantFile(fileId);
	}

	@Override
	@Transactional
	public List<ApplicantFile> getApplicantFiles(int applicantId) {
		return applicantDAO.getApplicantFiles(applicantId);
	}
}
